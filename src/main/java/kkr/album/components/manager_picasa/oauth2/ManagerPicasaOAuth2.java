package kkr.album.components.manager_picasa.oauth2;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.samples.calendar.cmdline.CalendarSample;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.youtube.VideoFeed;

import kkr.album.components.manager_picasa.ManagerPicasa;
import kkr.album.exception.BaseException;

public class ManagerPicasaOAuth2 extends ManagerPicasaOAuth2Fwk implements
		ManagerPicasa {
	private static final Logger logger = Logger
			.getLogger(ManagerPicasaOAuth2.class);

	// https://groups.google.com/forum/#!topic/google-picasa-data-api/ox3VOeLKk7k

	// https://console.developers.google.com/apis/credentials?project=kkr-albums
	private static final String CLIENT_ID = "290423829898-l747mj53a5f25ol5f5pt540696i3ceag.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "lBn0B-9tjjxaDwK2yR4ReL2W";

	public void createOrUpdateAlbum(String name, List<File> photos)
			throws BaseException {
		// TODO Auto-generated method stub

	}

	// https://developers.google.com/api-client-library/java/google-api-java-client/reference/1.20.0/com/google/api/client/googleapis/auth/oauth2/GoogleCredential
	
	private static class Config {
		static String GOOGLE_CLIENT_ID = "290423829898-l747mj53a5f25ol5f5pt540696i3ceag.apps.googleusercontent.com";
		static String GOOGLE_CLIENT_SECRET = "lBn0B-9tjjxaDwK2yR4ReL2W";
		static String GOOGLE_SCOPE_PICASA = "https://picasaweb.google.com/data/";
		static String GOOGLE_OAUTH_REFRESH_TOKEN = "";
		static String GOOGLE_REDIRECT_URI = "http://www.google.com";
		static String GOOGLE_OAUTH_REFRESH_TOKEN_FILE;
		static String GOOGLE_TOKEN_SERVER_URL = GoogleOAuthConstants.TOKEN_SERVER_URL;
		static String PICASAWEB_LOGIN = "karel.kralovec.2015@gmail.com";
		static String PICASAWEB_ALBUM_ID = "AAA";

	}

	public static String upload(String fullpathToImage) {
		// The url of the image
		String resultingURL = new String();

		try {
			String GOOGLE_REFRESH_TOKEN = "";
			String GOOGLE_ACCESS_TOKEN = "";

			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();
			TokenResponse tokenResponse = new TokenResponse();

			// Check of we have a previous Refresh Token cached
			if (Config.GOOGLE_OAUTH_REFRESH_TOKEN.length() == 0) {
				// No Google OAuth2 Key has been previously cached

				GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow(
						httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
						Arrays.asList(Config.GOOGLE_SCOPE_PICASA));

				AuthorizationCodeFlow.Builder codeFlowBuilder = new GoogleAuthorizationCodeFlow.Builder(
						httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
						Arrays.asList(Config.GOOGLE_SCOPE_PICASA));

				AuthorizationCodeFlow codeFlow = codeFlowBuilder.build();
				AuthorizationCodeRequestUrl authorizationUrl = codeFlow
						.newAuthorizationUrl();
				authorizationUrl.setRedirectUri(Config.GOOGLE_REDIRECT_URI);

				System.out.println("Go to the following address:\n"
						+ authorizationUrl);
				System.out.println("What is the 'code' url parameter?");
				String code = new Scanner(System.in).nextLine();

				// Use the code returned by the Google Authentication Server to
				// generate an Access Code
				AuthorizationCodeTokenRequest tokenRequest = codeFlow
						.newTokenRequest(code);
				tokenRequest.setRedirectUri(Config.GOOGLE_REDIRECT_URI);
				tokenResponse = tokenRequest.execute();

				GOOGLE_REFRESH_TOKEN = tokenResponse.getRefreshToken();
				GOOGLE_ACCESS_TOKEN = tokenResponse.getAccessToken();

				// Store the Refresh Token for later usage (this avoid having to
				// request the user to
				// Grant access to the application via the webbrowser again
				saveTextFile(Config.GOOGLE_OAUTH_REFRESH_TOKEN_FILE,
						GOOGLE_REFRESH_TOKEN);
			} else {
				// There is a Google OAuth2 Key cached previously.
				// Use the refresh token to get a new Access Token

				// Get the cached Refresh Token
				GOOGLE_REFRESH_TOKEN = new String(
						Config.GOOGLE_OAUTH_REFRESH_TOKEN);

				// Now we need to get a new Access Token using our previously
				// cached Refresh Token
				RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(
						httpTransport, jsonFactory, new GenericUrl(
								Config.GOOGLE_TOKEN_SERVER_URL),
						GOOGLE_REFRESH_TOKEN);

				refreshTokenRequest
						.setClientAuthentication(new BasicAuthentication(
								Config.GOOGLE_CLIENT_ID,
								Config.GOOGLE_CLIENT_SECRET));
				refreshTokenRequest.setScopes(Arrays
						.asList(Config.GOOGLE_SCOPE_PICASA));

				tokenResponse = refreshTokenRequest.execute();

				// Get and set the Refresn the Access Tokens
				GOOGLE_ACCESS_TOKEN = new String(tokenResponse.getAccessToken());
			}

			// At this point we have a valid Google Access Token
			// Let us access Picasa then!
			GoogleCredential credential = new GoogleCredential.Builder()
					.setClientSecrets(CLIENT_ID,
							CLIENT_SECRET)
					.setJsonFactory(jsonFactory).setTransport(httpTransport)
					.build().setAccessToken(GOOGLE_ACCESS_TOKEN)
					.setRefreshToken(GOOGLE_REFRESH_TOKEN);

			PicasawebService picasaWebSvc = new PicasawebService(
					"GOOGLE_APP_NAME");
			picasaWebSvc.setOAuth2Credentials(credential);

			URL feedUrl = new URL(
					"https://picasaweb.google.com/data/feed/api/user/"
							+ Config.PICASAWEB_LOGIN + "/albumid/"
							+ Config.PICASAWEB_ALBUM_ID);

			MediaFileSource myMedia = new MediaFileSource(new File(
					fullpathToImage), "image/jpeg");
			PhotoEntry returnedPhoto = picasaWebSvc.insert(feedUrl,
					PhotoEntry.class, myMedia);

			resultingURL = returnedPhoto.getMediaContents().get(0).getUrl();

			if (resultingURL.toLowerCase().contains(
					"please check your firewall")
					|| resultingURL.toLowerCase().contains("error")) {
				throw new Exception(
						"The Windows Firewall seems to be blocking the upload...");
			}

			System.out.println("...DONE!");
			System.out.println("Cover page URL: " + resultingURL);
		} catch (Exception ex) {
			System.err
					.println("[BloggerImageUploader.java]: There was an error: "
							+ ex.getMessage());
			ex.printStackTrace(System.err);
			return "";
		}

		// The result
		return resultingURL;
	}

	private static void saveTextFile(String a, String b) {

	}
	/** Authorizes the installed application to access user's protected data. */
	/*
	 * private static Credential authorize() throws Exception { // load client
	 * secrets GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
	 * JSON_FACTORY, new InputStreamReader(CalendarSample.class
	 * .getResourceAsStream("/client_secrets.json"))); // set up authorization
	 * code flow GoogleAuthorizationCodeFlow flow = new
	 * GoogleAuthorizationCodeFlow.Builder( httpTransport, JSON_FACTORY,
	 * clientSecrets, Collections.singleton(CalendarScopes.CALENDAR))
	 * .setDataStoreFactory(dataStoreFactory).build(); // authorize return new
	 * AuthorizationCodeInstalledApp(flow, new
	 * LocalServerReceiver()).authorize("user"); }
	 */

	public static final void main(String[] argv) {
		ManagerPicasaOAuth2 managerPicasaOAuth2 = new ManagerPicasaOAuth2();
		managerPicasaOAuth2.upload("d:/tmp/TMB/TIMES.jpg");
	}
}
