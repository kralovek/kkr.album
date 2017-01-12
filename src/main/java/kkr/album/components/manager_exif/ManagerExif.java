package kkr.album.components.manager_exif;

import java.io.File;

import kkr.album.exception.BaseException;
import kkr.album.model.DateNZ;

public interface ManagerExif {

	DateNZ determineDate(File file) throws BaseException;

	void modifyFile(File file, DateNZ date, Double longitude, Double latitude) throws BaseException;

	void modifyFile(File file, DateNZ date) throws BaseException;

	void copyExif(File fileSource, File fileTarget) throws BaseException;
}
