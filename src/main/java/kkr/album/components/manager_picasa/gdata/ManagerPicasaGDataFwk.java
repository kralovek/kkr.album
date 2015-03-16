package kkr.album.components.manager_picasa.gdata;

import kkr.album.components.manager_password.ManagerPassword;
import kkr.album.exception.ConfigurationException;

public abstract class ManagerPicasaGDataFwk {
	private boolean configured;

	protected ManagerPassword managerPassword;
	
	protected String user;
	protected String password;

	public void config() throws ConfigurationException {
		configured = false;
		if (user == null) {
			throw new ConfigurationException(
					"Parameter 'user' is not configured");
		}
		if (password == null) {
			throw new ConfigurationException(
					"Parameter 'password' is not configured");
		}
		if (managerPassword == null) {
			throw new ConfigurationException(
					"Parameter 'managerPassword' is not configured");
		}
		configured = true;
	}

	public void testConfigured() {
		if (!configured) {
			throw new IllegalStateException(this.getClass().getName()
					+ ": The component is not configured");
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ManagerPassword getManagerPassword() {
		return managerPassword;
	}

	public void setManagerPassword(ManagerPassword managerPassword) {
		this.managerPassword = managerPassword;
	}
}
