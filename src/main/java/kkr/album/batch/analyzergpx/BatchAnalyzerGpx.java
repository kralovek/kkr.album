package kkr.album.batch.analyzergpx;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.analyzer_gpx.TraceStat;
import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;
import kkr.album.utils.UtilsFile;

public class BatchAnalyzerGpx extends BatchAnalyzerGpxFwk {
	private static transient final Logger LOGGER = Logger
			.getLogger(BatchAnalyzerGpx.class);

	private static class FileFilterGpx implements FileFilter {
		private Pattern pattern;

		public FileFilterGpx(Pattern pattern) {
			this.pattern = pattern;
		}

		public boolean accept(File file) {
			if (!file.isFile()
					|| !file.getName().toLowerCase().endsWith(".gpx")) {
				return false;
			}
			if (pattern != null) {
				return pattern.matcher(file.getName()).matches();
			}
			return true;
		}
	}

	public TraceStat runFile(File fileGpx, File dirResult) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			LOGGER.info("Working file: " + fileGpx.getName());
			if (!fileGpx.getName().toLowerCase().endsWith(".gpx")) {
				throw new IllegalArgumentException("Not a GPX file: "
						+ fileGpx.getAbsolutePath());
			}

			Gpx gpx = managerGpx.loadGpx(fileGpx);

			TraceStat traceStat = null;

			if (gpx.getTraces().size() != 0) {
				Trace trace = gpx.getTraces().get(0);
				traceStat = analyzerGpx.analyzeTrace(trace);
				traceStat.setSource(fileGpx.getName());
			}

			statGpxReporter.report(traceStat, fileGpx.getAbsolutePath());

			LOGGER.trace("OK");
			return traceStat;
		} finally {
			LOGGER.trace("END");
		}
	}

	private void listFiles(File dir, List<File> files, FileFilter fileFilterGpx)
			throws BaseException {
		File[] filesGpx = dir.listFiles(fileFilterGpx);

		for (File fileGpx : filesGpx) {
			files.add(fileGpx);
		}

		File[] dirs = dir.listFiles(UtilsFile.fileFilterDir);

		for (File dirLoc : dirs) {
			listFiles(dirLoc, files, fileFilterGpx);
		}
	}

	public void runDir(File dirGpx, File dirResult, Pattern pattern)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			FileFilter fileFilterGpx = new FileFilterGpx(pattern);

			List<File> files = new ArrayList<File>();

			listFiles(dirGpx, files, fileFilterGpx);

			GlobalTraceStat globalTraceStat = new GlobalTraceStat();
			globalTraceStat.setSource(dirGpx.getName());

			for (File fileGpx : files) {
				TraceStat traceStat = runFile(fileGpx, dirResult);

				if (traceStat.getDistance() != null) {
					globalTraceStat.setDistance(globalTraceStat.getDistance()
							+ traceStat.getDistance());
				}

				if (globalTraceStat.getTotalAscent() != null) {
					globalTraceStat.setTotalAscent(globalTraceStat
							.getTotalAscent() + traceStat.getTotalAscent());
				}

				if (globalTraceStat.getTotalDescent() != null) {
					globalTraceStat.setTotalDescent(globalTraceStat
							.getTotalDescent() + traceStat.getTotalDescent());
				}
			}

			statGpxReporter.report(globalTraceStat, dirGpx.getAbsolutePath());

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
