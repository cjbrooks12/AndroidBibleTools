package com.caseybrooks.androidbibletools.providers.simple;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;

public class SimplePassage extends Passage<SimpleVerse> {
	String text;

	public SimplePassage(Reference reference) {
		super(reference);
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
