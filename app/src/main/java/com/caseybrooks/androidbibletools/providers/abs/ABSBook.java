package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.data.Downloadable;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class ABSBook extends Book implements Downloadable {


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ABSBook() {
		this.id = null;
	}

	public ABSBook(String id) {
		this.id = id;
	}

	protected String id;

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public Document getDocument() throws IOException {
		return null;
	}

	@Override
	public boolean parseDocument(Document doc) {
		return false;
	}

	@Override
	public String toString() {
		String s = getName() + " ";
		for(int chapter : getChapters()) s += chapter + ", ";
		s += getId();
		return s;
	}
}
