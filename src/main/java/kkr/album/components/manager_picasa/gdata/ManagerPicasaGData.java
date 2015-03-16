package kkr.album.components.manager_picasa.gdata;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.util.AuthenticationException;

import kkr.album.components.manager_picasa.ManagerPicasa;
import kkr.album.exception.BaseException;
import kkr.album.exception.TechnicalException;

public class ManagerPicasaGData extends ManagerPicasaGDataFwk implements
		ManagerPicasa {
	private static final Logger logger = Logger
			.getLogger(ManagerPicasaGData.class);

	private static final String KEY_USER = "(USER)";
	private static final String URL_ALBUMS = "https://picasaweb.google.com/data/feed/api/user/"
			+ KEY_USER + "?kind=album";

	public void createAlbum(String name, List<File> photos)
			throws BaseException {
		logger.trace("BEGIN");
		try {
			PicasawebService picasawebService = authentificate();

			String url = URL_ALBUMS.replace(KEY_USER, user);
			URL feedUrl = null;
			try {
				feedUrl = new URL(url);
			} catch (MalformedURLException ex) {
				throw new TechnicalException("The URL is incorrect: " + url, ex);
			}

			AlbumEntry albumEntry = new AlbumEntry();

			albumEntry.setTitle(new PlainTextConstruct("AAA_Test"));
			albumEntry.setDescription(new PlainTextConstruct(
					"AAA_Test_Description"));
			albumEntry.setAccess("public");
			Date date = new Date(new Date().getTime() - 86400000);
			albumEntry.setDate(date);

			AlbumEntry albumEntryInserted = null;
			try {
				albumEntryInserted = picasawebService.insert(feedUrl,
						albumEntry);
			} catch (Exception ex) {
				throw new TechnicalException(ex);
			}

			logger.trace("OK");
		} finally {
			logger.trace("END");
		}
	}

	private PicasawebService authentificate() throws BaseException {
		PicasawebService myService = new PicasawebService(
				"exampleCo-exampleApp-1");
		try {
			String passwordDecoded = managerPassword.decodePassword(password);
			myService.setUserCredentials(user, passwordDecoded);
		} catch (AuthenticationException ex) {
			throw new TechnicalException(
					"Cannot authentificate the picasa user: " + user, ex);
		}
		return myService;
	}

	private static void addPhotos(PicasawebService myService) throws Exception {
		URL albumPostUrl = new URL(
				"https://picasaweb.google.com/data/feed/api/user/103209144468244685151/albumid/6126591751368282913");

		PhotoEntry myPhoto = new PhotoEntry();
		myPhoto.setTitle(new PlainTextConstruct("Puppies FTW"));
		myPhoto.setDescription(new PlainTextConstruct(
				"Puppies are the greatest."));
		myPhoto.setClient("myClientName");

		MediaFileSource myMedia = new MediaFileSource(new File(
				"d:\\media\\traces\\2015\\albums\\AAA_Test\\00038636n_20150315-114421.jpg"), "image/jpeg");
		myPhoto.setMediaSource(myMedia);

		PhotoEntry returnedPhoto = myService.insert(albumPostUrl, myPhoto);
	}

}
