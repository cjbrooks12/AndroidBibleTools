package com.caseybrooks.androidbibletools;

import android.util.Log;

import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.io.Download;
import com.caseybrooks.androidbibletools.io.PrivateKeys;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public class BookCountTest extends TestCase {
	//---------------------------------
	//---------------------------------
	//----BE CAREFUL WITH THIS TEST----
	//---------------------------------
	//---------------------------------
	//This test was just to gain knownledge about the versions available
	//by providing a histogram of the number of books in every available Bible,
	//and it takes a very long time to run (i think it took me 15+ minutes).
	//It was not designed to test any functionality, but rather to help me understand
	//more about the data this library consumes. In the end, it showed me that the
	//majority of available Bibles are New Testament only, and the ordering of each
	//NT book starts at 0, so to compare a book in one of the NT only languages to
	//the full Bible versions, we must add 39 to the order (the number of books in
	//the Old Testament)
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
