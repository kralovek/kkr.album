package kkr.album.components.manager_gpx;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.exception.BaseException;

import java.io.File;

public interface ManagerGpx {
    void formatGpxFile(File fileSource, File fileTarget, String traceName) throws BaseException;

    Gpx loadGpx(File file) throws BaseException;

    void printGpx(Gpx gpx, File file) throws BaseException;
}
