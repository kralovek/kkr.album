package kkr.album.components.manager_image;

import java.io.File;

import kkr.album.exception.BaseException;

public interface ManagerImage {

	void resize(File fileIn, File fileOut, int width) throws BaseException;
}
