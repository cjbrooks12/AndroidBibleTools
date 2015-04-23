package com.caseybrooks.androidbibletools;

import android.util.Log;

import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.io.Download;
import com.caseybrooks.androidbibletools.io.PrivateKeys;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public class BookCountTest extends TestCase {
	public void testNumberOfBooksInEachVersion() throws Throwable {

		HashMap<String, Bible> availableVersions = Bible.getAvailableVersions(
				Download.availableVersions(PrivateKeys.API_KEY, null)
		);

		int[] versionChapterCounts = new int[100];

		for(String id : availableVersions.keySet()) {
			Document doc = Download.versionInfo(PrivateKeys.API_KEY, id);

			Bible bible = new Bible(id);
			bible.getVersionInfo(doc);

			versionChapterCounts[bible.books.size()]++;
		}

		for(int i = 0; i < versionChapterCounts.length; i++) {
			Log.i("Chapter count", "(" + i + ") " + versionChapterCounts[i]);
		}
	}

}
