package kkr.album.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import kkr.album.exception.BaseException;
import kkr.album.exception.ConfigurationException;

public class UtilsCommandLine {

	public static Map<String, String> commandLineToMap(String[] args) throws BaseException {
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		if (args.length%2 != 0) {
			throw new ConfigurationException("The command-line parameters count must be even");
		}
		for (int i = 0; i < args.length; i += 2) {
			parameters.put(args[i], args[i + 1]);
		}
		return parameters;
	}
	
}
