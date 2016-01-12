package com.caseybrooks.androidbibletools.providers.cjb;

import com.caseybrooks.androidbibletools.basic.Book;

public class CJBBook extends Book {
	protected String service;
	protected String bibleId;
	protected String bookId;

	public CJBBook() {
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getBibleId() {
		return bibleId;
	}

	public void setBibleId(String bibleId) {
		this.bibleId = bibleId;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
}
