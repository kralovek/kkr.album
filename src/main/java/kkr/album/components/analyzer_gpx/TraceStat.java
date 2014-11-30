package kkr.album.components.analyzer_gpx;

import java.util.Map;
import java.util.TreeMap;

public class TraceStat {

	private String source;

	private Double latitudeMin;
	private Double latitudeMax;
	private Double longitudeMin;
	private Double longitudeMax;

	private Double speedMax;
	private Double speedAvg;

	private Double elevationMin;
	private Double elevationMax;
	private Double elevationAvg;

	private Double totalAscent;
	private Double totalDescent;

	private Double heartRateMin;
	private Double heartRateMax;
	private Double heartRateAvg;

	private Double temperatureMin;
	private Double temperatureMax;
	private Double temperatureAvg;

	private Double distance;
	private Double duration;

	private Map<Integer, Long> cumulHeartRateSecond = new TreeMap<Integer, Long>();

	public Double getLatitudeMin() {
		return latitudeMin;
	}

	public void setLatitudeMin(Double latitudeMin) {
		this.latitudeMin = latitudeMin;
	}

	public Double getLatitudeMax() {
		return latitudeMax;
	}

	public void setLatitudeMax(Double latitudeMax) {
		this.latitudeMax = latitudeMax;
	}

	public Double getLongitudeMin() {
		return longitudeMin;
	}

	public void setLongitudeMin(Double longitudeMin) {
		this.longitudeMin = longitudeMin;
	}

	public Double getLongitudeMax() {
		return longitudeMax;
	}

	public void setLongitudeMax(Double longitudeMax) {
		this.longitudeMax = longitudeMax;
	}

	public Double getSpeedMax() {
		return speedMax;
	}

	public void setSpeedMax(Double speedMax) {
		this.speedMax = speedMax;
	}

	public Double getSpeedAvg() {
		return speedAvg;
	}

	public void setSpeedAvg(Double speedAvg) {
		this.speedAvg = speedAvg;
	}

	public Double getElevationMin() {
		return elevationMin;
	}

	public void setElevationMin(Double elevationMin) {
		this.elevationMin = elevationMin;
	}

	public Double getElevationMax() {
		return elevationMax;
	}

	public void setElevationMax(Double elevationMax) {
		this.elevationMax = elevationMax;
	}

	public Double getElevationAvg() {
		return elevationAvg;
	}

	public void setElevationAvg(Double elevationAvg) {
		this.elevationAvg = elevationAvg;
	}

	public Double getTotalAscent() {
		return totalAscent;
	}

	public void setTotalAscent(Double totalAscent) {
		this.totalAscent = totalAscent;
	}

	public Double getTotalDescent() {
		return totalDescent;
	}

	public void setTotalDescent(Double totalDescent) {
		this.totalDescent = totalDescent;
	}

	public Double getHeartRateMin() {
		return heartRateMin;
	}

	public void setHeartRateMin(Double heartRateMin) {
		this.heartRateMin = heartRateMin;
	}

	public Double getHeartRateMax() {
		return heartRateMax;
	}

	public void setHeartRateMax(Double heartRateMax) {
		this.heartRateMax = heartRateMax;
	}

	public Double getHeartRateAvg() {
		return heartRateAvg;
	}

	public void setHeartRateAvg(Double heartRateAvg) {
		this.heartRateAvg = heartRateAvg;
	}

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

	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	public Double getTemperatureAvg() {
		return temperatureAvg;
	}

	public void setTemperatureAvg(Double temperatureAvg) {
		this.temperatureAvg = temperatureAvg;
	}

	public Map<Integer, Long> getCumulHeartRateSecond() {
		return cumulHeartRateSecond;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String name) {
		this.source = name;
	}

}
