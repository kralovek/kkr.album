package kkr.album.components.batch_modifygpxs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsAlbums;
import kkr.album.utils.UtilsGpx;

public class BatchModifyGpxs extends BatchModifyGpxsFwk {
	private static final Logger LOGGER = Logger
			.getLogger(BatchModifyGpxs.class);

//	private static final long MB = 1048576L;
	private static final long MB = 100576L;
	
	private static final Pattern PATTERN_GPX_AUTO = Pattern
			.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}\\.[0-9]{2}\\.[0-9]{2} Auto\\.[gG][pP][xX]");

	private static final FileFilter FILE_FILTER_GPX_AUTO = new FileFilter() {
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return PATTERN_GPX_AUTO.matcher(file.getName()).matches();
		}
	};

	public void run(File dirBase) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			if (!dirBase.isDirectory()) {
				throw new FunctionalException("The directory does not exist: "
						+ dirBase.getAbsolutePath());
			}

			String name = UtilsAlbums.determineName(dirBase);

			File dirGps = new File(dirBase, "gps");

			File fileGpxCurrent = new File(dirGps, "Current.gpx");

			if (!fileGpxCurrent.isFile()) {
				throw new FunctionalException("Missing current GPX file: "
						+ fileGpxCurrent.getAbsolutePath());
			}

			Gpx gpxCurrent = managerGpx.loadGpx(fileGpxCurrent);

			if (gpxCurrent.getTraces().size() == 0) {
				throw new FunctionalException(
						"The GPX file does not contain any trace: "
								+ fileGpxCurrent.getAbsolutePath());
			}

			File[] fileGpxAutos = dirGps.listFiles(FILE_FILTER_GPX_AUTO);

			List<Gpx> gpxAutos = new ArrayList<Gpx>();

			if (fileGpxAutos != null) {
				for (File fileGpxAuto : fileGpxAutos) {
					Gpx gpxAuto = managerGpx.loadGpx(fileGpxAuto);
					gpxAutos.add(gpxAuto);
				}
			}

			Gpx gpx = UtilsGpx.joinGpxs(gpxCurrent, gpxAutos);

			gpx.getTraces().get(0).setName(name);
			
			File fileGpxOutput = new File(dirGps, name + ".gpx");
			managerGpx.saveGpx(gpx, fileGpxOutput);

			if (!fileGpxCurrent.delete()) {
				throw new TechnicalException("Cannot remove the CURRENT GPX file: " + fileGpxCurrent.getAbsolutePath());
			}
			if (fileGpxAutos != null) {
				for (File fileGpxAuto : fileGpxAutos) {
					if (!fileGpxAuto.delete()) {
						throw new TechnicalException("Cannot remove the AUTO GPX file: " + fileGpxAuto.getAbsolutePath());
					}
				}
			}
			

			long length = fileGpxOutput.length();
			
			if (length > MB) {
				LOGGER.warn("The size of created GPX file has more than 1MB");
				File fileGpxSimplyOutput = new File(dirGps, "x" + name + ".gpx");
				Gpx gpxSimply = UtilsGpx.simlifyGpx(gpx);
				managerGpx.saveGpx(gpxSimply, fileGpxSimplyOutput);
				length = fileGpxSimplyOutput.length();
				
				if (length > MB) {
					LOGGER.warn("The size of created GPX file HAS STILL more than 1MB !!!");
				}
			}
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

}
