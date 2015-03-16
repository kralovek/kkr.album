package kkr.album.components.manager_password.md5;

import kkr.album.components.manager_password.ManagerPassword;
import kkr.album.exception.BaseException;

public class ManagerPasswordMD5 extends ManagerPasswordMD5Fwk implements
		ManagerPassword {

	public String decodePassword(String encoded) throws BaseException {
		return encoded;
	}
}
