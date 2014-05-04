package kkr.album.components.batch_modifyphotos;

import kkr.album.components.manager_exif.ManagerExif;
import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.components.timeevaluator.TimeEvaluator;
import kkr.album.exception.ConfigurationException;

public abstract class BatchModifyPhotosFwk {
	private boolean configured;

	protected String filenameTags;

	protected TimeEvaluator timeEvaluator;

	protected ManagerGpx managerGpx;
	protected ManagerExif managerExif;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerGpx == null) {
			throw new ConfigurationException(
					"Parameter 'managerGpx' is not configured.");
		}
		if (filenameTags != null) {
			// OK
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String getFilenameTags() {
		return filenameTags;
	}

	public void setFilenameTags(String filenameTags) {
		this.filenameTags = filenameTags;
	}

	public TimeEvaluator getTimeEvaluator() {
		return timeEvaluator;
	}

	public void setTimeEvaluator(TimeEvaluator timeEvaluator) {
		this.timeEvaluator = timeEvaluator;
	}

	public ManagerGpx getManagerGpx() {
		return managerGpx;
	}

	public void setManagerGpx(ManagerGpx managerGpx) {
		this.managerGpx = managerGpx;
	}

	public ManagerExif getManagerExif() {
		return managerExif;
	}

	public void setManagerExif(ManagerExif managerExif) {
		this.managerExif = managerExif;
	}
}
