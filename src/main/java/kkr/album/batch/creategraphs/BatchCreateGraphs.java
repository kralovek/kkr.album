package kkr.album.batch.creategraphs;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.components.manager_graph.data.PointData;
import kkr.album.components.manager_graph.data.SetData;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.utils.UtilsGpx;
import kkr.album.utils.UtilsPattern;

public class BatchCreateGraphs extends BatchCreateGraphsFwk {
	private static final Logger LOG = Logger.getLogger(BatchCreateGraphs.class);

	private static final Pattern PATTERN_GPX_STOPWATCH = Pattern.compile(UtilsPattern.MASK_GPX_STOPWATCH);
	private static final Pattern PATTERN_GPX_X = Pattern.compile(UtilsPattern.MASK_GPX_X);
	private static final Pattern PATTERN_GPX_WAYPOINT = Pattern.compile(UtilsPattern.MASK_GPX_WAYPOINT);

	private static final Pattern PATTERN_GPX = Pattern.compile(".*\\.[gG][pP][xX]");

	private static final Color COLOR_ALTITUDE_FILL = new Color(173, 216, 167);
	private static final Color COLOR_ALTITUDE_LINE = new Color(0, 100, 0);

	private static final Color COLOR_HEART_RATE_FILL = new Color(255, 204, 255);
	private static final Color COLOR_HEART_RATE_LINE = new Color(128, 0, 0);

	private static final Color COLOR_TEMPERATURE_FILL = new Color(183, 228, 240);
	private static final Color COLOR_TEMPERATURE_LINE = new Color(0, 64, 128);

	private static final FileFilter FILE_FILTER_GPX = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			String filename = file.getName().toLowerCase();
			if (PATTERN_GPX_STOPWATCH.matcher(filename).matches() //
					|| PATTERN_GPX_WAYPOINT.matcher(filename).matches() //
					|| PATTERN_GPX_X.matcher(filename).matches()) {
				return false;
			}
			return PATTERN_GPX.matcher(filename).matches();
		}
	};

	private static enum DataType {
		ELEVATION, HEART_RATE, TEMPERATURE
	}

	public void runFile(File fileGpx) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			if (!fileGpx.isFile()) {
				throw new FunctionalException(
						"File GPX defined explicitely, but does not exist: " + fileGpx.getAbsolutePath());
			}
			work(fileGpx);
			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void runAuto(File dirBase) throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			//
			// DIR
			//
			File dirGps = new File(dirBase, "gps");
			if (!dirGps.exists()) {
				if ("gps".equals(dirBase.getName())) {
					dirGps = dirBase;
					dirBase = dirBase.getParentFile();
				} else {
					throw new FunctionalException("Incorrect gps directory: " + dirBase.getAbsolutePath());
				}
			}

			//
			// FILE
			//
			File fileGpx = new File(dirGps, dirBase.getName() + ".gpx");

			if (!fileGpx.isFile()) {
				LOG.warn("Predicted GPX file does not exist: " + fileGpx.getAbsolutePath());

				File[] filesGpx = dirGps.listFiles(FILE_FILTER_GPX);
				if (filesGpx.length == 0) {
					throw new FunctionalException("No GPX file in gps directory: " + dirGps.getAbsolutePath());
				} else if (filesGpx.length != 1) {
					throw new FunctionalException(
							"GPX file does not defined explicitely, but more than one GPX file in gps directory: "
									+ dirGps.getAbsolutePath());
				}
				fileGpx = filesGpx[0];
			}

			work(fileGpx);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}

	public void work(File fileGpx) throws BaseException {
		LOG.trace("BEGIN");
		try {
			LOG.info("WORKING FILE: " + fileGpx.getAbsolutePath());
			Gpx gpx = managerGpx.loadGpx(fileGpx);

			File dirGps = fileGpx.getParentFile();
			String title = titleFromFile(fileGpx);

			SetData setDataAltitude = null;
			setDataAltitude = gpxToSetData(gpx, DataType.ELEVATION);
			setDataAltitude.setColorFill(COLOR_ALTITUDE_FILL);
			setDataAltitude.setColorLine(COLOR_ALTITUDE_LINE);
			setDataAltitude.setMinExplicit(altitudeMin);
			setDataAltitude.setMaxExplicit(altitudeMax);
			if (setDataAltitude != null && !setDataAltitude.getPoints().isEmpty()) {
				File fileGraphAltitude = new File(dirGps, "03.jpg");
				LOG.info("    Writing Graph Altutude: " + fileGraphAltitude.getAbsolutePath());
				managerGraph.createGraph(fileGraphAltitude, width, height, title, "Trace [km]", setDataAltitude);
			}

			SetData setDataHeartRate = gpxToSetData(gpx, DataType.HEART_RATE);
			setDataHeartRate.setColorFill(COLOR_HEART_RATE_FILL);
			setDataHeartRate.setColorLine(COLOR_HEART_RATE_LINE);
			setDataHeartRate.setMinExplicit(heartRateMin);
			setDataHeartRate.setMaxExplicit(heartRateMax);
			if (setDataHeartRate != null && !setDataHeartRate.getPoints().isEmpty()) {
				File fileGraphHeartRate = new File(dirGps, "04.jpg");
				if (setDataAltitude != null && !setDataAltitude.getPoints().isEmpty()) {
					setDataAltitude.setColorFill(null);
					LOG.info("    Writing Graph Heart Rate: " + fileGraphHeartRate.getAbsolutePath());
					managerGraph.createGraph(fileGraphHeartRate, width, height, title, "Trace [km]", setDataHeartRate,
							setDataAltitude);
				} else {
					LOG.info("    Writing Graph Heart Rate: " + fileGraphHeartRate.getAbsolutePath());
					managerGraph.createGraph(fileGraphHeartRate, width, height, title, "Trace [km]", setDataHeartRate);
				}
			}

			SetData setDataTemperature = gpxToSetData(gpx, DataType.TEMPERATURE);
			setDataTemperature.setColorFill(COLOR_TEMPERATURE_FILL);
			setDataTemperature.setColorLine(COLOR_TEMPERATURE_LINE);
			setDataTemperature.setMinExplicit(temperatureMin);
			setDataTemperature.setMaxExplicit(temperatureMax);
			if (setDataTemperature != null && !setDataTemperature.getPoints().isEmpty()) {
				File fileGraphTemperature = new File(dirGps, "05.jpg");
				if (setDataAltitude != null && !setDataAltitude.getPoints().isEmpty()) {
					setDataAltitude.setColorFill(null);
					LOG.info("    Writing Graph Temperature: " + fileGraphTemperature.getAbsolutePath());
					managerGraph.createGraph(fileGraphTemperature, width, height, title, "Trace [km]",
							setDataTemperature, setDataAltitude);
				} else {
					LOG.info("    Writing Graph Temperature: " + fileGraphTemperature.getAbsolutePath());
					managerGraph.createGraph(fileGraphTemperature, width, height, title, "Trace [km]",
							setDataTemperature);
				}
			}

			LOG.trace("OK");
		} finally

		{
			LOG.trace("END");
		}

	}

	private SetData gpxToSetData(Gpx gpx, DataType dataType) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Collection<PointData> points = new ArrayList<PointData>();

			if (gpx.getTraces().size() > 1) {
				LOG.warn("More than one trace: " + gpx.getTraces().size() + " in the GPX file");
			}

			Point pointLast = null;
			double length = 0.0;
			String name = null;

			switch (dataType) {
			case ELEVATION:
				name = "Altitude [m]";
				break;
			case HEART_RATE:
				name = "Heart Rate [m]";
				break;
			case TEMPERATURE:
				name = "Temperature [°C]";
				break;
			}

			for (Trace trace : gpx.getTraces()) {
				for (Point point : trace.getPoints()) {
					if (point.getLatitude() == null && point.getLongitude() == null) {
						throw new FunctionalException("Point without latitude/longitude");
					}
					Double y = 0.;
					switch (dataType) {
					case ELEVATION:
						y = point.getElevation();
						break;
					case HEART_RATE:
						y = point.getHeartRate();
						break;
					case TEMPERATURE:
						y = point.getTemperature();
						break;
					}
					if (y == null) {
						continue;
					}

					PointData pointData = null;
					if (pointLast != null) {
						length += UtilsGpx.distanceKm(pointLast, point);
					}
					pointData = new PointData(length, y);
					pointLast = point;
					points.add(pointData);
				}
			}

			SetData retval = new SetData(points, name);
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

	private String titleFromFile(File file) {
		int pos = file.getName().lastIndexOf(".");
		return file.getName().substring(0, pos);
	}
}
