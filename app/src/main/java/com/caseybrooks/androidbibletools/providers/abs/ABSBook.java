package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.data.Downloadable;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class ABSBook extends Book implements Downloadable {


	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public ABSBook() {
		this.bookId = null;
	}

	public ABSBook(String bookId) {
		this.bookId = bookId;
	}

	protected String bookId;

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
		s += getBookId();
		return s;
	}
}
