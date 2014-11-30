package kkr.album.batch.analyzergpx;

import kkr.album.components.analyzer_gpx.AnalyzerGpx;
import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.components.stat_gpx_reporter.StatGpxReporter;
import kkr.album.exception.ConfigurationException;

public abstract class BatchAnalyzerGpxFwk {

	private boolean configured;

	protected ManagerGpx managerGpx;
	protected AnalyzerGpx analyzerGpx;
	protected StatGpxReporter statGpxReporter;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerGpx == null) {
			throw new ConfigurationException(
					"The parameter 'managerGpx' is not configured");
		}
		if (analyzerGpx == null) {
			throw new ConfigurationException(
					"The parameter 'analyzerGpx' is not configured");
		}
		if (statGpxReporter == null) {
			throw new ConfigurationException(
					"The parameter 'statGpxReporter' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public ManagerGpx getManagerGpx() {
		return managerGpx;
	}

	public void setManagerGpx(ManagerGpx managerGpx) {
		this.managerGpx = managerGpx;
	}

	public AnalyzerGpx getAnalyzerGpx() {
		return analyzerGpx;
	}

	public void setAnalyzerGpx(AnalyzerGpx analyzerGpx) {
		this.analyzerGpx = analyzerGpx;
	}

	public StatGpxReporter getStatGpxReporter() {
		return statGpxReporter;
	}

	public void setStatGpxReporter(StatGpxReporter statGpxReporter) {
		this.statGpxReporter = statGpxReporter;
	}
}
