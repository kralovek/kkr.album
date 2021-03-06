package kkr.album.main.group;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.main.MainArchiveFiles;
import kkr.album.main.MainIndexFiles;
import kkr.album.main.MainModifyPhotos;
import kkr.album.main.MainResizePhotos;

public class MainGroupA {

	private static final Logger LOGGER = Logger.getLogger(MainGroupA.class);

	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			Config_MainGroupA config = new Config_MainGroupA(args);

			work(config);
			LOGGER.trace("OK");
		} catch (Throwable th) {
			TreatErrors.treatException(th);
		} finally {
			LOGGER.trace("END");
		}
	}

	public static void work(Config_MainGroupA config) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("####################################################");
			LOGGER.info("BATCH_MODIFY_PHOTOS");
			LOGGER.info("####################################################");
			MainModifyPhotos.work(new String[] {});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_INDEX_FILES");
			LOGGER.info("####################################################");
			MainIndexFiles.work(new String[] {});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_RESIZE_PHOTOS");
			LOGGER.info("####################################################");
			MainResizePhotos.work(new String[] {});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_ARCHIVE_FILES");
			LOGGER.info("####################################################");
			MainArchiveFiles.work(new String[] {});

			// LOGGER.info("####################################################");
			// LOGGER.info("BATCH_COPY_PICASA");
			// LOGGER.info("####################################################");
			// MainCopyPicasa.work(new String[] {});

			// LOGGER.info("####################################################");
			// LOGGER.info("BATCH_UPLOAD_PICASA");
			// LOGGER.info("####################################################");
			// MainUploadPicasa.work(new String[] {});

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
