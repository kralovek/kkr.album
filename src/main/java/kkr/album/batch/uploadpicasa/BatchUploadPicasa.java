package kkr.album.batch.uploadpicasa;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsConsole;
import kkr.album.utils.UtilsFile;

public class BatchUploadPicasa extends BatchUploadPicasaFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchUploadPicasa.class);

	public void run(File dirCurrent) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			String name = UtilsAlbums.determineName(dirBase);
			Date date = UtilsAlbums.determineDate(dirBase);

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);

			File dirPicasaYear = new File(dirPicasa, String.format("%04d",
					calendar.get(Calendar.YEAR)));
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
