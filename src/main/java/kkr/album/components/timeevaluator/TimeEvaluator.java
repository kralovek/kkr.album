package kkr.album.components.timeevaluator;

import java.io.File;
import java.util.Map;

import kkr.album.exception.BaseException;

public interface TimeEvaluator {

	static interface FileTime {
		Long getMove();
		File getFile();
	}
	
	Map<String, FileTime> loadTimes(File dir) throws BaseException;
}
