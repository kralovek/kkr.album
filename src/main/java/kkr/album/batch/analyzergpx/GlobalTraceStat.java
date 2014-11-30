package kkr.album.batch.analyzergpx;

import java.util.Map;
import java.util.TreeMap;

public class GlobalTraceStat {
	private String source;
	private Double totalAscent = 0.;
	private Double totalDescent = 0.;
	private Double distance = 0.;

	private Map<Integer, Long> cumulHeartRateSecond = new TreeMap<Integer, Long>();

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

	public Map<Integer, Long> getCumulHeartRateSecond() {
		return cumulHeartRateSecond;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
