package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Verse;

import org.json.JSONObject;

public class ABSVerse extends Verse {
	public ABSVerse(Reference reference) {
		super(reference);
		this.formatter = new ABSFormatter();

		if(reference.getBook() instanceof ABSBook) {
			ABSBook absBook = (ABSBook) reference.getBook();
			this.id = absBook.getId() + "." + reference.getChapter();
		}
		else {
			this.id = "Matt.1";
		}
	}

	@Override
	public String serialize() {
		try {
			JSONObject verseJSON = new JSONObject();
			verseJSON.put("id", this.id);
			return verseJSON.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void deserialize(String string) {
		try {
			JSONObject verseJSON = new JSONObject(string);
			this.id = verseJSON.getString("id");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
