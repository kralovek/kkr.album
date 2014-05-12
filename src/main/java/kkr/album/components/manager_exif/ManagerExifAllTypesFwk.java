package kkr.album.components.manager_exif;

import java.util.HashMap;
import java.util.Map;

import kkr.album.exception.ConfigurationException;

public abstract class ManagerExifAllTypesFwk {

	protected boolean configured;

	protected Map<String, ManagerExif> managersExif;

	public void config() throws ConfigurationException {
		configured = false;
		if (managersExif == null) {
			managersExif = new HashMap<String, ManagerExif>();
		} else {
			Map<String, ManagerExif> managersExifModified = new HashMap<String, ManagerExif>();
			for (Map.Entry<String, ManagerExif> entry : managersExif.entrySet()) {
				if (managersExifModified.put(entry.getKey().toLowerCase(), entry.getValue()) != null) {
					throw new ConfigurationException("Parameter map 'managersExif' is not case sensitive");
				}
			}
			managersExif.clear();
			managersExif = managersExifModified;
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public Map<String, ManagerExif> getManagersExif() {
		return managersExif;
	}

	public void setManagersExif(Map<String, ManagerExif> managersExif) {
		this.managersExif = managersExif;
	}
}
