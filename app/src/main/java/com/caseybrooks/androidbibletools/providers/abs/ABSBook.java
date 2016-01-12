package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Book;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class ABSBook extends Book {
	protected String APIKey;
	protected String id;

	public ABSBook() {
	}

	public String getAPIKey() {
		return APIKey;
	}

	public void setAPIKey(String APIKey) {
		this.APIKey = APIKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() throws IOException {
		return null;
	}

	public boolean parseData(String data) {
		return false;
	}

	public boolean isAvailable() {
		return APIKey != null && id != null;
	}

	public Document getDocument() throws IOException {
		return null;
	}

	public boolean parseDocument(Document doc) {
		return false;
	}

	@Override
	public String toString() {
		String s = getName() + " ";
		for(int chapter : getChapters()) {
			s += chapter + ", ";
		}
		s += getId();
		return s;
	}
}
