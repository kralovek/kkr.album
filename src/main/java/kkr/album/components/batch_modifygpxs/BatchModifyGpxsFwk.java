package kkr.album.components.batch_modifygpxs;

import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.exception.ConfigurationException;

public abstract class BatchModifyGpxsFwk {
	private boolean configured;

	protected ManagerGpx managerGpx;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerGpx == null) {
			throw new ConfigurationException(
					"Parameter 'managerGpx' is not configured.");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

}
