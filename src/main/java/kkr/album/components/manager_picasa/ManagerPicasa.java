package kkr.album.components.manager_picasa;

import java.io.File;
import java.util.List;

import kkr.album.exception.BaseException;

public interface ManagerPicasa {
	public void createOrUpdateAlbum(String name, List<File> photos)
			throws BaseException;
}
