package kkr.album.utils;

import java.util.Map;
import java.util.Properties;

import kkr.album.exception.BaseException;
import kkr.album.exception.ConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UtilsBean {
	private static final Logger LOGGER = Logger.getLogger(UtilsBean.class);

	private static final String FILE_CONFIG = "kkr.album.xml";
	private static final String FILE_PROPERTIES = "kkr.album.properties";
	
	public static BeanFactory createBeanFactory(Map<String, String> parameters) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			try {
				ConfigurableApplicationContext configurableApplicationContext;
				final ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
				classPathXmlApplicationContext.setConfigLocation(FILE_CONFIG);
				configurableApplicationContext = classPathXmlApplicationContext;

				if (parameters != null && !parameters.isEmpty()) {
					final PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
					Properties properties = new Properties();
					properties.putAll(parameters);
					propertyPlaceholderConfigurer.setProperties(properties);
					propertyPlaceholderConfigurer
							.setBeanFactory(configurableApplicationContext);
					configurableApplicationContext
							.addBeanFactoryPostProcessor(propertyPlaceholderConfigurer);
				}
				configurableApplicationContext.refresh();

				LOGGER.trace("OK");
				return configurableApplicationContext;
			} catch (final BeansException ex) {
				throw new ConfigurationException(ex.getMessage(), ex);
			}
		} finally {
			LOGGER.trace("END");
		}
	}
}
