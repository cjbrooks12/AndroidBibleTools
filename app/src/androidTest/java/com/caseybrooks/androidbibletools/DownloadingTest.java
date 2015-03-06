package com.caseybrooks.androidbibletools;

import android.util.Base64;
import android.util.Log;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DownloadingTest extends TestCase {
	public String API_KEY = "9vso4MjssaJe27tn3uqahfYZsUUU8T5XGK3iOr9g";

	public String testURL = "https://" + API_KEY + ":bibles.org/v2/versions/eng-GNTD.xml";
	public String testURL2 = "http://9vso4MjssaJe27tn3uqahfYZsUUU8T5XGK3iOr9g:x@bibles.org/v2/versions/eng-GNTD.xml";

	public void testDownloadingVerse() throws Throwable {

		String headerValue = "" + API_KEY + ":x";

		// Sending side
		byte[] data = headerValue.getBytes("UTF-8");
		String base64 = Base64.encodeToString(data, Base64.DEFAULT);

		Document doc = Jsoup.connect(testURL2).header("Authorization", "Basic " + base64).get();

		assertNotNull(doc);

		Log.i("RESPONSE", doc.toString());
	}
}
