package kkr.album.components.timeevaluator;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;

public class TimeEvaluatorGeneric extends TimeEvaluatorGenericFwk implements
		TimeEvaluator {
	private static final Logger LOGGER = Logger
			.getLogger(TimeEvaluatorGeneric.class);

	private static final Pattern PATTERN_TIME = Pattern
			.compile("time.*\\.[jJ][pP][eE]?[gG]");
	private static final Pattern PATTERN_TIME_DATES = Pattern
			.compile("time[A-Z]*_[0-9]{8}-[0-9]{6}_[0-9]{8}-[0-9]{6}(_[0-9]{8}-[0-9]{6})?\\.[jJ][pP][eE]?[gG]");

	private static final DateFormat DATE_FORMAT;

	static {
		DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private static class FileInfo {
		private String symbol;
		private Date dateMove;
		private Date dateOrigin;

		private File file;

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public Date getDateOrigin() {
			return dateOrigin;
		}

		public void setDateOrigin(Date dateOrigin) {
			this.dateOrigin = dateOrigin;
		}

		public Date getDateMove() {
			return dateMove;
		}

		public void setDateMove(Date dateMove) {
			this.dateMove = dateMove;
		}

		public String toString() {
			return "["
					+ symbol
					+ "] "
					+ (dateMove != null ? DATE_FORMAT.format(dateMove) : "")
					+ (dateOrigin != null ? " <- "
							+ DATE_FORMAT.format(dateOrigin) : "");
		}
	}

	private static FileFilter fileFilterTime = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			if (PATTERN_TIME.matcher(file.getName()).matches()) {
				return true;
			}
			return false;
		}
	};

	public Map<String, Long> loadTimes(File dir) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			Map<String, Long> retval = new HashMap<String, Long>();

			File[] filesAny = dir.listFiles(fileFilterTime);

			if (filesAny != null) {
				for (File file : filesAny) {
					FileInfo fileInfo = evaluateFileTime(file);
					if (fileInfo.getDateOrigin() == null) {
						modifyFile(fileInfo);
					}
					long seconds = (fileInfo.getDateMove().getTime() - fileInfo
							.getDateOrigin().getTime()) / 1000L;
					;
					if (retval.put(fileInfo.getSymbol(), seconds) != null) {
						throw new FunctionalException(
								"More than on time file for the symbol: "
										+ fileInfo.getSymbol());
					}
				}
			}

			LOGGER.trace("OK");
			return retval;
		} finally {
			LOGGER.trace("END");
		}
	}

	private void modifyFile(FileInfo fileInfo) throws BaseException {
		Date date = managerExif.determineDate(fileInfo.getFile());
		if (date == null) {
			throw new FunctionalException(
					"The time file does not contain the CreationDate: "
							+ fileInfo.getFile().getAbsolutePath());
		}

		String dateString = DATE_FORMAT.format(date);
		int pos = fileInfo.getFile().getName().indexOf(".");
		String newFilename = fileInfo.getFile().getName().substring(0, pos)
				+ "_" + dateString
				+ fileInfo.getFile().getName().substring(pos);

		File newFile = new File(fileInfo.getFile().getParentFile(), newFilename);

		if (!fileInfo.getFile().renameTo(newFile)) {
			throw new TechnicalException("Cannot rename the file: "
					+ fileInfo.getFile().getAbsolutePath() + " to: "
					+ newFile.getAbsolutePath());
		}

		fileInfo.setFile(newFile);
		fileInfo.setDateOrigin(date);
	}

	private FileInfo evaluateFileTime(File file) throws BaseException {
		FileInfo retval = new FileInfo();
		retval.setFile(file);

		String name = file.getName();
		if (!PATTERN_TIME_DATES.matcher(file.getName()).matches()) {
			return null;
		}

		String[] parts = name.split("_");
		retval.setSymbol(parts[0].substring(4));

		Date date = null;
		try {
			date = DATE_FORMAT.parse(parts[1]);
			retval.setDateMove(date);
			if (parts.length == 3) {
				date = DATE_FORMAT.parse(parts[2]);
				retval.setDateOrigin(date);
			}
		} catch (ParseException ex) {
			throw new FunctionalException("Bad date in time file: "
					+ file.getAbsolutePath());
		}

		return retval;
	}
}