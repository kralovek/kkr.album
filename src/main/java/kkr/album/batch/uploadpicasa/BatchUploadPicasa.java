package kkr.album.batch.uploadpicasa;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsPattern;

public class BatchUploadPicasa extends BatchUploadPicasaFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchUploadPicasa.class);

	private static final Pattern PATTERN_N = Pattern.compile("[0-9]{8}n_"
			+ UtilsPattern.MASK_TIME + "\\." + UtilsPattern.MASK_EXT_FILE);

	private static final FileFilter FILE_FILTER_N = new UtilsPattern.FileFilterFile(
			PATTERN_N);

	public void run(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File dirGps = new File(dirBase, "gps");
			File dirPhotos = new File(dirBase, "photos");

			String name = UtilsAlbums.determineName(dirBase);

			List<File> filePhotos = new ArrayList<File>(); 
			
			if (dirGps.isDirectory()) {
				File[] files = dirGps.listFiles(FILE_FILTER_N);
				for (File file : files) {
					filePhotos.add(file);
				}
			}

			if (dirPhotos.isDirectory()) {
				File[] files = dirPhotos.listFiles(FILE_FILTER_N);
				for (File file : files) {
					filePhotos.add(file);
				}
			}

			managerPicasa.createOrUpdateAlbum(name, filePhotos);
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
