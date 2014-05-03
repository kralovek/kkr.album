package kkr.album.components.batch_modifyphotos;

import kkr.album.components.manager_exif.ManagerExif;
import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.components.timeevaluator.TimeEvaluator;
import kkr.album.exception.ConfigurationException;

public abstract class BatchModifyPhotosFwk {
	private boolean configured;

	protected String filenameTags;
	
	protected String timeSymbol;
	protected TimeEvaluator timeEvaluator;
	
	protected ManagerGpx managerGpx;
	protected ManagerExif managerExif;
	
	
	public void config() throws ConfigurationException {
		configured = false;
		if (managerGpx == null) {
			throw new ConfigurationException("Parameter 'managerGpx' is not configured.");
		}
		if (timeSymbol == null) {
			timeSymbol = "";
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
}
