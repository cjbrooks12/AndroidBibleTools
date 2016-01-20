package com.caseybrooks.androidbibletools.providers.simple;

import com.caseybrooks.androidbibletools.basic.Bible;

import org.json.JSONObject;

public class SimpleBible extends Bible<SimpleBook> {
	@Override
	public void setName(String name) {
		super.setName(name);

		String abbr = "";
		for(String word : name.split("\\s")) {
			abbr += word.charAt(0);
		}
		setAbbreviation(abbr);
	}

	@Override
	public SimpleBook parseBook(String bookName) {
		SimpleBook book = new SimpleBook();
		book.setName(bookName);
		return book;
	}

	@Override
	public String serialize() {
		try {
			JSONObject bibleJSON = new JSONObject();
			bibleJSON.put("id", this.id);
			bibleJSON.put("name", this.name);
			bibleJSON.put("abbr", this.abbreviation);
			bibleJSON.put("language", this.language);
			bibleJSON.put("languageEnglish", this.languageEnglish);
			return bibleJSON.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void deserialize(String string) {
		try {
			JSONObject bibleJSON = new JSONObject(string);
			this.id = bibleJSON.getString("id");
			this.name = bibleJSON.getString("name");
			this.abbreviation = bibleJSON.getString("abbr");
			this.language = bibleJSON.getString("language");
			this.languageEnglish = bibleJSON.getString("languageEnglish");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
