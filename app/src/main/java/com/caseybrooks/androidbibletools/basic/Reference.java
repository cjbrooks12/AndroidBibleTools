package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.caseybrooks.androidbibletools.io.ReferenceParser;
import com.caseybrooks.androidbibletools.providers.simple.SimpleBible;
import com.caseybrooks.androidbibletools.providers.simple.SimpleBook;

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
	private final Bible bible;
	private final Book book;
	private final int chapter;
	private final ArrayList<Integer> verses;

//All constructors private. Force caller to use of the static initializer methods
//--------------------------------------------------------------------------------------------------
	private Reference(Bible bible, Book book, int chapter, ArrayList<Integer> verses) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verses = verses;
		Collections.sort(this.verses);
	}

//Getters
//--------------------------------------------------------------------------------------------------
	public Bible getBible() {
		return bible;
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
//Bitflags for state
//--------------------------------------------------------------------------------------------------
		public static int DEFAULT_BIBLE_FLAG = 0x1;
		public static int DEFAULT_BOOK_FLAG = 0x2;
		public static int DEFAULT_CHAPTER_FLAG = 0x4;
		public static int DEFAULT_VERSES_FLAG = 0x8;
		public static int PREVENT_AUTO_ADD_VERSES_FLAG = 0x10;

		private int flags = 0;

		public void setFlag(int flag) { flags = flags | flag; }
		public void unsetFlag(int flag) { flags = (flags & ~flag); }
		public boolean checkFlag(int flag) { return ((flags & flag) == flag); }

//Data Members, used to ultimately create a Reference
//--------------------------------------------------------------------------------------------------
		private Bible bible;
		private Book book;
		private int chapter;
		private ArrayList<Integer> verses;

//Constructor
//--------------------------------------------------------------------------------------------------
		public Builder() {
			setDefaultBible();
			setDefaultBook();
			setDefaultChapter();
			setDefaultVerses();
		}

//Set default properties
//--------------------------------------------------------------------------------------------------
		public Builder setDefaultBible() {
			this.bible = new SimpleBible();

			setFlag(DEFAULT_BIBLE_FLAG);
			return this;
		}

		public Builder setDefaultBook() {
			this.book = new SimpleBook();

			setFlag(DEFAULT_BOOK_FLAG);
			return this;
		}

		public Builder setDefaultChapter() {
			this.chapter = 0;

			setFlag(DEFAULT_CHAPTER_FLAG);
			return this;
		}

		public Builder setDefaultVerses() {
			this.verses = new ArrayList<>();

			setFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

//Getters
//--------------------------------------------------------------------------------------------------
		public Bible getBible() { return bible; }
		public Book getBook() { return book; }
		public int getChapter() { return chapter; }
		public ArrayList<Integer> getVerses() { return verses; }

//Parse raw input into this Builder
//--------------------------------------------------------------------------------------------------
		public Builder parseReference(String reference) {
			ReferenceParser parser = new ReferenceParser(this);
			parser.getPassageReference(reference);

			return this;
		}

		public Builder setBible(String versionName, @Nullable String versionAbbr) {
			this.bible = new SimpleBible();
			this.bible.setName(versionName);

			if(!TextUtils.isEmpty(versionAbbr)) {
				this.bible.setAbbreviation(versionAbbr);
			}

			unsetFlag(DEFAULT_BIBLE_FLAG);
			return this;
		}

		public Builder setBook(String bookName) {
			if(bible == null)
				setDefaultBible();

			book = bible.parseBook(bookName);

			if(book == null) {
				book = new SimpleBook();
				book.setName(bookName);
			}

			unsetFlag(DEFAULT_BOOK_FLAG);
			return this;
		}

		public Builder setBook(String bookName, @Nullable String bookAbbr, @Nullable int location, @Nullable int[] chapters) {
			book = new SimpleBook();
			book.setName(bookName);

			if(bookAbbr != null)
				book.setAbbreviation(bookAbbr);

			if(chapters != null)
				book.setChapters(chapters);

			if(location != 0)
				book.setLocation(location);
			else
				book.setLocation(Integer.MAX_VALUE);

			unsetFlag(DEFAULT_BOOK_FLAG);
			return this;
		}

//Set concrete values for properties
//--------------------------------------------------------------------------------------------------
		public Builder setBible(Bible bible) {
			this.bible = bible;

			unsetFlag(DEFAULT_BIBLE_FLAG);
			return this;
		}

		public Builder setBook(Book book) {
			this.book = book;

			unsetFlag(DEFAULT_BOOK_FLAG);
			return this;
		}

		public Builder setChapter(int chapter) {
			this.chapter = chapter;

			unsetFlag(DEFAULT_CHAPTER_FLAG);
			return this;
		}

		public Builder setVerses(int... verses) {
			this.verses = new ArrayList<>();
			for(int verse : verses) {
				this.verses.add(verse);
			}

			unsetFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

		public Builder setVerses(ArrayList<Integer> verses) {
			this.verses = verses;

			unsetFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

		public Builder addVerse(int verse) {
			if(!verses.contains(verse)) {
				this.verses.add(verse);
			}

			unsetFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

		public Builder addAllVersesInChapter() {
			if(book != null
					&& book.getChapters() != null
					&& book.getChapters().length > 0
					&& chapter >= 0
					&& chapter < book.getChapters().length)
			{
				this.verses = new ArrayList<>();
				for(int i = 1; i <= book.numVersesInChapter(chapter); i++) {
					this.verses.add(i);
				}
			}

			unsetFlag(DEFAULT_VERSES_FLAG);
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
			if(bible == null)
				setDefaultBible();

			if(verses == null || verses.size() == 0) {
				if(!checkFlag(PREVENT_AUTO_ADD_VERSES_FLAG)) {
					if(chapter != 0) {
						addAllVersesInChapter();
					}
					else {
						setFlag(DEFAULT_CHAPTER_FLAG);

						chapter = 1;
						verses = new ArrayList<>();
						verses.add(1);
					}

					setFlag(DEFAULT_VERSES_FLAG);
				}
			}

			if(book == null) {
				setDefaultBook();
			}

			return new Reference(bible, book, chapter, verses);
		}
	}

	@Override
	public String toString() {
		String refString = book.getName();

		if(chapter == 0)
			return refString;

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
