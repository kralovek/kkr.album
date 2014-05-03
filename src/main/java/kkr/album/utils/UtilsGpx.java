package kkr.album.utils;

import java.util.List;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;

public class UtilsGpx {

	public static Gpx joinGpxs(Gpx gpxMain, List<Gpx> gpxs) throws BaseException {
		Gpx gpx = (Gpx) gpxMain.clone();
		
		Trace trace = null;
		
		for (Trace traceLoc : gpxMain.getTraces()) {
			if (trace == null) {
				trace = traceLoc;
			} else {
				trace.getPoints().addAll(traceLoc.getPoints());
			}
		}

		if (gpxs != null) {
			for (Gpx gpxLoc : gpxs) {
				Gpx gpxClone = (Gpx) gpxLoc.clone();
				for (Trace traceL : gpxClone.getTraces()) {
					if (trace == null) {
						trace = traceL;
					} else {
						trace.getPoints().addAll(traceL.getPoints());
					}
				}
			}
		}
		
		gpx.getTraces().clear();
		gpx.getTraces().add(trace);
		
		return gpx;
	}
}
