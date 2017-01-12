package kkr.album.components.timeevaluator;

import java.io.File;
import java.util.Map;

import kkr.album.exception.BaseException;

public interface TimeEvaluator {

	Map<String, Map<TimeType, FileTime>> loadTimes(File dir) throws BaseException;
}
