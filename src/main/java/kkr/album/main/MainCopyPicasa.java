package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.batch.copypicasa.BatchCopyPicasa;
import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainCopyPicasa {
	private static final Logger LOGGER = Logger.getLogger(MainCopyPicasa.class);

	private static final String BEAN_ID = "batchCopyPicasa";

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
			
			BatchCopyPicasa batchCopyPicasa = beanFactory.getBean(BEAN_ID, BatchCopyPicasa.class);
			
			File dirCurrent = new File(System.getProperty("user.dir"));
			
			batchCopyPicasa.run(dirCurrent);
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
