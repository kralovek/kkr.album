package kkr.album.components.analyzer_gpx;

public class PointStat {

	private Double distance = null;
	private Double duration = null;

	private Double elevation = null;
	private Double speed = null;
	private Double heartRate = null;
	private Double temperature = null;

	private Double ascent = null;

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double altitude) {
		this.elevation = altitude;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Double heartRate) {
		this.heartRate = heartRate;
	}

	public Double getAscent() {
		return ascent;
	}

	public void setAscent(Double ascent) {
		this.ascent = ascent;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
}
