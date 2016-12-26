package kkr.album.main;

import java.io.File;

import kkr.album.exception.ConfigurationException;

public class ConfigModifyGpx {

	private String name;
	private File file;

	public ConfigModifyGpx(final String[] pArgs) throws ConfigurationException {
		init(pArgs);
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	protected void init(final String[] pArgs) throws ConfigurationException {
		if (pArgs.length % 2 != 0) {
			throw new ConfigurationException("A command-line parameter is missing a value");
		}
		for (int i = 0; i < pArgs.length; i += 2) {
			if (pArgs[i + 1].isEmpty()) {
				throw new ConfigurationException("Value of the parameter '" + pArgs[i] + "' is empty");
			}
			if ("-name".equals(pArgs[i])) {
				name = pArgs[i + 1];
				continue;
			} else if ("-file".equals(pArgs[i])) {
				file = new File(pArgs[i + 1]);
				continue;
			}
			throw new ConfigurationException("Unknown parameter '" + pArgs[i + 1] + "'.");
		}

		if (file != null) {
			try {
				file = file.getCanonicalFile();
				if (!file.getName().toLowerCase().endsWith(".gpx")) {
					throw new ConfigurationException(
							"Parameter '-file' does not contain a GPX file: " + file.getAbsolutePath());
				}
			} catch (Exception ex) {
				throw new ConfigurationException("Bad format of the parameter '-file': " + file.getAbsolutePath());
			}
		}
	}
}
