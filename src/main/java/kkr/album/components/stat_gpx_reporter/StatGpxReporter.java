package kkr.album.components.stat_gpx_reporter;

import kkr.album.batch.analyzergpx.GlobalTraceStat;
import kkr.album.components.analyzer_gpx.TraceStat;
import kkr.album.exception.BaseException;

public interface StatGpxReporter {
	void report(TraceStat traceStat, String target) throws BaseException;

	void report(GlobalTraceStat globalTraceStat, String target) throws BaseException;
}
