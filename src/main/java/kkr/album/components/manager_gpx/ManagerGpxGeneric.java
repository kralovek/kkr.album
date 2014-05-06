package kkr.album.components.manager_gpx;

import kkr.album.components.manager_gpx.model.Gpx;
import kkr.album.components.manager_gpx.model.Point;
import kkr.album.components.manager_gpx.model.Trace;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerGpxGeneric extends ManagerGpxGenericFwk implements
		ManagerGpx {

	private static DateFormat dateFormat;
	static {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
	}

	private static Comparator<Point> comparatorPointsTime = new Comparator<Point>() {
		public int compare(Point point1, Point point2) {
			return point1.getTime() == null ? -1
					: point2.getTime() == null ? +1 : point1.getTime()
							.compareTo(point2.getTime());
		}
	};

	public void formatGpxFile(File fileSource, File fileTarget, String traceName)
			throws BaseException {
		XMLStreamReader xmlStreamReader = null;
		PrintStream printStream = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(fileSource);
			xmlStreamReader = XMLInputFactory.newInstance()
					.createXMLStreamReader(fileInputStream);

			FileOutputStream fileOutputStream = new FileOutputStream(fileTarget);
			printStream = new PrintStream(fileOutputStream);

			workFormat(xmlStreamReader, printStream);

			printStream.close();
			xmlStreamReader.close();
			xmlStreamReader = null;

		} catch (FileNotFoundException ex) {
			throw new TechnicalException("The fileSource does not exist: "
					+ fileSource.getAbsolutePath(), ex);
		} catch (XMLStreamException ex) {
			throw new TechnicalException("Bad format of the XML: "
					+ fileSource.getAbsolutePath(), ex);
		} finally {
			UtilsFile.closeRessource(printStream);
			UtilsFile.closeRessource(xmlStreamReader);
		}
		// fileSource.delete();
		// if (fileSource.isFile()) {
		// fileSource.deleteOnExit();
		// }
	}

	public Gpx loadGpx(File file) throws BaseException {
		XMLStreamReader xmlStreamReader = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			xmlStreamReader = XMLInputFactory.newInstance()
					.createXMLStreamReader(fileInputStream);

			Gpx retval = workLoad(xmlStreamReader);

			xmlStreamReader.close();
			xmlStreamReader = null;

			return retval;

		} catch (FileNotFoundException ex) {
			throw new TechnicalException("The fileSource does not exist: "
					+ file.getAbsolutePath(), ex);
		} catch (XMLStreamException ex) {
			throw new TechnicalException("Bad format of the XML: "
					+ file.getAbsolutePath(), ex);
		} finally {
			UtilsFile.closeRessource(xmlStreamReader);
		}
	}

	public void saveGpx(Gpx gpx, File file) throws BaseException {
		PrintStream printStream = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			printStream = new PrintStream(fileOutputStream);

			workPrint(gpx, printStream);

			printStream.close();

		} catch (FileNotFoundException ex) {
			throw new TechnicalException("The fileSource does not exist: "
					+ file.getAbsolutePath(), ex);
		} finally {
			UtilsFile.closeRessource(printStream);
		}
	}

	//
	// PRIVATE
	//
	private void workPrint(Gpx gpx, PrintStream printStream)
			throws BaseException {
		printStream
				.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
		printStream.println("<gpx");
		for (XmlReader.Attribute attribute : gpx.getAttributes()) {
			printStream.println("    "
					+ (attribute.getPrefix() != null ? attribute.getPrefix()
							+ ":" + attribute.getName() : attribute.getName())
					+ "=\"" + attribute.getValue() + "\"");
		}
		printStream.println(">");
		if (false) {
			printStream.println("<metadata>");
			printStream
					.println("<link href=\"http://www.garmin.com\"><text>Garmin International</text></link>");
			if (gpx.getTime() != null) {
				printStream.println("<time>" + dateFormat.format(gpx.getTime())
						+ "</time>");
			}
			printStream.println("</metadata>");
			printStream.println();
		}

		for (Trace trace : gpx.getTraces()) {
			printStream.println("<trk>");
			printStream.println("<name>" + trace.getName() + "</name>");
			if (false && trace.getColor() != null) {
				printStream.println("<extensions>");
				printStream.println("<gpxx:TrackExtension><gpxx:DisplayColor>"
						+ trace.getColor()
						+ "</gpxx:DisplayColor></gpxx:TrackExtension>");
				printStream.println("</extensions>");
			}
			printStream.println("<trkseg>");
			printStream.println();
			for (Point point : trace.getPoints()) {
				printStream.print("<trkpt lat=\""
						+ String.format("%.10f", point.getLatitude()).replace(
								",", ".")
						+ "\" lon=\""
						+ String.format("%.10f", point.getLongitude()).replace(
								",", ".") + "\">");
				if (point.getElevation() != null) {
					printStream.print("<ele>"
							+ String.format("%.2f", point.getElevation())
									.replace(",", ".") + "</ele>");
				}
				if (point.getTime() != null) {
					printStream.print("<time>"
							+ dateFormat.format(point.getTime()) + "</time>");
				}
				if (point.getHeartRate() != null || point.getCadence() != null
						|| point.getTemperature() != null) {
					printStream
							.print("<extensions><gpxtpx:TrackPointExtension>");
					if (point.getTemperature() != null) {
						printStream.print("<gpxtpx:atemp>"
								+ point.getTemperature() + "</gpxtpx:atemp>");
					}
					if (point.getHeartRate() != null) {
						printStream.print("<gpxtpx:hr>" + point.getHeartRate()
								+ "</gpxtpx:hr>");
					}
					if (point.getCadence() != null) {
						printStream.print("<gpxtpx:cad>" + point.getCadence()
								+ "</gpxtpx:cad>");
					}
					printStream
							.print("</gpxtpx:TrackPointExtension></extensions>");
				}
				printStream.println("</trkpt>");
			}
			printStream.println();
			printStream.println("</trkseg>");
			printStream.println("</trk>");
			printStream.println();
		}

		printStream.println("</gpx>");
	}

	private Gpx workLoad(XMLStreamReader xmlStreamReader) throws BaseException {
		Gpx retval = new Gpx();
		try {
			XmlReader xmlReader = new XmlReader(xmlStreamReader);
			boolean openedMetadata = false;
			boolean openedTrk = false;
			boolean openedTrkpt = false;
			Trace trace = null;
			Point point = null;
			while (true) {
				if (xmlStreamReader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
					xmlStreamReader.close();
				} else if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
					XmlReader.StartTag startTag = xmlReader.readCompleteTag();
					if (openedMetadata) {
						if (startTag.getPrefix() == null
								&& "time".equals(startTag.getName())) {
							String value = xmlReader.getTextValue();
							Date time = valueDate(value);
							retval.setTime(time);
						}
					} else if (openedTrk) {
						if (openedTrkpt) {
							if (startTag.getPrefix() == null
									&& "ele".equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								Double elevation = valueDouble(value);
								point.setElevation(elevation);
							} else if (startTag.getPrefix() == null
									&& "time".equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								Date time = valueDate(value);
								point.setTime(time);
							} else if ("gpxtpx".equals(startTag.getPrefix())
									&& "hr".equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								Integer heartRate = valueInteger(value);
								point.setHeartRate(heartRate);
							} else if ("gpxtpx".equals(startTag.getPrefix())
									&& "cad".equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								Integer cadence = valueInteger(value);
								point.setCadence(cadence);
							} else if ("gpxtpx".equals(startTag.getPrefix())
									&& "atemp".equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								Double temperature = valueDouble(value);
								point.setTemperature(temperature);
							}
						} else {
							if (startTag.getPrefix() == null
									&& "name".equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								trace.setName(value);
							} else if ("gpxx".equals(startTag.getPrefix())
									&& "DisplayColor"
											.equals(startTag.getName())) {
								String value = xmlReader.getTextValue();
								trace.setColor(value);
							} else if (startTag.getPrefix() == null
									&& "trkpt".equals(startTag.getName())) {
								openedTrkpt = true;
								point = new Point();
								trace.getPoints().add(point);
								for (XmlReader.Attribute attribute : startTag
										.getAttributes()) {
									if (attribute.getPrefix() == null
											&& "lat".equals(attribute.getName())) {
										Double latitude = valueDouble(attribute
												.getValue());
										point.setLatitude(latitude);
									} else if (attribute.getPrefix() == null
											&& "lon".equals(attribute.getName())) {
										Double longitude = valueDouble(attribute
												.getValue());
										point.setLongitude(longitude);
									}
								}
							}
						}
					} else {
						if (startTag.getPrefix() == null
								&& "gpx".equals(startTag.getName())) {
							for (XmlReader.Attribute attribute : startTag
									.getAttributes()) {
								retval.getAttributes().add(attribute);
							}
						} else if (startTag.getPrefix() == null
								&& "metadata".equals(startTag.getName())) {
							openedMetadata = true;
						} else if (startTag.getPrefix() == null
								&& "trk".equals(startTag.getName())) {
							openedTrk = true;
							trace = new Trace();
							retval.getTraces().add(trace);
						}
					}
				} else if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
					XmlReader.Tag tag = xmlReader.readTag();
					if (tag.getPrefix() == null
							&& "metadata".equals(tag.getName())) {
						openedMetadata = false;
					} else if (tag.getPrefix() == null
							&& "trk".equals(tag.getName())) {
						openedTrk = false;
						trace = null;
					} else if (tag.getPrefix() == null
							&& "trkpt".equals(tag.getName())) {
						openedTrkpt = false;
						point = null;
					}
				}

				if (xmlStreamReader.hasNext()) {
					xmlStreamReader.next();
				} else {
					break;
				}
			}

			for (Trace traceLoc : retval.getTraces()) {
				Collections.sort(traceLoc.getPoints(), comparatorPointsTime);
			}

			return retval;
		} catch (XMLStreamException ex) {
			throw new TechnicalException("Cannot read the xml", ex);
		}
	}

	private void workFormat(XMLStreamReader xmlStreamReader,
			PrintStream printStream) throws BaseException {
		try {
			XmlReader xmlReader = new XmlReader(xmlStreamReader);
			while (true) {
				if (xmlStreamReader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
					printDocumentTag(xmlStreamReader.getVersion(),
							xmlStreamReader.getEncoding(),
							xmlStreamReader.isStandalone(), printStream);
					printStream.println();
				} else if (xmlStreamReader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
					xmlStreamReader.close();
				} else if (xmlStreamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
					XmlReader.StartTag startTag = xmlReader.readCompleteTag();
					if (startTag.getPrefix() == null
							&& "gpx".equals(startTag.getName())) {
						printStartTag(startTag, printStream, true);
						printStream.println();
					} else {
						printStartTag(startTag, printStream, false);
						if (startTag.getPrefix() == null
								&& "trk".equals(startTag.getName())) {
							printStream.println();
						} else if (startTag.getPrefix() == null
								&& "trkseg".equals(startTag.getName())) {
							printStream.println();
						}
					}
				} else if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
					XmlReader.Tag tag = xmlReader.readTag();
					printEndTag(tag, printStream);
					if (tag.getPrefix() == null
							&& "metadata".equals(tag.getName())) {
						printStream.println();
					} else if (tag.getPrefix() == null
							&& "trk".equals(tag.getName())) {
						printStream.println();
					} else if (tag.getPrefix() == null
							&& "trkseg".equals(tag.getName())) {
						printStream.println();
					}
					if (tag.getPrefix() == null
							&& "trkpt".equals(tag.getName())) {
						printStream.println();
					}
				}

				if (xmlStreamReader.hasNext()) {
					xmlStreamReader.next();
				} else {
					break;
				}
			}

		} catch (XMLStreamException ex) {
			throw new TechnicalException("Cannot read the xml", ex);
		}
	}

	private void printDocumentTag(String version, String encoding,
			boolean standalone, PrintStream printStream) {
		printStream.print("<?xml version=\"" + version + "\" encoding=\""
				+ encoding + "\" standalone=\"" + (standalone ? "yes" : "no")
				+ "\" ?>");
	}

	private void printStartTag(XmlReader.StartTag startTag,
			PrintStream printStream, boolean attributePerLine) {
		printStream.print("<");
		if (startTag.getPrefix() != null) {
			printStream.print(startTag.getPrefix() + ":");
		}
		printStream.print(startTag.getName());
		for (XmlReader.Attribute attribute : startTag.getAttributes()) {
			if (attributePerLine) {
				printStream.print("\n    ");
			} else {
				printStream.print(" ");
			}
			if (attribute.getPrefix() != null) {
				printStream.print(attribute.getPrefix() + ":");
			}
			printStream.print(attribute.getName() + "=\""
					+ attribute.getValue() + "\"");
		}
		printStream.print(">");
	}

	private void printEndTag(XmlReader.Tag tag, PrintStream printStream) {
		printStream.print("</");
		if (tag.getPrefix() != null) {
			printStream.print(tag.getPrefix() + ":");
		}
		printStream.print(tag.getName() + ">");
	}

	private static Date valueDate(String value) throws BaseException {
		if (value == null) {
			return null;
		}
		try {
			Date retval = dateFormat.parse(value);
			return retval;
		} catch (ParseException ex) {
			throw new FunctionalException("Not a dade format: " + value, ex);
		}
	}

	private static Double valueDouble(String value) throws BaseException {
		if (value == null) {
			return null;
		}
		try {
			Double retval = Double.parseDouble(value);
			return retval;
		} catch (NumberFormatException ex) {
			throw new FunctionalException("Not a double format: " + value, ex);
		}
	}

	private static Integer valueInteger(String value) throws BaseException {
		if (value == null) {
			return null;
		}
		try {
			Integer retval = Integer.parseInt(value);
			return retval;
		} catch (NumberFormatException ex) {
			throw new FunctionalException("Not a double format: " + value, ex);
		}
	}
}
