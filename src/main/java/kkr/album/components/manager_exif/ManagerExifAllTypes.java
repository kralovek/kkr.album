package kkr.album.components.manager_exif;

import java.io.File;
import java.util.Date;

import kkr.album.exception.BaseException;
import kkr.album.exception.ConfigurationException;

import org.apache.log4j.Logger;

public class ManagerExifAllTypes extends ManagerExifAllTypesFwk implements
		ManagerExif {
	private static final Logger LOGGER = Logger
			.getLogger(ManagerExifAllTypes.class);

	public Date determineDate(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			ManagerExif managerExif = determineManagerExif(file);
			Date date = managerExif.determineDate(file);
			LOGGER.trace("OK");
			return date;
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, Date date, Double longitude,
			Double latitude) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			ManagerExif managerExif = determineManagerExif(file);
			managerExif.modifyFile(file, date, longitude, latitude);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, Date date) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			ManagerExif managerExif = determineManagerExif(file);
			managerExif.modifyFile(file, date);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private ManagerExif determineManagerExif(File file) throws BaseException {
		String ext = "";
		int pos = file.getName().lastIndexOf(".");
		if (pos != -1 && file.getName().length() != pos + 1) {
			ext = file.getName().substring(pos + 1).toLowerCase();
		}
		ManagerExif managerExif = managersExif.get(ext);
		if (managerExif == null) {
			throw new ConfigurationException(
					"No ManagerExif is configured for extension: " + ext);
		}
		return managerExif;
	}
	
	public void copyExif(File fileSource, File fileTarget) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			ManagerExif managerExif = determineManagerExif(fileSource);
			managerExif.copyExif(fileSource, fileTarget);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
}
