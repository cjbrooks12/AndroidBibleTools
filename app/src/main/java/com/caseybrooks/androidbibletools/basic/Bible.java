package com.caseybrooks.androidbibletools.basic;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A base class to give a verse the Bible translation or version that it is. A basic Bible should
 * have a full name, an abbreviation, which can be derived from the full name, and a list of Books
 * from which we can determine exactly where in the Bible a verse exists.
 */
public abstract class Bible<T extends Book> implements Comparable<Bible> {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected String id;
	protected String name;
	protected String abbreviation;
	protected String language;
	protected String languageEnglish;
	protected ArrayList<T> books;

//Constructors
//--------------------------------------------------------------------------------------------------
	public Bible() {
	}

//Getters and Setters
//--------------------------------------------------------------------------------------------------
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public ArrayList<T> getBooks() {
		return books;
	}

	public void setBooks(Collection<T> books) {
		this.books = new ArrayList<>();
		this.books.addAll(books);
	}

	public String getLanguageEnglish() {
		return languageEnglish;
	}

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
	 * deserialize this Bible from String
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
