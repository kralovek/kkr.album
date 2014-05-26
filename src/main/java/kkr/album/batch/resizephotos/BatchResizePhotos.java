package kkr.album.batch.resizephotos;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import kkr.album.exception.BaseException;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;

import org.apache.log4j.Logger;

public class BatchResizePhotos extends BatchResizePhotosFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchResizePhotos.class);

	private static final Pattern PATTERN_O = Pattern.compile("[0-9]{8}o_"
			+ UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_O = new UtilsPattern.FileFilterFile(
			PATTERN_O);
	
	public void runResize(File dirBase) throws BaseException {
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
					UtilsFile.copyFile(file, fileTarget);
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
					managerImage.resize(file, fileTarget, toWidth);
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
