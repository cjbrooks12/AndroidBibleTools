package com.caseybrooks.androidbibletools.providers.abs;

import android.util.Base64;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.basic.Reference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ABSPassage extends Passage implements Downloadable {
	protected String APIKey;
	protected String verseId;

	public ABSPassage(Reference reference) {
		super(reference);
	}

	public ABSPassage(String APIKey, Reference reference) {
		super(reference);
		this.APIKey = APIKey;

		try {
			ABSBook absBook = (ABSBook) reference.book;
			this.verseId = absBook.getBookId() +"." + reference.chapter;
		}
		catch(ClassCastException cce) {
			this.verseId = null;
		}
	}

	@Override
	public boolean isAvailable() {
		return (APIKey != null) && (verseId != null);
	}

	@Override
	public Document getDocument() throws IOException {
		if(reference != null) {
			String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/chapters/" +
					verseId + "/verses.xml?include_marginalia=false";

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
		allText = null;

		for(int i = 0; i < verses.size(); i++) {
			Verse verse = verses.get(i);

			Elements scripture = doc.select("verse[id=" + verseId + "." + verse.getReference().verses.get(0) + "]");

			Document textHTML = Jsoup.parse(scripture.select("text").text());
			textHTML.select("sup").remove();

			verse.setText(textHTML.text());
		}

		return true;
	}
}
