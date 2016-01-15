package com.caseybrooks.androidbibletools.basic;

import com.caseybrooks.androidbibletools.data.Optional;
import com.caseybrooks.androidbibletools.io.ReferenceParser;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A generic Reference for a Bible verse. A Reference simply refers to a location in the Bible, as
 * denoted by a book, a chapter, and a list of verses contained within this chapter. The chapter and
 * verses are represented as integers, and the Book, while represented as a String as the name, is
 * supplemented by an integer as its position in the Bible. This class is final, because any type of
 * Bible verse should always be able to reference another by use of the same Reference, entirely
 * independent of the Bible version, language, or service providing the verse text. A reference for
 * a Verse should be compatible with any other implementation of a Verse, and a reference for a
 * Passage should be compatible with any other implementation of a Passage, a Verse reference will
 * always take first verse in a Passage reference.
 */
public final class Reference implements Comparable<Reference> {
	private final Book book;
	private final int chapter;
	private final ArrayList<Integer> verses;

//Make constructors private. Force caller to use of the static initializer methods
//--------------------------------------------------------------------------------------------------
	private Reference(Book book, int chapter, int... verses) {
		this.book = book;
		this.chapter = chapter;
		this.verses = new ArrayList<>();

		for(int i : verses) {
			this.verses.add(i);
		}

		Collections.sort(this.verses);
	}

	private Reference(Book book, int chapter, ArrayList<Integer> verses) {
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
		Collections.sort(this.verses);
	}

//static initializer methods
//--------------------------------------------------------------------------------------------------
	public Book getBook() {
		return book;
	}

	public int getChapter() {
		return chapter;
	}

	public ArrayList<Integer> getVerses() {
		return verses;
	}

//static initializer methods
//--------------------------------------------------------------------------------------------------

	/**
	 * Builder class to aid in creating a reference, especially when working with user input. The
	 * Builder offers much flexibility is the data that is used to construct a Reference, and will
	 * do whatever it needs to return some Reference. In any situation where insufficient
	 * information is given, it chooses to use default values or create dummy Books with a not-real
	 * book name so that the user will at least have something to work with. In this sense,
	 * arbitrary user input will be accepted and a Reference will be created, but there is no
	 * guarantee that the verse can be downloaded or have much meaningful information associated
	 * with it.
	 */
	public static class Builder {
		public static int DEFAULT_BIBLE_FLAG = 0x1;
		public static int DEFAULT_BOOK_FLAG = 0x2;
		public static int DEFAULT_CHAPTER_FLAG = 0x4;
		public static int DEFAULT_VERSES_FLAG = 0x8;
		public static int PREVENT_AUTO_ADD_VERSES_FLAG = 0x10;

		private int flags = 0;

		private Bible bible;
		private Book book;

		private int chapter;
		private ArrayList<Integer> verses;

		public Builder() {
			verses = new ArrayList<>();
		}

		public Book getBook() {
			return book;
		}

		public int getChapter() {
			return chapter;
		}

		public ArrayList<Integer> getVerses() {
			return verses;
		}

		/**
		 * Attempts to parse the full String into this Builder, extracting the book, chapter, and
		 * all verses and populating this Builder with the extracted information.
		 *
		 * @param reference the String reference to be parsed
		 *
		 * @return this Builder object
		 */
		public Builder parseReference(String reference) {
			ReferenceParser parser = new ReferenceParser(this);
			parser.getPassageReference(reference);

			return this;
		}

		/**
		 * Set the book of this Builder
		 *
		 * @param book the Book to be set
		 *
		 * @return this Builder object
		 */
		public Builder setBook(Book book) {
			this.book = book;

			//since we are having the Book provided, assume that it is not defaulted
			unsetFlag(DEFAULT_BOOK_FLAG);

			return this;
		}

		/**
		 * Attempts to parse the bookName according to the bible that was set. If that fails, it
		 * tries to parse against the default ESV. Failing both, it simply creates a Book with the
		 * given bookName, but this book cannot be downloaded because we can't quite say which verse
		 * it is in the Bible.
		 *
		 * @param bookName the bookname to be parsed
		 *
		 * @return this Builder object
		 */
		public Builder setBook(String bookName) {
			//no bookname provided, set it to empty
			if(bookName == null) {
				bookName = "";
			}

			//no bible provided, assume default Bible and use that
			if(bible == null) {
				setFlag(DEFAULT_BIBLE_FLAG);
				//				bible = new Bible();
			}
			else {
				unsetFlag(DEFAULT_BIBLE_FLAG);
			}

			Book parsedBook = bible.parseBook(bookName);

			if(parsedBook != null) {
				unsetFlag(DEFAULT_BOOK_FLAG);

				book = parsedBook;
				return this;
			}
			//could not find this book in the given bible, just add the book as-is
			else {
				parsedBook = new Book();
				parsedBook.setName(bookName);
				setFlag(DEFAULT_BOOK_FLAG);

				book = parsedBook;
				return this;
			}
		}

		/**
		 * Makes no attempt to parse the bookName, but instead just creates a Book with the given
		 * parameters.
		 *
		 * @param bookName the name of the book
		 * @param bookAbbr (optional) the book's abbreviated name. If not specified, the first three
		 * letters of the name will be used
		 * @param chapters (optional) the list of chapters in this Book
		 *
		 * @return this Builder object
		 */
		public Builder setBook(
				String bookName,
				@Optional String bookAbbr,
				@Optional int location,
				@Optional int[] chapters) {

			Book parsedBook = new Book();
			parsedBook.setName(bookName);

			if(bookAbbr != null) {
				parsedBook.setAbbreviation(bookAbbr);
			}
			else {
				parsedBook.setAbbreviation(bookName.substring(0, 3));
			}

			if(chapters != null) {
				parsedBook.setChapters(chapters);
			}

			if(location != 0) {
				parsedBook.setLocation(location);
			}
			else {
				//choose highest order to ensure it will always be
				//sorted to the end of a list of verses
				parsedBook.setLocation(Integer.MAX_VALUE);
			}

			book = parsedBook;

			//we assume that the book is not default, since it is being created now
			//we also assume that the Bible is not default, since it was not involved
			//in creating the Book, so we had no way to default it
			unsetFlag(DEFAULT_BOOK_FLAG);
			unsetFlag(DEFAULT_BIBLE_FLAG);

			return this;
		}

		public Builder clearBook() {
			this.book = null;

			return this;
		}

		public Builder clearChapter() {
			this.chapter = 0;

			return this;
		}

		public Builder clearVerses() {
			this.verses.clear();

			return this;
		}

		/**
		 * Sets the Bible of this Builder to the given Bible object
		 *
		 * @param bible the Bible to be set
		 *
		 * @return this Builder object
		 */
		public Builder setBible(Bible bible) {
			this.bible = bible;
			unsetFlag(DEFAULT_BIBLE_FLAG);

			return this;
		}

		/**
		 * Creates a new Bible with the given name. This Bible cannot be used to download verses,
		 * but just provides a convenient way to accept arbitrary versions from user input as valid
		 * versions. If no abbreviation is given, one will be created from the first letter of each
		 * word in the version name, or if it is only one word, it will be copied over.
		 *
		 * @param versionName the name of this Bible
		 * @param versionAbbr (optional) the abbreviation of this Bible
		 *
		 * @return this Builder object
		 */
		public Builder setBible(String versionName, @Optional String versionAbbr) {
			//			this.bible = new Bible();
			this.bible.name = versionName;
			if(versionAbbr != null) {
				this.bible.abbreviation = versionAbbr;
			}

			unsetFlag(DEFAULT_BIBLE_FLAG);

			return this;
		}

		/**
		 * A reference can point to a single chapter, so set that chapter.
		 *
		 * @param chapter the chapter to set
		 *
		 * @return this Builder object
		 */
		public Builder setChapter(int chapter) {
			this.chapter = chapter;
			unsetFlag(DEFAULT_CHAPTER_FLAG);

			return this;
		}

		/**
		 * Set the list of verses in the specified chapter for the reference. Any previously set
		 * verses will be removed
		 *
		 * @param verses an array of verses to be set
		 */
		public Builder setVerses(int... verses) {
			this.verses = new ArrayList<>();
			for(int verse : verses) {
				this.verses.add(verse);
			}
			unsetFlag(DEFAULT_VERSES_FLAG);

			return this;
		}

		/**
		 * Add a verse to the current list of verses if it has not already been added
		 *
		 * @param verse the verse to add
		 *
		 * @return this Builder object
		 */
		public Builder addVerse(int verse) {
			if(!verses.contains(verse)) {
				this.verses.add(verse);
			}
			unsetFlag(DEFAULT_VERSES_FLAG);

			return this;
		}

		/**
		 * Sets the chapter of this Builder, then adds all verses in that chapter if that data is available
		 *
		 * @param chapter the chapter to add all verses from
		 *
		 * @return this Builder object
		 */
		public Builder addAllVersesInChapter(int chapter) {
			setChapter(chapter);
			unsetFlag(DEFAULT_VERSES_FLAG);

			if(
				book != null
				&& book.getChapters() != null
				&& book.getChapters().length > 0
				&& chapter >= 0
				&& chapter < book.getChapters().length) {

				this.verses = new ArrayList<>();
				for(int i = 1; i <= book.numVersesInChapter(chapter); i++) {
					this.verses.add(i);
				}
			}

			return this;
		}

		/**
		 * Using the currently set chapter of this Builder, add all verses in that chapter if that data is available
		 *
		 * @return this Builder object
		 */
		public Builder addAllVersesInChapter() {
			unsetFlag(DEFAULT_VERSES_FLAG);

			if(
					book != null
							&& book.getChapters() != null
							&& book.getChapters().length > 0
							&& chapter >= 0
							&& chapter < book.getChapters().length) {

				this.verses = new ArrayList<>();
				for(int i = 1; i <= book.numVersesInChapter(chapter); i++) {
					this.verses.add(i);
				}
			}

			return this;
		}

		/**
		 * Given the current state of this Builder, create a Reference. It will always produce a
		 * non-null reference, but that reference may have default values if you were not careful
		 * and did not create it well enough. Ideally, some reference is better than none, because
		 * the reference returned should at least contain most of the information you put in, which
		 * makes it easier to determine what was not valid and fix it.
		 *
		 * @return the reference that was incrementally built by this Builder
		 */
		public Reference create() {
			if(verses == null || verses.size() == 0) {
				setFlag(DEFAULT_VERSES_FLAG);

				if(!checkFlag(PREVENT_AUTO_ADD_VERSES_FLAG)) {
					//no verses, but chapter was given.
					if(chapter != 0) {
						//book has a count of the chapters, so add all verses in that chapter
						if(book.getChapters() != null && book.getChapters().length > 0) {
							int verseCount = book.numVersesInChapter(chapter);

							this.verses = new ArrayList<>();
							for(int i = 1; i <= verseCount; i++) {
								this.verses.add(i);
							}
						}
						//book does not have a list of the chapters, so just use use the first verse
						else {
							verses = new ArrayList<>();
							verses.add(1);
						}
					}
					//no verses or chapter given. Set to the first verse of the first chapter
					else {
						setFlag(DEFAULT_CHAPTER_FLAG);

						chapter = 1;
						verses = new ArrayList<>();
						verses.add(1);
					}
				}
			}

			//the book was not specified, so use the first book of the New Testament
			// since many translations only have the New Testament.
			if(book == null) {
				setBook("Matthew");
				setFlag(DEFAULT_BOOK_FLAG);
			}

			return new Reference(book, chapter, verses);
		}

		//bitflags for defaults during creation

		public void setFlag(int flag) {
			flags = flags | flag;
		}

		public void unsetFlag(int flag) {
			flags = (flags & ~flag);
		}

		public boolean checkFlag(int flag) {
			return ((flags & flag) == flag);
		}
	}

	@Override
	public String toString() {
		String refString = book.getName();
		refString += " " + chapter;

		if(verses == null || verses.size() == 0)
			return refString;

		refString += ":";

		refString += verses.get(0);
		int lastVerse = verses.get(0);

		int i = 1;
		while(i < verses.size()) {
			if(verses.get(i) == lastVerse + 1) {
				refString += "-";
				while(true) {
					if(i < verses.size() && verses.get(i) == lastVerse + 1) {
						lastVerse++;
						i++;
					}
					else {
						refString += Integer.toString(lastVerse);
						break;
					}
				}
			}
			else {
				refString += ", " + verses.get(i);
				lastVerse = verses.get(i);
				i++;
			}
		}

		return refString;
	}

	//Compares two Reference with respect to classical reference order, according to the first verse
	//RETURN VALUES (negative indicates lhs is less than rhs)
	//
	//  0: Verses are equal, since they point to the same verse
	//  1: Verses are adjacent
	//  2: Verses are not adjacent, but are in the same chapter
	//  3: Verses are not adjacent, but are in different chapters of the same Book
	//  4: Verses are not adjacent, and aren't even in the same Book
	@Override
	public int compareTo(Reference rhs) {
		Reference lhs = this;

		try {

			if(lhs.book.getLocation() - rhs.book.getLocation() == 1) {
				if((lhs.chapter == 1 && lhs.verses.get(0) == 1) &&
						(rhs.chapter == rhs.book.numChapters() &&
								(rhs.verses.get(0) == rhs.book.numVersesInChapter(rhs.chapter)))) {
					return 1;
				}
				else {
					return 4;
				}
			}
			else if(lhs.book.getLocation() - rhs.book.getLocation() == -1) {
				if((rhs.chapter == 1 && rhs.verses.get(0) == 1) &&
						(lhs.chapter == lhs.book.numChapters() &&
								(lhs.verses.get(0) == lhs.book.numVersesInChapter(lhs.chapter)))) {
					return -1;
				}
				else {
					return -4;
				}
			}
			else if(lhs.book.getLocation() > rhs.book.getLocation()) {
				return 4;
			}
			else if(lhs.book.getLocation() < rhs.book.getLocation()) {
				return -4;
			}
			else {
				//same book
				if(lhs.chapter - rhs.chapter == 1) {
					if((lhs.verses.get(0) == 1) &&
							(rhs.verses.get(0) == rhs.book.numVersesInChapter(rhs.chapter))) {
						return 1;
					}
					else {
						return 3;
					}
				}
				if(lhs.chapter - rhs.chapter == -1) {
					if((rhs.verses.get(0) == 1) &&
							(lhs.verses.get(0) == lhs.book.numVersesInChapter(lhs.chapter))) {
						return -1;
					}
					else {
						return -3;
					}
				}
				else if(lhs.chapter > rhs.chapter) {
					return 3;
				}
				else if(lhs.chapter < rhs.chapter) {
					return -3;
				}
				else {
					//same chapter
					if(lhs.verses.get(0) - rhs.verses.get(0) == 1) {
						return 1;
					}
					else if(lhs.verses.get(0) - rhs.verses.get(0) == -1) {
						return -1;
					}
					else if(lhs.verses.get(0) > rhs.verses.get(0)) {
						return 2;
					}
					else if(lhs.verses.get(0) < rhs.verses.get(0)) {
						return -2;
					}
					else {
						return 0; //lhs.verses.get(0) == rhs.verses.get(0)
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();

			return lhs.toString().compareTo(rhs.toString());
		}
	}

	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(!(other instanceof Reference)) {
			return false;
		}

		Reference ref = (Reference) other;

		//		if(this.book != ref.book) return false;
		if(this.chapter != ref.chapter) {
			return false;
		}
		if(this.verses.size() != ref.verses.size()) {
			return false;
		}

		for(Integer i : this.verses) {
			if(!ref.verses.contains(i)) {
				return false;
			}
		}
		for(Integer i : ref.verses) {
			if(!this.verses.contains(i)) {
				return false;
			}
		}

		return true;
	}

	public int hashCode() {
		int result = book.hashCode();
		result = 31 * result + chapter;
		result = 31 * result + verses.hashCode();
		return result;
	}
}
