package kkr.album.components.timeevaluator;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsPattern;

public class TimeEvaluatorGeneric extends TimeEvaluatorGenericFwk implements
		TimeEvaluator {
	private static final Logger LOGGER = Logger
			.getLogger(TimeEvaluatorGeneric.class);

	private static final Pattern PATTERN_FILE_TIME_TO = Pattern
			.compile("time.*\\.[jJ][pP][eE]?[gG]");
	private static final Pattern PATTERN_FILE_TIME_TO_FROM = Pattern
			.compile("time[A-Z0-9]+_[0-9]{8}-[0-9]{6}(_[0-9]{8}-[0-9]{6})?\\.[jJ][pP][eE]?[gG]");

	private static class FileTimeImpl implements FileTime {
		private Long move;
		private File file;

		public Long getMove() {
			return move;
		}
		public void setMove(Long move) {
			this.move = move;
		}
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
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
					+ (dateMove != null ? UtilsPattern.DATE_FORMAT_DATETIME.format(dateMove) : "")
					+ (dateOrigin != null ? " <- "
							+ UtilsPattern.DATE_FORMAT_DATETIME.format(dateOrigin) : "");
		}
	}

	private static final FileFilter fileFilterTime = new UtilsPattern.FileFilterFile(
			PATTERN_FILE_TIME_TO);

	public Map<String, FileTime> loadTimes(File dir) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			Map<String, FileTime> retval = new HashMap<String, FileTime>();

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
					FileTimeImpl fileTimeImpl = new FileTimeImpl();
					fileTimeImpl.setFile(fileInfo.getFile());
					fileTimeImpl.setMove(seconds);
					
					if (retval.put(fileInfo.getSymbol(), fileTimeImpl) != null) {
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

		String dateString = UtilsPattern.DATE_FORMAT_DATETIME.format(date);
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

		managerExif.modifyFile(newFile, date);
	}

	private FileInfo evaluateFileTime(File file) throws BaseException {
		FileInfo retval = new FileInfo();
		retval.setFile(file);

		String name = file.getName();
		if (!PATTERN_FILE_TIME_TO_FROM.matcher(file.getName()).matches()) {
			throw new FunctionalException("Time filename has bad format: "
					+ file.getAbsolutePath());
		}

		String[] parts = name.split("[_.]");
		retval.setSymbol(parts[0].substring(4));

		Date date;
		String format;
		try {
			String stringDateMove = parts[1];
			date = UtilsPattern.DATE_FORMAT_DATETIME.parse(stringDateMove);
			format = UtilsPattern.DATE_FORMAT_DATETIME.format(date);
			if (!format.equals(stringDateMove)) {
				throw new FunctionalException(
						"The time file has bad (time to) format: "
								+ file.getAbsolutePath());
			}
			retval.setDateMove(date);
			if (parts.length == 4) {
				String stringDateOrigin = parts[2];
				date = UtilsPattern.DATE_FORMAT_DATETIME.parse(stringDateOrigin);
				format = UtilsPattern.DATE_FORMAT_DATETIME.format(date);
				if (!format.equals(stringDateOrigin)) {
					throw new FunctionalException(
							"The time file has bad (time from) format: "
									+ file.getAbsolutePath());
				}
				retval.setDateOrigin(date);
			}
		} catch (ParseException ex) {
			throw new FunctionalException("Bad date in time file: "
					+ file.getAbsolutePath());
		}

		return retval;
	}
}
