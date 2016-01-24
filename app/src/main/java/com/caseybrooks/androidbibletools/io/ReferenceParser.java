package com.caseybrooks.androidbibletools.io;

import com.caseybrooks.androidbibletools.basic.Reference;

import java.util.ArrayList;

/**
 * The class used to parse a String into Reference objects. Uses the following grammar
 * <p>
 * Passage ::= book (punctuation) chapter ((punctuation) verseList) Verse ::= book (punctuation)
 * chapter (punctuation) verse
 * <p>
 * number ::= { [0..9] } word ::= { [a..zA..Z] } punctuation ::= [;:,.-\/]
 * <p>
 * book ::= ([123]) word+ chapter ::= number verse ::= number
 * <p>
 * verseSequence ::= verse punctuation verse verseList ::= { [verse | verseSequence] punctuation }
 */
public class ReferenceParser {
	Reference.Builder builder;
	String reference;

	TokenStream ts;

	public ReferenceParser(Reference.Builder builder) {
		this.builder = builder;
	}

	//verse ::= book chapter (punctuation) verse
	public Reference.Builder getVerseReference(String reference) {
		this.reference = reference;
		ts = new TokenStream(reference);

		//Book will throw its own exception if it fails to parse
		book();
		punctuation();
		chapter();
		punctuation();
		verse();

		return builder;
	}

	//Passage ::= book (punctuation) chapter ((punctuation) verseList)
	public Reference.Builder getPassageReference(String reference) {
		this.reference = reference;
		return getPassageReference(new TokenStream(reference));
	}

	public Reference.Builder getPassageReference(TokenStream reference) {
		ts = reference;

		book();
		punctuation();
		chapter();
		punctuation();
		verseList();

		return builder;
	}

	//punctuation ::= [;:,.-\/]
	private boolean punctuation() {
		Token a = ts.get();

		//if actual token is punctuation
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
		//if token is a word that implies punctuation
		else {
			if(a != null && a.equals(Token.Type.WORD)) {
				if(a.getStringValue().equalsIgnoreCase("and") ||
						a.getStringValue().equalsIgnoreCase("through") ||
						a.getStringValue().equalsIgnoreCase("to")) {

					return true;
				}
			}
		}

		ts.unget(a);
		return false;
	}

	//book ::= ([123]) word+
	private void book() {
		Token a = ts.get();
		boolean includesNumber;

		//optional number between 1 and 3
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() <= 3 && a.getIntValue() > 0) {
			//token was valid number, leave it out and continue parsing book
			includesNumber = true;
		}
		else {
			//token wasn't a valid number, put it back in and continue parsing book
			ts.unget(a);
			includesNumber = false;
		}

		//mandatory set of words before a number
		ArrayList<Token> tokens = new ArrayList<>();

		while(true) {
			Token t = ts.get();
			if(t != null && t.equals(Token.Type.WORD)) {
				tokens.add(t);
				continue;
			}
			else {
				ts.unget(t);
				break;
			}
		}

		String bookName = (includesNumber) ? a.getIntValue() + " " : "";

		for(Token t : tokens) {
			bookName += t.getStringValue() + " ";
		}

		bookName = bookName.trim();

		builder.setBook(bookName);
	}

	//chapter ::= number
	private boolean chapter() {
		Token a = ts.get();
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() > 0) {
			builder.setChapter(a.getIntValue());
			return true;
		}
		else {
			ts.unget(a);
			return false;
		}
	}

	//verse ::= number
	private boolean verse() {
		Token a = ts.get();
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() > 0) {
			builder.addVerse(a.getIntValue());
			return true;
		}
		else {
			ts.unget(a);
			return false;
		}
	}

	//verseSequence ::= number dash number
	private boolean verseSequence() {
		Token a = ts.get();
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() > 0) {
			int numA = a.getIntValue();

			Token dash = ts.get();
			if(dash != null &&
					(dash.equals(Token.Type.DASH) ||
							(dash.equals(Token.Type.WORD) && dash.getStringValue().equalsIgnoreCase(
									"through"
							)) ||
							(dash.equals(Token.Type.WORD) && dash.getStringValue()
							                                     .equalsIgnoreCase("to")))) {

				Token b = ts.get();
				if(b != null && b.equals(Token.Type.NUMBER) && b.getIntValue() > 0) {
					int numB = b.getIntValue();

					for(int i = numA; i <= numB; i++) {
						builder.addVerse(i);
					}
					return true;
				}
				else {
					ts.unget(b);
					ts.unget(dash);
					ts.unget(a);
					return false;
				}
			}
			else {
				ts.unget(dash);
				ts.unget(a);
				return false;
			}
		}
		else {
			ts.unget(a);
			return false;
		}
	}

	//verseList ::= { [verse | verseSequence] comma }
	private void verseList() {
		while(true) {
			if(!verseSequence()) {
				if(!verse()) {
					return;
				}
			}

			if(!punctuation()) {
				return;
			}
		}
	}
}
