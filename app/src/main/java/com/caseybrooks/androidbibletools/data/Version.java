package com.caseybrooks.androidbibletools.data;

import android.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class to manage the version of a Bible, which governs everything else like
 * the Books within, the number of chapters in a book, the number of verses in
 * a chapter, and the language of the book.
 */
public class Version {
	public String id;
	public String name;
	public String abbr;
	public String copyright;
	public String copyrightInfo;
	public String languageCode;
	public String languageName;

	public OnDownloadListener listener;

	public ArrayList<Book> books;

	/**
	 * Downloads the full information for the Version. This includes the copyright,
	 * and all books and counts for verses and chapters in each book.
	 *
	 * @param APIKey required free API key from Bibles.org/api
	 * @throws IOException
	 */
	public void downloadVersionInfo(String APIKey) throws IOException {
		if(listener != null) listener.onPreDownload();

		String url = "http://" + APIKey + ":x@bibles.org/v2/versions/" +
				id + "/books.xml?include_chapters=true";

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
		Document doc = Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();

		ArrayList<Book> newBooks = new ArrayList<>();
		Elements bookElements = doc.select("book");

		//parse each <book> element to get the information for that book
		for(Element book : bookElements) {
			Book newBook = new Book();
			newBook.id   = book.select("id").text();
			newBook.name = book.select("name").text();
			newBook.abbr = book.select("abbr").text();

			//parse individual <chapter> elements to get the number of verses in each chapter
			Elements chapters = book.select("chapters").select("chapter");
			ArrayList<Integer> versesInChapter = new ArrayList<>();
			for(Element chapter : chapters) {
				if(chapter.childNodeSize() <= 1) continue;

				Elements osisEnd = chapter.select("osis_end");

				if(osisEnd != null) {
					String[] chapterOsisEnd = osisEnd.text().split("\\.");

					if(chapterOsisEnd.length != 3) throw new IOException(chapter.toString() + " not split");
					int endVerse = Integer.parseInt(chapterOsisEnd[2]);

					versesInChapter.add(endVerse);
				}
			}

			newBook.chapters = versesInChapter;

			newBooks.add(newBook);
		}

		this.books = newBooks;

		if(listener != null) listener.onDownloadSuccess();
	}

	/**
	 * Query the server for a list of all available Versions. Leaving the languageAbbr
	 * parameter null will return a list of Versions in all languages, or else will
	 * return all versions in the specified language.
	 *
	 * @param APIKey required free API key from Bibles.org/api
	 * @param languageAbbr optional: the ISO 639-2 abbreviation to get all versions of a specific language. Leave null to get versions in all languages
	 * @return a HashMap containing all retrieved Versions. The key is the abbreviation of the Version in all lowercase letters
	 * @throws IOException
	 */
	public static HashMap<String, Version> getAvailableVersions(String APIKey, String languageAbbr) throws IOException {
		HashMap<String, Version> versionMap = new HashMap<>();

		String url = "http://" + APIKey + ":x@bibles.org/v2/versions.xml";
		if(languageAbbr != null) { url += "?language=" + languageAbbr; }

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
		Document doc = Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();

		Elements versions = doc.select("version");

		for(Element element : versions) {
			Version version = new Version();
			version.id = element.select("id").text();
			version.name = element.select("name").text();
			version.abbr = element.select("abbreviation").text();
			version.languageCode = element.select("lang_code").text();
			version.languageName = element.select("lang_name").text();
			version.copyright = element.select("copyright").text();
			version.copyrightInfo = element.select("info").text();

			versionMap.put(version.abbr.toLowerCase(), version);
		}

		return versionMap;
	}

	public static HashMap<String, String> getAvailableLanguages(String APIKey) throws IOException {
		HashMap<String, String> languageMap = new HashMap<>();

		String url = "http://" + APIKey + ":x@bibles.org/v2/versions.xml";
		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
		Document doc = Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();

		Elements versions = doc.select("version");

		for(Element element : versions) {
			languageMap.put(
					element.select("lang_name").text(),
					element.select("lang").text().toLowerCase());
		}

		return languageMap;
	}
}
