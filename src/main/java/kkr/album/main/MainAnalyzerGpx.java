package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.batch.analyzergpx.BatchAnalyzerGpx;
import kkr.album.exception.BaseException;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainAnalyzerGpx {
	private static transient final Logger LOGGER = Logger
			.getLogger(MainAnalyzerGpx.class);

	private static final String BEAN_ID = "batchAnalyzerGpx";

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
			Map<String, String> parameters = UtilsCommandLine
					.commandLineToMap(args);
			BeanFactory beanFactory = UtilsBean.createBeanFactory(parameters);

			Config_MainAnalyzerGpx config = new Config_MainAnalyzerGpx(args);

			BatchAnalyzerGpx batchAnalyzerGpx = beanFactory.getBean(BEAN_ID,
					BatchAnalyzerGpx.class);

			File dirCurrent = new File(System.getProperty("user.dir"));

			if (config.getFileGpx() != null) {
				batchAnalyzerGpx.runFile(config.getFileGpx(), config.getDirResult());
			} else {
				batchAnalyzerGpx.runDir(config.getDirGpx(), config.getDirResult(), config.getPattern());
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

}
