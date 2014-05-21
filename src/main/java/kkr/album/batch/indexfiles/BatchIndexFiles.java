package kkr.album.batch.indexfiles;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import kkr.album.batch.archivefiles.BatchArchiveFiles;
import kkr.album.exception.BaseException;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;

import org.apache.log4j.Logger;

public class BatchIndexFiles extends BatchIndexFilesFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchArchiveFiles.class);

	private static final Pattern PATTERN_FILES_FROM_PHOTOS = Pattern
			.compile(UtilsPattern.MASK_TIME + "_[A-Z0-9]+"
					+ "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final Pattern PATTERN_PHOTOS_FROM_GPS = Pattern
			.compile(UtilsPattern.MASK_TIME + "_[0-9]{2}"
					+ "\\." + UtilsPattern.MASK_EXT_PHOTO);

	private static final Pattern PATTERN_O = Pattern.compile("[0-9]{8}o_"
			+ UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_PHOTOS_FROM_GPS = new UtilsPattern.FileFilterFile(
			PATTERN_PHOTOS_FROM_GPS);

	private static final FileFilter FILE_FILTER_FILES_FROM_PHOTOS = new UtilsPattern.FileFilterFile(
			PATTERN_FILES_FROM_PHOTOS);

	private static final FileFilter FILE_FILTER_O = new UtilsPattern.FileFilterFile(
			PATTERN_O);

	public void runON(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			if (dirGps.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Renaming files: ");
				File[] files = dirGps.listFiles(FILE_FILTER_O);
				for (File file : files) {
					String filenameN = file.getName().substring(0, 8) + "n" + file.getName().substring(9);
					File fileTarget = new File(file.getParentFile(), filenameN);
					LOGGER.info("\tRenaming file: " + file.getName() + " to: " + fileTarget.getName());
					UtilsFile.moveFile(file, fileTarget);
				}
			}

			if (dirPhotos.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Renaming files: ");
				File[] files = dirPhotos.listFiles(FILE_FILTER_O); 
				for (File file : files) {
					String filenameN = file.getName().substring(0, 8) + "n" + file.getName().substring(9);
					File fileTarget = new File(file.getParentFile(), filenameN);
					LOGGER.info("\tRenaming file: " + file.getName() + " to: " + fileTarget.getName());
					UtilsFile.moveFile(file, fileTarget);
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
	
	public void runIndex(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			int newIndex = managerArchive.nextIndex();

			if (dirGps.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Indexing GPS files");

				File[] files = dirGps.listFiles(FILE_FILTER_PHOTOS_FROM_GPS);
				List<File> listFiles = new ArrayList<File>();
				for (File file : files) {
					listFiles.add(file);
				}
				// Sort by name
				Collections.sort(listFiles);

				for (File file : listFiles) {
					String name = String.format("%08d", newIndex) + "o";
					String time = timeStringFromFile(file);
					String ext = UtilsFile.extension(file);

					File fileTarget = new File(dirGps, name + "_" + time
							+ "." + ext);

					LOGGER.info("\tIndexing file: " + file.getName() + " to: "
							+ fileTarget.getName());
					UtilsFile.moveFile(file, fileTarget);
					newIndex++;
				}
			}

			if (dirPhotos.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirPhotos.getAbsolutePath());
				LOGGER.info("Indexing PHOTOS files");

				File[] files = dirPhotos
						.listFiles(FILE_FILTER_FILES_FROM_PHOTOS);
				List<File> listFiles = new ArrayList<File>();
				for (File file : files) {
					listFiles.add(file);
				}
				Collections.sort(listFiles);

				for (File file : listFiles) {
					String symbol = "";
					if (isPhoto(file)) {
						symbol = "o";
					} else if (isVideo(file)) {
						symbol = "v";
					}

					String name = String.format("%08d", newIndex) + symbol;
					String time = timeStringFromFile(file);
					String ext = UtilsFile.extension(file);

					File fileTarget = new File(dirPhotos, name + "_" + time
							+ "." + ext);

					LOGGER.info("\tIndexing file: " + file.getName() + " to: "
							+ fileTarget.getName());
					UtilsFile.moveFile(file, fileTarget);
					newIndex++;
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private String timeStringFromFile(File file) {
		return file.getName().replaceFirst("_.*", "");
	}

	private boolean isPhoto(File file) {
		return UtilsPattern.PATTERN_PHOTO.matcher(file.getName()).matches();
	}

	private boolean isVideo(File file) {
		return UtilsPattern.PATTERN_VIDEO.matcher(file.getName()).matches();
	}
}
