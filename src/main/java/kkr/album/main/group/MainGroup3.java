package kkr.album.main.group;

import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.main.MainArchiveFilesN;
import kkr.album.main.MainArchiveFilesOV;
import kkr.album.main.MainCopyPicasa;

import org.apache.log4j.Logger;

public class MainGroup3 {
	private static final Logger LOGGER = Logger.getLogger(MainGroup3.class);
	
	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			work(args);
			LOGGER.trace("OK");
		} catch (Throwable th) {
			TreatErrors.treatException(th);
		} finally {
			LOGGER.trace("END");
		}
	}

	public static void work(String[] args) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("####################################################");
			LOGGER.info("BATCH_ARCHIVE_FILES N");
			LOGGER.info("####################################################");
			MainArchiveFilesN.work(new String[] {});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_COPY_PICASA");
			LOGGER.info("####################################################");
			MainCopyPicasa.work(new String[] {});
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
