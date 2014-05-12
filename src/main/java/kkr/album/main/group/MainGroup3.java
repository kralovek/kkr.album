package kkr.album.main.group;

import kkr.album.main.MainArchiveFiles;
import kkr.album.main.MainCopyPicasa;

import org.apache.log4j.Logger;

public class MainGroup3 {
	private static final Logger LOGGER = Logger.getLogger(MainGroup3.class);
	
	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("####################################################");
			LOGGER.info("BATCH_ARCHIVE_FILES N");
			LOGGER.info("####################################################");
			MainArchiveFiles.main(new String[] {"-move", "n"});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_COPY_PICASA");
			LOGGER.info("####################################################");
			MainCopyPicasa.main(new String[] {});
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
