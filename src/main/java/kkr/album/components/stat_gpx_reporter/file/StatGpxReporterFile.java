package kkr.album.components.stat_gpx_reporter.file;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import kkr.album.batch.analyzergpx.GlobalTraceStat;
import kkr.album.components.analyzer_gpx.TraceStat;
import kkr.album.components.stat_gpx_reporter.StatGpxReporter;
import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsFile;

public class StatGpxReporterFile extends StatGpxReporterFileFwk implements
		StatGpxReporter {
	private static transient final Logger LOGGER = Logger
			.getLogger(StatGpxReporterFile.class);

	public void report(TraceStat traceStat, String target) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			File fileTargetTxt = generateFile(target, "txt");
			PrintWriter printWriter = null;
			try {
				printWriter = new PrintWriter(fileTargetTxt);
				printWriter.close();
				
				printWriter.println("trace.source=" + traceStat.getSource());
				printWriter.println("trace.distance[km]=" + traceStat.getDistance());
				printWriter.println("trace.duration[h]=" + traceStat.getDuration());
				printWriter.println("trace.elevation.max[m]=" + traceStat.getElevationMax());
				printWriter.println("trace.elevation.min[m]=" + traceStat.getElevationMin());
				printWriter.println("trace.ascent.total[m]=" + traceStat.getTotalAscent());
				printWriter.println("trace.descent.total[m]=" + traceStat.getTotalDescent());
				printWriter.println("trace.heartrate.max=" + traceStat.getHeartRateMax());
				printWriter.println("trace.heartrate.min=" + traceStat.getHeartRateMin());
				printWriter.println("trace.latitude.max[d.dd]=" + traceStat.getLatitudeMax());
				printWriter.println("trace.latitude.min[d.dd]=" + traceStat.getLatitudeMin());
				printWriter.println("trace.longitude.max[d.dd]=" + traceStat.getLongitudeMax());
				printWriter.println("trace.longitude.min[d.dd]=" + traceStat.getLongitudeMin());
				printWriter.println("trace.speed.max[km/h]=" + traceStat.getSpeedMax());
				printWriter.println("trace.speed.avg[km/h]=" + traceStat.getSpeedAvg());
				printWriter.println("trace.temperature.max=[C]" + traceStat.getTemperatureMax());
				printWriter.println("trace.temperature.min[C]=" + traceStat.getTemperatureMin());
				
				printWriter = null;
			} catch (IOException ex) {
				throw new TechnicalException("Cannot create the report file: "
						+ fileTargetTxt.getAbsolutePath());
			} finally {
				UtilsFile.closeRessource(printWriter);
			}

			createGraphHR(traceStat.getCumulHeartRateSecond(), target);
			
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void report(GlobalTraceStat globalTraceStat, String target)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			File fileTarget = generateFile(target, "txt");
			PrintWriter printWriter = null;
			try {
				printWriter = new PrintWriter(fileTarget);
				
				printWriter.println("globaltrace.source=" + globalTraceStat.getSource());
				printWriter.println("globaltrace.distance[km]=" + globalTraceStat.getDistance());
				printWriter.println("globaltrace.ascent.total[m]=" + globalTraceStat.getTotalAscent());
				printWriter.println("globaltrace.descent.total[m]=" + globalTraceStat.getTotalDescent());

				createGraphHR(globalTraceStat.getCumulHeartRateSecond(), target);

				printWriter.close();
				printWriter = null;
			} catch (IOException ex) {
				throw new TechnicalException("Cannot create the report file: "
						+ fileTarget.getAbsolutePath());
			} finally {
				UtilsFile.closeRessource(printWriter);
			}
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void createGraphHR(Map<Integer, Long> hrDuration, String target) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			File fileTargetHR = generateFile(target, "csv");

			List<Integer> hrs = new ArrayList<Integer>();
			Map<Integer, Long> hrCumulDuration = new TreeMap<Integer, Long>();
			
			for (Map.Entry<Integer, Long> entry : hrDuration.entrySet()) {
				hrs.add(entry.getKey());
			}
			
			Collections.sort(hrs);
			
			ListIterator<Integer> iterator = hrs.listIterator();
			
			long secCumul = 0;
			for (int hr = hrs.size() - 1; hr >= 0; hr++) {
				long sec = hrDuration.get(hr);
				secCumul += sec;
				hrCumulDuration.put(hr,  secCumul);
			}

			PrintWriter printWriter = null;
			try {
				printWriter = new PrintWriter(fileTargetHR);
				printWriter.close();

				printWriter.println("HR;Duration");
				
				for (Map.Entry<Integer, Long> entry : hrCumulDuration.entrySet()) {
					printWriter.println(entry.getKey() + ";" + entry.getValue());
				}
				
				printWriter = null;
			} catch (IOException ex) {
				throw new TechnicalException("Cannot create the graphHR file: "
						+ fileTargetHR.getAbsolutePath());
			} finally {
				UtilsFile.closeRessource(printWriter);
			}
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}
	
	private File generateFile(String target, String extension) throws BaseException {
		if (target.toLowerCase().endsWith(".gpx")) {
			target = target.substring(0, target.length() - 4);
		}
		File fileTarget = new File(target + "." + extension);
		UtilsFile.createFileDir(fileTarget);
		return fileTarget;
	}
}
