package kkr.album.components.manager_kml.generic;

import java.util.HashMap;
import java.util.Map;

import kkr.album.exception.ConfigurationException;

public abstract class ManagerKmlGenericFwk {
	private boolean configured;

	protected Map<String, String> styleColors;
	protected Integer styleLineSize;

	public void config() throws ConfigurationException {
		configured = false;
		if (styleLineSize == null) {
			throw new ConfigurationException(
					"Parameter 'styleLineSize' is not configured");
		}
		if (styleColors == null) {
			styleColors = new HashMap<String, String>();
		} else {
			for (Map.Entry<String, String> entry : styleColors.entrySet()) {
				String color = entry.getValue();
				boolean result = color.matches("[0-9a-f]{8}");
				if (!result) {
					throw new ConfigurationException(
							"Parameter 'styleColors' has bad format for "
									+ entry.getKey() + ": " + entry.getValue());
				}
			}
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public Map<String, String> getStyleColors() {
		return styleColors;
	}

	public void setStyleColors(Map<String, String> styleColors) {
		this.styleColors = styleColors;
	}

	public Integer getStyleLineSize() {
		return styleLineSize;
	}

	public void setStyleLineSize(Integer styleLineSize) {
		this.styleLineSize = styleLineSize;
	}
}
