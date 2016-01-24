package com.caseybrooks.androidbibletools.basic;

import java.util.ArrayList;
import java.util.Collection;

//TODO: decide whether I really want to keep language and languageEnglish
//TODO: decide whether a Bible should have a Metadata object for itself to hold other information
/**
 * A base class to give a verse the Bible translation or version that it is. A basic Bible should
 * have a full name, an abbreviation (which can be derived from the full name), and a list of Books
 * from which we can determine exactly where in the Bible a verse exists.
 */
public abstract class Bible<T extends Book> implements Comparable<Bible> {
	protected String id;
	protected String name;
	protected String abbreviation;
	protected String language;
	protected String languageEnglish;
	protected ArrayList<T> books;

	public Bible() {
	}


	/**
	 * Get this Bibles's id.
	 *
	 * @return this Bibles's id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Most implementations of Bibles have some concept of an Id. This may be a key needed to retrieve
	 * its text from a webservice, or its primary key to look it up in a local databaseRegardless of
	 * how it is used, it is simpler to keep this in the base class since it is such a common use case.
	 *
	 * @param id  the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the name of this Bible.
	 *
	 * @return this Bibles's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this Bible.
	 *
	 * @param name  the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the abbreviation of this Bible's name.
	 *
	 * @return this Bibles's abbreviation
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Set the abbreviation of the name of this Bible.
	 *
	 * @param abbreviation  the abbreviation to set
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Get the name of the language of this Bible in that language
	 *
	 * @return this Bibles's language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Set the name of the language of this Bible in that language.
	 *
	 * @param language  the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Get this Bibles's listing of books.
	 *
	 * @return this Bibles's books
	 *
	 * @see Book
	 */
	public ArrayList<T> getBooks() {
		return books;
	}

	/**
	 * Set the list of Books in this Bible.
	 *
	 * @param books  the books to set
	 * @see Book
	 */
	public void setBooks(Collection<T> books) {
		this.books = new ArrayList<>();
		this.books.addAll(books);
	}

	/**
	 * Get the name of the language of this Bible in English
	 *
	 * @return this Bibles's language in English
	 */
	public String getLanguageEnglish() {
		return languageEnglish;
	}

	/**
	 * Set the name of the language of this Bible in English.
	 *
	 * @param languageEnglish  the language to set
	 */
	public void setLanguageEnglish(String languageEnglish) {
		this.languageEnglish = languageEnglish;
	}

	/**
	 * Serialize this Bible into a string that can be used to restore this Bible from persistent
	 * memory. Should serialize only that which is necessary to be able to identify this Bible and
	 * download the rest of the information. This would typically be the fully qualified class name
	 * of this Bible, and any IDs/API keys necessary.
	 *
	 * @return serialized String representation
	 */
	public String serialize() {
		return "";
	}

	/**
	 * deserialize this Bible from String.
	 * @see Bible#serialize()
	 */
	public void deserialize(String string) {

	}

	/**
	 * Attemps to parse a given String and determine the name of the book. Failing to find it within
	 * the specified books, return nothing, so that the user can either create a blank Book to use
	 * instead, throw an exception, or anything else.
	 *
	 * @param bookName the text of the book to attempt to parse
	 *
	 * @return a Book if the name matches one of the Books in this Bible, null otherwise
	 */
	public T parseBook(String bookName) {
		for(T book : books) {
			//check equality of the full book name
			if(bookName.equalsIgnoreCase(book.getName())) {
				return book;
			}
			//equality of the abbreviation last, since it is smallest
			else if(bookName.equalsIgnoreCase(book.getAbbreviation())) {
				return book;
			}

			//failing equality, check if the name is close
			else if(book.getName().contains(bookName)) {
				return book;
			}
			else if(bookName.contains(book.getName())) {
				return book;
			}

			//failing close name, check close to abbreviation
			else if(book.getAbbreviation().contains(bookName)) {
				return book;
			}
			else if(bookName.contains(book.getAbbreviation())) {
				return book;
			}
		}
		return null;
	}

	@Override
	public int compareTo(Bible another) {
		return this.getName().compareTo(another.getName());
	}
}
