package com.caseybrooks.androidbibletools.data;

import com.caseybrooks.androidbibletools.enumeration.Book;
import com.caseybrooks.androidbibletools.io.ReferenceParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

public class Reference implements Comparable<Reference> {
    public final Book book;
    public final int chapter;
    public final int verse;
//    public final boolean isValid;
    public final ArrayList<Integer> verses;

//Parse the input string using recursive descent parsing
//------------------------------------------------------------------------------
    public Reference(Book book, int chapter, int... verses) {
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

	public Reference(Book book, int chapter, ArrayList<Integer> verses) {
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

	public static Reference parseReference(String reference) throws ParseException {
		ReferenceParser parser = new ReferenceParser();

		return parser.getPassageReference(reference);
	}

//    public Reference(String expression) throws ParseException {
//        this(new TokenStream(expression));
//    }
//
//    private Reference(TokenStream ts) throws ParseException {
//        String expression = ts.toString();
//        this.ts = ts;
//        Book book = book();
//        if(book != null) {
//            int chapter = chapter();
//            if(chapter != 0) {
//                ArrayList<Integer> verseList = verseList();
//
//                if(verseList != null && verseList.size() > 0) {
//                    this.book = book;
//                    this.chapter = chapter;
//                    Collections.sort(verseList);
//                    this.verses = verseList;
//                    this.verse = verseList.get(0);
//
//                    isValid = validate();
//                }
//                else {
//                    throw new ParseException("'" + expression + "' is not formatted correctly(verseList)", 3);
//                }
//            }
//            else {
//                throw new ParseException("'" + expression + "' is not formatted correctly(chapter)", 2);
//            }
//        }
//        else {
//            throw new ParseException("'" + expression + "' is not formatted correctly(book)", 1);
//        }
//    }

//    public boolean validate() {
//        if(chapter > book.numChapters() || chapter < 1) return false;
//        if(verses != null && verses.size() > 0) {
//            for (Integer i : verses) {
//                if (i > book.numVersesInChapter(chapter) || i < 1) return false;
//            }
//        }
//        else {
//            if (verse > book.numVersesInChapter(chapter) || verse < 1) return false;
//        }
//
//        return true;
//    }
//
//    public static Reference extractReference(String reference) {
//		TokenStream streamBase = new TokenStream(reference);
//		TokenStream stream = new TokenStream(reference);
//
//        while(stream.toString().length() > 0) {
//            try {
//                Reference ref = new Reference(stream);
//                return ref;
//            }
//            catch(ParseException e) {
//                streamBase.get();
//                stream = new TokenStream(streamBase.toString());
//                continue;
//            }
//        }
//
//        return null;
//    }

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

        //get the position of each book as an integer so we can work with it
        int aBook = -1, bBook = -1;
        for(int i = 0; i < Book.values().length; i++) {
            if(Book.values()[i] == lhs.book) aBook = i;
            if(Book.values()[i] == rhs.book) bBook = i;
        }

        if(aBook - bBook == 1) {
            if((lhs.chapter == 1 && lhs.verses.get(0) == 1) &&
                    (rhs.chapter == rhs.book.numChapters() &&
                            (rhs.verses.get(0) == rhs.book.numVersesInChapter(rhs.chapter)))) return 1;
            else return 4;
        }
        else if(aBook - bBook == -1) {
            if((rhs.chapter == 1 && rhs.verses.get(0) == 1) &&
                    (lhs.chapter == lhs.book.numChapters() &&
                            (lhs.verses.get(0) == lhs.book.numVersesInChapter(lhs.chapter)))) return -1;
            else return -4;
        }
        else if(aBook > bBook) return 4;
        else if(aBook < bBook) return -4;
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

    public boolean equals(Reference ref) {
        if(ref == null) return false;
        if(this.book != ref.book) return false;
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

	public boolean equals(Object ref) {
		if(ref instanceof Reference) return this.equals((Reference) ref);
		else return false;
	}

    public int hashCode() {
        int result = book.hashCode();
        result = 31 * result + chapter;
        result = 31 * result + (verses != null ? verses.hashCode() : 0);
        return result;
    }
}
