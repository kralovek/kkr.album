package kkr.album.main;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import kkr.album.exception.ConfigurationException;

public class ConfigAnalyzerGpx {

	private File dirResult;
	private File dirGpx;
	private File fileGpx;
	private Pattern pattern;

	public ConfigAnalyzerGpx(final String[] pArgs)
			throws ConfigurationException {
		init(pArgs);
	}

	protected void init(final String[] pArgs) throws ConfigurationException {
		if (pArgs.length % 2 != 0) {
			throw new ConfigurationException(
					"A command-line parameter is missing a value");
		}
		for (int i = 0; i < pArgs.length; i += 2) {
			if (pArgs[i + 1].isEmpty()) {
				throw new ConfigurationException("Value of the parameter '"
						+ pArgs[i] + "' is empty");
			}
			if ("-fileGpx".equals(pArgs[i])) {
				fileGpx = toFile(pArgs[i + 1], "-fileGpx");
			} else if ("-dirGpx".equals(pArgs[i])) {
				dirGpx = toFile(pArgs[i + 1], "-dirGpx");
			} else if ("-dirResult".equals(pArgs[i])) {
				dirResult = toFile(pArgs[i + 1], "-dirResult");
			} else if ("-pattern".equals(pArgs[i])) {
				pattern = toPattern(pArgs[i + 1], "-pattern");
			} else {
				throw new ConfigurationException("Unknown parameter '"
						+ pArgs[i + 1] + "'.");
			}
		}

		if (dirResult == null) {
			// OK
		}

		if (fileGpx == null && dirGpx == null) {
			throw new ConfigurationException(
					"No of Command-line parameters '-fileGpx' and '-dirGpx' is configured");
		}
	}

	private File toFile(String filename, String parameter)
			throws ConfigurationException {
		try {
			File file = new File(filename).getCanonicalFile();
			return file;
		} catch (IOException ex) {
			throw new ConfigurationException("Command-line parameter '"
					+ parameter + "' has bad format: " + filename);
		}
	}

	private Pattern toPattern(String value, String parameter)
			throws ConfigurationException {
		try {
			Pattern pattern = Pattern.compile(value);
			return pattern;
		} catch (Exception ex) {
			throw new ConfigurationException("Command-line parameter '"
					+ parameter + "' has bad format: " + value);
		}
	}

	public File getDirResult() {
		return dirResult;
	}

	public File getDirGpx() {
		return dirGpx;
	}

	public File getFileGpx() {
		return fileGpx;
	}

	public Pattern getPattern() {
		return pattern;
	}
}
