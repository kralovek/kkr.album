package kkr.album.main;

import kkr.album.exception.ConfigurationException;

public class Config_MainArchiveFiles {
	
	private static final String MODE_OV = "ov";
	private static final String MODE_N = "n";
	
	public static enum Mode {
		OV, N
	}
	
	private String valueMode; 
	private Mode mode; 
	
	public Config_MainArchiveFiles(final String[] pArgs)
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
			if ("-mode".equals(pArgs[i])) {
				valueMode = pArgs[i + 1];
			}
			throw new ConfigurationException("Unknown parameter '"
					+ pArgs[i + 1] + "'.");
		}
		
		if (mode == null) {
			throw new ConfigurationException("Command-line parameter '-mode' is not configured");
		} else {
			if (MODE_OV.equals(valueMode)) {
				mode = Mode.OV;
			} else if (MODE_N.equals(valueMode)) {
				mode = Mode.N;
			} else {
				throw new ConfigurationException("Command-line parameter '-mode' has bad value: " + valueMode);
			}
		}
	}

	public Mode getMode() {
		return mode;
	}
}
