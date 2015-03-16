package com.caseybrooks.androidbibletools.io;

import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.enumeration.BookEnum;

import java.text.ParseException;
import java.util.ArrayList;

/** The class used to parse a String into Reference objects. Uses the following grammar
 *
 * Passage ::= book (punctuation) chapter ((punctuation) verseList)
 * Verse ::= book (punctuation) chapter (punctuation) verse
 *
 * number ::= { [0..9] }
 * word ::= { [a..zA..Z] }
 * punctuation ::= [;:,.-\/]
 *
 * book ::= ([123]) word
 * chapter ::= number
 * verse ::= number
 *
 * verseSequence ::= verse punctuation verse
 * verseList ::= { [verse | verseSequence] punctuation }
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
		BookEnum book = book();

		punctuation();

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

	//Passage ::= book (punctuation) chapter ((punctuation) verseList)
	public Reference getPassageReference(String reference) throws ParseException {
		this.reference = reference;
		return getPassageReference(new TokenStream(reference));
	}

	public Reference getPassageReference(TokenStream reference) throws ParseException {
		ts = reference;

		//Book will throw its own exception if it fails to parse
		BookEnum book = book();

		if(book == null) {
			throw new ParseException("Cannot parse Passage [" + reference + "]: could not match book name", 2);
		}

		punctuation();

		int chapter = chapter();
		if(chapter == -1) {
			throw new ParseException("Cannot parse Passage [" + reference + "]: expected number after book", 2);
		}

		boolean hasPunctuation = punctuation();
		ArrayList<Integer> verseList;

		if(hasPunctuation) {
			verseList = verseList();

			if(verseList.size() > 0) {
				return new Reference(book, chapter, verseList);
			}
			else {
				throw new ParseException("Cannot parse Passage [" + reference + "]: expected verse list after book", 2);
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

	public static Reference extractReferences(String reference)  {
		TokenStream streamBase = new TokenStream(reference);
		TokenStream stream = new TokenStream(reference);

		while(stream.toString().length() > 0) {
			try {
				return new ReferenceParser().getPassageReference(stream);
			}
			catch(ParseException e) {
				streamBase.get();
				stream = new TokenStream(streamBase.toString());
				continue;
			}
		}


		return null;
	}

	//punctuation ::= [;:,.-\/]
	private boolean punctuation() {
		Token a = ts.get();
		if(a != null && (
				a.equals(Token.Type.COLON) ||
				a.equals(Token.Type.SEMICOLON) ||
				a.equals(Token.Type.COMMA) ||
				a.equals(Token.Type.DOT) ||
				a.equals(Token.Type.DASH) ||
				a.equals(Token.Type.SLASH) ||
				a.equals(Token.Type.BACKSLASH))) {

			return true;
		}
		else {
			ts.unget(a);
			return false;
		}
	}

	//book ::= ([123]) word
	private BookEnum book() throws ParseException {
		Token a = ts.get();

		//book ::= [123] word (.)
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() <= 3 && a.getIntValue() > 0) {
			Token b = ts.get();
			if(b != null && b.equals(Token.Type.WORD)) {
				BookEnum book = BookEnum.parseBook(a.getIntValue() + " " + b.getStringValue());

				if(book != null) {
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
			BookEnum book = BookEnum.parseBook(a.getStringValue());

			if(book != null) {
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

	//verseSequence ::= verse dash verse
	private ArrayList<Integer> verseSequence() {
		int a = verse();
		if(a != -1) {
			ArrayList<Integer> verses = new ArrayList<>();
			Token dash = ts.get();

			if(dash != null && dash.equals(Token.Type.DASH)) {
				int b = verse();
				if(b != -1) {
					for(int i = a; i <= b; i++) {
						verses.add(i);
					}
					return verses;
				}
				else {
					ts.unget(dash);
					ts.unget(new Token(Token.Type.NUMBER, a));
				}
			}
			else {
				ts.unget(dash);
				ts.unget(new Token(Token.Type.NUMBER, a));
			}
		}

		return new ArrayList<>();
	}

	//verseList ::= { [verse | verseSequence] comma }
	private ArrayList<Integer> verseList() {
		ArrayList<Integer> verseList = new ArrayList<>();
		while (true) {
			ArrayList<Integer> b = verseSequence();
			if (b != null && b.size() > 0) {
				for(Integer i : b) {
					if(!verseList.contains(i)) {
						verseList.add(i);
					}
				}
			}
			else {
				int c = verse();
				if (c > 0 &&  !verseList.contains(c)) {
					verseList.add(c);
				}
			}

			boolean hasPunctuation = punctuation();
			if (!hasPunctuation) {
				return verseList;
			}
		}
	}
}
