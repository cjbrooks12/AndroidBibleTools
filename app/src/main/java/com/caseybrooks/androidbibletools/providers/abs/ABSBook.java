package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.data.Downloadable;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class ABSBook extends Book implements Downloadable {
	protected final String APIKey;
	protected final String id;

	public ABSBook(String APIKey, String id) {
		this.APIKey = APIKey;
		this.id = (id != null) ? id : "eng-ESV:Matt";
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean isAvailable() {
		return APIKey != null && id != null;
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
