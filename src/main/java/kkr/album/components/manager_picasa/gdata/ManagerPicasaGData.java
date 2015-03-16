package kkr.album.components.manager_picasa.gdata;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.data.photos.impl.AlbumDataImpl;
import com.google.gdata.util.AuthenticationException;

import java.text.ParseException;

import kkr.album.components.manager_picasa.ManagerPicasa;
import kkr.album.exception.BaseException;
import kkr.album.exception.FunctionalException;
import kkr.album.exception.TechnicalException;
import kkr.album.utils.UtilsPattern;

public class ManagerPicasaGData extends ManagerPicasaGDataFwk implements
		ManagerPicasa {
	private static final Logger logger = Logger
			.getLogger(ManagerPicasaGData.class);

	private static final Pattern PATTERN_TIMESTAMP = Pattern
			.compile("[12][0-9]{3}[01][0-9][0-3][0-9]-[012][0-9][0-5][0-9][0-5][0-9]");

	public void createOrUpdateAlbum(String name, List<File> photos)
			throws BaseException {
		logger.trace("BEGIN");
		try {
			testConfigured();

			PicasawebService picasawebService = authentificate();

			AlbumEntry albumEntry = findAlbum(picasawebService, name);
			if (albumEntry == null) {
				albumEntry = createAlbum(picasawebService, name);
			}

			// listPhotos(picasawebService, albumEntry, photos);
			updatePhotos(picasawebService, albumEntry, photos);

			logger.trace("OK");
		} finally {
			logger.trace("END");
		}
	}

	private String retrieveAlbumId(AlbumEntry albumEntry) throws BaseException {
		String urlId = albumEntry.getId();
		int iPos = urlId.lastIndexOf("/");
		if (iPos == -1 || iPos + 1 >= urlId.length()) {
			throw new TechnicalException("Cannot retrieve album id from: "
					+ urlId);
		}
		String id = urlId.substring(iPos + 1);
		return id;
	}

	private void updatePhotos(PicasawebService picasawebService,
			AlbumEntry albumEntry, List<File> filePhotos) throws BaseException {
		String albumId = retrieveAlbumId(albumEntry);
		URL url = createUrlPhotos(user, albumId);

		List<File> filePhotosWork = new ArrayList<File>();
		filePhotosWork.addAll(filePhotos);

		AlbumFeed feed = null;
		try {
			feed = picasawebService.getFeed(url, AlbumFeed.class);
		} catch (Exception ex) {
			throw new TechnicalException(
					"Cannot retrieve list of photos from album: "
							+ albumEntry.getName(), ex);
		}
		for (PhotoEntry photoEntry : feed.getPhotoEntries()) {
			String photoEntryName = photoEntry.getTitle().getPlainText();
			Iterator<File> iterator = filePhotosWork.iterator();
			boolean found = false;
			while (iterator.hasNext()) {
				File filePhoto = iterator.next();
				long photoEntrySize;
				try {
					photoEntrySize = photoEntry.getSize();
				} catch (Exception ex) {
					throw new TechnicalException(
							"Cannot retrieve size of photo: " + photoEntryName
									+ " from album: " + albumEntry.getName(),
							ex);
				}
				if (filePhoto.getName().equals(photoEntryName)) {
					if (filePhoto.length() != photoEntrySize) {
						try {
							logger.info("Updating [" + albumEntry.getName() + "]:" + photoEntryName);
							photoEntry.delete();
						} catch (Exception ex) {
							throw new TechnicalException(
									"Cannot delete photo: " + photoEntryName
											+ " from album: "
											+ albumEntry.getName(), ex);
						}
						addPhoto(picasawebService, url, filePhoto);
					}
					iterator.remove();
					found = true;
					break;
				}
			}
			if (!found) {
				try {
					logger.info("Deleting [" + albumEntry.getName() + "]:" + photoEntryName);
					photoEntry.delete();
				} catch (Exception ex) {
					throw new TechnicalException(
							"Cannot delete photo: " + photoEntryName
									+ " from album: "
									+ albumEntry.getName(), ex);
				}
			}
		}

		for (File filePhoto : filePhotosWork) {
			logger.info("Uploading [" + albumEntry.getName() + "]:" + filePhoto.getName());
			addPhoto(picasawebService, url, filePhoto);
		}
	}

	private URL createUrlPhotos(String user, String albumId)
			throws BaseException {
		String urlString = "https://picasaweb.google.com/data/feed/api/user/"
				+ user + "/albumid/" + albumId;
		try {
			URL url = new URL(urlString);
			return url;
		} catch (MalformedURLException ex) {
			throw new TechnicalException("The URL is incorrect: " + urlString,
					ex);
		}
	}

	private URL createUrlAlbums(String user) throws BaseException {
		String urlString = "https://picasaweb.google.com/data/feed/api/user/"
				+ user + "?kind=album";
		try {
			URL url = new URL(urlString);
			return url;
		} catch (MalformedURLException ex) {
			throw new TechnicalException("The URL is incorrect: " + urlString,
					ex);
		}
	}

	private AlbumEntry createAlbum(PicasawebService picasawebService,
			String name) throws BaseException {
		URL url = createUrlAlbums(user);

		Date date = retrieveDateAlbumName(name);
		if (date == null) {
			throw new FunctionalException(
					"The album name does not start by date: " + name);
		}

		AlbumEntry albumEntry = new AlbumEntry();

		albumEntry.setTitle(new PlainTextConstruct(name));
		albumEntry.setAccess("public");
		albumEntry.setDate(date);

		try {
			AlbumEntry albumEntryInserted = picasawebService.insert(url,
					albumEntry);
			return albumEntryInserted;
		} catch (Exception ex) {
			throw new TechnicalException(ex);
		}
	}

	private AlbumEntry findAlbum(PicasawebService picasawebService, String name)
			throws BaseException {
		URL url = createUrlAlbums(user);

		try {
			UserFeed userFeed = picasawebService.getFeed(url, UserFeed.class);
			for (AlbumEntry albumEntry : userFeed.getAlbumEntries()) {
				if (albumEntry.getName().equals(name)) {
					return albumEntry;
				}
			}
		} catch (Exception ex) {
			throw new TechnicalException("Cannot retrieve Album list. URL: "
					+ url.toString(), ex);
		}
		return null;
	}

	private PicasawebService authentificate() throws BaseException {
		PicasawebService picasawebService = new PicasawebService(
				"exampleCo-exampleApp-1");
		try {
			String passwordDecoded = managerPassword.decodePassword(password);
			picasawebService.setUserCredentials(user, passwordDecoded);
		} catch (AuthenticationException ex) {
			throw new TechnicalException(
					"Cannot authentificate the picasa user: " + user, ex);
		}
		return picasawebService;
	}

	private PhotoEntry addPhoto(PicasawebService picasawebService, URL url,
			File filePhoto) throws BaseException {

		Date date = retrieveDatePhotoName(filePhoto.getName());

		PhotoEntry photoEntry = new PhotoEntry();
		photoEntry.setTitle(new PlainTextConstruct(filePhoto.getName()));
		MediaFileSource myMedia = new MediaFileSource(filePhoto, "image/jpeg");
		photoEntry.setMediaSource(myMedia);
		if (date != null) {
			photoEntry.setTimestamp(date);
		}

		try {
			PhotoEntry photoEntryInserted = picasawebService.insert(url,
					photoEntry);
			return photoEntryInserted;
		} catch (Exception ex) {
			throw new TechnicalException("Cannot upload photo: "
					+ filePhoto.getAbsolutePath(), ex);
		}
	}

	private Date retrieveDateAlbumName(String name) throws BaseException {
		try {
			Date date = UtilsPattern.DATE_FORMAT_DATE.parse(name);
			return date;
		} catch (ParseException ex) {
			return null;
		}
	}

	private Date retrieveDatePhotoName(String name) throws BaseException {
		Matcher matcher = PATTERN_TIMESTAMP.matcher(name);

		if (!matcher.find()) {
			return null;
		}

		int iStart = matcher.start();
		int iEnd = matcher.end();

		String timestamp = name.substring(iStart, iEnd);
		try {
			Date date = UtilsPattern.DATE_FORMAT_DATETIME.parse(timestamp);
			return date;
		} catch (ParseException ex) {
			return null;
		}
	}
}
