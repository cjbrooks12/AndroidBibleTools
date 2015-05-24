package com.caseybrooks.androidbibletools.basic;

import com.caseybrooks.androidbibletools.defaults.DefaultBible;

import java.util.ArrayList;

/**
 * A base class to give a verse the Bible translation or version that it is. A
 * basic Bible should have a full name, an abbreviation, which can be derived
 * from the full name, and a list of Books from which we can determine exactly
 * where in the Bible a verse exists.
 */
public class Bible {
//Data Members
//------------------------------------------------------------------------------
	protected String name;
	protected String abbr;
	protected ArrayList<Book> books;

//Constructors
//------------------------------------------------------------------------------
	public Bible() {
		this.name = DefaultBible.defaultBibleName;
		this.abbr = DefaultBible.DefaultBibleAbbr;

		//set up books with default values to ensure that we can always work without
		//needing to download anything first
		books = new ArrayList<>();
		for(int i = 0; i < DefaultBible.defaultBookName.length; i++) {
			Book book = new Book();
			book.setName(DefaultBible.defaultBookName[i]);
			book.setAbbreviation(DefaultBible.defaultBookAbbr[i]);
			book.setChapters(DefaultBible.defaultBookVerseCount[i]);
			book.setLocation(i+1);

			books.add(book);
		}
	}

//Getters and Setters
//------------------------------------------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	public ArrayList<Book> getBooks() {
		return books;
	}

	public void setBooks(ArrayList<Book> books) {
		this.books = books;
	}

	/**
	 * Attemps to parse a given String and determine the name of the book.
	 * Failing to find it within the specified books, return nothing, so that
	 * the user can either create a blank Book to use instead, throw an exception,
	 * or anything else.
	 *
	 * @param bookName the text of the book to attempt to parse
	 * @return a Book if the name matches one of the Books in this Bible, null otherwise
	 */
	public Book parseBook(String bookName) {
		for(Book book : books) {
			//check equality of the full book name
			if(bookName.equalsIgnoreCase(book.getName())) {
				return book;
			}
			//equality of the abbreviation last, since it is smallest
			else if(bookName.equalsIgnoreCase(book.getAbbreviation())) {
				return book;
			}

			//failing equality, check if the name is close
			else if(bookName.contains(book.getName())) {
				return book;
			}
			else if(book.getName().contains(bookName)) {
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
}
