package kkr.album.components.manager_gpx;

import java.io.File;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.exception.BaseException;

public interface ManagerGpx {
	Gpx loadGpx(File file) throws BaseException;

	void saveGpx(Gpx gpx, File file) throws BaseException;
}
