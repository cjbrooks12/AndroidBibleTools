package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

//TODO: make verses an unmodifiable list

/**
 * A base class for a list of Verses of a particular type. As with all implementations of
 * {@link AbstractVerse}, the {@link Reference} is immutable, and so this passage will always refer
 * to the same set of verses within one chapter of the Bible. In most cases, you will want to primarily
 * use some implementation of Passage as opposed to Verse, because it is more powerful, but when set
 * with just a single verse, acts very similar to the Verse class.
 *
 * @param <T>  the type of Verse that is contained in this Passage's list of verses
 *
 * @see Verse
 */
public abstract class Passage<T extends Verse> extends AbstractVerse {
	protected List<T> verses;

	/**
	 * Create this Passage with the given Reference. Upon creation, an unmodifiable list of Verses
	 * will be created of the type specified by the type parameter T. As of now, this feature is buggy,
	 * and does not work with any deeper subclasses of Passage. As such, only direct descendants of
	 * Passage should be created, and those implementations should always be final classes.
	 *
	 * @param reference  the reference of this Passage
	 */
	public Passage(Reference reference) {
		super(reference);

		Class<? extends Verse> verseClass = getVerseClass();

		this.verses = new ArrayList<>();
		for(Integer verseNum : this.reference.getVerses()) {
			try {
				Reference ref = new Reference.Builder()
						.setBook(this.reference.getBook())
						.setChapter(this.reference.getChapter())
						.setVerses(verseNum).create();

				T verse = (T) verseClass.getDeclaredConstructor(Reference.class).newInstance(ref);

				this.verses.add(verse);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

//		this.verses = Collections.unmodifiableList(this.verses);
	}

	public abstract Class<? extends Verse> getVerseClass();

	/**
	 * Get the list of Verses associated with this Passage
	 * @return
	 */
	public List<T> getVerses() {
		return verses;
	}

	@Override
	public String getFormattedText() {
		if(verses.size() > 0) {
			String text = "";

			text += formatter.onPreFormat(this);

			for(int i = 0; i < verses.size(); i++) {
				T verse = verses.get(i);

				text += formatter.onFormatVerseStart(verse.getVerseNumber());
				text += formatter.onFormatText(verse.getText());

				if(i < verses.size() - 1) {
					text += formatter.onFormatVerseEnd();
				}
			}

			text += formatter.onPostFormat();

			return text.trim();
		}
		else {
			return "";
		}
	}

	@Override
	public String getText() {
		if(verses.size() > 0) {
			String text = "";

			for(int i = 0; i < verses.size(); i++) {
				if(verses.get(i) != null)
					text += verses.get(i).getText();
			}

			return text;
		}
		else {
			return "";
		}
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

		Passage verse = (Passage) o;

		return this.getReference().equals(verse.getReference());
	}

	@Override
	public int hashCode() {
		return this.reference != null ? this.reference.hashCode() : 0;
	}
}
