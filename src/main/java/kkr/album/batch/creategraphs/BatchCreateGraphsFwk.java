package kkr.album.batch.creategraphs;

import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.components.manager_graph.ManagerGraph;
import kkr.album.exception.ConfigurationException;

public abstract class BatchCreateGraphsFwk {
	private boolean configured;

	protected ManagerGpx managerGpx;
	protected ManagerGraph managerGraph;

	protected Integer width;
	protected Integer height;

	protected Double altitudeMin;
	protected Double altitudeMax;

	protected Double heartRateMin;
	protected Double heartRateMax;

	protected Double temperatureMin;
	protected Double temperatureMax;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerGpx == null) {
			throw new ConfigurationException("Parameter 'managerGpx' is not configured");
		}
		if (managerGraph == null) {
			throw new ConfigurationException("Parameter 'managerGraph' is not configured");
		}
		if (height == null) {
			throw new ConfigurationException("Parameter 'height' is not configured");
		}
		if (width == null) {
			throw new ConfigurationException("Parameter 'width' is not configured");
		}

		if (altitudeMin != null && altitudeMax != null && altitudeMin > altitudeMax) {
			throw new ConfigurationException(
					"Parameter 'altitudeMin' > 'altitudeMax': " + altitudeMin + " > " + altitudeMax);
		}
		if (heartRateMin != null && heartRateMax != null && heartRateMin > heartRateMax) {
			throw new ConfigurationException(
					"Parameter 'heartRateMin' > 'heartRateMax': " + heartRateMin + " > " + heartRateMax);
		}
		if (temperatureMin != null && temperatureMax != null && temperatureMin > temperatureMax) {
			throw new ConfigurationException(
					"Parameter 'temperatureMin' > 'temperatureMax': " + temperatureMin + " > " + temperatureMax);
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public ManagerGpx getManagerGpx() {
		return managerGpx;
	}

	public void setManagerGpx(ManagerGpx managerGpx) {
		this.managerGpx = managerGpx;
	}

	public ManagerGraph getManagerGraph() {
		return managerGraph;
	}

	public void setManagerGraph(ManagerGraph managerGraph) {
		this.managerGraph = managerGraph;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Double getAltitudeMin() {
		return altitudeMin;
	}

	public void setAltitudeMin(Double altitudeMin) {
		this.altitudeMin = altitudeMin;
	}

	public Double getAltitudeMax() {
		return altitudeMax;
	}

	public void setAltitudeMax(Double altitudeMax) {
		this.altitudeMax = altitudeMax;
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
}
