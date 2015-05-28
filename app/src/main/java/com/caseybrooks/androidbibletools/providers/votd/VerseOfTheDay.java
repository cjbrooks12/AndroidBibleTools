package com.caseybrooks.androidbibletools.providers.votd;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Tag;
import com.caseybrooks.androidbibletools.data.Downloadable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VerseOfTheDay implements Downloadable {
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

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public Document getDocument() throws IOException {
		return Jsoup.connect("http://verseoftheday.com").get();
	}

	@Override
	public boolean parseDocument(Document doc) {
		Elements scripture = doc.select(".scripture");
		Elements verseReference = scripture.select("a");

		passage = new Passage(
				new Reference.Builder()
				.setBible(new Bible())
				.parseReference(verseReference.text())
				.create()
		);

		scripture.select(".reference").remove();

		passage.setText(scripture.text());
		passage.setTags(new Tag("VOTD"));

		return true;
	}
}
