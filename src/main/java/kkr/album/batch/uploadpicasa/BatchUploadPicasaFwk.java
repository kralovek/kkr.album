package kkr.album.batch.uploadpicasa;

import kkr.album.components.manager_picasa.ManagerPicasa;
import kkr.album.exception.ConfigurationException;

public abstract class BatchUploadPicasaFwk {
	private boolean configured;

	protected ManagerPicasa managerPicasa;
	
	public void config() throws ConfigurationException {
		configured = false;
		if (managerPicasa == null) {
			throw new ConfigurationException("Parameter 'managerPicasa' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public ManagerPicasa getManagerPicasa() {
		return managerPicasa;
	}

	public void setManagerPicasa(ManagerPicasa managerPicasa) {
		this.managerPicasa = managerPicasa;
	}
}
