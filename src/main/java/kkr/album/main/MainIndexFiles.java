package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.batch.indexfiles.BatchIndexFiles;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainIndexFiles {
	private static final Logger LOGGER = Logger.getLogger(MainIndexFiles.class);
	
	private static final String BEAN_ID = "batchIndexFiles";
	
	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			Map<String, String> parameters = UtilsCommandLine.commandLineToMap(args);
			BeanFactory beanFactory = UtilsBean.createBeanFactory(parameters);
			
			BatchIndexFiles batchIndexFiles = beanFactory.getBean(BEAN_ID, BatchIndexFiles.class);
			
			File dirCurrent = new File(System.getProperty("user.dir"));
			
			Config_MainRenameON config = new Config_MainRenameON(args);
			
			batchIndexFiles.runIndex(dirCurrent);
			
			LOGGER.trace("OK");
		} catch (Throwable th) {
			TreatErrors.treatException(th);
		} finally {
			LOGGER.trace("END");
		}
	}
}
