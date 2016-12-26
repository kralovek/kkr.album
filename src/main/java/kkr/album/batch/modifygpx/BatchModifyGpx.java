package kkr.album.batch.modifygpx;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsGpx;
import kkr.album.utils.UtilsPattern;

public class BatchModifyGpx extends BatchModifyGpxFwk {
	private static final Logger LOGGER = Logger.getLogger(BatchModifyGpx.class);

	private static final long MB = 1048576L;

	private static final Pattern PATTERN_GPX_STOPWATCH = Pattern.compile(UtilsPattern.MASK_GPX_STOPWATCH);
	private static final Pattern PATTERN_GPX_X = Pattern.compile(UtilsPattern.MASK_GPX_X);
	private static final Pattern PATTERN_GPX_WAYPOINT = Pattern.compile(UtilsPattern.MASK_GPX_WAYPOINT);

	private static final Pattern PATTERN_GPX = Pattern.compile(".*\\.[gG][pP][xX]");

	private static final FileFilter FILE_FILTER_GPX = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			if (PATTERN_GPX_WAYPOINT.matcher(file.getName()).matches()) {
				return false;
			}
			if (PATTERN_GPX_X.matcher(file.getName()).matches()) {
				return false;
			}
			return PATTERN_GPX.matcher(file.getName()).matches();
		}
	};

	private static final FileFilter FILE_FILTER_GPX_AUTO = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			String filename = file.getName().toLowerCase();
			return filename.endsWith(".gpx") && !filename.startsWith("x") && !"Current.gpx".equals(file.getName())
					&& !PATTERN_GPX_STOPWATCH.matcher(file.getName()).matches();
		}
	};

	private static final FileFilter FILE_FILTER_GPX_STOPWATCH = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_GPX_STOPWATCH.matcher(file.getName()).matches();
		}
	};

	private static final FileFilter FILE_FILTER_GPX_X = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_GPX_X.matcher(file.getName()).matches();
		}
	};

	public void runFile(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			runFile(file, null);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runFile(File file, String name) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			LOGGER.info("WORKING FILE: " + file.getAbsolutePath());
			String nameLoc = name != null ? name : getNameFromFilename(file.getName());
			work(file, new File[0], nameLoc);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runAuto(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			runAuto(dirBase, null);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runAuto(File dirBase, String name) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			boolean exists = dirGps.exists();
			if (exists) {
				int k = 0;
			} else {
				if ("gps".equals(dirBase.getName())) {
					dirGps = dirBase;
					dirBase = dirBase.getParentFile();
				} else {
					throw new FunctionalException("Incorrect gps directory: " + dirBase.getAbsolutePath());
				}
			}

			LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());

			String nameLoc;
			if (name != null) {
				nameLoc = name;
			} else {
				nameLoc = UtilsAlbums.determineName(dirBase);
				if (nameLoc == null) {
					throw new FunctionalException("The base dir is not album name: " + dirBase.getAbsolutePath());
				}
			}

			cleanFiles(dirGps);

			File fileGpxCurrent = new File(dirGps, "Current.gpx");

			File[] fileGpxAutos = dirGps.listFiles(FILE_FILTER_GPX_AUTO);

			if (!fileGpxCurrent.isFile()) {
				File[] filesGpx = dirGps.listFiles(FILE_FILTER_GPX);
				if (filesGpx.length == 1) {
					fileGpxCurrent = filesGpx[0];
					fileGpxAutos = null;
				} else if (filesGpx.length == 0) {
					throw new FunctionalException(
							"The directory does not contain any GPX file: " + dirGps.getAbsolutePath());
				} else {
					throw new FunctionalException(
							"The directory does not contain Current.gpx, but contains more than one GPX file: "
									+ filesGpx.length);
				}
			}

			work(fileGpxCurrent, fileGpxAutos, nameLoc);

			workStopwatch(dirBase, nameLoc);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void work(File fileGpxCurrent, File[] fileGpxAutos, String name) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			if (!fileGpxCurrent.isFile()) {
				throw new FunctionalException("Missing current GPX file: " + fileGpxCurrent.getAbsolutePath());
			}

			LOGGER.info("\tLoading: " + fileGpxCurrent.getName());
			Gpx gpxCurrent = managerGpx.loadGpx(fileGpxCurrent);

			if (gpxCurrent.getTraces().size() == 0) {
				throw new FunctionalException(
						"The GPX file does not contain any trace: " + fileGpxCurrent.getAbsolutePath());
			}

			List<Gpx> gpxAutos = new ArrayList<Gpx>();

			if (fileGpxAutos != null) {
				for (File fileGpxAuto : fileGpxAutos) {
					LOGGER.info("\tLoading: " + fileGpxAuto.getName());
					Gpx gpxAuto = managerGpx.loadGpx(fileGpxAuto);
					gpxAutos.add(gpxAuto);
				}
			}

			Gpx gpx = UtilsGpx.joinGpxs(gpxCurrent, gpxAutos);

			gpx.getTraces().get(0).setName(name);

			File dirGps = fileGpxCurrent.getParentFile();

			File fileGpxOutput = new File(dirGps, name + ".gpx");
			File fileKmlOutput = new File(dirGps, name + ".kml");

			File fileGpxTarget = null;
			if (fileGpxOutput.getName().equals(fileGpxCurrent.getName())) {
				fileGpxTarget = fileGpxOutput;
				fileGpxOutput = new File(dirGps, name + ".gpx.tmp");
			}
			LOGGER.info("\tWritting GPX: " + fileGpxOutput.getName());
			managerGpx.saveGpx(gpx, fileGpxOutput);
			LOGGER.info("\tWritting KML: " + fileKmlOutput.getName());
			managerKml.saveKml(gpx, fileKmlOutput);

			if (!fileGpxCurrent.delete()) {
				throw new TechnicalException("Cannot remove the CURRENT GPX file: " + fileGpxCurrent.getAbsolutePath());
			}

			if (fileGpxTarget != null) {
				if (!fileGpxOutput.renameTo(fileGpxTarget)) {
					LOGGER.warn("Cannot rename file: " + fileGpxOutput.getAbsolutePath() + " -> "
							+ fileGpxTarget.getAbsolutePath());
				}
				fileGpxOutput = fileGpxTarget;
			}

			if (fileGpxAutos != null) {
				for (File fileGpxAuto : fileGpxAutos) {
					if (!fileGpxAuto.delete()) {
						throw new TechnicalException(
								"Cannot remove the AUTO GPX file: " + fileGpxAuto.getAbsolutePath());
					}
				}
			}

			long length = fileGpxOutput.length();

			if (length > MB) {
				LOGGER.warn("### BATCH_MODIFY_GPX: The size of created GPX file has more than 1MB");
				File fileGpxSimplyOutput = new File(dirGps, "x" + name + ".gpx");
				LOGGER.info("\tWritting: " + fileGpxSimplyOutput.getName());
				Gpx gpxSimply = UtilsGpx.simlifyGpx(gpx);
				managerGpx.saveGpx(gpxSimply, fileGpxSimplyOutput);
				length = fileGpxSimplyOutput.length();

				if (length > MB) {
					for (int i = 5; i > 1; i--) {
						LOGGER.warn(
								"### BATCH_MODIFY_GPX: The size of created GPX file HAS STILL more than 1MB !!! ... So I remove each "
										+ i + " point");
						Gpx gpxReduced = UtilsGpx.reduce(gpxSimply, i);
						fileGpxSimplyOutput.delete();
						managerGpx.saveGpx(gpxReduced, fileGpxSimplyOutput);
						length = fileGpxSimplyOutput.length();
						if (length < MB) {
							break;
						}
					}
				}
				if (length > MB) {
					LOGGER.warn("### BATCH_MODIFY_GPX: The size of created GPX file HAS STILL more than 1MB !!!");
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void workStopwatch(File dirBase, String name) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			File dirGps = new File(dirBase, "gps");
			File[] filesGpxStopwatch = dirGps.listFiles(FILE_FILTER_GPX_STOPWATCH);

			if (filesGpxStopwatch == null || filesGpxStopwatch.length == 0) {
				LOGGER.info("No Stopwatch");
				LOGGER.trace("OK");
				return;
			}

			if (filesGpxStopwatch.length > 1) {
				for (File file : filesGpxStopwatch) {
					LOGGER.error("More than one Stopwatch file: " + file.getAbsolutePath());
				}
				throw new FunctionalException("More than one Stopwatch file: " + filesGpxStopwatch.length);
			}

			String nameStopwatch = "Stopwatch_" + name;

			File fileStopwatch = new File(dirGps, nameStopwatch + ".gpx");

			LOGGER.info("\tLoading Stopwatch: " + filesGpxStopwatch[0].getName());
			Gpx gpx = managerGpx.loadGpx(filesGpxStopwatch[0]);

			if (!gpx.getTraces().isEmpty()) {
				Trace trace = gpx.getTraces().get(0);
				trace.setName(nameStopwatch);
			}

			managerGpx.saveGpx(gpx, fileStopwatch);

			if (!filesGpxStopwatch[0].delete()) {
				throw new TechnicalException("Cannot remove the file: " + filesGpxStopwatch[0].getAbsolutePath());
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private String getNameFromFilename(String filename) {
		String name = filename.replaceFirst("\\.[gG][pP][xX]$", "");
		return name;
	}

	private void cleanFiles(File dirGps) throws BaseException {
		File[] files = dirGps.listFiles(FILE_FILTER_GPX_X);
		for (File file : files) {
			if (!file.delete()) {
				throw new TechnicalException("Cannot remove the file: " + file.getAbsolutePath());
			}
		}
	}
}
