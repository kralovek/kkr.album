package kkr.album.batch.modifyphotos;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.components.timeevaluator.TimeEvaluator;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;

public class BatchModifyPhotos extends BatchModifyPhotosFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchModifyPhotos.class);

	private static final Pattern PATTERN_FILES_FROM_GPS = Pattern
			.compile("[0-9]{2}" + "\\." + UtilsPattern.MASK_EXT_PHOTO);

	private static final Pattern PATTERN_GPX = Pattern
			.compile("[0-9]{8}.*\\.[gG][pP][xX]");

	private static final Pattern PATTERN_PHOTOS_DIR = Pattern
			.compile("photos[A-Z0-9]+");

	private static final Pattern PATTERN_FILES_FROM_PHOTOS = Pattern
			.compile(".*" + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_PHOTOS_DIRS = new UtilsPattern.FileFilterDir(
			PATTERN_PHOTOS_DIR);

	private static final FileFilter FILE_FILTER_FILES_FROM_GPS = new UtilsPattern.FileFilterFile(
			PATTERN_FILES_FROM_GPS);

	private static final FileFilter FILE_FILTER_FILES_FROM_PHOTOS = new UtilsPattern.FileFilterFile(
			PATTERN_FILES_FROM_PHOTOS);

	private static final FileFilter FILE_FILTER_GPX = new UtilsPattern.FileFilterFile(
			PATTERN_GPX);

	public void run(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			String name = UtilsAlbums.determineName(dirBase);
			Date date = UtilsAlbums.determineDate(dirBase);

			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			if (!dirGps.isDirectory()) {
				throw new FunctionalException("GPS directory does not exist: "
						+ dirGps.getAbsolutePath());
			}

			Map<String, File> dirsPhotos = loadFilesPhotos(dirBase);

			Map<String, TimeEvaluator.FileTime> times;
			times = timeEvaluator.loadTimes(dirGps);

			Gpx gpx = loadFilesGpx(dirGps);

			for (TimeEvaluator.FileTime fileTime : times.values()) {
				workTime(fileTime, gpx);
			}

			if (dirsPhotos.size() != 0) {

				Map<String, String> tags = loadTags(dirGps);

				for (Map.Entry<String, File> entry : dirsPhotos.entrySet()) {
					if (!times.containsKey(entry.getKey())) {
						throw new FunctionalException(
								"No time file for the photos directory: "
										+ entry.getValue().getAbsolutePath());
					}
				}

				for (Map.Entry<String, File> entry : dirsPhotos.entrySet()) {
					TimeEvaluator.FileTime fileTime = times.get(entry.getKey());
					workDirPhotos(entry.getKey(), entry.getValue(), dirPhotos,
							fileTime.getMove(), tags, gpx);
				}
			}

			workDirGps(dirGps, gpx, date);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private Gpx loadFilesGpx(File dirGps) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			File[] files = dirGps.listFiles(FILE_FILTER_GPX);

			if (files == null || files.length == 0) {
				throw new FunctionalException(
						"No album GPX file found in the directory: "
								+ dirGps.getAbsolutePath());
			}

			if (files.length > 1) {
				throw new FunctionalException(
						"More than one album GPX file found in the directory: "
								+ dirGps.getAbsolutePath());
			}

			File fileGpx = files[0];
			try {
				Gpx gpx = managerGpx.loadGpx(fileGpx);
				LOGGER.trace("OK");
				return gpx;
			} catch (Exception ex) {
				throw new TechnicalException("Cannot read the GPX file: "
						+ fileGpx.getAbsolutePath(), ex);
			}

		} finally {
			LOGGER.trace("END");
		}
	}

	private Map<String, File> loadFilesPhotos(File dirBase)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			Map<String, File> retval = new HashMap<String, File>();

			File[] dirsPhotos = dirBase.listFiles(FILE_FILTER_PHOTOS_DIRS);

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

			if (!fileTags.isFile()) {
				LOGGER.trace("OK");
				return null;
			}

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
				LOGGER.warn("### The specified tags file does not exist");
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

	private void workTime(TimeEvaluator.FileTime fileTime, Gpx gpx)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			Date time = managerExif.determineDate(fileTime.getFile());
			Date timeMove = moveTime(time, fileTime.getMove());

			Point point = interpolateGpx(timeMove, gpx);
			if (point == null) {
				LOGGER.warn("### No GPS position found for the file: "
						+ fileTime.getFile().getAbsolutePath());
			}
			managerExif.modifyFile(fileTime.getFile(), timeMove,
					(point != null ? point.getLongitude() : null),
					(point != null ? point.getLatitude() : null));

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workDirGps(File dirGps, Gpx gpx, Date date)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
			LOGGER.info("Modifying GPS files");

			File[] files = dirGps.listFiles(FILE_FILTER_FILES_FROM_GPS);

			if (files != null && files.length > 0) {
				if (gpx == null) {
					gpx = loadFilesGpx(dirGps);
				}
				Point point = mainPointGpx(gpx);

				for (File file : files) {
					String stringTime = UtilsPattern.DATE_FORMAT_DATETIME0
							.format(date);
					String ext = UtilsFile.extension(file);
					File fileTarget = new File(dirGps, stringTime + "_"
							+ file.getName());
					LOGGER.info("\tRenaming file: " + file.getName() + " to: "
							+ fileTarget.getName());
					UtilsFile.moveFile(file, fileTarget);

					LOGGER.info("\tModifying file: " + fileTarget.getName()
							+ " Date: " + stringTime + " Lon/Lat: "
							+ toString(point.getLongitude()) + "/"
							+ toString(point.getLatitude()));
					managerExif.modifyFile(fileTarget, date,
							point.getLongitude(), point.getLatitude());
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workDirPhotos(String symbolName, File dir, File dirTarget,
			Long move, Map<String, String> tags, Gpx gpx) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("WORKING DIR: " + dir.getAbsolutePath());
			LOGGER.info("Modifying PHOTOS files: " + move + " sec.");

			File[] files = dir.listFiles(FILE_FILTER_FILES_FROM_PHOTOS);
			if (files == null) {
				throw new TechnicalException(
						"The listed directory does not exist: "
								+ dir.getAbsolutePath());
			}
			boolean eqDir = dir.equals(dirTarget);
			for (File file : files) {
				Date time = managerExif.determineDate(file);
				Date timeMove = moveTime(time, move);
				Point point = interpolateGpx(timeMove, gpx);
				if (point == null) {
					LOGGER.warn("### No GPS position found for the file: "
							+ file.getAbsolutePath());
				}
				String stringTime = UtilsPattern.DATE_FORMAT_DATETIME
						.format(timeMove);
				String ext = UtilsFile.extension(file);
				File fileTarget = new File(dirTarget, stringTime + "_"
						+ symbolName + "." + ext);
				LOGGER.info("\tRenaming file: " + file.getName() + " to: "
						+ fileTarget.getName());
				UtilsFile.moveFile(file, fileTarget);

				LOGGER.info("\tModifying file: " + fileTarget.getName()
						+ " Date: " + stringTime + " Lon/Lat: "
						+ (point != null ? toString(point.getLongitude()) : "-") + "/"
						+ (point != null ? toString(point.getLatitude()) : "-"));

				managerExif.modifyFile(fileTarget, timeMove,
						(point != null ? point.getLongitude() : null),
						(point != null ? point.getLatitude() : null));
			}

			if (!eqDir) {
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
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private Date moveTime(Date date, long seconds) {
		Date retval = new Date(date.getTime() + seconds * 1000L);
		return retval;
	}

	private Point mainPointGpx(Gpx gpx) {
		for (Trace trace : gpx.getTraces()) {
			for (Point point : trace.getPoints()) {
				return point;
			}
		}
		return null;
	}

	private Point interpolateGpx(Date time, Gpx gpx) {
		Point point1 = null;
		Point point2 = null;
		go_gpx: for (Trace trace : gpx.getTraces()) {
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

	private String toString(Object object) {
		return object != null ? object.toString() : "-";
	}
}
