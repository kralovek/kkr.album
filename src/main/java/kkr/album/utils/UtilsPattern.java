package kkr.album.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class UtilsPattern {

	public static final DateFormat DATE_FORMAT_DATETIME;
	static {
		DATE_FORMAT_DATETIME = new SimpleDateFormat("yyyyMMdd-HHmmss");
		DATE_FORMAT_DATETIME.setTimeZone(TimeZone.getTimeZone("GPS"));
	}

	public static final DateFormat DATE_FORMAT_DATE;
	static {
		DATE_FORMAT_DATE = new SimpleDateFormat("yyyyMMdd");
		DATE_FORMAT_DATE.setTimeZone(TimeZone.getTimeZone("GPS"));
	}

	public static final DateFormat DATE_FORMAT_DATETIME0;
	static {
		DATE_FORMAT_DATETIME0 = new SimpleDateFormat("yyyyMMdd-000000");
		DATE_FORMAT_DATETIME0.setTimeZone(TimeZone.getTimeZone("GPS"));
	}

	public static final String MASK_EXT_PHOTO = "([jJ][pP][eE]?[gG])";
	public static final String MASK_EXT_VIDEO = "([mM][oO][vV]|[aA][vV][iI]|[mM][pP]4|[mM][tT][sS]|[mM][pP][gG])";
	public static final String MASK_EXT_FILE = "(" + MASK_EXT_PHOTO + "|"
			+ MASK_EXT_VIDEO + ")";
	public static final String MASK_TIME = "[0-9]{8}-[0-9]{6}";

	public static final Pattern PATTERN_PHOTO = Pattern.compile(".*"
			+ UtilsPattern.MASK_EXT_PHOTO);

	public static final Pattern PATTERN_VIDEO = Pattern.compile(".*"
			+ UtilsPattern.MASK_EXT_VIDEO);

	public static class FileFilterFile implements FileFilter {
		private Pattern pattern;

		public FileFilterFile(Pattern patern) {
			this.pattern = patern;
		}

		public boolean accept(File pathname) {
			if (!pathname.isFile()) {
				return false;
			}
			return pattern.matcher(pathname.getName()).matches();
		}
	}

	public static class FileFilterDir implements FileFilter {
		private Pattern pattern;

		public FileFilterDir(Pattern patern) {
			this.pattern = patern;
		}

		public boolean accept(File pathname) {
			if (!pathname.isDirectory()) {
				return false;
			}
			return pattern.matcher(pathname.getName()).matches();
		}
	}
}
