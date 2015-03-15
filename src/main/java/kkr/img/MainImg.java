package kkr.img;

import java.net.URL;

import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.client.photos.*;
import com.google.gdata.data.*;
import com.google.gdata.data.photos.*;

public class MainImg {

	public static final void main(String[] args) throws Exception {
		PicasawebService myService = new PicasawebService("exampleCo-exampleApp-1");
		myService.setUserCredentials("karel.kralovec.2014@gmail.com", "tjmjhs12");
		
		String requestUrl =
			    AuthSubUtil.getRequestUrl("http://www.example.com/RetrieveToken",
			                        "https://picasaweb.google.com/data/",
			                        false,
			                        true);
		
		//String sessionToken = AuthSubUtil.exchangeForSessionToken(requestUrl, null);
		
		//myService.setAuthSubToken(sessionToken, null);
		
		URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/karel.kralovec.2014?kind=album");

		UserFeed myUserFeed = myService.getFeed(feedUrl, UserFeed.class);

		for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
		    System.out.println(myAlbum.getTitle().getPlainText());
		}
		
		AlbumEntry myAlbum = new AlbumEntry();

		myAlbum.setTitle(new PlainTextConstruct("Trip to France"));
		myAlbum.setDescription(new PlainTextConstruct("My recent trip to France was delightful!"));

		// AlbumEntry insertedEntry = myService.insert(postUrl, myAlbum);
	}
}
