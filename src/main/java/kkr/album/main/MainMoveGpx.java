package kkr.album.main;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import kkr.album.batch.movegpx.BatchMoveGpx;
import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

public class MainMoveGpx {
	private static final Logger LOGGER = Logger.getLogger(MainMoveGpx.class);

	private static final String BEAN_ID = "batchMoveGpx";

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

			BatchMoveGpx batchMoveGpx = beanFactory.getBean(BEAN_ID, BatchMoveGpx.class);

			File dirCurrent = new File(System.getProperty("user.dir"));

			ConfigMoveGpx config = new ConfigMoveGpx(args);

			batchMoveGpx.run(dirCurrent, config.getFileIn(), config.getFileOut(), config.getLat(), config.getLon());

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
