package kkr.album.components.batch_modifyphotos;

import java.io.File;

import kkr.album.components.manager_exif.ManagerExif;
import kkr.album.components.manager_gpx.ManagerGpx;
import kkr.album.components.timeevaluator.TimeEvaluator;
import kkr.album.exception.ConfigurationException;

public abstract class BatchModifyPhotosFwk {
	private boolean configured;

	protected File dirBase;
	
	protected File dirGps;
	protected File fileTags;
	private String filenameTags;
	
	protected Long timeMove;
	protected String timeSymbol;
	protected TimeEvaluator timeEvaluator;
	
	protected ManagerGpx managerGpx;
	protected ManagerExif managerExif;
	
	
	public void config() throws ConfigurationException {
		configured = false;
		if (timeMove == null && timeEvaluator == null) {
			throw new ConfigurationException("Parameter 'timeMove' or 'timeEvaluator' must be configured.");
		}
		if (managerGpx == null) {
			throw new ConfigurationException("Parameter 'managerGpx' is not configured.");
		}
		if (timeSymbol == null) {
			timeSymbol = "";
		}
		if (dirBase == null) {
			dirBase = new File(".");
		}
		dirGps = new File(dirBase, "gps");
		if (filenameTags != null) {
			fileTags = new File(dirGps, filenameTags);
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
