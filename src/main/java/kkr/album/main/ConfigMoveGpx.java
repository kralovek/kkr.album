package kkr.album.main;

import java.io.File;

import kkr.album.exception.ConfigurationException;

public class ConfigMoveGpx {

	private File fileIn;
	private File fileOut;
	private Double lat;
	private Double lon;

	public ConfigMoveGpx(final String[] pArgs) throws ConfigurationException {
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
			if ("-fileIn".equals(pArgs[i])) {
				fileIn = new File(pArgs[i + 1]);
				continue;
			} else if ("-fileOut".equals(pArgs[i])) {
				fileOut = new File(pArgs[i + 1]);
				continue;
			} else if ("-lat".equals(pArgs[i])) {
				try {
					lat = Double.parseDouble(pArgs[i + 1]);
				} catch (Exception ex) {
					throw new ConfigurationException("Parameter 'lat' has bad value: " + pArgs[i + 1]);
				}
				continue;
			} else if ("-lon".equals(pArgs[i])) {
				try {
					lon = Double.parseDouble(pArgs[i + 1]);
				} catch (Exception ex) {
					throw new ConfigurationException("Parameter 'lon' has bad value: " + pArgs[i + 1]);
				}
				continue;
			}
			throw new ConfigurationException("Unknown parameter '" + pArgs[i] + "'.");
		}

		fileIn = adaptFile(fileIn);
		fileOut = adaptFile(fileOut);

		if (fileIn == null) {
			throw new ConfigurationException("Parameter 'fileIn' is not configured");
		}
		if (fileOut == null) {
			throw new ConfigurationException("Parameter 'fileOut' is not configured");
		}
		if (lat == null && lon == null) {
			throw new ConfigurationException("Parameter 'lat' and 'lon' are not configured");
		}

		if (lat == null) {
			lat = 0.;
		}
		if (lon == null) {
			lon = 0.;
		}
	}

	private File adaptFile(File file) throws ConfigurationException {
		if (file != null) {
			try {
				file = file.getCanonicalFile();
				if (!file.getName().toLowerCase().endsWith(".gpx")) {
					throw new ConfigurationException(
							"Parameter '-file' does not contain a GPX file: " + file.getAbsolutePath());
				}
				return file;
			} catch (Exception ex) {
				throw new ConfigurationException("Bad format of the parameter '-file': " + file.getAbsolutePath());
			}
		} else {
			return file;
		}
	}

	public File getFileIn() {
		return fileIn;
	}

	public File getFileOut() {
		return fileOut;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLon() {
		return lon;
	}

}
