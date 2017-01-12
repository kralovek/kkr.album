package kkr.album.batch.modifyphotos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.components.timeevaluator.FileTime;
import kkr.album.components.timeevaluator.TimeType;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.model.DateNZ;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsConsole;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;
import kkr.album.utils.UtilsTimes;
import kkr.common.utils.UtilsResource;

public class BatchModifyPhotos extends BatchModifyPhotosFwk {
	private static final Logger LOGGER = Logger.getLogger(BatchModifyPhotos.class);

	private static final Pattern PATTERN_FILES_FROM_GPS = Pattern
			.compile("[0-9]{2}" + "\\." + UtilsPattern.MASK_EXT_PHOTO);

	private static final Pattern PATTERN_GPX = Pattern.compile("[0-9]{8}.*\\.[gG][pP][xX]");

	private static final Pattern PATTERN_PHOTOS_DIR = Pattern.compile("photos[A-Z0-9]+");

	private static final Pattern PATTERN_FILES_FROM_PHOTOS = Pattern.compile(".*" + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_PHOTOS_DIRS = new UtilsPattern.FileFilterDir(PATTERN_PHOTOS_DIR);

	private static final FileFilter FILE_FILTER_FILES_FROM_GPS = new UtilsPattern.FileFilterFile(
			PATTERN_FILES_FROM_GPS);

	private static final FileFilter FILE_FILTER_FILES_FROM_PHOTOS = new UtilsPattern.FileFilterFile(
			PATTERN_FILES_FROM_PHOTOS);

	private static final FileFilter FILE_FILTER_GPX = new UtilsPattern.FileFilterFile(PATTERN_GPX);

	private static final String DATE_PATTERN_CREATE = "yyyyMMdd-HHmmss";

	public void run(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			DateNZ date = UtilsAlbums.determineDate(dirBase);

			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, dirnamePhotos);

			if (!dirGps.isDirectory()) {
				throw new FunctionalException("GPS directory does not exist: " + dirGps.getAbsolutePath());
			}

			Map<String, File> dirsPhotos = loadFilesPhotos(dirBase);

			Map<String, Map<TimeType, FileTime>> times;
			times = timeEvaluator.loadTimes(dirGps);

			Gpx gpx = loadFilesGpx(dirGps);

			for (Map<TimeType, FileTime> map : times.values()) {
				for (FileTime fileTime : map.values()) {
					workTime(fileTime, gpx);
				}
			}

			if (dirsPhotos.size() != 0) {
				Map<String, String> tags = loadTags(dirGps);

				for (Map.Entry<String, File> entry : dirsPhotos.entrySet()) {
					Map<TimeType, FileTime> types = times.get(entry.getKey());
					if (types == null) {
						throw new FunctionalException(
								"No time file for the photos directory: " + entry.getValue().getAbsolutePath());
					}
				}

				for (Map.Entry<String, File> entry : dirsPhotos.entrySet()) {
					String symbolName = entry.getKey();
					File dir = entry.getValue();
					Map<TimeType, FileTime> fileTimeTypes = times.get(symbolName);
					workDirPhotos(symbolName, dir, dirPhotos, fileTimeTypes, tags, gpx);
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
				String message = "No album GPX file found in the directory: " + dirGps.getAbsolutePath();
				if (UtilsConsole.readAnswerYN(message + "\nDo you want to continue?")) {
					LOGGER.warn("No GPX file found. Photos will not be geolocalized!");
					return new Gpx();
				} else {
					throw new FunctionalException(message);
				}
			}

			if (files.length > 1) {
				throw new FunctionalException(
						"More than one album GPX file found in the directory: " + dirGps.getAbsolutePath());
			}

			File fileGpx = files[0];
			try {
				Gpx gpx = managerGpx.loadGpx(fileGpx);
				LOGGER.trace("OK");
				return gpx;
			} catch (Exception ex) {
				throw new TechnicalException("Cannot read the GPX file: " + fileGpx.getAbsolutePath(), ex);
			}

		} finally {
			LOGGER.trace("END");
		}
	}

	private Map<String, File> loadFilesPhotos(File dirBase) throws BaseException {
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
					retval.put((String) entry.getKey(), (String) entry.getValue());
				}

				inputStream.close();
				inputStream = null;

				LOGGER.trace("OK");
				return retval;
			} catch (FileNotFoundException ex) {
				LOGGER.warn("### The specified tags file does not exist");
				return retval;
			} catch (IOException ex) {
				throw new TechnicalException("Cannot read the tags file: " + fileTags.getAbsolutePath(), ex);
			} finally {
				UtilsFile.closeRessource(inputStream);
			}
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workTime(FileTime fileTime, Gpx gpx) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			DateNZ time = managerExif.determineDate(fileTime.getFile());
			DateNZ timeMove = time.moveSeconds(fileTime.getMove() != null ? fileTime.getMove().intValue() : 0);

			Point point = interpolateGpx(timeMove, gpx);
			if (point == null) {
				LOGGER.warn("### No GPS position found for the file: " + fileTime.getFile().getAbsolutePath());
			}
			managerExif.modifyFile(fileTime.getFile(), timeMove, (point != null ? point.getLongitude() : null),
					(point != null ? point.getLatitude() : null));

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workDirGps(File dirGps, Gpx gpx, DateNZ date) throws BaseException {
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
					String stringTime = date.toString(UtilsPattern.DATE_PATTERN_DATETIME0);
					File fileTarget = new File(dirGps, stringTime + "_" + file.getName());
					LOGGER.info("\tRenaming file: " + file.getName() + " to: " + fileTarget.getName());
					UtilsFile.moveFile(file, fileTarget);

					LOGGER.info("\tModifying file: " + fileTarget.getName() + " Date: " + stringTime + " Lon/Lat: "
							+ toString(point.getLongitude()) + "/" + toString(point.getLatitude()));
					managerExif.modifyFile(fileTarget, date, point.getLongitude(), point.getLatitude());
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workDirPhotos(String symbolName, File dir, File dirTarget, Map<TimeType, FileTime> fileTimeTypes,
			Map<String, String> tags, Gpx gpx) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("WORKING DIR: " + dir.getAbsolutePath());

			File[] files = dir.listFiles(FILE_FILTER_FILES_FROM_PHOTOS);
			if (files == null) {
				throw new TechnicalException("The listed directory does not exist: " + dir.getAbsolutePath());
			}

			Map<TimeType, Collection<File>> filesByType = new TreeMap<TimeType, Collection<File>>();
			for (File file : files) {
				TimeType timeType = UtilsTimes.timeTypeFromFile(file);
				if (!fileTimeTypes.containsKey(timeType)) {
					throw new FunctionalException(
							"No time file for the type: " + timeType + " in the directory: " + dir.getAbsolutePath());
				}
				Collection<File> filesOfType = filesByType.get(timeType);
				if (filesOfType == null) {
					filesOfType = new ArrayList<File>();
					filesByType.put(timeType, filesOfType);
				}
				filesOfType.add(file);
			}

			Collection<File> filesNoGps = new ArrayList<File>();

			for (Map.Entry<TimeType, Collection<File>> entry : filesByType.entrySet()) {
				TimeType timeType = entry.getKey();
				FileTime fileTime = fileTimeTypes.get(timeType);
				Collection<File> filesOfType = entry.getValue();

				int moveSeconds = fileTime.getMove() != null ? fileTime.getMove().intValue() : 0;
				LOGGER.info("Modifying PHOTOS files " + timeType.name() + ": " + moveSeconds + " sec.");

				workPhotosType(dir, dirTarget, filesOfType, moveSeconds, symbolName, gpx, filesNoGps);
			}

			if (!filesNoGps.isEmpty()) {
				LOGGER.warn("\tList of NON GPS files: " + filesNoGps.size());
				for (File file : filesNoGps) {
					LOGGER.warn("\t\tFile: " + file.getAbsolutePath());
				}
				String message = "Now you can geolocalize them yourself.";
				if (UtilsConsole.readAnswerYN(message + "\nDo you want to continue?")) {
					LOGGER.warn("No GPX file found. Photos will not be geolocalized!");
				} else {
					throw new FunctionalException(message);
				}
			}

			files = dir.listFiles();
			if (files.length == 0) {
				if (!dir.delete()) {
					throw new TechnicalException("Cannot remove the directory: " + dir.getAbsolutePath());
				}
			} else {
				LOGGER.warn("The directory will not be removed because it's not empty: " + dir.getAbsolutePath());
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workPhotosType(File dir, File dirTarget, Collection<File> files, int moveSeconds, String symbolName,
			Gpx gpx, Collection<File> filesNoGps) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			for (File file : files) {
				// Date time = managerExif.determineDate(file);
				DateNZ time = determineDate(file);

				if (time == null) {
					throw new TechnicalException("Cannot determine the time from the file: " + file.getAbsolutePath());
				}
				DateNZ timeMove = time.moveSeconds(moveSeconds);
				Point point = interpolateGpx(timeMove, gpx);
				if (point == null) {
					LOGGER.warn("### No GPS position found for the file: " + file.getAbsolutePath());
				}
				String stringTime = timeMove.toString(UtilsPattern.DATE_PATTERN_DATETIME);
				String ext = UtilsFile.extension(file);
				File fileTarget = new File(dirTarget, "CRUIDE_" + stringTime + "_" + symbolName + "." + ext);
				LOGGER.info("\tRenaming file: " + file.getName() + " to: " + fileTarget.getName());
				UtilsFile.moveFile(file, fileTarget);

				LOGGER.info("\tModifying file: " + fileTarget.getName() + " Date: " + stringTime + " Lon/Lat: "
						+ (point != null ? toString(point.getLongitude()) : "-") + "/"
						+ (point != null ? toString(point.getLatitude()) : "-"));

				managerExif.modifyFile(fileTarget, timeMove, (point != null ? point.getLongitude() : null),
						(point != null ? point.getLatitude() : null));
				if (point == null) {
					filesNoGps.add(fileTarget);
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private Point mainPointGpx(Gpx gpx) {
		for (Trace trace : gpx.getTraces()) {
			for (Point point : trace.getPoints()) {
				return point;
			}
		}
		return null;
	}

	private Point interpolateGpx(DateNZ time, Gpx gpx) {
		Point point1 = null;
		Point point2 = null;
		go_gpx: for (Trace trace : gpx.getTraces()) {
			Point lastPoint = null;
			for (Point point : trace.getPoints()) {
				if (point.getTime() == null) {
					continue;
				}
				if (lastPoint != null && lastPoint.getTime().compareTo(time) <= 0
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
		point.setLongitude(
				interpolate(time, point1.getTime(), point2.getTime(), point1.getLongitude(), point2.getLongitude()));
		point.setLatitude(
				interpolate(time, point1.getTime(), point2.getTime(), point1.getLatitude(), point2.getLatitude()));
		point.setCadence(
				interpolate(time, point1.getTime(), point2.getTime(), point1.getCadence(), point2.getCadence()));
		point.setHeartRate(
				interpolate(time, point1.getTime(), point2.getTime(), point1.getHeartRate(), point2.getHeartRate()));
		point.setTemperature(interpolate(time, point1.getTime(), point2.getTime(), point1.getTemperature(),
				point2.getTemperature()));
		point.setElevation(
				interpolate(time, point1.getTime(), point2.getTime(), point1.getElevation(), point2.getElevation()));
		return point;
	}

	private Double interpolate(DateNZ time, DateNZ time1, DateNZ time2, Double value1, Double value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		double dt = (double) time2.durationMs(time1);
		double dtt = (double) time.durationMs(time1);
		double dv = (double) (value2 - value1);
		double value = dv * dtt / dt + value1;
		return value;
	}

	private String toString(Object object) {
		return object != null ? object.toString() : "-";
	}

	private DateNZ determineDate(File file) throws BaseException {
		DateNZ date = managerExif.determineDate(file);
		return date;
	}

	private DateNZ determineDateX(File file) throws BaseException {
		String line;
		Process process = null;
		try {
			String command = "xBatchCreateDate.bat -file \"" + file.getAbsolutePath() + "\"";
			process = Runtime.getRuntime().exec(command);
		} catch (IOException ex) {
			throw new TechnicalException(
					"Cannot retrieve date by external xBatchCreateDate - execution. File: " + file.getAbsolutePath(),
					ex);
		}
		InputStreamReader isr = null;
		BufferedReader bri = null;

		try {
			isr = new InputStreamReader(process.getInputStream());
			bri = new BufferedReader(isr);

			line = bri.readLine();

			bri.close();
			bri = null;

			isr.close();
			isr = null;
		} catch (IOException ex) {
			throw new TechnicalException("Cannot read output from external xBatchCreateDate", ex);
		} finally {
			UtilsResource.closeResource(bri);
			UtilsResource.closeResource(isr);
		}

		if (line == null) {
			throw new TechnicalException(
					"Cannot retrieve date by external xBatchCreateDate - no return. File: " + file.getAbsolutePath());
		}

		if ("NULL".equals(line)) {
			return null;
		}

		try {
			DateNZ date = new DateNZ(line, DATE_PATTERN_CREATE);
			return date;
		} catch (Exception ex) {
			throw new TechnicalException("Cannot retrieve date by external xBatchCreateDate - bad format. File: "
					+ file.getAbsolutePath() + " Source: " + line, ex);
		}
	}
}
