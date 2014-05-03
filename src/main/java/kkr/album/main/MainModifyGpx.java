package kkr.album.main;

import java.io.File;
import java.util.Map;

import kkr.album.components.batch_modifygpxs.BatchModifyGpxs;
import kkr.album.exception.TreatErrors;
import kkr.album.utils.UtilsBean;
import kkr.album.utils.UtilsCommandLine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

public class MainModifyGpx {
	private static final Logger LOGGER = Logger.getLogger(MainModifyGpx.class);

	private static final String BEAN_ID = "batchModifyGpxs";
	
	public static final void main(String[] args) {
		LOGGER.trace("BEGIN");
		try {
			Map<String, String> parameters = UtilsCommandLine.commandLineToMap(args);
			BeanFactory beanFactory = UtilsBean.createBeanFactory(parameters);
			
			BatchModifyGpxs batchModifyGpxs = beanFactory.getBean(BEAN_ID, BatchModifyGpxs.class);
			
			batchModifyGpxs.run(new File("."));
			
			LOGGER.trace("OK");
		} catch (Throwable th) {
			TreatErrors.treatException(th);
		} finally {
			LOGGER.trace("END");
		}
	}
}
