package com.caseybrooks.androidbibletools.io;

import com.caseybrooks.androidbibletools.basic.Reference;

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
	private void book() {
		Token a = ts.get();

		//book ::= [123] word
		if(a != null && a.equals(Token.Type.NUMBER) && a.getIntValue() <= 3 && a.getIntValue() > 0) {
			Token b = ts.get();
			if(b != null && b.equals(Token.Type.WORD)) {
				builder.setBook(a.getIntValue() + " " + b.getStringValue());
			}
			else {
				ts.unget(b);
				ts.unget(a);
			}
		}

		//book ::= word
		else if(a != null && a.equals(Token.Type.WORD)) {
			builder.setBook(a.getStringValue());
		}
		else {
			ts.unget(a);
		}
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
			if(dash != null && dash.equals(Token.Type.DASH)) {
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
		while (true) {
			if (!verseSequence()) {
				if(!verse()) {
					return;
				}
			}

			if (!punctuation()) {
				return;
			}
		}
	}
}
