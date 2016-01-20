package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Passage<T extends Verse> extends AbstractVerse {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected ArrayList<T> verses;

//Constructors
//--------------------------------------------------------------------------------------------------
	public Passage(Reference reference) {
		super(reference);

		Class<? extends Verse> verseClass = getTypeParamemeterClass();

		Collections.sort(this.reference.getVerses());
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
	}

	private Class<? extends Verse> getTypeParamemeterClass() {
		ParameterizedType genericType = (ParameterizedType) getClass().getGenericSuperclass();
		Class typeParameter = (Class) genericType.getActualTypeArguments()[0];

		try {
			return (Class<? extends Verse>) typeParameter;
		}
		catch(ClassCastException e) {
			throw new ClassCastException("Passage generic type parameter class [" + typeParameter.getName() + "] does not extend " + Verse.class.getName());
		}
	}

//Setters and Getters
//--------------------------------------------------------------------------------------------------
	@Override
	public String getFormattedText() {
		if(verses.size() > 0) {
			String text = "";

			text += formatter.onPreFormat(reference);

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
				text += verses.get(i).getText();
			}

			return text;
		}
		else {
			return "";
		}
	}

	public ArrayList<T> getVerses() {
		return verses;
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
