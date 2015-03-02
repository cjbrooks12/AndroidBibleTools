package com.caseybrooks.androidbibletools.io;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.enumeration.Book;

import java.text.ParseException;
import java.util.ArrayList;

/** The class used to parse a String into Reference objects. Uses the following grammar
 *
 *
 * Passage ::= book chapter ((punctuation) verseList)
 * Verse ::= book chapter verse
 *
 * number ::= { [0..9] }
 * word ::= { [a..zA..Z] }
 * punctuation ::= [;:,.]
 *
 * book ::= ([123]) word (punctuation)
 * chapter ::= number
 * verse ::= number
 *
 * verseSequence ::= verse punctuation verse
 * verseList ::= (punctuation) { [verse | verseSequence] punctuation } [verse | verseSequence]
 *
 *
 * ParseException Error Codes:
 * 1: could not determine the name of a book from the given input string
 */




public class ReferenceParser {
	String reference;
	public ArrayList<Integer> verses;
	TokenStream ts;

	//verse ::= book chapter (punctuation) verse
	public Reference getVerseReference(String reference) throws ParseException {
		this.reference = reference;
		ts = new TokenStream(reference);

		//Book will throw its own exception if it fails to parse
		Book book = book();

		int chapter = chapter();
		if(chapter == -1) {
			throw new ParseException("Cannot parse Verse [" + reference + "]: expected number after book", 2);
		}

		punctuation();

		int verse = verse();
		if(verse == -1) {
			throw new ParseException("Cannot parse Verse [" + reference + "]: expected a number after chapter", 2);
		}

		return new Reference(book, chapter, verse);
	}

	//Passage ::= book chapter ((punctuation) verseList)
	public Reference getPassageReference(String reference) throws ParseException {
		this.reference = reference;
		ts = new TokenStream(reference);

		//Book will throw its own exception if it fails to parse
		Book book = book();

		int chapter = chapter();
		if(chapter == -1) {
			throw new ParseException("Cannot parse Verse [" + reference + "]: expected number after book", 2);
		}

		boolean hasPunctuation = punctuation();
		ArrayList<Integer> verseList;

		if(hasPunctuation) {
			verseList = verseList();
			if(verseList.size() > 0) {
				return new Reference(book, chapter, verseList);
			}
			else {
				throw new ParseException("Cannot parse Verse [" + reference + "]: expected verse list after book", 2);
			}
		}
		else {
			verseList = verseList();

			//just given the chapter, add all verses in that chapter
			if(verseList == null || verseList.size() == 0) {
				verseList = new ArrayList<>();

				for(int i = 1; i <= book.numVersesInChapter(chapter); i++) {
					verseList.add(i);
				}
			}

			return new Reference(book, chapter, verseList);
		}
	}

	public ArrayList<Passage> extractReferences(String reference) {
		this.reference = reference;

		return null;
	}

	private boolean punctuation() {
		Token a = ts.get();
		if(a != null && (
				a.equals(Token.Type.COLON) ||
				a.equals(Token.Type.COMMA) ||
				a.equals(Token.Type.DOT) ||
				a.equals(Token.Type.SEMICOLON))) {

			return true;
		}
		else {
			ts.unget(a);
			return false;
		}
	}

	//book ::= ([123]) word (punctuation)
	private Book book() throws ParseException {
		Token a = ts.get();

		//book ::= [123] word (.)
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() <= 3 && a.getIntValue() > 0) {
			Token b = ts.get();
			if(b != null && b.equals(Token.Type.WORD)) {
				Book book = Book.parseBook(a.getIntValue() + " " + b.getStringValue());

				if(book != null) {
					punctuation();
					return book;
				}
				else {
					throw new ParseException("[" + b.getStringValue() + "] is not a valid name for Book", 1);
				}
			}
			else {
				ts.unget(b);
				ts.unget(a);
				return null;
			}
		}

		//book ::= word (punctuation)
		if(a != null && a.equals(Token.Type.WORD)) {
			Book book = Book.parseBook(a.getIntValue() + " " + a.getStringValue());

			if(book != null) {
				punctuation();
				return book;
			}
			else {
				throw new ParseException("[" + a.getStringValue() + "] is not a valid name for Book", 1);
			}
		}
		else {
			ts.unget(a);
			return null;
		}
	}

	//chapter ::= number
	private int chapter() {
		Token a = ts.get();
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() > 0) {
			return a.getIntValue();
		}
		else {
			ts.unget(a);
			return -1;
		}
	}

	//verse ::= number
	private int verse() {
		Token a = ts.get();
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() > 0) {
			return a.getIntValue();
		}
		else {
			ts.unget(a);
			return -1;
		}
	}

	//verseSequence ::= verse punctuation verse
	private ArrayList<Integer> verseSequence() {
		int a = verse();
		if(a != -1) {
			boolean hasPunctuation = punctuation();

			if(hasPunctuation) {
				int b = verse();
				if(b != -1) {
					ArrayList<Integer> verses = new ArrayList<>();
					for(int i = a; i <= b; i++) {
						verses.add(i);
					}
					return verses;
				}
				else {
					ts.unget(new Token(Token.Type.NUMBER, a));
				}
			}
			else {
				ts.unget(new Token(Token.Type.NUMBER, a));
			}
		}

		return new ArrayList<>();
	}

	//verseList ::= (punctuation) { [verse | verseSequence] punctuation } [verse | verseSequence]
    private ArrayList<Integer> verseList() {
        ArrayList<Integer> verseList = new ArrayList<Integer>();

		punctuation();

		while (true) {
			ArrayList<Integer> b = verseSequence();
			if (b.size() > 0) {
				for(Integer i : b) {
					if(!verseList.contains(i)) {
						verseList.add(i);
					}
				}
			}
			else {
				int c = verse();
				if (c != -1 && !verseList.contains(c)) {
					verseList.add(c);
				}
			}

			boolean hasPunctuation = punctuation();
			if(!hasPunctuation) {
				return verseList;
			}
		}
	}
}
