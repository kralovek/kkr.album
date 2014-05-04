package kkr.album.utils;

import java.util.Collections;
import java.util.List;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;

public class UtilsGpx {

	public static Gpx joinGpxs(Gpx gpxMain, List<Gpx> gpxs)
			throws BaseException {
		if (gpxMain.getTraces().size() == 0) {
			throw new FunctionalException("No trace in the GPX");
		}

		Gpx gpx = (Gpx) gpxMain.clone();

		Trace trace = gpx.getTraces().get(0);

		if (gpxs != null) {
			for (Gpx gpxLoc : gpxs) {
				Gpx gpxClone = (Gpx) gpxLoc.clone();
				for (Trace traceL : gpxClone.getTraces()) {
					trace.getPoints().addAll(traceL.getPoints());
				}
			}
		}

		sortTrace(trace);

		return gpx;
	}
	
	public static void sortTrace(Trace trace) {
		Collections.sort(trace.getPoints());
	}

	public static Gpx simlifyGpx(Gpx gpx) {
		Gpx gpxSimplify = (Gpx) gpx.clone();
		for (Trace trace : gpxSimplify.getTraces()) {
			for (Point point : trace.getPoints()) {
				point.setCadence(null);
				point.setElevation(null);
				point.setHeartRate(null);
				point.setTemperature(null);
				point.setTime(null);
			}
		}
 		return gpxSimplify;
	}

}
