package kkr.album.utils;

import javax.xml.stream.XMLStreamReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;

public class UtilsFile {
	public static final void closeRessource(XMLStreamReader ressource) {
		if (ressource != null) {
			try {
				ressource.close();
			} catch (Exception ex) {
			}
		}
	}

	public static final void closeRessource(Closeable ressource) {
		if (ressource != null) {
			try {
				ressource.close();
			} catch (Exception ex) {
			}
		}
	}

	public static String extension(File file) {
		int pos = file.getName().lastIndexOf('.');
		if (pos == -1) {
			return null;
		}
		if (pos == file.getName().length()) {
			return "";
		}
		return file.getName().substring(pos + 1).toLowerCase();
	}

	public static void moveFile(File fromFile, File toFile)
			throws BaseException {
		if (toFile.isFile()) {
			throw new TechnicalException(
					"The destination file exists already: "
							+ toFile.getAbsolutePath());
		}

		if (!toFile.getParentFile().isDirectory()
				&& !toFile.getParentFile().mkdirs()) {
			throw new TechnicalException("Cannot create the directory: "
					+ toFile.getParentFile().getAbsolutePath());
		}

		if (!fromFile.renameTo(toFile)) {
			throw new TechnicalException("Cannot rename the file: "
					+ fromFile.getAbsolutePath() + " to: "
					+ toFile.getAbsolutePath());
		}
	}

	public static void copyFile(File fromFile, File toFile)
			throws BaseException {
		if (toFile.isFile()) {
			throw new TechnicalException(
					"The destination file exists already: "
							+ toFile.getAbsolutePath());
		}

		if (!toFile.getParentFile().isDirectory()
				&& !toFile.getParentFile().mkdirs()) {
			throw new TechnicalException("Cannot create the directory: "
					+ toFile.getParentFile().getAbsolutePath());
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {

			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytesRead);
			}
			to.close();
			to = null;
			from.close();
			from = null;

			toFile.setLastModified(fromFile.lastModified());

		} catch (FileNotFoundException ex) {
			throw new TechnicalException("The file does not exist: "
					+ fromFile.getAbsolutePath(), ex);
		} catch (IOException ex) {
			throw new TechnicalException(
					"The destination file exists already: "
							+ toFile.getAbsolutePath());
		} finally {
			closeRessource(from);
			closeRessource(to);
		}
	}
}
