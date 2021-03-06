package kkr.album.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class UtilsPattern {

	public static final TimeZone GMT = new SimpleTimeZone(0, "GMT");

	public static final String DATE_PATTERN_DATETIME = "yyyyMMdd-HHmmss";
	public static final DateFormat DATE_FORMAT_DATETIME;

	static {
		DATE_FORMAT_DATETIME = new SimpleDateFormat(DATE_PATTERN_DATETIME);
		DATE_FORMAT_DATETIME.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static final String DATE_PATTERN_DATE = "yyyyMMdd";
	public static final DateFormat DATE_FORMAT_DATE;

	static {
		DATE_FORMAT_DATE = new SimpleDateFormat(DATE_PATTERN_DATE);
		DATE_FORMAT_DATE.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static final String DATE_PATTERN_DATETIME0 = "yyyyMMdd-000000";
	public static final DateFormat DATE_FORMAT_DATETIME0;

	static {
		DATE_FORMAT_DATETIME0 = new SimpleDateFormat(DATE_PATTERN_DATETIME0);
		DATE_FORMAT_DATETIME0.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static final String MASK_EXT_PHOTO = "([jJ][pP][eE]?[gG])";
	public static final String MASK_EXT_VIDEO = "([mM][oO][vV]|[aA][vV][iI]|[mM][pP]4|[mM][tT][sS]|[mM][pP][gG])";
	public static final String MASK_EXT_FILE = "(" + MASK_EXT_PHOTO + "|" + MASK_EXT_VIDEO + ")";
	public static final String MASK_TIME = "[0-9]{8}-[0-9]{6}";

	public static final String MASK_GPX = ".*\\.[gG][pP][xX]";
	public static final String MASK_GPX_X = "x.*\\.[gG][pP][xX]";
	public static final String MASK_GPX_STOPWATCH = ".*(Stopwatch|Cron�metro|Chronom�tre).*\\.[gG][pP][xX]";
	public static final String MASK_GPX_WAYPOINT = "Waypoints_.*\\.[gG][pP][xX]";

	public static final Pattern PATTERN_PHOTO = Pattern.compile(".*" + UtilsPattern.MASK_EXT_PHOTO);

	public static final Pattern PATTERN_PHOTO_CRUIDE = Pattern
			.compile("CRUIDE_" + MASK_TIME + "_" + "[A-Z0-9]+\\." + UtilsPattern.MASK_EXT_PHOTO);

	public static final Pattern PATTERN_VIDEO_CRUIDE = Pattern
			.compile("CRUIDE_" + MASK_TIME + "_" + "[A-Z0-9]+\\." + UtilsPattern.MASK_EXT_VIDEO);

	public static final Pattern PATTERN_VIDEO = Pattern.compile(".*" + UtilsPattern.MASK_EXT_VIDEO);

	public static class FileFilterFile implements FileFilter {
		private Pattern[] patterns;

		public FileFilterFile(Pattern... paterns) {
			this.patterns = paterns;
		}

		public boolean accept(File pathname) {
			if (!pathname.isFile()) {
				return false;
			}
			for (Pattern pattern : patterns) {
				if (pattern.matcher(pathname.getName()).matches()) {
					return true;
				}
			}
			return false;
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
