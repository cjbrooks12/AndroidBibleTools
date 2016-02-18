package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Book;

import org.json.JSONObject;

public class ABSBook extends Book {
	protected String id;

	public ABSBook() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public String serialize() {
		String s = super.serialize();
		try {
			JSONObject bookJSON = new JSONObject(s);
			bookJSON.put("id", this.id);
			return bookJSON.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void deserialize(String string) {
		super.deserialize(string);
		try {
			JSONObject bookJSON = new JSONObject(string);
			this.id = bookJSON.getString("id");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
