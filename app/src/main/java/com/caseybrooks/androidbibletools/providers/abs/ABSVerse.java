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
	protected final String id;
	protected final String APIKey;

	public ABSVerse(String APIKey, Reference reference) {
		super(reference);
		this.APIKey = APIKey;

		if(reference.book instanceof ABSBook) {
			ABSBook absBook = (ABSBook) reference.book;
			this.id = absBook.getId() + "." + reference.chapter;
		}
		else {
			this.id = "Matt.1";
		}
	}

	public String getId() {
		return id;
	}

	public String getAPIKey() {
		return APIKey;
	}

	@Override
	public boolean isAvailable() {
		return APIKey != null && id != null;
	}

	@Override
	public Document getDocument() throws IOException {
		String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/chapters/" +
				id + "/verses.xml?include_marginalia=false";

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
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