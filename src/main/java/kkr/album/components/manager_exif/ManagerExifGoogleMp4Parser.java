package kkr.album.components.manager_exif;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;

import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;
import kkr.album.model.DateNZ;

public class ManagerExifGoogleMp4Parser extends ManagerExifGoogleMp4ParserFwk implements ManagerExif {
	private static final Logger LOGGER = Logger.getLogger(ManagerExifGoogleMp4Parser.class);

	private void rn(File file, String text) {
		File fileTest = new File(file.getParentFile(), "test");
		if (!file.renameTo(fileTest)) {
			LOGGER.info(text + " NO1");
		} else {
			LOGGER.info(text + " YES1");
			if (!fileTest.renameTo(file)) {
				LOGGER.info(text + " NO2");
			} else {
				LOGGER.info(text + " YES2");
			}
		}
	}

	public DateNZ determineDate(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			IsoFile isoFile = null;
			try {
				isoFile = new IsoFile(file.getAbsolutePath());

				MovieBox moov = isoFile.getBoxes(MovieBox.class).get(0);

				MovieHeaderBox movieHeaderBox = moov.getMovieHeaderBox();

				long duration = movieHeaderBox.getDuration();

				Date date = movieHeaderBox.getCreationTime();
				DateNZ dateNZ = new DateNZ(date);

				DateNZ dateNZbeg = dateNZ.moveMiliSeconds((int) -duration);

				isoFile.close();
				isoFile = null;

				LOGGER.trace("OK");
				return dateNZbeg;
			} catch (IOException ex) {
				throw new TechnicalException("Problem", ex);
			} finally {
				if (isoFile != null) {
					try {
						isoFile.close();
						isoFile = null;
					} catch (Exception ex) {
						//
					}
				}
			}
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, DateNZ date, Double longitude, Double latitude) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.warn("NOT IMPLEMENTED FOR MP4");
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, DateNZ date) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.warn("NOT IMPLEMENTED FOR MP4");
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public static final void main(String[] args) throws Exception {
		ManagerExifGoogleMp4Parser main = new ManagerExifGoogleMp4Parser();
		DateNZ date = main.determineDate(new File("00033719v.mp4"));
		LOGGER.debug("Date: " + date);
	}

	public void copyExif(File fileSource, File fileTarget) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
