package kkr.album.main.group;

import org.apache.log4j.Logger;

import kkr.album.main.MainArchiveFiles;
import kkr.album.main.MainIndexFiles;
import kkr.album.main.MainModifyPhotos;
import kkr.album.main.MainRenameON;

public class MainGroup2 {
	private static final Logger LOGGER = Logger.getLogger(MainGroup2.class);
	
	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("####################################################");
			LOGGER.info("BATCH_MODIFY_PHOTOS");
			LOGGER.info("####################################################");
			MainModifyPhotos.main(new String[]{});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_INDEX_FILES");
			LOGGER.info("####################################################");
			MainIndexFiles.main(new String[]{});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_ARCHIVE_FILES OV");
			LOGGER.info("####################################################");
			MainArchiveFiles.main(new String[] {"-move", "ov"});

			LOGGER.info("####################################################");
			LOGGER.info("BATCH_RENAME_ON");
			LOGGER.info("####################################################");
			MainRenameON.main(new String[]{});

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

}
