package kkr.album.batch.movegpx;

import java.io.File;

import org.apache.log4j.Logger;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;
import kkr.album.utils.UtilsFile;

public class BatchMoveGpx extends BatchMoveGpxFwk {
	private static final Logger LOG = Logger.getLogger(BatchMoveGpx.class);

	public void run(File dirCurrent, File fileIn, File fileOut, double lat, double lon) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Gpx gpx = managerGpx.loadGpx(fileIn);

			for (Trace trace : gpx.getTraces()) {
				for (Point point : trace.getPoints()) {
					point.setLongitude(point.getLongitude() + lon);
					point.setLatitude(point.getLatitude() + lat);
				}
			}

			UtilsFile.createFileDir(fileOut);

			managerGpx.saveGpx(gpx, fileOut);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
