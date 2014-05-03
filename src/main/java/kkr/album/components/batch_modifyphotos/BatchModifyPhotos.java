package kkr.album.components.batch_modifyphotos;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsFile;

public class BatchModifyPhotos extends BatchModifyPhotosFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchModifyPhotos.class);

	private static final Pattern PATTERN_GPX = Pattern
			.compile(".*\\.[gG][pP][xX]");

	private static final Pattern PATTERN_PROHOTS_DIR = Pattern
			.compile("photos[A-Z]*");

	private static final Pattern PATTERN_PROHOTS_FILE = Pattern
			.compile(".*\\.[jJ][pP][eE]?[gG]");

	private static final FileFilter FILE_FILTER_PHOTOS_DIRS = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isDirectory()) {
				return false;
			}
			return PATTERN_PROHOTS_DIR.matcher(file.getName()).matches();
		}
	};

	private static final FileFilter FILE_FILTER_PHOTOS_FILES = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_PROHOTS_FILE.matcher(file.getName()).matches();
		}
	};

	private static final FileFilter FILE_FILTER_GPX = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_GPX.matcher(file.getName()).matches();
		}
	};

	public void run(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			String name = UtilsAlbums.determineName(dirBase);

			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			Map<String, File> dirsPhotos = loadPhotosDirs(dirGps);

			if (dirsPhotos.size() != 0) {

				Map<String, String> tags = loadTags(dirGps);

				List<Gpx> gpxs = loadGpxs(dirGps);

				Map<String, Long> times;
				times = timeEvaluator.loadTimes(dirGps);

				for (Map.Entry<String, File> entry : dirsPhotos.entrySet()) {
					if (!times.containsKey(entry.getKey())) {
						throw new FunctionalException(
								"No time file for the photos directory: "
										+ entry.getValue().getAbsolutePath());
					}
				}

				for (Map.Entry<String, File> entry : dirsPhotos.entrySet()) {
					Long move = times.get(entry.getKey());
					workPhotosDir(entry.getKey(), entry.getValue(), dirPhotos,
							move, tags, gpxs);
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private List<Gpx> loadGpxs(File dirGps) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			List<Gpx> gpxs = new ArrayList<Gpx>();

			File[] files = dirGps.listFiles(FILE_FILTER_GPX);

			if (files != null) {
				for (File file : files) {
					try {
						Gpx gpx = managerGpx.loadGpx(file);
						gpxs.add(gpx);
					} catch (Exception ex) {
						throw new TechnicalException(
								"Cannot read the GPX file: "
										+ file.getAbsolutePath(), ex);
					}
				}
			}

			LOGGER.trace("OK");
			return gpxs;
		} finally {
			LOGGER.trace("END");
		}
	}

	private Map<String, File> loadPhotosDirs(File dirGps) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			Map<String, File> retval = new HashMap<String, File>();

			File[] dirsPhotos = dirGps.listFiles(FILE_FILTER_PHOTOS_DIRS);

			for (File dirLoc : dirsPhotos) {
				retval.put(dirLoc.getName().substring(6), dirLoc);
			}

			LOGGER.trace("OK");
			return retval;
		} finally {
			LOGGER.trace("END");
		}
	}

	private Map<String, String> loadTags(File dirGps) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			Map<String, String> retval = new LinkedHashMap<String, String>();

			File fileTags = new File(dirGps, filenameTags);

			InputStream inputStream = null;
			try {
				Properties properties = new Properties();

				inputStream = new FileInputStream(fileTags);
				properties.load(inputStream);

				for (Map.Entry<Object, Object> entry : properties.entrySet()) {
					retval.put((String) entry.getKey(),
							(String) entry.getValue());
				}

				inputStream.close();
				inputStream = null;

				LOGGER.trace("OK");
				return retval;
			} catch (FileNotFoundException ex) {
				LOGGER.warn("The specified tags file does not exist");
				return retval;
			} catch (IOException ex) {
				throw new TechnicalException("Cannot read the tags file: "
						+ fileTags.getAbsolutePath(), ex);
			} finally {
				UtilsFile.closeRessource(inputStream);
			}
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workPhotosDir(String symbol, File dir, File dirTarget,
			Long move, Map<String, String> tags, List<Gpx> gpxs)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			File[] files = dir.listFiles(FILE_FILTER_PHOTOS_FILES);
			if (files == null) {
				throw new TechnicalException(
						"The listed directory does not exist: "
								+ dir.getAbsolutePath());
			}
			boolean eqDir = dir.equals(dirTarget);
			for (File file : files) {
				Date time = managerExif.determineDate(file);
				Point point = interpolateGpx(time, gpxs);
				managerExif.modifyFile(file, time, point.getLongitude(),
						point.getLatitude(), tags);

				if (!eqDir) {
					File fileTarget = new File(dirTarget, symbol + "_"
							+ file.getName());
					renameTo(file, fileTarget);
				}
			}

			files = dir.listFiles();
			if (files.length == 0) {
				if (!dir.delete()) {
					throw new TechnicalException(
							"Cannot remove the directory: "
									+ dir.getAbsolutePath());
				}
			} else {
				LOGGER.warn("The directory will not be removed because it's not empty: "
						+ dir.getAbsolutePath());
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void renameTo(File fileSource, File fileTarget)
			throws BaseException {
		File dirParent = fileTarget.getParentFile();
		if (!dirParent.isDirectory() && !dirParent.mkdirs()) {
			throw new TechnicalException("Cannot create the directory: "
					+ dirParent.getAbsolutePath());
		}
	}

	private Point interpolateGpx(Date time, List<Gpx> gpxs) {
		Point point1 = null;
		Point point2 = null;
		go_gpx: for (Gpx gpx : gpxs) {
			for (Trace trace : gpx.getTraces()) {
				Point lastPoint = null;
				for (Point point : trace.getPoints()) {
					if (lastPoint != null
							&& lastPoint.getTime().compareTo(time) <= 0
							&& point.getTime().compareTo(time) >= 0) {
						point1 = lastPoint;
						point2 = point;
						break go_gpx;
					}
					lastPoint = point;
				}
			}
		}

		if (point1 == null) {
			return null;
		}

		Point point = new Point();
		point.setTime(time);
		point.setLongitude(interpolate(time, point1.getTime(),
				point2.getTime(), point1.getLongitude(), point2.getLongitude()));
		point.setLatitude(interpolate(time, point1.getTime(), point2.getTime(),
				point1.getLatitude(), point2.getLatitude()));
		point.setCadence(interpolate(time, point1.getTime(), point2.getTime(),
				point1.getCadence(), point2.getCadence()));
		point.setHeartRate(interpolate(time, point1.getTime(),
				point2.getTime(), point1.getHeartRate(), point2.getHeartRate()));
		point.setTemperature(interpolate(time, point1.getTime(),
				point2.getTime(), point1.getTemperature(),
				point2.getTemperature()));
		point.setElevation(interpolate(time, point1.getTime(),
				point2.getTime(), point1.getElevation(), point2.getElevation()));
		return point;
	}

	private Double interpolate(Date time, Date time1, Date time2,
			Double value1, Double value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		double dt = (double) (time2.getTime() - time1.getTime());
		double dtt = (double) (time.getTime() - time1.getTime());
		double dv = (double) (value2 - value1);
		double value = dv * dtt / dt + value1;
		return value;
	}

	private Integer interpolate(Date time, Date time1, Date time2,
			Integer value1, Integer value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		double dt = (double) (time2.getTime() - time1.getTime());
		double dtt = (double) (time.getTime() - time1.getTime());
		double dv = (double) (value2 - value1);
		double value = dv * dtt / dt + (double) value1;
		return (int) value;
	}
}
