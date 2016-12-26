package kkr.album.batch.archivefiles;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;

public class BatchArchiveFiles extends BatchArchiveFilesFwk {
	private static final Logger LOGGER = Logger.getLogger(BatchArchiveFiles.class);

	private static final Pattern PATTERN_O = Pattern
			.compile("[0-9]{8}o_" + UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final Pattern PATTERN_N = Pattern
			.compile("[0-9]{8}n_" + UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final Pattern PATTERN_V = Pattern
			.compile("[0-9]{8}v_" + UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_O = new UtilsPattern.FileFilterFile(PATTERN_O);
	private static final FileFilter FILE_FILTER_N = new UtilsPattern.FileFilterFile(PATTERN_N);
	private static final FileFilter FILE_FILTER_V = new UtilsPattern.FileFilterFile(PATTERN_V);

	public void runCopyOV(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "_photos");

			if (dirGps.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Archiving COPY O&V GPS files");

				File[] files = dirGps.listFiles(FILE_FILTER_O);
				for (File file : files) {
					LOGGER.info("\tArchiving file: " + file.getAbsolutePath());
					managerArchive.copyToArchiv(file);
				}
			}

			if (dirPhotos.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirPhotos.getAbsolutePath());
				LOGGER.info("Archiving COPY O&V PHOTOS files");
				File[] files = dirPhotos.listFiles(FILE_FILTER_O);
				for (File file : files) {
					managerArchive.copyToArchiv(file);
				}

				files = dirPhotos.listFiles(FILE_FILTER_V);
				for (File file : files) {
					LOGGER.info("\tArchiving file: " + file.getAbsolutePath());
					managerArchive.moveToArchiv(file);
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runMoveOV(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "_photos");

			if (dirGps.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Archiving MOVE O&V GPS files");

				File[] files = dirGps.listFiles(FILE_FILTER_O);
				for (File file : files) {
					LOGGER.info("\tArchiving file: " + file.getAbsolutePath());
					managerArchive.moveToArchiv(file);
				}
			}

			if (dirPhotos.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirPhotos.getAbsolutePath());
				LOGGER.info("Archiving MOVE O&V PHOTOS files");
				File[] files = dirPhotos.listFiles(FILE_FILTER_O);
				for (File file : files) {
					managerArchive.moveToArchiv(file);
				}

				files = dirPhotos.listFiles(FILE_FILTER_V);
				for (File file : files) {
					LOGGER.info("\tArchiving file: " + file.getAbsolutePath());
					managerArchive.moveToArchiv(file);
				}
			}

			UtilsFile.removeEmptyDir(dirPhotos);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void runCopyN(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "_photos");
			File dirPhotosFinal = new File(dirBase, "photos");

			if (dirGps.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Archiving COPY N GPS files");
				File[] files = dirGps.listFiles(FILE_FILTER_N);
				for (File file : files) {
					LOGGER.info("\tArchiving file: " + file.getName());
					try {
						managerArchive.copyToArchiv(file);
					} catch (FunctionalException ex) {
						LOGGER.warn("\t\tfile already archived: " + file.getName());
					}
				}
			}

			if (dirPhotos.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirPhotos.getAbsolutePath());
				LOGGER.info("Archiving COPY N PHOTOS files");
				File[] files = dirPhotos.listFiles(FILE_FILTER_N);
				for (File file : files) {
					LOGGER.info("\tArchiving file: " + file.getName());
					managerArchive.copyToArchiv(file);
					moveToFinal(file, dirPhotosFinal);
				}
			}

			UtilsFile.removeEmptyDir(dirPhotos);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void moveToFinal(File file, File dirFinal) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File fileTarget = new File(dirFinal, file.getName());
			UtilsFile.createFileDir(fileTarget);
			if (fileTarget.isFile()) {
				throw new FunctionalException("File is already archived: " + file.getAbsolutePath());
			}
			UtilsFile.moveFile(file, fileTarget);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
