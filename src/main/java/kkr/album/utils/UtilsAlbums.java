package kkr.album.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.model.DateNZ;

public class UtilsAlbums {

	private static final Pattern PATTERN_NAME = Pattern.compile("[0-9]{8}.+");

	private static final String DATE_PATTERN = "yyyyMMdd";
	private static final DateFormat DATE_FORMAT;
	static {
		DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static String determineName(File dirBase) throws BaseException {
		try {
			File dirAdapt = dirBase.getCanonicalFile();
			String name = dirAdapt.getName();
			if (!PATTERN_NAME.matcher(name).matches()) {
				return null;
			}
			return name;
		} catch (IOException ex) {
			return null;
		}
	}

	public static DateNZ determineDate(File dirBase) throws BaseException {
		try {
			String dateString = dirBase.getName().substring(0, 8);
			DateNZ date = new DateNZ(dateString, DATE_PATTERN);
			return date;
		} catch (Exception ex) {
			throw new FunctionalException("Album's name has bad format. Does not contain a date: " + dirBase.getName());
		}
	}
}
