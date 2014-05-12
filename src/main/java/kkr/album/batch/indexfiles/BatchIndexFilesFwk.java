package kkr.album.batch.indexfiles;

import kkr.album.components.manager_archiv.ManagerArchive;
import kkr.album.exception.ConfigurationException;

public abstract class BatchIndexFilesFwk {
	private boolean configured;

	protected ManagerArchive managerArchive;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerArchive == null) {
			throw new ConfigurationException(
					"Parameter 'managerArchive' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public ManagerArchive getManagerArchive() {
		return managerArchive;
	}

	public void setManagerArchive(ManagerArchive managerArchive) {
		this.managerArchive = managerArchive;
	}
}
