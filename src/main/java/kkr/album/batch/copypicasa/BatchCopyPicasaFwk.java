package kkr.album.batch.copypicasa;

import java.io.File;

import kkr.album.exception.ConfigurationException;

public abstract class BatchCopyPicasaFwk {
	private boolean configured;

	protected File dirPicasa;
	
	public void config() throws ConfigurationException {
		configured = false;
		if (dirPicasa == null) {
			throw new ConfigurationException("Parameter 'dirPicasa' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public File getDirPicasa() {
		return dirPicasa;
	}

	public void setDirPicasa(File dirPicasa) {
		this.dirPicasa = dirPicasa;
	}
}
