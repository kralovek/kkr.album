package kkr.album.components.manager_archiv;

import java.io.File;

import kkr.album.exception.ConfigurationException;

public abstract class ManagerArchiveGenericFwk implements ManagerArchive {

	private boolean configured;

	protected File dirOriginal;
	protected File dirNormal;
	protected File dirVideo;

	public void config() throws ConfigurationException {
		configured = false;
		if (dirOriginal == null) {
			throw new ConfigurationException(
					"The parameter 'dirOriginal' is not configured");
		}
		if (dirNormal == null) {
			throw new ConfigurationException(
					"The parameter 'dirNormal' is not configured");
		}
		if (dirVideo == null) {
			throw new ConfigurationException(
					"The parameter 'dirVideo' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public File getDirOriginal() {
		return dirOriginal;
	}

	public void setDirOriginal(File dirOriginal) {
		this.dirOriginal = dirOriginal;
	}

	public File getDirNormal() {
		return dirNormal;
	}

	public void setDirNormal(File dirNormal) {
		this.dirNormal = dirNormal;
	}

	public File getDirVideo() {
		return dirVideo;
	}

	public void setDirVideo(File dirVideo) {
		this.dirVideo = dirVideo;
	}
}
