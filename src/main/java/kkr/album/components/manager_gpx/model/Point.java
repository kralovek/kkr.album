package kkr.album.components.manager_gpx.model;

import kkr.album.model.DateNZ;

public class Point implements Cloneable, Comparable<Point> {
	private Double latitude;
	private Double longitude;
	private Double elevation;
	private Double temperature;
	private Double heartRate;
	private Double cadence;
	private DateNZ time;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public Double getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Double heartRate) {
		this.heartRate = heartRate;
	}

	public Double getCadence() {
		return cadence;
	}

	public void setCadence(Double cadence) {
		this.cadence = cadence;
	}

	public DateNZ getTime() {
		return time;
	}

	public void setTime(DateNZ time) {
		this.time = time;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Object clone() {
		Point point = new Point();
		point.cadence = cadence;
		point.elevation = elevation;
		point.heartRate = heartRate;
		point.latitude = latitude;
		point.longitude = longitude;
		point.temperature = temperature;
		point.time = time;
		return point;
	}

	public int compareTo(Point point) {
		if (time != null && point.time != null) {
			return time.compareTo(point.time);
		}
		return 0;
	}

	public int hashCode() {
		return time.hashCode();
	}

	public String toString() {
		return "(" + time.toString() + ") [" + latitude + "/" + longitude + "] E:" + elevation + " HR:" + heartRate
				+ " T:" + temperature;
	}
}
