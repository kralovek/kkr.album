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
import java.util.HashMap;
import java.util.List;
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
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.constants.TiffDirectoryConstants;
import org.apache.sanselan.formats.tiff.fieldtypes.FieldType;
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
				LOGGER.warn("### The file does not contain JPEG METADATA: "
						+ file.getAbsolutePath() + ". Metadata: "
						+ imageMetadata.getClass().getName());
				LOGGER.trace("OK");
				return null;
			}

			JpegImageMetadata jpegImageMetadata = (JpegImageMetadata) imageMetadata;

			TiffImageMetadata tiffImageMetadata = jpegImageMetadata.getExif();

			TagInfo tagInfoCreationDate = TiffConstants.EXIF_TAG_CREATE_DATE;
			TiffField tiffFieldCreationDate = tiffImageMetadata
					.findField(tagInfoCreationDate);

			if (tiffFieldCreationDate == null) {
				TagInfo tagInfoModifyDate = TiffConstants.EXIF_TAG_MODIFY_DATE;
				tiffFieldCreationDate = tiffImageMetadata
						.findField(tagInfoModifyDate);
				if (tiffFieldCreationDate == null) {
					LOGGER.debug("No CreationDate in the file: "
							+ file.getAbsolutePath());
					LOGGER.trace("OK");
					return null;
				} else {
					LOGGER.warn("Using ModificationDate because there is not any CreationDate: "
							+ file.getAbsolutePath());
				}
			}

			TagInfo tagInfoTimeZoneOffset = TiffConstants.EXIF_TAG_TIME_ZONE_OFFSET;
			TiffField tiffFieldTimeZoneOffset = tiffImageMetadata
					.findField(tagInfoTimeZoneOffset);

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

	public void modifyFile(File file, Date date) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			modifyFile(file, date, null, null);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	public void modifyFile(File file, Date date, Double longitude,
			Double latitude) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();

			Map<String, String> parameters = new HashMap<String, String>();

			LOGGER.debug("Modifying file: " + file.getAbsolutePath());
			LOGGER.debug("Date: "
					+ DATE_FORMAT.format(date)
					+ " Lon: "
					+ longitude
					+ " Lat: "
					+ latitude
					+ (parameters != null && !parameters.isEmpty() ? " Parameters: "
							+ toStringParameters(parameters)
							: ""));

			IImageMetadata imageMetadata = null;
			try {
				imageMetadata = Sanselan.getMetadata(file);
			} catch (IOException ex) {
				throw new TechnicalException("Cannot read the file: "
						+ file.getAbsolutePath(), ex);
			}

			if (imageMetadata == null
					|| !(imageMetadata instanceof JpegImageMetadata)) {
				LOGGER.warn("\t### The file has not JPEG Metadata: "
						+ file.getAbsolutePath()
						+ (imageMetadata != null ? ". Metadata: "
								+ imageMetadata.getClass().getName() : ""));
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

			if (parameters != null && !parameters.isEmpty()) {
				modifyParameters(tiffOutputSet, parameters);
			}
			File fileTmp = new File(file.getParentFile(), file.getName()
					+ ".tmp");
			writeFile(file, fileTmp, tiffOutputSet);

			if (!file.delete()) {
				throw new TechnicalException("Cannot remove the file: "
						+ file.getAbsolutePath());
			}

			if (!fileTmp.renameTo(file)) {
				throw new TechnicalException("Cannot rename the file: "
						+ fileTmp.getAbsolutePath() + " to: "
						+ file.getAbsolutePath());
			}

			file.setLastModified(date.getTime());

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

	private void modifyParameters(TiffOutputSet outputSet,
			Map<String, String> parameters) throws ImageWriteException {
		LOGGER.trace("BEGIN");
		try {
			TiffOutputDirectory exifDir = outputSet
					.findDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_EXIF);

			exifDir.removeField(ExifTagConstants.EXIF_TAG_ARTIST);

			TiffOutputField fieldCreated = new TiffOutputField(
					ExifTagConstants.EXIF_TAG_ARTIST,
					FieldType.FIELD_TYPE_ASCII, "Karel Kralovec".length(),
					"Karel Kralovec".getBytes());

			// TiffOutputField field = TiffOutputField.create(
			// ExifTagConstants.EXIF_TAG_ARTIST, 1, "Karel Kralovec");

			exifDir.add(fieldCreated);

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

	private String toStringParameters(Map<String, String> parameters) {
		StringBuffer buffer = new StringBuffer();
		if (parameters != null) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				if (buffer.length() != 0) {
					buffer.append(",");
				}
				buffer.append(entry.getKey()).append("=")
						.append(entry.getValue());
			}
		}
		return buffer.toString();
	}

	public void copyExif(File fileSource, File fileTarget) throws BaseException {
		LOGGER.trace("BEGIN");
		try {
			testConfigured();
			copyExifData(fileSource, fileTarget, null);
			LOGGER.trace("OK");
		} finally {
			LOGGER.trace("END");
		}
	}

	private static boolean copyExifData(File sourceFile, File destFile,
			List<TagInfo> excludedFields) {
		String tempFileName = destFile.getAbsolutePath() + ".tmp";
		File tempFile = null;
		OutputStream tempStream = null;

		try {
			tempFile = new File(tempFileName);

			TiffOutputSet sourceSet = getSanselanOutputSet(sourceFile,
					TiffConstants.DEFAULT_TIFF_BYTE_ORDER);
			TiffOutputSet destSet = getSanselanOutputSet(destFile,
					sourceSet.byteOrder);

			// If the EXIF data endianess of the source and destination files
			// differ then fail. This only happens if the source and
			// destination images were created on different devices. It's
			// technically possible to copy this data by changing the byte
			// order of the data, but handling this case is outside the scope
			// of this implementation
			if (sourceSet.byteOrder != destSet.byteOrder)
				return false;

			destSet.getOrCreateExifDirectory();

			// Go through the source directories
			List<?> sourceDirectories = sourceSet.getDirectories();
			for (int i = 0; i < sourceDirectories.size(); i++) {
				TiffOutputDirectory sourceDirectory = (TiffOutputDirectory) sourceDirectories
						.get(i);
				TiffOutputDirectory destinationDirectory = getOrCreateExifDirectory(
						destSet, sourceDirectory);

				if (destinationDirectory == null)
					continue; // failed to create

				// Loop the fields
				List<?> sourceFields = sourceDirectory.getFields();
				for (int j = 0; j < sourceFields.size(); j++) {
					// Get the source field
					TiffOutputField sourceField = (TiffOutputField) sourceFields
							.get(j);

					// Check exclusion list
					if (excludedFields != null
							&& excludedFields.contains(sourceField.tagInfo)) {
						destinationDirectory.removeField(sourceField.tagInfo);
						continue;
					}

					// Remove any existing field
					destinationDirectory.removeField(sourceField.tagInfo);

					// Add field
					destinationDirectory.add(sourceField);
				}
			}

			// Save data to destination
			tempStream = new BufferedOutputStream(
					new FileOutputStream(tempFile));
			new ExifRewriter().updateExifMetadataLossless(destFile, tempStream,
					destSet);
			tempStream.close();

			// Replace file
			if (destFile.delete()) {
				tempFile.renameTo(destFile);
			}

			return true;
		} catch (ImageReadException exception) {
			exception.printStackTrace();
		} catch (ImageWriteException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			if (tempStream != null) {
				try {
					tempStream.close();
				} catch (IOException e) {
				}
			}

			if (tempFile != null) {
				if (tempFile.exists())
					tempFile.delete();
			}
		}

		return false;
	}

	private static TiffOutputSet getSanselanOutputSet(File jpegImageFile,
			int defaultByteOrder) throws IOException, ImageReadException,
			ImageWriteException {
		TiffImageMetadata exif = null;
		TiffOutputSet outputSet = null;

		IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
		JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
		if (jpegMetadata != null) {
			exif = jpegMetadata.getExif();

			if (exif != null) {
				outputSet = exif.getOutputSet();
			}
		}

		// If JPEG file contains no EXIF metadata, create an empty set
		// of EXIF metadata. Otherwise, use existing EXIF metadata to
		// keep all other existing tags
		if (outputSet == null)
			outputSet = new TiffOutputSet(exif == null ? defaultByteOrder
					: exif.contents.header.byteOrder);

		return outputSet;
	}

	private static TiffOutputDirectory getOrCreateExifDirectory(
			TiffOutputSet outputSet, TiffOutputDirectory outputDirectory) {
		TiffOutputDirectory result = outputSet
				.findDirectory(outputDirectory.type);
		if (result != null)
			return result;
		result = new TiffOutputDirectory(outputDirectory.type);
		try {
			outputSet.addDirectory(result);
		} catch (ImageWriteException e) {
			return null;
		}
		return result;
	}

	public static void main(String[] argv) throws Exception {
		LOGGER.trace("BEGIN");
		try {
			ManagerExifSanselan main = new ManagerExifSanselan();
			main.config();

			File fileSource = new File("h:/tmp/01/00035773o_20140603-192248.jpg");
			File fileTarget = new File("h:/tmp/01/00035773n_20140603-192248.jpg");
			
			main.copyExif(fileSource, fileTarget);
			
			LOGGER.trace("OK");
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			LOGGER.trace("END");
		}
	}

}
