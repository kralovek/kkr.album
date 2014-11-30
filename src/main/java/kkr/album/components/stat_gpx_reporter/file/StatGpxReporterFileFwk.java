package kkr.album.components.stat_gpx_reporter.file;

import kkr.album.exception.ConfigurationException;

public abstract class StatGpxReporterFileFwk {
	private boolean configured;
	
	public void config() throws ConfigurationException {
		configured = false;
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

}
