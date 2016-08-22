package kkr.album.batch.createdate;

import kkr.album.components.manager_exif.ManagerExif;
import kkr.album.exception.ConfigurationException;

public abstract class BatchCreateDateFwk {
	private boolean configured;

	protected ManagerExif managerExif;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerExif == null) {
			throw new ConfigurationException("Parameter 'managerExif' is not configured");
		}

		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName() + ": The component is not configured");
		}
	}

	public ManagerExif getManagerExif() {
		return managerExif;
	}

	public void setManagerExif(ManagerExif managerExif) {
		this.managerExif = managerExif;
	}

}
