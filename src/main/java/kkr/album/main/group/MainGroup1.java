package kkr.album.main.group;

import kkr.album.main.MainModifyGpx;

import org.apache.log4j.Logger;

public class MainGroup1 {
	private static final Logger LOGGER = Logger.getLogger(MainGroup1.class);

	public static void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("####################################################");
			LOGGER.info("BATCH_MODIFY_GPX");
			LOGGER.info("####################################################");
			MainModifyGpx.main(args);
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

}
