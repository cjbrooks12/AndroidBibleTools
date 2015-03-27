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
	public enum Exists { YES, NO, MAYBE }

	private final String versionId;
	public Exists exists;

	public String name;
	public String abbr;
	public String copyright;
	public String info;
	public String contact;
	public String language;
	public String languageName;
	public String languageNameEnglish;
	public String languageCode;

	public OnDownloadListener listener;

	public ArrayList<Book> books;

	public Bible(String versionId) {
		if (versionId == null) {
			this.versionId = DefaultBible.defaultBibleId;
			this.name = DefaultBible.defaultBibleName;
			this.abbr = DefaultBible.DefaultBibleAbbr;
			this.copyright = DefaultBible.defaultBibleCopyright;
			this.info = DefaultBible.defaultBibleInfo;
			this.contact = DefaultBible.defaultBibleContact;
			this.language = DefaultBible.defaultBibleLang;
			this.languageName = DefaultBible.defaultBibleLangName;
			this.languageNameEnglish= DefaultBible.defaultBibleLangNameEnglish;
			this.languageCode = DefaultBible.defaultBibleLangCode;
			exists = Exists.YES;
		}
		else {
			this.versionId = versionId;
			exists = Exists.MAYBE;
		}

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
			//check equality of the full book name
			if(bookName.equalsIgnoreCase(book.getName())) {
				return book;
			}
			//equality of the id
			else if(bookName.equalsIgnoreCase(book.getId())) {
				return book;
			}
			//equality of the abbreviation last, since it is smallest
			else if(bookName.equalsIgnoreCase(book.getAbbr())) {
				return book;
			}

			//failing equality, check if the name is close
			else if(bookName.contains(book.getName())) {
				return book;
			}
			else if(book.getName().contains(bookName)) {
				return book;
			}

			//failing close name, check close to abbreviation. Don't check close
			//to id, since the id should be exactly equal, or at least the abbr
			else if(book.getAbbr().contains(bookName)) {
				return book;
			}
			else if(bookName.contains(book.getAbbr())) {
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

		exists = Exists.MAYBE;

		if(doc == null) {
			//assert to the user that since we couldn't get the version info,
			//then it must not exist within the limits of this library
			exists = Exists.NO;
			return;
		}

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
		exists = Exists.YES;

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
			bible.name = element.select("name").text();
			bible.abbr = element.select("abbreviation").text();
			bible.language = element.select("lang").text();
			bible.languageName = element.select("lang_name").text();
			bible.languageNameEnglish = element.select("lang_name_english").text();
			bible.languageCode = element.select("lang_code").text();
			bible.copyright = element.select("copyright").text();
			bible.info = element.select("info").text();
			bible.contact = element.select("contact_url").text();

			versionMap.put(bible.getVersionId(), bible);
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
					element.select("lang").text(),
					element.select("lang_name").text());
		}

		return languageMap;
	}
}
