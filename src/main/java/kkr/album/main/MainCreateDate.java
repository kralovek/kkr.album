package kkr.album.main;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import kkr.album.batch.createdate.BatchCreateDate;
import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

public class MainCreateDate {
	private static final Logger LOGGER = Logger.getLogger(MainCreateDate.class);

	private static final String BEAN_ID = "batchCreateDate";

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

			BatchCreateDate batchCreateDate = beanFactory.getBean(BEAN_ID, BatchCreateDate.class);

			ConfigCreateDate config = new ConfigCreateDate(args);

			batchCreateDate.run(config.getFile());

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

}
