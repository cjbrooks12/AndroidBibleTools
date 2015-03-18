package com.caseybrooks.androidbibletools.io;

import android.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Casey on 3/18/2015.
 */
public class Download {

	/**
	 * Query the server for a list of all available Versions. Leaving the languageAbbr
	 * parameter null will return a list of Versions in all languages, or else will
	 * return all versions in the specified language.
	 *
	 * @param APIKey required free API key from Bibles.org/api
	 * @param languageAbbr optional: the ISO 639-2 abbreviation to get all versions of a specific language. Leave null to get versions in all languages
	 * @return a Jsoup Document to be parsed or cached
	 * @throws IOException
	 *
	 * @see com.caseybrooks.androidbibletools.data.Bible#getAvailableVersions(org.jsoup.nodes.Document)
	 * @see com.caseybrooks.androidbibletools.data.Bible#getAvailableLanguages(org.jsoup.nodes.Document)
	 */
	public static Document availableVersions(String APIKey, String languageAbbr) throws IOException {
		String url = "http://" + APIKey + ":x@bibles.org/v2/versions.xml";
		if(languageAbbr != null) { url += "?language=" + languageAbbr; }

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
	}

	/**
	 *
	 * @param APIKey required free API key from Bibles.org/api
	 * @param versionId id of the bible to download text in
	 * @param bookId id of the book
	 * @param chapter chapter number
	 * @return a Jsoup Document to be parsed or cached
	 * @throws IOException
	 *
	 * @see com.caseybrooks.androidbibletools.basic.AbstractVerse#loadFromServer(org.jsoup.nodes.Document)
	 */
	public static Document bibleChapter(String APIKey, String versionId, String bookId, int chapter) throws IOException {
		String verseID = versionId + ":" + bookId +"." + chapter;

		String url = "http://" + APIKey + ":x@bibles.org/v2/chapters/" +
				verseID + "/verses.xml?include_marginalia=false";

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
	}
}
