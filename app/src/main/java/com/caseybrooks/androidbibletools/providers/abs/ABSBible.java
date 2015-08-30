package com.caseybrooks.androidbibletools.providers.abs;

import android.util.Base64;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.Optional;
import com.caseybrooks.androidbibletools.defaults.DefaultBible;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ABSBible extends Bible implements Downloadable {
//Data Members
//------------------------------------------------------------------------------
	protected final String APIKey;
	protected final String id;

	protected String languageKey;
	protected String languageName;
	protected String languageNameEnglish;
	protected String languageCode;
	protected String copyright;
	protected String info;
	protected String contact;

//Constructors
//------------------------------------------------------------------------------
	public ABSBible(String APIKey, String id) {
		super();
		this.APIKey = APIKey;
		this.id = (id != null) ? id : "eng-ESV";

		this.languageKey = DefaultBible.defaultBibleLang;
		this.languageName = DefaultBible.defaultBibleLangName;
		this.languageNameEnglish = DefaultBible.defaultBibleLangNameEnglish;
		this.languageCode = DefaultBible.defaultBibleLangCode;
		this.copyright = DefaultBible.defaultBibleCopyright;
		this.info = DefaultBible.defaultBibleInfo;
		this.contact = DefaultBible.defaultBibleContact;

		books = new ArrayList<>();
		for(int i = 0; i < DefaultBible.defaultBookName.length; i++) {
			ABSBook book = new ABSBook(APIKey, this.id + ":" + DefaultBible.defaultBookAbbr[i]);
			book.setName(DefaultBible.defaultBookName[i]);
			book.setAbbreviation(DefaultBible.defaultBookAbbr[i]);
			book.setChapters(DefaultBible.defaultBookVerseCount[i]);
			book.setLocation(i+1);

			books.add(book);
		}
	}

//Getters and Setters
//------------------------------------------------------------------------------
	public String getId() {
		return id;
	}

	public String getLanguageKey() {
		return languageKey;
	}

	public void setLanguageKey(String language) {
		this.languageKey = languageKey;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getLanguageNameEnglish() {
		return languageNameEnglish;
	}

	public void setLanguageNameEnglish(String languageNameEnglish) {
		this.languageNameEnglish = languageNameEnglish;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	//Downloadable Interface Implementation
//------------------------------------------------------------------------------
	@Override
	public boolean isAvailable() {
		return APIKey != null && id != null;
	}

	@Override
	public Document getDocument() throws IOException {
		String url = "http://" + APIKey + ":x@bibles.org/v2/versions/" +
				id + "/books.xml?include_chapters=true";

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
	}

	@Override
	public boolean parseDocument(Document doc) {
		if(doc == null) {
			return false;
		}

		ArrayList<Book> newBooks = new ArrayList<>();

		Element parent = doc.select("parent").first();

		name = (parent != null) ? parent.select("name").text() : DefaultBible.defaultBibleName;
		abbreviation = (parent != null) ? parent.select("id").text().replaceAll(".*-", "") : DefaultBible.defaultBibleId;
		copyright = doc.select("copyright").first().text();

		doc.select("next").remove();
		doc.select("previous").remove();
		doc.select("version").remove();

		Elements bookElements = doc.select("book");

		//parse each <book> element to get the information for that book
		for(Element book : bookElements) {
			//get basic information about a Book
			ABSBook newBook = new ABSBook(APIKey, book.attr("id"));
			newBook.setName(book.select("name").text());
			newBook.setAbbreviation(book.select("abbr").text());
			newBook.setLocation(Integer.parseInt(book.select("ord").text()));

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
			return true;
	}


//Static methods to get listing of Bibles and languages available
//------------------------------------------------------------------------------
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
	 * @see com.caseybrooks.androidbibletools.providers.abs.ABSBible#getAvailableLanguages(org.jsoup.nodes.Document)
	 * @see com.caseybrooks.androidbibletools.providers.abs.ABSBible#parseAvailableVersions(org.jsoup.nodes.Document)
	 */
	public static Document availableVersionsDoc(String APIKey, @Optional String languageAbbr) throws IOException {
		String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/versions.xml";
		if(languageAbbr != null) { url += "?language=" + languageAbbr; }

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
	}

	/**
	 //	 * Parse the XML response from Bibles.org of available versions. The document
	 //	 * can be recently downloaded or cached, and either file is parsed in exactly
	 //	 * the same way
	 //	 *
	 //	 * @param doc a Jsoup Document containing the XML response to be parsed
	 //	 * @return a HashMap containing all available Versions, with the abbreviated name as a key
	 //	 *
	 //	 * @see com.caseybrooks.androidbibletools.io.Download#availableVersions(String, String)
	 //	 */
	public static HashMap<String, Bible> parseAvailableVersions(Document doc) {
		HashMap<String, Bible> versionMap = new HashMap<>();

		Elements versions = doc.select("version");

		for(Element element : versions) {
			ABSBible bible = new ABSBible(null, element.attr("id"));
			bible.setName(element.select("name").text());
			bible.setAbbreviation(element.select("abbreviation").text());
			bible.setLanguage(element.select("lang_name").text());
			bible.setLanguageKey(element.select("lang").text());
			bible.setLanguageName(element.select("lang_name").text());
			bible.setLanguageNameEnglish(element.select("lang_name_english").text());
			bible.setLanguageCode(element.select("lang_code").text());
			bible.setCopyright(element.select("copyright").text());
			bible.setInfo(element.select("info").text());
			bible.setContact(element.select("contact_url").text());

			versionMap.put(bible.getId(), bible);
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
