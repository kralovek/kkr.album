package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.batch.archivefiles.BatchArchiveFiles;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainArchiveFiles {
	private static final Logger LOGGER = Logger
			.getLogger(MainArchiveFiles.class);

	private static final String BEAN_ID = "batchArchiveFiles";

	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			Map<String, String> parameters = UtilsCommandLine.commandLineToMap(args);
			BeanFactory beanFactory = UtilsBean.createBeanFactory(parameters);
			
			BatchArchiveFiles batchArchiveFiles = beanFactory.getBean(BEAN_ID, BatchArchiveFiles.class);
			
			File dirCurrent = new File(System.getProperty("user.dir"));
			
			Config_MainArchiveFiles config = new Config_MainArchiveFiles(args);
			
			if (config.getMode().equals(Config_MainArchiveFiles.Mode.OV)) {
				batchArchiveFiles.runOV(dirCurrent);
			} else if (config.getMode().equals(Config_MainArchiveFiles.Mode.OV)) {
				batchArchiveFiles.runN(dirCurrent);
			}
			
			LOGGER.trace("OK");
		} catch (Throwable th) {
			TreatErrors.treatException(th);
		} finally {
			LOGGER.trace("END");
		}
	}
}
