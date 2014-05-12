package kkr.album.batch.indexfiles;

import kkr.album.components.manager_archiv.ManagerArchive;
import kkr.album.exception.ConfigurationException;

public abstract class BatchIndexFilesFwk {
	private boolean configured;

	protected ManagerArchive managerArchiv;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerArchiv == null) {
			throw new ConfigurationException(
					"Parameter 'managerArchiv' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public ManagerArchive getManagerArchiv() {
		return managerArchiv;
	}

	public void setManagerArchiv(ManagerArchive managerArchiv) {
		this.managerArchiv = managerArchiv;
	}
}
