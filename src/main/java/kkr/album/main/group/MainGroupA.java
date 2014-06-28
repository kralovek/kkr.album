package kkr.album.main.group;

import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.main.MainArchiveFiles;
import kkr.album.main.MainCopyPicasa;
import kkr.album.main.MainIndexFiles;
import kkr.album.main.MainModifyPhotos;
import kkr.album.main.MainResizePhotos;

import org.apache.log4j.Logger;

public class MainGroupA {

	private static final Logger LOGGER = Logger.getLogger(MainGroup23.class);

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
			if (config.getEtapes().contains(
					Config_MainGroupA.Etape.MODIFY_PHOTOS)) {
				LOGGER.info("####################################################");
				LOGGER.info("BATCH_MODIFY_PHOTOS");
				LOGGER.info("####################################################");
				MainModifyPhotos.work(new String[] {});
			}
			if (config.getEtapes().contains(
					Config_MainGroupA.Etape.INDEX_FILES)) {
				LOGGER.info("####################################################");
				LOGGER.info("BATCH_INDEX_FILES");
				LOGGER.info("####################################################");
				MainIndexFiles.work(new String[] {});
			}
			if (config.getEtapes().contains(
					Config_MainGroupA.Etape.RESIZE_PHOTOS)) {
				LOGGER.info("####################################################");
				LOGGER.info("BATCH_RESIZE_PHOTOS");
				LOGGER.info("####################################################");
				MainResizePhotos.work(new String[] {});
			}
			if (config.getEtapes().contains(
					Config_MainGroupA.Etape.ARCHIVE_FILES)) {
				LOGGER.info("####################################################");
				LOGGER.info("BATCH_ARCHIVE_FILES");
				LOGGER.info("####################################################");
				MainArchiveFiles.work(new String[] {});
			}
			if (config.getEtapes().contains(
					Config_MainGroupA.Etape.COPY_PICASA)) {
				LOGGER.info("####################################################");
				LOGGER.info("BATCH_COPY_PICASA");
				LOGGER.info("####################################################");
				MainCopyPicasa.work(new String[] {});
			}
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
