package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.batch.indexfiles.BatchIndexFiles;
import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainRenameON {
	private static final Logger LOGGER = Logger.getLogger(MainRenameON.class);

	private static final String BEAN_ID = "batchIndexFiles";

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

	public static final void work(String[] args) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			Map<String, String> parameters = UtilsCommandLine.commandLineToMap(args);
			BeanFactory beanFactory = UtilsBean.createBeanFactory(parameters);
			
			BatchIndexFiles batchIndexFiles = beanFactory.getBean(BEAN_ID, BatchIndexFiles.class);
			
			File dirCurrent = new File(System.getProperty("user.dir"));
			
			batchIndexFiles.runON(dirCurrent);
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
