package com.caseybrooks.androidbibletools.providers.votd;

import com.caseybrooks.androidbibletools.basic.Passage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class VerseOfTheDay {
	private Passage passage;
	private long date;


	public VerseOfTheDay() {
		this.date = 0l;
	}

	public VerseOfTheDay(long date) {
		this.date = date;
	}

	public Passage getPassage() {
		return passage;
	}

	public boolean isAvailable() {
		return true;
	}

	public String getData() throws IOException {
		return null;
	}

	public boolean parseData(String data) {
		return false;
	}

	public Document getDocument() throws IOException {
		return Jsoup.connect("http://verseoftheday.com").get();
	}

	public boolean parseDocument(Document doc) {
//		Elements scripture = doc.select(".scripture");
//		Elements verseReference = scripture.select("a");

		//		passage = new Passage(
		//				new Reference.Builder()
		//				.setBible(new Bible())
		//				.parseReference(verseReference.text())
		//				.create()
		//		);

//		scripture.select(".reference").remove();

//		passage.setText(scripture.text());
//		passage.setTags(new Tag("VOTD"));

		return true;
	}
}
