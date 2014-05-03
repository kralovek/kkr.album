package kkr.album.components.manager_exif;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsFile;

public class ManagerExifSanselan extends ManagerExifSanselanFwk implements
		ManagerExif {
	private static final Logger LOGGER = Logger
			.getLogger(ManagerExifSanselan.class);

	private static final DateFormat DATE_FORMAT;
	static {
		DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public Date determineDate(File file) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			IImageMetadata imageMetadata = null;
			try {
				imageMetadata = Sanselan.getMetadata(file);
			} catch (IOException ex) {
				throw new TechnicalException("Cannot read the file: "
						+ file.getAbsolutePath(), ex);
			}

			if (!(imageMetadata instanceof JpegImageMetadata)) {
				LOGGER.warn("The file is not a JPEG file: "
						+ file.getAbsolutePath());
				LOGGER.trace("OK");
				return null;
			}

			JpegImageMetadata jpegImageMetadata = (JpegImageMetadata) imageMetadata;

			TiffImageMetadata tiffImageMetadata = jpegImageMetadata.getExif();

			TagInfo tagInfoCreationDate = TiffConstants.EXIF_TAG_CREATE_DATE;
			TiffField tiffFieldCreationDate = tiffImageMetadata
					.findField(tagInfoCreationDate);

			TagInfo tagInfoTimeZoneOffset = TiffConstants.EXIF_TAG_TIME_ZONE_OFFSET;
			TiffField tiffFieldTimeZoneOffset = tiffImageMetadata
					.findField(tagInfoTimeZoneOffset);

			if (tiffFieldCreationDate == null) {
				LOGGER.debug("No CreationDate in the file: "
						+ file.getAbsolutePath());
				LOGGER.trace("OK");
				return null;
			}

			String stringValueCD = tiffFieldCreationDate.getStringValue();

			String stringValueTZO = tiffFieldTimeZoneOffset != null ? tiffFieldTimeZoneOffset
					.getStringValue() : null;

			try {
				Date date = DATE_FORMAT.parse(stringValueCD);
				LOGGER.debug("CreationDate: " + DATE_FORMAT.format(date));
				LOGGER.trace("OK");
				return date;
			} catch (ParseException ex) {
				LOGGER.warn("Bad format of CreationDate in the file: "
						+ file.getAbsolutePath() + ": " + stringValueCD);
				LOGGER.trace("OK");
				return null;
			}

		} catch (ImageReadException ex) {
			throw new TechnicalException("Cannot read the image: "
					+ file.getAbsolutePath(), ex);
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, Date date, Double longitude,
			Double latitude, Map<String, String> parameters)
			throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			IImageMetadata imageMetadata = null;
			try {
				imageMetadata = Sanselan.getMetadata(file);
			} catch (IOException ex) {
				throw new TechnicalException("Cannot read the file: "
						+ file.getAbsolutePath(), ex);
			}

			if (!(imageMetadata instanceof JpegImageMetadata)) {
				LOGGER.warn("The file is not a JPEG file: "
						+ file.getAbsolutePath());
				LOGGER.trace("OK");
				return;
			}

			JpegImageMetadata jpegImageMetadata = (JpegImageMetadata) imageMetadata;

			TiffImageMetadata tiffImageMetadata = jpegImageMetadata.getExif();

			TiffOutputSet tiffOutputSet = tiffImageMetadata.getOutputSet();

			if (date != null) {
				modifyDate(tiffOutputSet, date);
			}

			if (longitude != null && latitude != null) {
				modifyGPS(tiffOutputSet, longitude, latitude);
			}

			File fileTmp = new File(file.getParentFile(), file.getName() + ".tmp");
			writeFile(file, fileTmp, tiffOutputSet);
			
			if (file.delete()) {
				throw new TechnicalException("Cannot remove the file: "
						+ file.getAbsolutePath());
			}
			
			if (fileTmp.renameTo(file)) {
				throw new TechnicalException("Cannot rename the file: " + fileTmp.getAbsolutePath() + " to: "
						+ file.getAbsolutePath());
			}
			
			LOGGER.trace("OK");
		} catch (ImageWriteException ex) {
			throw new TechnicalException("Cannot write to the image: "
					+ file.getAbsolutePath(), ex);
		} catch (ImageReadException ex) {
			throw new TechnicalException("Cannot read the image: "
					+ file.getAbsolutePath(), ex);
		} finally {
			LOGGER.trace("END");
		}
	}

	private void writeFile(File fileSource, File fileTarget,
			TiffOutputSet tiffOutputSet) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			OutputStream os = null;
			BufferedOutputStream bos = null;
			try {
				os = new FileOutputStream(fileTarget);
				bos = new BufferedOutputStream(os);

				new ExifRewriter().updateExifMetadataLossless(fileSource, bos,
						tiffOutputSet);

				bos.close();
				os.close();
			} catch (IOException ex) {
				throw new TechnicalException("Cannot write data to the file: "
						+ fileTarget.getAbsolutePath(), ex);
			} catch (ImageReadException ex) {
				throw new TechnicalException("Cannot read data to the file: "
						+ fileSource.getAbsolutePath(), ex);
			} catch (ImageWriteException ex) {
				throw new TechnicalException("Cannot write data to the file: "
						+ fileTarget.getAbsolutePath(), ex);
			} finally {
				UtilsFile.closeRessource(bos);
				UtilsFile.closeRessource(os);
			}

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void modifyGPS(TiffOutputSet tiffOutputSet, double longitude,
			double latitude) throws ImageWriteException {
		LOGGER.trace("BEGIN");
		try {
			tiffOutputSet.setGPSInDegrees(longitude, latitude);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private void modifyDate(TiffOutputSet tiffOutputSet, Date date)
			throws ImageWriteException {
		LOGGER.trace("BEGIN");
		try {
			String dateString = DATE_FORMAT.format(date);

			TiffOutputField tiffOutputFieldCreationDate = new TiffOutputField(
					TiffConstants.EXIF_TAG_CREATE_DATE,
					TiffConstants.FIELD_TYPE_ASCII, dateString.length(),
					dateString.getBytes());
			TiffOutputField tiffOutputFieldDateTimeOriginal = new TiffOutputField(
					TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL,
					TiffConstants.FIELD_TYPE_ASCII, dateString.length(),
					dateString.getBytes());

			TiffOutputDirectory exifDirectory = tiffOutputSet
					.getOrCreateExifDirectory();

			exifDirectory.removeField(TiffConstants.EXIF_TAG_CREATE_DATE);
			exifDirectory
					.removeField(TiffConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
			exifDirectory.add(tiffOutputFieldCreationDate);
			exifDirectory.add(tiffOutputFieldDateTimeOriginal);

			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public static void main(String[] argv) throws Exception {
		LOGGER.trace("BEGIN");
		try {
			ManagerExifSanselan main = new ManagerExifSanselan();
			main.config();

			// File file = new File("00035413n.JPG");
			File file = new File("image.jpeg");

			Date date = main.determineDate(file);

			LOGGER.debug("date: " + date);

			Date newDate = new Date(date.getTime() + 1000L);

			main.modifyFile(file, newDate, 5.56, 42.18, null);

			File file2 = new File("image2.jpeg");

			Date dateMod = main.determineDate(file2);
			LOGGER.debug("dateMod: " + dateMod);

			LOGGER.trace("OK");
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			LOGGER.trace("END");
		}
	}
}
