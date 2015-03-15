package kkr.album.components.manager_kml;

import java.io.File;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.exception.BaseException;

public interface ManagerKml {
	void saveKml(Gpx gpx, File file) throws BaseException;
}
