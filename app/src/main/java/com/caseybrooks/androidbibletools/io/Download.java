package com.caseybrooks.androidbibletools.io;

public class Download {

//	/**
//	 * Query the server for a list of all available Versions. Leaving the languageAbbr
//	 * parameter null will return a list of Versions in all languages, or else will
//	 * return all versions in the specified language.
//	 *
//	 * @param APIKey required free API key from Bibles.org/api
//	 * @param languageAbbr optional: the ISO 639-2 abbreviation to get all versions of a specific language. Leave null to get versions in all languages
//	 * @return a Jsoup Document to be parsed or cached
//	 * @throws IOException
//	 *
//	 * @see com.caseybrooks.androidbibletools.providers.abs.ABSBible#getAvailableLanguages(org.jsoup.nodes.Document)
//	 * @see com.caseybrooks.androidbibletools.providers.abs.ABSBible#getAvailableVersions(org.jsoup.nodes.Document)
//	 */
//	public static Document availableVersions(String APIKey, @Optional String languageAbbr) throws IOException {
//		String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/versions.xml";
//		if(languageAbbr != null) { url += "?language=" + languageAbbr; }
//
//		String header = APIKey + ":x";
//		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
//
//		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
//	}
//
//	/**
//	 * Download the full information for a particular Bible version. Includes such
//	 * information as the listing of all books and their chapters, the copyright,
//	 * the full name of the Bible, etc.
//	 *
//	 * @param APIKey required free API key from Bibles.org/api
//	 * @param id id of the desired Bible version to get info for
//	 * @return a Jsoup Document to be parsed or cached
//	 * @throws IOException
//	 */
//	public static Document versionInfo(String APIKey, String id) throws IOException {
//		String url = "http://" + APIKey + ":x@bibles.org/v2/versions/" +
//				id + "/books.xml?include_chapters=true";
//
//		String header = APIKey + ":x";
//		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
//
//		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
//	}
//
//	/**
//	 * Download the text for a specified chapter in the Bible
//	 *
//	 * @param APIKey required free API key from Bibles.org/api
//	 * @param reference the reference of the chapter to be downloaded
//	 * @return a Jsoup Document to be parsed or cached
//	 * @throws IOException
//	 *
//	 * @see com.caseybrooks.androidbibletools.basic.AbstractVerse#getVerseInfo(org.jsoup.nodes.Document)
//	 */
//	public static Document bibleChapter(String APIKey, Reference reference) throws IOException {
//		String verseID = reference.book.getId() +"." + reference.chapter;
//
//		String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/chapters/" +
//				verseID + "/verses.xml?include_marginalia=false";
//
//		String header = APIKey + ":x";
//		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
//
//		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
//	}
}