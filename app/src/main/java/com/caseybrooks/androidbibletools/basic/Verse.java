package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

/**
 * A base class for simplest complete unit of data in this library. Each verse contains exactly one
 * Bible verse, described in the Reference passed to the constructor. The Reference may contain multiple
 * verses in its list of verses, but will be treated as if it only has the first verse in that list.
 */
public abstract class Verse extends AbstractVerse {
	protected String text;

	/**
	 * Any Reference can be used to create a Verse, but only the first verse in that Reference will
	 * be used by this Verse.
	 *
	 * @param reference  the reference pointing to the location in the Bible for this one verse
	 */
	public Verse(Reference reference) {
		super(reference);
	}

	/**
	 * Get the number for this verse in its chapter.
	 *
	 * @return the verse number
	 */
	public int getVerseNumber() {
		return reference.getVerses().get(0);
	}

	/**
	 * Since a verse cannot be broken down further, its text can be set directly.
	 *
	 * @param verseText  the text of this verse
	 * @return this verse, for chaining
	 */
	public Verse setText(String verseText) {
		this.text = verseText;
		return this;
	}

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
