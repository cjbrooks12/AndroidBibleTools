package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

//A Passage is a group of Verse objects that are in a sequence (i.e. Galatians 2:19-21)
//	A Passage is a basic type, and so its reference is non-modifiable. In addition,
//	a Passage can only contain verses that are all adjacent in the same Book. It ensures
//	this by setting the start and end verses, then populating everything between them.
//	Passage objects can parse an input string for the reference, and will optionally
//	parse a string for the individual verse text. When parsing the verse text, if it
//  cannot determine where to split it for the different verses, it will store
//  the whole text locally and keep the Verse objects just as markers. That being
//  said, if the verse text to be parsed was passed in from anywhere in this
//  library or read from a file created by the library, then the verse text
//  will be marked up to be able to be parsed.
public class Passage extends AbstractVerse {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected ArrayList<Verse> verses;
	protected String allText;
	protected String rawText;

//Constructors
//--------------------------------------------------------------------------------------------------
	public Passage(Reference reference) {
		super(reference);

		Collections.sort(this.reference.verses);
		this.verses = new ArrayList<>();
		for(Integer verseNum : this.reference.verses) {
			this.verses.add(
					new Verse(
							new Reference.Builder()
									.setBook(this.reference.book)
									.setChapter(this.reference.chapter)
									.setVerses(verseNum).create()
					)
			);
		}
	}

//Setters and Getters
//--------------------------------------------------------------------------------------------------
	@Override
	public void setBible(Bible bible) {
		super.setBible(bible);
		for(Verse verse : verses) {
			verse.setBible(this.bible);
		}
	}

	public Passage setText(String text) {
		this.verses.clear();
		this.allText = text;
		this.rawText = text;
		return this;
	}

	public Passage setRawText(String rawText) {
		this.rawText = rawText;
		return this;
	}

	/**
	 * Get the formatted text of this Passage. If the verses are listed individually, format each
	 * verse individually according to the Formatter. Otherwise, just show the full block of text
	 * and format it the best it can.
	 *
	 * @return the formatted Passage text
	 */
	@Override
	public String getText() {
		if(verses.size() > 0) {
			String text = "";

			text += formatter.onPreFormat(reference);

			for(int i = 0; i < verses.size(); i++) {
				Verse verse = verses.get(i);

				text += formatter.onFormatVerseStart(verse.reference.verses.get(0));
				text += formatter.onFormatText(verse.verseText);

				if(i < verses.size() - 1) {
					text += formatter.onFormatVerseEnd();
				}
			}

			text += formatter.onPostFormat();

			return text.trim();
		}
		else {
			String text = "";

			text += formatter.onPreFormat(reference);
			text += formatter.onFormatText(allText);
			text += formatter.onPostFormat();

			return text;
		}
	}

	@Override
	public String getRawText() {
		return rawText;
	}

	public Verse[] getVerses() {
		Verse[] versesArray = new Verse[verses.size()];
		verses.toArray(versesArray);
		return versesArray;
	}

	@Override
	public int compareTo(@NonNull AbstractVerse verse) {
		Verse lhs = this.verses.get(0);
		Verse rhs = ((Passage) verse).verses.get(0);

		return lhs.compareTo(rhs);
	}

	@Override
	public boolean equals(Object passage) {
		if(passage instanceof Passage) {
			Passage lhs = this;
			Passage rhs = ((Passage) passage);

			return lhs.reference.equals(rhs.reference);
		}
		else {
			return false;
		}
	}
}
