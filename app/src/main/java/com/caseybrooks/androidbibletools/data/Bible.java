package com.caseybrooks.androidbibletools.data;

import com.caseybrooks.androidbibletools.defaults.DefaultBible;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class to manage the bible of a Bible, which governs everything else like
 * the Books within, the number of chapters in a book, the number of verses in
 * a chapter, and the language of the book.
 */
public class Bible {
	private final String versionId;
//	public String name;
//	public String abbr;
//	public String copyright;
//	public String copyrightInfo;
//	public String languageCode;
//	public String languageName;

	public OnDownloadListener listener;

	public ArrayList<Book> books;

	public Bible(String versionId) {
		this.versionId = (versionId == null) ? "eng-ESV" : versionId;

		//set up books with default values to ensure that we can always work without
		//needing to download anything first
		books = new ArrayList<>();
		for(int i = 0; i < DefaultBible.defaultBookName.length; i++) {
			Book book = new Book(this.versionId + ":" + DefaultBible.defaultBookAbbr[i]);
			book.setName(DefaultBible.defaultBookName[i]);
			book.setAbbr(DefaultBible.defaultBookAbbr[i]);
			book.setChapters(DefaultBible.defaultBookVerseCount[i]);
			book.setOrder(i);

			books.add(book);
		}
	}

	/**
	 * Attemps to parse a given String and determine the name of the
	 *
	 * @param bookName the text of the book to attempt to parse
	 * @return
	 */
	public Book parseBook(String bookName) {
		for(Book book : books) {
			//attempt to parse the full book name
			if(bookName.equalsIgnoreCase(book.getName())) {
				return book;
			}
			else if(bookName.equalsIgnoreCase(book.getAbbr())) {
				return book;
			}
			else if(bookName.equalsIgnoreCase(book.getId())) {
				return book;
			}
			else if(book.getName().contains(bookName)) {
				return book;
			}
		}

		return null;
	}

	public String getVersionId() {
		return versionId;
	}

	/**
	 * Populates this Bible with data parsed from the XML response of the
	 * Bibles.org API to get the full information for the Bible. This includes
	 * all books and counts for verses and chapters in each book.
	 *
	 * @param doc
	 */
	public void getVersionInfo(Document doc) {
		if(listener != null) listener.onPreDownload();

		ArrayList<Book> newBooks = new ArrayList<>();

		doc.select("parent").remove();
		doc.select("next").remove();
		doc.select("previous").remove();

		Elements bookElements = doc.select("book");

		//parse each <book> element to get the information for that book
		for(Element book : bookElements) {
			//get basic information about a Book
			Book newBook = new Book(book.attr("id"));
			newBook.setName(book.select("name").text());
			newBook.setAbbr(book.select("abbr").text());
			newBook.setOrder(Integer.parseInt(book.select("ord").text()));

			//get info about the number of chapters and verses in a Book
			ArrayList<Integer> chapterNums = new ArrayList<>();
			Elements chapters = book.select("chapter");

			for(Element chapter : chapters) {
				Element osisEnd = chapter.select("osis_end").first();
				if(osisEnd != null) {
					String lastVerse = osisEnd.text();
					lastVerse = lastVerse.replaceAll(chapter.select("id").text() + ".", "");
					try {
						chapterNums.add(Integer.parseInt(lastVerse));
					}
					catch(NumberFormatException nfe) {
						nfe.printStackTrace();
					}
				}
			}
			int[] chaptersArray = new int[chapterNums.size()];
			for(int i = 0; i < chapterNums.size(); i++) {
				chaptersArray[i] = chapterNums.get(i);
			}
			newBook.setChapters(chaptersArray);

			newBooks.add(newBook);
		}
		this.books = newBooks;

		if(listener != null) listener.onPostDownload();
	}

	/**
	 * Parse the XML response from Bibles.org of available versions. The document
	 * can be recently downloaded or cached, and either file is parsed in exactly
	 * the same way
	 *
	 * @param doc a Jsoup Document containing the XML response to be parsed
	 * @return a HashMap containing all available Versions, with the abbreviated name as a key
	 *
	 * @see com.caseybrooks.androidbibletools.io.Download#availableVersions(String, String)
	 */
	public static HashMap<String, Bible> getAvailableVersions(Document doc) {
		HashMap<String, Bible> versionMap = new HashMap<>();

		Elements versions = doc.select("version");

		for(Element element : versions) {
			Bible bible = new Bible(element.select("id").text());
//			bible.name = element.select("name").text();
//			bible.abbr = element.select("abbreviation").text();
//			bible.languageCode = element.select("lang_code").text();
//			bible.languageName = element.select("lang_name").text();
//			bible.copyright = element.select("copyright").text();
//			bible.copyrightInfo = element.select("info").text();
//
			versionMap.put(element.select("abbreviation").text(), bible);
		}

		return versionMap;
	}

	/**
	 * Parse the XML response from Bibles.org of available versions to determine
	 * all the available languages. The document can be recently downloaded or
	 * cached, and either file is parsed in exactly the same way
	 *
	 * @param doc a Jsoup Document containing the XML response to be parsed
	 * @return a HashMap containing all available Versions, with the abbreviated name as a key
	 *
	 * @see com.caseybrooks.androidbibletools.io.Download#availableVersions(String, String)
	 */
	public static HashMap<String, String> getAvailableLanguages(Document doc)  {
		HashMap<String, String> languageMap = new HashMap<>();

		Elements versions = doc.select("version");

		for(Element element : versions) {
			languageMap.put(
					element.select("lang_name").text(),
					element.select("lang").text().toLowerCase());
		}

		return languageMap;
	}
}
