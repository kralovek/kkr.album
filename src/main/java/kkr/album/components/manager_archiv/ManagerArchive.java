package kkr.album.components.manager_archiv;

import java.io.File;

import kkr.album.exception.BaseException;

public interface ManagerArchive {
    int nextIndex() throws BaseException;
    
    void copyToArchiv(File file) throws BaseException;
    
    void moveToArchiv(File file) throws BaseException;
}
