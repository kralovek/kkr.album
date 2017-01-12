package kkr.album.batch.copypicasa;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.model.DateNZ;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsConsole;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;

public class BatchCopyPicasa extends BatchCopyPicasaFwk {
	private static final Logger LOGGER = Logger.getLogger(BatchCopyPicasa.class);

	private static final Pattern PATTERN_N = Pattern
			.compile("[0-9]{8}n_" + UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_N = new UtilsPattern.FileFilterFile(PATTERN_N);

	public void run(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			String name = UtilsAlbums.determineName(dirBase);
			DateNZ date = UtilsAlbums.determineDate(dirBase);

			File dirPicasaYear = new File(dirPicasa, String.format("%04d", date.getYear()));
			File dirPicasaAlbum = new File(dirPicasaYear, name);

			if (dirPicasaAlbum.isDirectory()) {
				String message = "The PICASA directory exists already: " + dirPicasaAlbum.getAbsolutePath();
				if (UtilsConsole.readAnswerYN(message + "\nDo you want to continue?")) {
					LOGGER.warn("Files will be copied to existing directory: " + dirPicasaAlbum.getAbsolutePath());
				} else {
					throw new FunctionalException(message);
				}
			}

			if (dirGps.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirGps.getAbsolutePath());
				LOGGER.info("Copyig GPS files to PICASA: " + dirPicasaAlbum.getAbsolutePath());

				File[] files = dirGps.listFiles(FILE_FILTER_N);
				for (File file : files) {
					File fileTarget = new File(dirPicasaAlbum, file.getName());
					LOGGER.info("\tCopying to PICASA: " + file.getName() + " -> " + fileTarget.getAbsolutePath());
					UtilsFile.copyFile(file, fileTarget);
				}
			}

			if (dirPhotos.isDirectory()) {
				LOGGER.info("WORKING DIR: " + dirPhotos.getAbsolutePath());
				LOGGER.info("Copyig PHOTOS files to PICASA: " + dirPicasaAlbum.getAbsolutePath());

				File[] files = dirPhotos.listFiles(FILE_FILTER_N);
				for (File file : files) {
					File fileTarget = new File(dirPicasaAlbum, file.getName());
					LOGGER.info("\tCopying to PICASA: " + file.getName() + " -> " + fileTarget.getAbsolutePath());
					UtilsFile.copyFile(file, fileTarget);
				}
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
