package kkr.album.components.manager_password;

import kkr.album.exception.BaseException;

public interface ManagerPassword {

	String decodePassword(String encoded) throws BaseException;
}
