package kkr.album.components.manager_gpx;

import kkr.album.exception.ConfigurationException;

public abstract class ManagerGpxGenericFwk {

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
