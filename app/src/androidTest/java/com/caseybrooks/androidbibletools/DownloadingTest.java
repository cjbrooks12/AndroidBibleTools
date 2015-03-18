package com.caseybrooks.androidbibletools;

import android.util.Log;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.io.Download;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public class DownloadingTest extends TestCase {
	public void testDownloadingVerse() throws Throwable {
		Verse verse = Verse.parseVerse("Galatians 2:19", null);
		Document doc1 = Download.bibleChapter(
				PrivateKeys.API_KEY,
				verse.getBible().getVersionId(),
				verse.getReference().book.getId(),
				verse.getReference().chapter);
		verse.loadFromServer(doc1);

		if(verse.getText().length() == 0) throw new Exception("Downloaded nothing");
		Log.i("downloading Galatians 2:20", verse.getReference().toString() + " - " + verse.getText());

		Passage passage = Passage.parsePassage("Galatians 2:19-21", null);
		Document doc2 = Download.bibleChapter(
				PrivateKeys.API_KEY,
				verse.getBible().getVersionId(),
				verse.getReference().book.getId(),
				verse.getReference().chapter);
		passage.loadFromServer(doc2);

		if(passage.getText().length() == 0) throw new Exception("Downloaded nothing");
		Log.i("downloading Galatians 2:19-21", passage.getReference().toString() + " - " + passage.getText());
	}

	public void testGettingVersionsList() throws Throwable {
		HashMap<String, String> availableLanguages =
				Bible.getAvailableLanguages(Download.availableVersions(PrivateKeys.API_KEY, null));

		assertNotNull(availableLanguages);
		Log.i("Number of languages available", availableLanguages.size() + " languages");

		String langKey = "English (US)";
		if(availableLanguages.containsKey(langKey)) {

			HashMap<String, Bible> versionsList =
					Bible.getAvailableVersions(
							Download.availableVersions(PrivateKeys.API_KEY, availableLanguages.get(langKey)));

			assertNotNull(versionsList);
			Log.i("Number of versions in language", versionsList.size() + " versions in " + availableLanguages.get(langKey));

			Bible esv = versionsList.get("esv");
			assertNotNull(esv);

//			esv.downloadVersionInfo(PrivateKeys.API_KEY);
//			Log.i("ESV Copyright", esv.books.size() + " books " + esv.copyright);
		}
	}
}
