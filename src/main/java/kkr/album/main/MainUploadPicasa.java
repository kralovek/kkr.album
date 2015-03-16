package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.batch.copypicasa.BatchCopyPicasa;
import kkr.album.batch.uploadpicasa.BatchUploadPicasa;
import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainUploadPicasa {
	private static final Logger LOGGER = Logger.getLogger(MainCopyPicasa.class);

	private static final String BEAN_ID = "batchUploadPicasa";

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
			
			BatchUploadPicasa batchUploadPicasa = beanFactory.getBean(BEAN_ID, BatchUploadPicasa.class);
			
			File dirCurrent = new File(System.getProperty("user.dir"));
			
			batchUploadPicasa.run(dirCurrent);
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
