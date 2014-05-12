package kkr.album.main.group;

import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.main.MainModifyGpx;

import org.apache.log4j.Logger;

public class MainGroup1 {
	private static final Logger LOGGER = Logger.getLogger(MainGroup1.class);

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
			LOGGER.info("BATCH_MODIFY_GPX");
			LOGGER.info("####################################################");
			MainModifyGpx.work(args);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

}
