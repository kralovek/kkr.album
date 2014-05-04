package kkr.album.components.manager_gpx.model;

import java.util.Date;

public class Point implements Cloneable, Comparable<Point> {
	private Double latitude;
	private Double longitude;
	private Double elevation;
	private Double temperature;
	private Integer heartRate;
	private Integer cadence;
	private Date time;

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

	public Integer getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
	}

	public Integer getCadence() {
		return cadence;
	}

	public void setCadence(Integer cadence) {
		this.cadence = cadence;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
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
}