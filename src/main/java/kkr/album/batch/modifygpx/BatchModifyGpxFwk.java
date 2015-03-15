package kkr.album.batch.modifygpx;

import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.components.manager_kml.ManagerKml;
import kkr.album.exception.ConfigurationException;

public abstract class BatchModifyGpxFwk {
	private boolean configured;

	protected ManagerGpx managerGpx;
	protected ManagerKml managerKml;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerGpx == null) {
			throw new ConfigurationException(
					"Parameter 'managerGpx' is not configured.");
		}
		if (managerKml == null) {
			throw new ConfigurationException(
					"Parameter 'managerKml' is not configured.");
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

	public ManagerKml getManagerKml() {
		return managerKml;
	}

	public void setManagerKml(ManagerKml managerKml) {
		this.managerKml = managerKml;
	}
}
