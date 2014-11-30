package kkr.album.components.analyzer_gpx;

import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;

public interface AnalyzerGpx {
	
	TraceStat analyzeTrace(Trace trace) throws BaseException;
}
