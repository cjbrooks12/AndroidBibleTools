package com.caseybrooks.androidbibletools.providers.votd;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VerseOfTheDay implements Downloadable {
	String APIKey;
	ABSPassage passage;
	Bible bible;

	public VerseOfTheDay(String APIKey) {
		this.APIKey = APIKey;
		bible = new Bible();
	}

	public VerseOfTheDay(String APIKey, Bible bible) {
		this.APIKey = APIKey;
		this.bible = bible;
	}

	@Override
	public boolean isAvailable() {
		return APIKey != null;
	}

	@Override
	public Document getDocument() throws IOException {
		return Jsoup.connect("http://verseoftheday.com").get();
	}

	@Override
	public boolean parseDocument(Document doc) {
		Elements reference = doc.select("meta[property=og:title]");
		Reference ref = new Reference.Builder()
				.setBible(bible)
				.parseReference(reference.attr("content").substring(18))
				.create();
		try {
			passage = new ABSPassage(APIKey, ref);
			return passage.parseDocument(passage.getDocument());
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
	}
}
