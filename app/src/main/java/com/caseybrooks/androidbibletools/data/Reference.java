package com.caseybrooks.androidbibletools.data;

import com.caseybrooks.androidbibletools.io.ReferenceParser;

import java.util.ArrayList;
import java.util.Collections;

public class Reference implements Comparable<Reference> {
    public final Book book;
    public final int chapter;
    public final int verse;
    public final ArrayList<Integer> verses;

//Make constructors private. Force caller to use of the static initializer methods
//------------------------------------------------------------------------------
    private Reference(Book book, int chapter, int... verses) {
		this.book = book;
		this.chapter = chapter;
		if(verses.length == 1) {
			this.verses = new ArrayList<>();
			this.verses.add(verses[0]);
			verse = verses[0];
		}
		else {
			verse = 0;
			this.verses = new ArrayList<>();

			for(int i : verses) {
				this.verses.add(i);
			}

			Collections.sort(this.verses);
		}
    }

	private Reference(Book book, int chapter, ArrayList<Integer> verses) {
		this.book = book;
		this.chapter = chapter;
		if(verses.size() == 1) {
			this.verses = new ArrayList<>();
			this.verses.add(verses.get(0));
			verse = verses.get(0);
		}
		else {
			verse = 0;
			this.verses = verses;
			Collections.sort(this.verses);
		}
	}

//static initializer methods
//------------------------------------------------------------------------------
	/**
	 * Builder class to aid in creating a reference, especially when working with
	 * user input. The Builder offers much flexibility is the data that is used to
	 * construct a Reference, and will do whatever it needs to return some Reference.
	 * In any situation where insufficient information is given, it chooses to use
	 * default values or create dummy Books with a not-real book name so that the
	 * user will at least have something to work with. In this sense, arbitrary
	 * user input will be accepted and a Reference will be created, but there is
	 * no guarantee that the verse can be downloaded or have much meaningful information
	 * associated with it.
	 */
	public static class Builder {
		private Bible bible;
		private Book book;

		private int chapter;
		private ArrayList<Integer> verses;

		public Builder() {
			verses = new ArrayList<>();
		}

		/**
		 * Attempts to parse the full String into this Builder, extracting the
		 * book, chapter, and all verses and populating this Builder with the
		 * extracted information.
		 *
		 * @param reference the String reference to be parsed
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
		 * @return this Builder object
		 */
		public Builder setBook(Book book) {
			this.book = book;

			return this;
		}

		/**
		 * Attempts to parse the bookName according to the bible that was set.
		 * If that fails, it tries to parse against the default ESV. Failing both,
		 * it simply creates a Book with the given bookName, but this book
		 * cannot be downloaded because we can't quite say which verse it is in
		 * the Bible.
		 *
		 * @param bookName the bookname to be parsed
		 * @return this Builder object
		 */
		public Builder setBook(String bookName) {
			Book parsedBook = null;
			if(bible != null) {
				parsedBook = bible.parseBook(bookName);
			}

			if(parsedBook == null) {
				parsedBook = new Bible(null).parseBook(bookName);

				if(parsedBook == null) {
					parsedBook = new Book(null);
					parsedBook.setName(bookName);
				}
			}

			book = parsedBook;

			return this;
		}

		/**
		 * Attempts to parse the bookName according to the bible that was set.
		 * If that fails, it tries to parse against the default ESV. Failing both,
		 * it simply creates a Book with the given bookName and bookId. The bookId
		 * is a unique identifier that allows this reference to be downloaded
		 *
		 * @param bookName the bookname to be parsed
		 * @param bookId the unique identifier of the book which enabled downloading it
		 * @return this Builder object
		 */
		public Builder setBook(String bookName, @Optional String bookId) {
			Book parsedBook = null;
			if(bible != null) {
				parsedBook = bible.parseBook(bookName);
			}

			if(parsedBook == null) {
				parsedBook = new Bible(null).parseBook(bookName);

				if(parsedBook == null) {
					if(bookId != null) {
						parsedBook = new Book(bookId);
					}
					else {
						parsedBook = new Book(null);
					}
					parsedBook.setName(bookName);
				}
			}

			book = parsedBook;

			return this;
		}

		/**
		 * Makes no attempt to parse the bookName, but instead just creates a Book
		 * with the given parameters.
		 *
		 * @param bookName the name of the book
		 * @param bookId (optional) the unique identifer of the book to enable downloading
		 * @param bookAbbr (optional) the book's abbreviated name. If not specified, the first three letters of the name will be used
		 * @param chapters (optional) the list of chapters in this Book
		 * @return this Builder object
		 */
		public Builder setBook(
				String bookName,
				@Optional String bookId,
				@Optional String bookAbbr,
				@Optional int order,
				@Optional int[] chapters) {

			Book parsedBook;
			if(bookId != null) {
				parsedBook = new Book(bookId);
			}
			else {
				parsedBook = new Book(null);
			}

			parsedBook.setName(bookName);

			if(bookAbbr != null) {
				parsedBook.setAbbr(bookAbbr);
			}
			else {
				parsedBook.setAbbr(bookName.substring(0, 3));
			}

			parsedBook.setChapters(chapters);

			if(order != 0) {
				parsedBook.setOrder(order);
			}
			else {
				//choose arbitrarily high order to ensure it will always be
				//sorted to the end of a list of verses
				parsedBook.setOrder(10000);
			}

			book = parsedBook;

			return this;
		}

		/**
		 * Sets the Bible of this Builder to the given Bible object
		 *
		 * @param bible the Bible to be set
		 * @return this Builder object
		 */
		public Builder setBible(Bible bible) {
			this.bible = bible;
			return this;
		}

		/**
		 * Creates a new Bible with the given name. This Bible cannot be used to
		 * download verses, but just provides a convenient way to accept arbitrary
		 * versions from user input as valid versions. If no abbreviation is given,
		 * one will be created from the first letter of each word in the version
		 * name, or if it is only one word, it will be copied over.
		 *
		 * @param versionName the name of this Bible
		 * @param versionAbbr (optional) the abbreviation of this Bible
		 * @return this Builder object
		 */
		public Builder setBible(String versionName, @Optional String versionAbbr) {
			this.bible = new Bible(null);
			this.bible.name = versionName;
			return this;
		}

		/**
		 * Creates a new Bible with the given name and versionId. The versionId
		 * is what enables verses to be downloaded, but it is not necessary if you
		 * just want to work with raw user data and not allow downloading from
		 * this Bible. If no abbreviation is given, one will be created from the
		 * first letter of each word in the version name, or if it is only one
		 * word, it will be copied over.
		 *
		 * @param versionName the name of this Bible
		 * @param versionAbbr (optional) the abbreviation of this Bible
		 * @param versionId (optional) the unique identifier of this Bible which enables downloading
		 * @return this Builder object
		 */
		public Builder setBible(String versionName, @Optional String versionAbbr, @Optional String versionId) {
			if(versionId != null) {
				this.bible = new Bible(versionId);
			}
			else {
				this.bible = new Bible(null);
			}

			this.bible.name = versionName;
			return this;
		}

		/**
		 * A reference can point to a single chapter, so set that chapter.
		 *
		 * @param chapter the chapter to set
		 * @return this Builder object
		 */
		public Builder setChapter(int chapter) {
			this.chapter = chapter;
			return this;
		}

		/**
		 * Set the list of verses in the specified chapter for the reference. Any
		 * previously set verses will be removed
		 *
		 * @param verses an array of verses to be set
		 * @return
		 */
		public Builder setVerses(int... verses) {
			this.verses = new ArrayList<>();
			for(int verse : verses) {
				this.verses.add(verse);
			}
			return this;
		}

		/**
		 * Add a verse to the current list of verses if it has not already been added
		 *
		 * @param verse the verse to add
		 * @return this Builder object
		 */
		public Builder addVerse(int verse) {
			if(!verses.contains(verse)) {
				this.verses.add(verse);
			}
			return this;
		}

		/**
		 * Given the current state of this Builder, create a Reference. It will
		 * always produce a non-null reference, but that reference may have default
		 * values if you were not careful and did not create it well enough. Ideally,
		 * some reference is better than none, because the reference returned should
		 * at least contain most of the information you put in, which makes it
		 * easier to determine what was not valid and fix it.
		 *
		 * @return the reference that was incrementally built by this Builder
		 */
		public Reference create() {
			if(verses == null || verses.size() == 0) {
				//no verses, but chapter was given.
				if(chapter != 0 ) {

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
					chapter = 1;
					verses = new ArrayList<>();
					verses.add(1);
				}
			}

			//the book was not specified, so use the first book of the New Testament
			// since many translations only have the New Testament.
			if(book == null) {
				setBook("Matthew");
			}


			return new Reference(book, chapter, verses);
		}
	}

    @Override
    public String toString() {
        String refString = book.getName();
        refString += " " + chapter;
        refString += ":";

		if(verses.size() == 0) {
			refString += verse;
		}
        else if(verses.size() == 1) {
            refString += verse;
        }
        else {
            refString += verses.get(0);
            int lastVerse = verses.get(0);

            int i = 1;
            while(i < verses.size()) {
                if(verses.get(i) == lastVerse + 1) {
                    refString += "-";
                    while(true) {
                        if (i < verses.size() && verses.get(i) == lastVerse + 1) {
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

        if(lhs.book.getOrder() - rhs.book.getOrder() == 1) {
            if((lhs.chapter == 1 && lhs.verses.get(0) == 1) &&
                    (rhs.chapter == rhs.book.numChapters() &&
                            (rhs.verses.get(0) == rhs.book.numVersesInChapter(rhs.chapter)))) return 1;
            else return 4;
        }
        else if(lhs.book.getOrder() - rhs.book.getOrder() == -1) {
            if((rhs.chapter == 1 && rhs.verses.get(0) == 1) &&
                    (lhs.chapter == lhs.book.numChapters() &&
                            (lhs.verses.get(0) == lhs.book.numVersesInChapter(lhs.chapter)))) return -1;
            else return -4;
        }
        else if(lhs.book.getOrder() > rhs.book.getOrder()) return 4;
        else if(lhs.book.getOrder() < rhs.book.getOrder()) return -4;
        else {
			//TODO: get reference comparing for Verse objects (verses == null, verse is defined)
            //same book
            if(lhs.chapter - rhs.chapter == 1) {
                if((lhs.verses.get(0) == 1) &&
                        (rhs.verses.get(0) == rhs.book.numVersesInChapter(rhs.chapter))) return 1;
                else return 3;
            }
            if(lhs.chapter - rhs.chapter == -1) {
                if((rhs.verses.get(0) == 1) &&
                        (lhs.verses.get(0) == lhs.book.numVersesInChapter(lhs.chapter))) return -1;
                else return -3;
            }
            else if(lhs.chapter > rhs.chapter) return 3;
            else if(lhs.chapter < rhs.chapter) return -3;
            else {
                //same chapter
                if(lhs.verses.get(0) - rhs.verses.get(0) == 1) return 1;
                else if(lhs.verses.get(0) - rhs.verses.get(0) == -1) return -1;
                else if(lhs.verses.get(0) > rhs.verses.get(0)) return 2;
                else if(lhs.verses.get(0) < rhs.verses.get(0)) return -2;
                else return 0; //lhs.verses.get(0) == rhs.verses.get(0)
            }
        }
    }

    public boolean equals(Object other) {
		if(other == null) return false;
		if(!(other instanceof Reference)) return false;

		Reference ref = (Reference) other;

//		if(this.book != ref.book) return false;
		if(this.chapter != ref.chapter) return false;
		if(this.verses.size() != ref.verses.size()) return false;
		if(this.verse != ref.verse) return false;

		for(Integer i : this.verses) {
			if(!ref.verses.contains(i)) return false;
		}
		for(Integer i : ref.verses) {
			if(!this.verses.contains(i)) return false;
		}

		return true;

    }

    public int hashCode() {
        int result = book.hashCode();
        result = 31 * result + chapter;
        result = 31 * result + ((verses != null) ? verses.hashCode() : 0);
        return result;
    }
}
