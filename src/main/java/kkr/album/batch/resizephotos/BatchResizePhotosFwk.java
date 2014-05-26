package kkr.album.batch.resizephotos;

import kkr.album.components.manager_image.ManagerImage;
import kkr.album.exception.ConfigurationException;

public abstract class BatchResizePhotosFwk {
	private boolean configured;

	protected ManagerImage managerImage;
	protected Integer toWidth;

	public void config() throws ConfigurationException {
		configured = false;
		if (managerImage == null) {
			throw new ConfigurationException(
					"Parameter 'managerImage' is not configured");
		}
		if (toWidth == null) {
			throw new ConfigurationException(
					"Parameter 'toWidth' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public Integer getToWidth() {
		return toWidth;
	}

	public void setToWidth(Integer toWidth) {
		this.toWidth = toWidth;
	}

	public ManagerImage getManagerImage() {
		return managerImage;
	}

	public void setManagerImage(ManagerImage managerImage) {
		this.managerImage = managerImage;
	}
}
