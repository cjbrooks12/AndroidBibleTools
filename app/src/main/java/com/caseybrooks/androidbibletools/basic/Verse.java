package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

/**
 * The simplest unit of data in this data structure. Each verse contains one and only one Bible
 * verse, its corresponding Book, Chapter, and Verse Number, and the bible this specific Verse is.
 * <p/>
 * Verse objects are immutable. The necessary core information, the verse reference and its getText,
 * cannot be modified once set, as the Verse should always point to the same Verse, and the getText
 * should correspond directly to this verse, not anything the user wants. Should the user be unable
 * to connect to the internet at the time of creating a Verse, they may manually set the getText in
 * the constructor, but it cannot be modified after this.
 * <p/>
 * The display flags and Bible may be changed at will, so that the user may view the same verse in
 * different translations and formats. Think of a Verse object as a reference to a particular Verse,
 * not the getText of the verse. In this way, it makes sense that a Verse can be displayed in
 * multiple versions and in different formats.
 */
public abstract class Verse extends AbstractVerse {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected String text;

//Constructors
//--------------------------------------------------------------------------------------------------
	public Verse(Reference reference) {
		super(reference);
	}

//Getters and Setters
//--------------------------------------------------------------------------------------------------
	public Verse setText(String verseText) {
		this.text = verseText;
		return this;
	}

//Print the formatted String
//--------------------------------------------------------------------------------------------------
	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getFormattedText() {
		String text = "";

		text += formatter.onPreFormat(reference);
		text += formatter.onFormatVerseStart(reference.getVerses().get(0));
		text += formatter.onFormatText(text);
		text += formatter.onPostFormat();

		return text.trim();
	}

	public int getVerseNumber() {
		return reference.getVerses().get(0);
	}

//Comparison methods
//--------------------------------------------------------------------------------------------------

	@Override
	public int compareTo(@NonNull AbstractVerse verse) {
		return this.getReference().compareTo(verse.getReference());
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || this.getClass() != o.getClass()) {
			return false;
		}

		Verse verse = (Verse) o;

		return this.getReference().equals(verse.getReference());
	}

	@Override
	public int hashCode() {
		return this.reference != null ? this.reference.hashCode() : 0;
	}
}
