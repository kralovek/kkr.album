package kkr.album.components.manager_kml.generic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.components.manager_kml.ManagerKml;
import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsFile;

public class ManagerKmlGeneric extends ManagerKmlGenericFwk implements ManagerKml {

	private static final String DEFAULT_COLOR = "ffffffff";

	public void saveKml(Gpx gpx, File file) throws BaseException {
		PrintStream printStream = null;
		try {
			testConfigured();
			if (gpx.getTraces().isEmpty()) {
				return;
			}
			Trace traceFirst = gpx.getTraces().iterator().next();

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			printStream = new PrintStream(fileOutputStream);

			printHeader(traceFirst, printStream);
			Map<String, String> styles = printStyles(gpx, printStream);
			printTraces(gpx, styles, printStream);
			printFooter(printStream);

			printStream.close();

		} catch (FileNotFoundException ex) {
			throw new TechnicalException("The fileSource does not exist: " + file.getAbsolutePath(), ex);
		} finally {
			UtilsFile.closeRessource(printStream);
		}
	}

	private void printTraces(Gpx gpx, Map<String, String> styles, PrintStream printStream) {
		for (Trace trace : gpx.getTraces()) {
			String style = styles.get(trace.getName());
			printTrace(trace, style, printStream);
		}
	}

	private void printTrace(Trace trace, String style, PrintStream printStream) {
		printStream.println("\t<Placemark>");
		printStream.println("\t\t<name>" + trace.getName() + "</name>");
		printStream.println("\t\t<styleUrl>" + style + "</styleUrl>");
		printStream.println("\t\t<MultiGeometry>");
		printStream.println("\t\t\t<LineString>");
		printStream.println("\t\t\t\t<coordinates>");

		printStream.print("\t\t\t\t\t");
		for (Point point : trace.getPoints()) {
			printStream.print(" " + point.getLongitude() + "," + point.getLatitude() + ","
					+ (point.getElevation() != null ? point.getElevation() : 0));
		}
		printStream.println();

		printStream.println("\t\t\t\t</coordinates>");
		printStream.println("\t\t\t</LineString>");
		printStream.println("\t\t</MultiGeometry>");
		printStream.println("\t</Placemark>");
	}

	private String evalueateColor(Trace trace) {
		String symbol = trace.getName();
		int iPos = symbol.indexOf("_");
		if (iPos == -1) {
			return DEFAULT_COLOR;
		}
		symbol = symbol.substring(iPos + 1);
		iPos = symbol.indexOf("_");
		if (iPos != -1) {
			symbol = symbol.substring(0, iPos);
		}

		String color = styleColors.get(symbol);

		if (color == null) {
			return DEFAULT_COLOR;
		}

		return color;
	}

	private void printHeader(Trace traceFirst, PrintStream printStream) {
		printStream.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		printStream.println(
				"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
		printStream.println("<Document>");

		printStream.println("\t<name>" + traceFirst.getName() + ".kml</name>");
	}

	private void printFooter(PrintStream printStream) {
		printStream.println("</Document>");
		printStream.println("</kml>");
	}

	private Map<String, String> printStyles(Gpx gpx, PrintStream printStream) {
		Map<String, String> styles = new HashMap<String, String>();
		int index = 0;
		for (Trace trace : gpx.getTraces()) {
			String color = evalueateColor(trace);
			String styleMap = "path0Style" + index++;
			String styleNormal = "path0Style" + index++;
			String styleHighlight = "path0Style" + index++;

			styles.put(trace.getName(), styleMap);

			printStream.println("\t<StyleMap id=\"" + styleMap + "\">");
			printStream.println("\t\t<Pair>");
			printStream.println("\t\t\t<key>normal</key>");
			printStream.println("\t\t\t<styleUrl>#" + styleNormal + "</styleUrl>");
			printStream.println("\t\t</Pair>");
			printStream.println("\t\t<Pair>");
			printStream.println("\t\t\t<key>highlight</key>");
			printStream.println("\t\t\t<styleUrl>#" + styleHighlight + "</styleUrl>");
			printStream.println("\t\t</Pair>");
			printStream.println("\t</StyleMap>");

			printStream.println("\t<Style id=\"" + styleNormal + "\">");
			printStream.println("\t\t<LineStyle>");
			printStream.println("\t\t\t<color>" + color + "</color>");
			printStream.println("\t\t\t<width>" + styleLineSize + "</width>");
			printStream.println("\t\t</LineStyle>");
			printStream.println("\t</Style>");

			printStream.println("\t<Style id=\"" + styleHighlight + "\">");
			printStream.println("\t\t<LineStyle>");
			printStream.println("\t\t\t<color>" + color + "</color>");
			printStream.println("\t\t\t<width>" + styleLineSize + "</width>");
			printStream.println("\t\t</LineStyle>");
			printStream.println("\t</Style>");
		}
		return styles;
	}
}
