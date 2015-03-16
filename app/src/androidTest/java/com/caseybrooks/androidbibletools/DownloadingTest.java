package com.caseybrooks.androidbibletools;

import android.util.Log;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.data.Version;
import com.caseybrooks.androidbibletools.enumeration.VersionEnum;

import junit.framework.TestCase;

import java.util.HashMap;

public class DownloadingTest extends TestCase {
	public void testDownloadingVerse() throws Throwable {
		Verse verse = new Verse("Galatians 2:19");
		verse.setVersion(VersionEnum.ESV);
		verse.retrieve(PrivateKeys.API_KEY);

		if(verse.getText().length() == 0) throw new Exception("Downloaded nothing");
		Log.i("downloading Galatians 2:20", verse.getReference().toString() + " - " + verse.getText());

		Passage passage = new Passage("Galatians 2:19-21");
		passage.setVersion(VersionEnum.ESV);
		passage.retrieve(PrivateKeys.API_KEY);

		if(passage.getText().length() == 0) throw new Exception("Downloaded nothing");
		Log.i("downloading Galatians 2:19-21", passage.getReference().toString() + " - " + passage.getText());
	}

	public void testGettingVersionsList() throws Throwable {
		HashMap<String, String> availableLanguages = Version.getAvailableLanguages(PrivateKeys.API_KEY);
		assertNotNull(availableLanguages);
		Log.i("Number of languages available", availableLanguages.size() + " languages");

		String langKey = "English (US)";
		if(availableLanguages.containsKey(langKey)) {

			HashMap<String, Version> versionsList =
					Version.getAvailableVersions(
							PrivateKeys.API_KEY,
							availableLanguages.get(langKey));

			assertNotNull(versionsList);
			Log.i("Number of versions in language", versionsList.size() + " versions in " + availableLanguages.get(langKey));

			Version esv = versionsList.get("esv");
			assertNotNull(esv);

			esv.downloadVersionInfo(PrivateKeys.API_KEY);
			Log.i("ESV Copyright", esv.books.size() + " books " + esv.copyright);
		}
	}
}
