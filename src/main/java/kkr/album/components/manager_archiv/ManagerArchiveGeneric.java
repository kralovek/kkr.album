package kkr.album.components.manager_archiv;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsFile;
import kkr.album.utils.UtilsPattern;

public class ManagerArchiveGeneric extends ManagerArchiveGenericFwk implements
		ManagerArchive {
	private static final Logger LOGGER = Logger
			.getLogger(ManagerArchiveGeneric.class);

	private static final Pattern PATTERN_FILE = Pattern
			.compile("[0-9]{8}([oOnN].*" + UtilsPattern.MASK_EXT_PHOTO + "|v.*" + UtilsPattern.MASK_EXT_VIDEO + ")");

	private static final Pattern PATTERN_DIR = Pattern
			.compile("[oOnNvV][0-9]{8}");

	private static final char SYMBOL_O = 'o';
	private static final char SYMBOL_N = 'n';
	private static final char SYMBOL_V = 'v';

	private static final FileFilter FILE_FILTER_FILE = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_FILE.matcher(file.getName()).matches();
		}
	};

	private static final FileFilter FILE_FILTER_DIR = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isDirectory()) {
				return false;
			}
			return PATTERN_DIR.matcher(file.getName()).matches();
		}
	};

	public int nextIndex() throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			checkDirectories();
			int maxO = lastNumber(dirOriginal, SYMBOL_O);
			int maxN = lastNumber(dirNormal, SYMBOL_N);
			int maxV = lastNumber(dirVideo, SYMBOL_V);

			if (maxO != maxN) {
				throw new FunctionalException(
						"Direcotries O and N are not synchronized: " + maxO
								+ "/" + maxN);
			}

			int retval = (maxO > maxV ? maxO : maxV) + 1;
			LOGGER.trace("OK");
			return retval;
		} finally {
			LOGGER.trace("END");
		}
	}

	public void copyToArchiv(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File fileTarget = fileTarget(file);
			if (fileTarget.isFile()) {
				throw new FunctionalException("File is already archived: "
						+ file.getAbsolutePath());
			}
			UtilsFile.copyFile(file, fileTarget);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void moveToArchiv(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			File fileTarget = fileTarget(file);
			if (fileTarget.isFile()) {
				throw new FunctionalException("File is already archived: "
						+ file.getAbsolutePath());
			}
			UtilsFile.moveFile(file, fileTarget);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private File fileTarget(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			checkDirectories();
			if (!PATTERN_FILE.matcher(file.getName()).matches()) {
				throw new FunctionalException(
						"The file is not in allowed format: "
								+ file.getAbsolutePath());
			}
			int indexFile = indexFromFile(file);
			char symbolFile = symbolFromFile(file);
			int indexDir = indexSubdir(indexFile);
			String ext = UtilsFile.extension(file);

			String dirnameTarget = symbolFile + String.format("%08d", indexDir);
			String filenameTarget = String.format("%08d", indexFile)
					+ symbolFile + "." + ext;

			File dirTarget = null;
			switch (symbolFile) {
			case SYMBOL_O:
				dirTarget = new File(dirOriginal, dirnameTarget);
				break;
			case SYMBOL_N:
				dirTarget = new File(dirNormal, dirnameTarget);
				break;
			case SYMBOL_V:
				dirTarget = new File(dirVideo, dirnameTarget);
				break;
			default:
				// never hapens
			}
			File fileTarget = new File(dirTarget, filenameTarget);

			LOGGER.trace("OK");
			return fileTarget;
		} finally {
			LOGGER.trace("END");
		}
	}

	private int indexSubdir(int index) {
		int mod = index % 1000;
		int subindex = (index - mod) / 1000;
		if (mod != 0) {
			return subindex;
		} else if (subindex != 0) {
			return subindex - 1;
		} else {
			return 1;
		}
	}

	private int lastNumber(File dir, char symbol) throws BaseException {
		File[] dirs = dir.listFiles(FILE_FILTER_DIR);
		if (dirs.length == 0) {
			return 0;
		}

		int max = 0;
		File dirMax = null;
		for (File dirX : dirs) {
			char symbolDir = symbolFromDirectory(dirX);
			if (symbolDir != symbol) {
				throw new FunctionalException("Unexpected directory. Only ("
						+ symbol + ") are allowed in the directory: "
						+ dirX.getAbsolutePath());
			}
			int number = indexFromDirectory(dirX);
			if (dirMax == null) {
				dirMax = dirX;
			}
			if (number > max) {
				dirMax = dirX;
				max = number;
			}
		}

		max = 0;
		File[] files = dirMax.listFiles(FILE_FILTER_FILE);
		if (files.length == 0) {
			return 0;
		}
		for (File file : files) {
			char symbolFile = symbolFromFile(file);
			if (symbolFile != symbol) {
				throw new FunctionalException("Unexpected file. Only ("
						+ symbol + ") are allowed in the directory: "
						+ file.getAbsolutePath());
			}
			int number = indexFromFile(file);
			if (max < number) {
				max = number;
			}
		}
		return max;
	}

	private char symbolFromFile(File file) {
		return file.getName().charAt(8);
	}

	private char symbolFromDirectory(File dir) {
		return dir.getName().charAt(0);
	}

	private int indexFromFile(File file) {
		try {
			int number = Integer.parseInt(file.getName().substring(0, 8));
			return number;
		} finally {
			// never happens
		}
	}

	private int indexFromDirectory(File dir) {
		try {
			int number = Integer.parseInt(dir.getName().substring(1));
			return number;
		} finally {
			// never happens
		}
	}

	private void checkDirectories() throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			if (!dirNormal.isDirectory() && !dirNormal.mkdirs()) {
				throw new TechnicalException("Cannot create the directory: "
						+ dirNormal.getAbsolutePath());
			}
			if (!dirOriginal.isDirectory() && !dirOriginal.mkdirs()) {
				throw new TechnicalException("Cannot create the directory: "
						+ dirOriginal.getAbsolutePath());
			}
			if (!dirVideo.isDirectory() && !dirVideo.mkdirs()) {
				throw new TechnicalException("Cannot create the directory: "
						+ dirVideo.getAbsolutePath());
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
