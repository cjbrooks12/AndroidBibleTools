package com.caseybrooks.androidbibletools.providers.abs;

import android.util.Base64;

import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.basic.Reference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ABSVerse extends Verse implements Downloadable {
	protected String id;

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

	protected String APIKey;

	public ABSVerse(Reference reference) {
		super(reference);
	}

	public ABSVerse(String APIKey, Reference reference) {
		super(reference);
		this.APIKey = APIKey;

		try {
			ABSBook absBook = (ABSBook) reference.book;
			this.id = absBook.getId() + "." + reference.chapter;
		}
		catch(ClassCastException cce) {
			this.id = null;
		}
	}

	@Override
	public boolean isAvailable() {
		return id != null;
	}

	@Override
	public Document getDocument() throws IOException {
		if(reference != null && id != null) {
			String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/chapters/" +
					id + "/verses.xml?include_marginalia=false";

			String header = APIKey + ":x";
			String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

			return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
		}
		else {
			return null;
		}
	}

	@Override
	public boolean parseDocument(Document doc) {
		Elements scripture = doc.select("verse[id=" + id + "." + reference.verses.get(0) + "]");
		Document textHTML = Jsoup.parse(scripture.select("text").text());
		textHTML.select("sup").remove();
		String text = textHTML.text();

		if(text != null && text.length() > 0) {
			setText(text);
			return true;
		}
		else {
			return false;
		}
	}
}
