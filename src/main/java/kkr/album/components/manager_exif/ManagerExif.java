package kkr.album.components.manager_exif;

import java.io.File;
import java.util.Date;

import kkr.album.exception.BaseException;

public interface ManagerExif {

	Date determineDate(File file) throws BaseException;

	void modifyFile(File file, Date date, Double longitude, Double latitude) throws BaseException;

	void modifyFile(File file, Date date) throws BaseException;
}
