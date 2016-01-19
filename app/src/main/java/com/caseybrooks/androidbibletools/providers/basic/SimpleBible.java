package com.caseybrooks.androidbibletools.providers.basic;

import com.caseybrooks.androidbibletools.basic.Bible;

public class SimpleBible extends Bible<SimpleBook> {
	@Override
	public String serialize() {
		return "";
	}

	@Override
	public void deserialize(String string) {
	}

	@Override
	public SimpleBook parseBook(String bookName) {
		SimpleBook book = new SimpleBook();
		book.setName(bookName);
		return book;
	}
}
