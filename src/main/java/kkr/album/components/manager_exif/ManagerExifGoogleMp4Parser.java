package kkr.album.components.manager_exif;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;

import org.apache.log4j.Logger;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;

public class ManagerExifGoogleMp4Parser extends ManagerExifGoogleMp4ParserFwk
		implements ManagerExif {
	private static final Logger LOGGER = Logger
			.getLogger(ManagerExifGoogleMp4Parser.class);

	public Date determineDate(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {

			IsoFile isoFile = null;
			try {
				isoFile = new IsoFile(file.getAbsolutePath());
				MovieBox moov = isoFile.getBoxes(MovieBox.class).get(0);

				MovieHeaderBox movieHeaderBox = moov.getMovieHeaderBox();

				Date date = movieHeaderBox.getCreationTime();

				isoFile.close();
				isoFile = null;

				LOGGER.trace("OK");
				return date;
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

	public void modifyFile(File file, Date date, Double longitude,
			Double latitude) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.warn("NOT IMPLEMENTED FOR MP4");
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, Date date) throws BaseException {
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
		Date date = main.determineDate(new File("00033719v.mp4"));
		LOGGER.debug("Date: " + date);
	}
}
