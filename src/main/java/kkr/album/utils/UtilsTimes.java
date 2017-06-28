package kkr.album.utils;

import java.io.File;

import kkr.album.components.timeevaluator.TimeType;

public class UtilsTimes {

	public static TimeType timeTypeFromFile(File file) {
		String ext = "";
		int pos = file.getName().lastIndexOf('.');
		if (pos != -1 && pos + 1 < file.getName().length()) {
			ext = file.getName().substring(pos + 1);
		}
		ext = ext.toUpperCase();
		try {
			ext = ext.replaceAll("JPEG", "JPG");
			return TimeType.valueOf(ext);
		} catch (Exception ex) {
			return null;
		}
	}
}
