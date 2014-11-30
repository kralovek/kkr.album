package kkr.album.components.analyzer_gpx.generic;

import org.apache.log4j.Logger;

import kkr.album.components.analyzer_gpx.AnalyzerGpx;
import kkr.album.components.analyzer_gpx.PointStat;
import kkr.album.components.analyzer_gpx.TraceStat;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;

public class AnalyzerGpxGeneric extends AnalyzerGpxGenericFwk implements
		AnalyzerGpx {
	private static transient final Logger LOGGER = Logger
			.getLogger(AnalyzerGpxGeneric.class);

	public TraceStat analyzeTrace(Trace trace) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();

			TraceStat traceStat = new TraceStat();

			Point lastPoint = null;
			Point lastPointTime = null;
			Point lastPointElevation = null;
			Point lastPointHeartRate = null;
			Point lastPointTemperature = null;

			for (Point currentPoint : trace.getPoints()) {
				if (traceStat.getLatitudeMax() == null
						|| currentPoint.getLatitude() > traceStat
								.getLatitudeMax()) {
					traceStat.setLatitudeMax(currentPoint.getLatitude());
				}
				if (traceStat.getLatitudeMin() == null
						|| currentPoint.getLatitude() < traceStat
								.getLatitudeMin()) {
					traceStat.setLatitudeMin(currentPoint.getLatitude());
				}
				if (traceStat.getLongitudeMin() == null
						|| currentPoint.getLongitude() > traceStat
								.getLongitudeMin()) {
					traceStat.setLongitudeMin(currentPoint.getLongitude());
				}
				if (traceStat.getLongitudeMax() == null
						|| currentPoint.getLongitude() > traceStat
								.getLongitudeMax()) {
					traceStat.setLongitudeMax(currentPoint.getLongitude());
				}
				if (currentPoint.getElevation() != null) {
					if (traceStat.getElevationMax() == null
							|| currentPoint.getElevation() > traceStat
									.getElevationMax()) {
						traceStat.setElevationMax(currentPoint.getElevation());
					}
					if (traceStat.getElevationMin() == null
							|| currentPoint.getElevation() < traceStat
									.getElevationMin()) {
						traceStat.setElevationMin(currentPoint.getElevation());
					}
				}
				if (currentPoint.getHeartRate() != null) {
					if (traceStat.getElevationMax() == null
							|| currentPoint.getHeartRate() > traceStat
									.getElevationMax()) {
						traceStat.setHeartRateMax(currentPoint.getHeartRate());
					}
					if (traceStat.getElevationMin() == null
							|| currentPoint.getHeartRate() < traceStat
									.getElevationMin()) {
						traceStat.setHeartRateMin(currentPoint.getHeartRate());
					}
				}
				if (currentPoint.getTemperature() != null) {
					if (traceStat.getTemperatureMax() == null
							|| currentPoint.getTemperature() > traceStat
									.getTemperatureMax()) {
						traceStat
								.setHeartRateMax(currentPoint.getTemperature());
					}
					if (traceStat.getTemperatureMin() == null
							|| currentPoint.getTemperature() < traceStat
									.getTemperatureMin()) {
						traceStat.setTemperatureMin(currentPoint
								.getTemperature());
					}
				}

				if (lastPoint != null) {
					PointStat pointStat = analyzePoint(currentPoint, lastPoint,
							lastPointTime, lastPointElevation,
							lastPointHeartRate, lastPointTemperature);

					if (pointStat.getAscent() != null) {
						if (pointStat.getAscent() > 0.0) {
							traceStat.setTotalAscent((traceStat
									.getTotalAscent() != null ? traceStat
									.getTotalAscent() : 0.0)
									+ pointStat.getAscent());
						}
						if (pointStat.getAscent() < 0.0) {
							traceStat.setTotalDescent((traceStat
									.getTotalDescent() != null ? traceStat
									.getTotalDescent() : 0.0)
									- pointStat.getAscent());
						}
					}

					if (pointStat.getHeartRate() != null
							&& pointStat.getDuration() != null) {
						long dur = pointStat.getDuration().longValue() / 1000L;
						int hr = pointStat.getHeartRate().intValue();
						Long sec = traceStat.getCumulHeartRateSecond().get(hr);
						if (sec != null) {
							traceStat.getCumulHeartRateSecond().put(hr,
									sec + dur);
						} else {
							traceStat.getCumulHeartRateSecond().put(hr, dur);
						}
					}

				} else {
					traceStat.setLatitudeMin(currentPoint.getLatitude());
					traceStat.setLatitudeMax(currentPoint.getLatitude());
					traceStat.setLongitudeMin(currentPoint.getLongitude());
					traceStat.setLongitudeMax(currentPoint.getLongitude());
				}

				lastPoint = currentPoint;
				if (currentPoint.getTime() != null) {
					lastPointTime = currentPoint;
				}
				if (currentPoint.getElevation() != null) {
					lastPointElevation = currentPoint;
				}
				if (currentPoint.getHeartRate() != null) {
					lastPointHeartRate = currentPoint;
				}
				if (currentPoint.getTemperature() != null) {
					lastPointTemperature = currentPoint;
				}
			}

			LOGGER.trace("OK");
			return traceStat;
		} finally {
			LOGGER.trace("END");
		}
	}

	private PointStat analyzePoint(Point currentPoint, Point lastPoint,
			Point lastPointTime, Point lastPointElevation,
			Point lastPointHeartRate, Point lastPointTemperature) {
		PointStat pointStat = new PointStat();
		if (lastPointTime != null && currentPoint.getTime() != null) {
			long ms = currentPoint.getTime().getTime()
					- lastPointTime.getTime().getTime();
			double duration = ((double) ms) / 1000.;
			pointStat.setDuration(duration);
		}

		if (lastPoint.getLatitude() != null && lastPoint.getLongitude() != null
				&& lastPoint.getLatitude() != null
				&& lastPoint.getLongitude() != null) {
			double distance = distance(lastPoint.getLatitude(),
					lastPoint.getLongitude(), currentPoint.getLatitude(),
					currentPoint.getLongitude());
			pointStat.setDistance(distance);
		}

		pointStat.setHeartRate(difference(lastPointHeartRate.getHeartRate(),
				currentPoint.getHeartRate()));

		pointStat.setTemperature(difference(
				lastPointHeartRate.getTemperature(),
				currentPoint.getTemperature()));

		pointStat.setElevation(difference(lastPointElevation.getElevation(),
				currentPoint.getElevation()));
		if (lastPointElevation != null && currentPoint.getElevation() != null) {
			pointStat.setAscent(currentPoint.getElevation()
					- lastPointElevation.getElevation());
		}

		if (pointStat.getDuration() != null && pointStat.getDistance() != null) {
			pointStat.setSpeed(pointStat.getDistance() * 3.6
					/ pointStat.getDuration());
		}

		return pointStat;
	}

	private Double difference(Double d1, Double d2) {
		if (d1 != null && d2 != null) {
			return (d2 + d1) / 2.;
		} else if (d1 != null) {
			return d1;
		} else if (d2 != null) {
			return d2;
		} else {
			return null;
		}
	}

	private double distance(double lat_a, double lng_a, double lat_b,
			double lng_b) {
		double a1 = lat_a * Math.PI / 180.;
		double a2 = lng_a * Math.PI / 180.;
		double b1 = lat_b * Math.PI / 180.;
		double b2 = lng_b * Math.PI / 180.;

		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000 * tt;
	}
}
