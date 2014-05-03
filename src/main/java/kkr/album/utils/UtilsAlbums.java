package kkr.album.utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import kkr.album.exception.BaseException;

public class UtilsAlbums {

	private static final Pattern PATTERN_NAME = Pattern
			.compile("[0-9]{8}.+");


	public static String determineName(File dir) throws BaseException {
		try {
			File dirAdapt = dir.getCanonicalFile();
			String name = dirAdapt.getName();
			if (!PATTERN_NAME.matcher(name).matches()) {
				return null;
			}
			return name;
		} catch (IOException ex) {
			return null;
		}
	}
}
