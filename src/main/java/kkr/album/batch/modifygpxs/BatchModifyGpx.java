package kkr.album.batch.modifygpxs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsGpx;

public class BatchModifyGpx extends BatchModifyGpxsFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchModifyGpx.class);

	private static final long MB = 1048576L;

	private static final Pattern PATTERN_GPX_AUTO = Pattern
			.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}\\.[0-9]{2}\\.[0-9]{2} Auto\\.[gG][pP][xX]");

	private static final FileFilter FILE_FILTER_GPX_AUTO = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_GPX_AUTO.matcher(file.getName()).matches();
		}
	};

	public void runFile(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			runFile(file, null);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runFile(File file, String name) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("BATCH_MODIFY_GPX: working file: " + file.getAbsolutePath());
			String nameLoc = name != null ? name : getNameFromFilename(file.getName());
			run(file, new File[0], nameLoc);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}


	public void runAuto(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			runAuto(dirBase, null);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runAuto(File dirBase, String name) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("BATCH_MODIFY_GPX: working directory: " + dirBase.getAbsolutePath());
			if (!dirBase.isDirectory()) {
				throw new FunctionalException("The directory does not exist: "
						+ dirBase.getAbsolutePath());
			}

			String nameLoc;
			if (name != null) {
				nameLoc = name;
			} else {
				nameLoc = UtilsAlbums.determineName(dirBase);
			}

			File dirGps = new File(dirBase, "gps");

			File fileGpxCurrent = new File(dirGps, "Current.gpx");

			File[] fileGpxAutos = dirGps.listFiles(FILE_FILTER_GPX_AUTO);

			run(fileGpxCurrent, fileGpxAutos, nameLoc);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void run(File fileGpxCurrent, File[] fileGpxAutos, String name)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			if (!fileGpxCurrent.isFile()) {
				throw new FunctionalException("Missing current GPX file: "
						+ fileGpxCurrent.getAbsolutePath());
			}

			LOGGER.info("BATCH_MODIFY_GPX: loading: " + fileGpxCurrent.getName());
			Gpx gpxCurrent = managerGpx.loadGpx(fileGpxCurrent);

			if (gpxCurrent.getTraces().size() == 0) {
				throw new FunctionalException(
						"The GPX file does not contain any trace: "
								+ fileGpxCurrent.getAbsolutePath());
			}

			List<Gpx> gpxAutos = new ArrayList<Gpx>();

			if (fileGpxAutos != null) {
				for (File fileGpxAuto : fileGpxAutos) {
					LOGGER.info("BATCH_MODIFY_GPX: loading: " + fileGpxAuto.getName());
					Gpx gpxAuto = managerGpx.loadGpx(fileGpxAuto);
					gpxAutos.add(gpxAuto);
				}
			}

			Gpx gpx = UtilsGpx.joinGpxs(gpxCurrent, gpxAutos);

			gpx.getTraces().get(0).setName(name);

			File dirGps = fileGpxCurrent.getParentFile();

			File fileGpxOutput = new File(dirGps, name + ".gpx");
			LOGGER.info("BATCH_MODIFY_GPX: writting: " + fileGpxOutput.getName());
			managerGpx.saveGpx(gpx, fileGpxOutput);

			if (!fileGpxCurrent.delete()) {
				throw new TechnicalException(
						"Cannot remove the CURRENT GPX file: "
								+ fileGpxCurrent.getAbsolutePath());
			}
			if (fileGpxAutos != null) {
				for (File fileGpxAuto : fileGpxAutos) {
					if (!fileGpxAuto.delete()) {
						throw new TechnicalException(
								"Cannot remove the AUTO GPX file: "
										+ fileGpxAuto.getAbsolutePath());
					}
				}
			}

			long length = fileGpxOutput.length();

			if (length > MB) {
				LOGGER.warn("BATCH_MODIFY_GPX: The size of created GPX file has more than 1MB");
				File fileGpxSimplyOutput = new File(dirGps, "x" + name + ".gpx");
				LOGGER.info("BATCH_MODIFY_GPX: writting: " + fileGpxSimplyOutput.getName());
				Gpx gpxSimply = UtilsGpx.simlifyGpx(gpx);
				managerGpx.saveGpx(gpxSimply, fileGpxSimplyOutput);
				length = fileGpxSimplyOutput.length();

				if (length > MB) {
					LOGGER.warn("BATCH_MODIFY_GPX: The size of created GPX file HAS STILL more than 1MB !!!");
				}
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
}
