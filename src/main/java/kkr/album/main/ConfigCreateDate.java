package kkr.album.main;

import java.io.File;

import kkr.album.exception.ConfigurationException;

public class ConfigCreateDate {
	private File file;

	public ConfigCreateDate(final String[] pArgs) throws ConfigurationException {
		init(pArgs);
	}

	protected void init(final String[] pArgs) throws ConfigurationException {
		if (pArgs.length % 2 != 0) {
			throw new ConfigurationException("A command-line parameter is missing a value");
		}
		for (int i = 0; i < pArgs.length; i += 2) {
			if (pArgs[i + 1].isEmpty()) {
				throw new ConfigurationException("Value of the parameter '" + pArgs[i] + "' is empty");
			}
			if ("-file".equals(pArgs[i])) {
				file = new File(pArgs[i + 1]);
				continue;
			}
			throw new ConfigurationException("Unknown parameter '" + pArgs[i + 1] + "'.");
		}

		if (file == null) {
			throw new ConfigurationException("Parameter -file is mandatory");
		}
	}

	public File getFile() {
		return file;
	}
}
