package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.caseybrooks.androidbibletools.io.ReferenceParser;
import com.caseybrooks.androidbibletools.providers.simple.SimpleBible;
import com.caseybrooks.androidbibletools.providers.simple.SimpleBook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class that 'points' to a particular location in the Bible. A reference location never changes:
 * the {@link Bible} it refers to is constant, as well as the {@link Book} in that Bible and the
 * chapter in that book. The list of verses cannot be modified once created.
 * <p>
 * To create a reference, use a {@link com.caseybrooks.androidbibletools.basic.Reference.Builder}
 * and set modify the properties as needed or parse raw user input.
 * <p>
 * References can be compared to any other References, and in this way, allows Passages from multiple
 * providers to be compared to each other. Each provider is responsible for allowing their specific
 * implementations of AbstractVerse to be compared to any other generic AbstractVerse, and for
 * determining the behavior of this comparison.
 */
public final class Reference implements Comparable<Reference> {
	private final Bible bible;
	private final Book book;
	private final int chapter;
	private final List<Integer> verses;

	/**
	 * Reference constructor is private. A Reference can only be created with a Builder.
	 *
	 * @param bible
	 * @param book
	 * @param chapter
	 * @param verses
	 *
	 * @see com.caseybrooks.androidbibletools.basic.Reference.Builder
	 */
	private Reference(Bible bible, Book book, int chapter, ArrayList<Integer> verses) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;

		Collections.sort(verses);
		this.verses = Collections.unmodifiableList(verses);
	}

	/**
	 * Get the Bible set with this Reference.
	 *
	 * @return the reference's Bible
	 */
	public Bible getBible() {
		return bible;
	}

	/**
	 * Get the Book set with this Reference.
	 *
	 * @return the reference's Book
	 */
	public Book getBook() {
		return book;
	}

	/**
	 * Get the chapter set with this Reference.
	 *
	 * @return the reference's chapter
	 */
	public int getChapter() {
		return chapter;
	}

	/**
	 * Get the list of verses set with this Reference.
	 *
	 * @return the reference's verses
	 */
	public List<Integer> getVerses() {
		return verses;
	}

	/**
	 * Get a well-formatted String representation of this Reference. It contains all information
	 * except the set Bible, and can be parsed in a Builder to create an equivalent object, assuming
	 * the same Bible is set on that Builder as well.
	 *
	 * @return a well-formatted String representation of this Reference
	 */
	@Override
	public String toString() {
		String refString = book.getName();

		if(TextUtils.isEmpty(refString))
			return "";

		refString += " " + chapter;

		if(verses.size() == 0)
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
	/**
	 * Compares two Reference with respect to natural reference order, according to the first
	 * verse. Defines a natural ordering of verses based on their ordering within the Bible, and
	 * allows a list of References to be sorted, which by extension, makes it easy to sort a list of
	 * verses.
	 *
	 * RETURN VALUES (negative indicates lhs is less than rhs)
	 * 0: Verses are equal, since they point to the same verse
	 * 1: Verses are adjacent
	 * 2: Verses are not adjacent, but are in the same chapter
	 * 3: Verses are not adjacent, but are in different chapters of the same Book
	 * 4: Verses are not adjacent, and aren't even in the same Book
	 *
	 * @param rhs the Reference to compare to this verse
	 * @return result of comparison
	 */
	@Override
	public int compareTo(@NonNull Reference rhs) {
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
						return 0; //this means lhs.verses.get(0) == rhs.verses.get(0)
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();

			return lhs.toString().compareTo(rhs.toString());
		}
	}

	/**
	 * Compare two References for equality.
	 *
	 * @param other the Reference to compare
	 * @return true if the two References contain equivalent Books, chapters, and verse lists. False otherwise
	 */
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(!(other instanceof Reference)) {
			return false;
		}

		Reference ref = (Reference) other;

		if(!this.book.equals(ref.book)) {
			return false;
		}
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

	/**
	 * Generate a hashcode for this reference, based on the Book's hashcode, the chapter, and the verses.
	 *
	 * @return the hashcode
	 */
	public int hashCode() {
		int result = book.getName().hashCode();
		result = 31 * result + chapter;
		result = 31 * result + verses.hashCode();
		return result;
	}

//Builder class
//--------------------------------------------------------------------------------------------------
	/**
	 * Helper class to iteratively create a Reference.
	 *
	 * A Builder allows a user to set individual properties one at a time and check the validity of
	 * the Reference at any point before creating the Reference. As properties are set, bit flags
	 * maintain the state of data, which is necessary to ultimately create a valid Reference. These
	 * flags are set or unset as necessary by the Builder, but may optionally be manually set as well.
	 *
	 * @see Reference
	 */
	public static class Builder {
		public static int DEFAULT_BIBLE_FLAG = 0x1;
		public static int DEFAULT_BOOK_FLAG = 0x2;
		public static int DEFAULT_CHAPTER_FLAG = 0x4;
		public static int DEFAULT_VERSES_FLAG = 0x8;
		public static int PREVENT_AUTO_ADD_VERSES_FLAG = 0x10;
		public static int PARSED = 0x20;
		public static int PARSE_SUCCESS = 0x40;
		public static int PARSE_FAILURE = 0x80;

		private int flags = 0;

		/**
		 * Sets the given flag to true.
		 *
		 * @param flag  the bit flag to set
		 */
		public void setFlag(int flag) {
			flags = flags | flag;
		}

		/**
		 * Sets the given flag to false.
		 *
		 * @param flag  the bit flag to unset
		 */
		public void unsetFlag(int flag) {
			flags = (flags & ~flag);
		}

		/**
		 * Check to see if a given flag has been set.
		 *
		 * @param flag  the bit flag to check
		 */
		public boolean checkFlag(int flag) {
			return ((flags & flag) == flag);
		}

		@NonNull private Bible bible;
		@NonNull private Book book;
		private int chapter;
		@NonNull private ArrayList<Integer> verses;

		/**
		 * Create a new Builder with all properties set to their default values.
		 */
		public Builder() {
			this.bible = new SimpleBible();
			this.book = new SimpleBook();
			chapter = 1;
			this.verses = new ArrayList<>();

			setFlag(DEFAULT_BIBLE_FLAG);
			setFlag(DEFAULT_BOOK_FLAG);
			setFlag(DEFAULT_CHAPTER_FLAG);
			setFlag(DEFAULT_VERSES_FLAG);
		}

		/**
		 * Set the Bible to its default value, an empty SimpleBible.
		 *
		 * @return this Builder, for chaining
		 * @see SimpleBible
		 */
		public Builder setDefaultBible() {
			this.bible = new SimpleBible();

			setFlag(DEFAULT_BIBLE_FLAG);
			return this;
		}

		/**
		 * Set the Book to its default value, and empty SimpleBook.
		 *
		 * @return this Builder, for chaining
		 * @see SimpleBook
		 */
		public Builder setDefaultBook() {
			this.book = new SimpleBook();

			setFlag(DEFAULT_BOOK_FLAG);
			return this;
		}

		/**
		 * Set the chapter to its default value, 1.
		 *
		 * @return this Builder, for chaining
		 */
		public Builder setDefaultChapter() {
			this.chapter = 1;

			setFlag(DEFAULT_CHAPTER_FLAG);
			return this;
		}

		/**
		 * Set the verse to its default value, an empty list.
		 *
		 * @return this Builder, for chaining
		 */
		public Builder setDefaultVerses() {
			this.verses = new ArrayList<>();

			setFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

		/**
		 * Get the Bible set with this Builder.
		 *
		 * @return the reference's Bible
		 */
		public @NonNull Bible getBible() {
			return bible;
		}

		/**
		 * Get the Book set with this Builder.
		 *
		 * @return the reference's Bible
		 */
		public @NonNull Book getBook() {
			return book;
		}

		/**
		 * Get the chapter set with this Builder.
		 *
		 * @return the reference's Bible
		 */
		public int getChapter() {
			return chapter;
		}

		/**
		 * Get the list of verses set with this Reference.
		 *
		 * @return the reference's Bible
		 */
		public @NonNull List<Integer> getVerses() {
			return verses;
		}

		/**
		 * Parse a String reference into this Builder, using the current Bible to match Book names against.
		 * <p>
		 * Parsing a reference will fully extract the book (either taken directly from the set Bible,
		 * or failing that, creating a {@link SimpleBook}), the chapter, and an arbitrary list of
		 * verses from that book. See {@link ReferenceParser} for the expected format of a Reference.
		 *
		 * @param reference  A string input to be fully parsed into this Builder
		 * @return this Builder, for chaining
		 *
		 * @see ReferenceParser
		 */
		public Builder parseReference(String reference) {
			//first, set some flags to preserve that will help in determining success of parse
			boolean defaultBible = checkFlag(DEFAULT_BIBLE_FLAG);
			boolean autoAddVerses = checkFlag(PREVENT_AUTO_ADD_VERSES_FLAG);

			//clear the builder so no data can accidentally stay from a previous parse
			//keep the set Bible since that is not able to be parsed from a string (yet)
			setDefaultBook();
			setDefaultChapter();
			setDefaultVerses();

			//unset all flags and but flag this builder as being parsed
			flags = 0;
			setFlag(PARSED);

			ReferenceParser parser = new ReferenceParser(this);
			parser.getPassageReference(reference);

			//if adding verses is allowed, all we need for a successful parse is a non-default book
			//and chapter. If adding verses is prevented, we need non-default verses too
			if(autoAddVerses) {
				if(!checkFlag(DEFAULT_BOOK_FLAG) && !checkFlag(DEFAULT_CHAPTER_FLAG)) {
					setFlag(PARSE_SUCCESS);
					unsetFlag(PARSE_FAILURE);
				}
				else {
					unsetFlag(PARSE_SUCCESS);
					setFlag(PARSE_FAILURE);
				}
			}
			else {
				if(!checkFlag(DEFAULT_BOOK_FLAG) && !checkFlag(DEFAULT_CHAPTER_FLAG) && !checkFlag(DEFAULT_VERSES_FLAG)) {
					setFlag(PARSE_SUCCESS);
					unsetFlag(PARSE_FAILURE);
				}
				else {
					unsetFlag(PARSE_SUCCESS);
					setFlag(PARSE_FAILURE);
				}
			}

			//restore the state of flags that couldn't be modified by the parser
			if(defaultBible)
				setFlag(DEFAULT_BIBLE_FLAG);
			else
				unsetFlag(DEFAULT_BIBLE_FLAG);

			if(autoAddVerses)
				setFlag(PREVENT_AUTO_ADD_VERSES_FLAG);
			else
				unsetFlag(PREVENT_AUTO_ADD_VERSES_FLAG);

			return this;
		}

		/**
		 * Creates a Book for this Builder based on the set Bible. Whether the bookName can be found
		 * within the Bible is up to the implementation. If the Bible is not set, it will be set to
		 * its default value before proceeding. If the Bible is set to default, or if no match is
		 * found within the set Bible, then a new {@link SimpleBook} will be created using the bookName.
		 *
		 * @param bookName  the full name of the Book to search for in the set Bible
		 * @return this Builder, for chaining
		 *
		 * @see SimpleBible
		 * @see SimpleBook
		 */
		public Builder setBook(@NonNull String bookName) {
			Book book = bible.parseBook(bookName);

			if(book == null) {
				book = new SimpleBook();
				book.setName(bookName);
				setFlag(DEFAULT_BOOK_FLAG);
			}
			else {
				unsetFlag(DEFAULT_BOOK_FLAG);
			}

			this.book = book;

			return this;
		}

		/**
		 * Set the Bible of this builder.
		 *
		 * @param bible  the Bible to set
		 * @return this Builder, for chaining
		 */
		public Builder setBible(@NonNull Bible bible) {
			this.bible = bible;

			unsetFlag(DEFAULT_BIBLE_FLAG);
			return this;
		}

		/**
		 * Set the Book of this builder.
		 *
		 * @param book  the Book to set
		 * @return this Builder, for chaining
		 */
		public Builder setBook(Book book) {
			if(book != null) {
				this.book = book;
				unsetFlag(DEFAULT_BOOK_FLAG);
			}
			else {
				setDefaultBook();
			}

			return this;
		}

		/**
		 * Set the chapter of this builder, validating its range against the given Bible
		 *
		 * @param chapter  the chapter to set
		 * @return this Builder, for chaining
		 */
		public Builder setChapter(int chapter) {

			if(getBook().validateChapter(chapter)) {
				this.chapter = chapter;
				unsetFlag(DEFAULT_CHAPTER_FLAG);
			}
			else {
				//if there exists chapters in the selected book and the given chapter is too high,
				//assume the user wanted the last chapter in the book. Otherwise, default to 1
				if(getBook().numChapters() >= 1 && chapter > getBook().numChapters()) {
					this.chapter = getBook().numChapters();
				}
				else {
					this.chapter = 1;
				}
				setFlag(DEFAULT_CHAPTER_FLAG);
			}

			return this;
		}

		/**
		 * Set the verses of this builder as a varargs array of verse numbers.
		 *
		 * @param verses  the verses to set
		 * @return this Builder, for chaining
		 */
		public Builder setVerses(int... verses) {
			//invalid array of verses given, we can do nothing further
			if(verses == null || verses.length == 0) {
				setDefaultVerses();
				return this;
			}

			this.verses = new ArrayList<>();
			for(int verse : verses) {
				addVerse(verse);
			}

			unsetFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

		/**
		 * Set the verses of this builder from a Collection.
		 *
		 * @param verses  the verses to set
		 * @return this Builder, for chaining
		 */
		public Builder setVerses(Collection<Integer> verses) {
			//invalid collection of verses given, we can do nothing further
			if(verses == null || verses.size() == 0) {
				setDefaultVerses();
				return this;
			}

			this.verses = new ArrayList<>();
			for(int verse : verses) {
				addVerse(verse);
			}

			unsetFlag(DEFAULT_VERSES_FLAG);
			return this;
		}

		/**
		 * Adds a single verse to this Builder's list of verses.
		 *
		 * @param verse  the verse to add
		 * @return this Builder, for chaining
		 */
		public Builder addVerse(int verse) {
			if(getBook().validateVerseInChapter(chapter, verse)) {
				if(!verses.contains(verse)) {
					this.verses.add(verse);
				}
				unsetFlag(DEFAULT_VERSES_FLAG);
			}
			else {
				//we cannot validate the verse because no book has been created yet or we do not know
				//the chapter in the book to validate against, so add verse indiscriminately

				if(verse < 1) {
					verse = 1;
					setFlag(DEFAULT_VERSES_FLAG);
				}
				else {
					unsetFlag(DEFAULT_VERSES_FLAG);
				}

				if(!verses.contains(verse)) {
					this.verses.add(verse);
				}
			}

			return this;
		}

		/**
		 * Adds all verses in the set chapter from the set Books to this Builder.
		 * @return this Builder, for chaining
		 */
		public Builder addAllVersesInChapter() {
			if(book.getChapters() != null
					&& book.getChapters().size() > 0
					&& chapter >= 0
					&& chapter < book.getChapters().size())
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
		 * Creates a Reference from the current state of this Builder. Any null values will be set
		 * to their defaults, which will likely not yield a meaningful Reference, but will still be
		 * considered valid. IF the PREVENT_AUTO_ADD_VERSES_FLAG has been set, then the absence of
		 * set verses will not add any to the final Reference. If the PREVENT_AUTO_ADD_VERSES_FLAG
		 * is not set, but a chapter has been given, then all verses in that chapter will be added,
		 * otherwise, just the first verse will be added.
		 *
		 * @return a Reference containing the data of this Builder.
		 */
		public Reference create() {
			if(verses.size() == 0) {
				if(!checkFlag(PREVENT_AUTO_ADD_VERSES_FLAG)) {
					if(chapter > 0) {
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

			return new Reference(bible, book, chapter, verses);
		}
	}
}
