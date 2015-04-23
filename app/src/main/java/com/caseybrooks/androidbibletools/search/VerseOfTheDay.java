package com.caseybrooks.androidbibletools.search;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.io.Download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class VerseOfTheDay {
	public static Passage retrieve(Bible bible, String APIKey) throws IOException {
		Document votdDoc = Jsoup.connect("http://verseoftheday.com").get();

		Elements reference = votdDoc.select("meta[property=og:title]");
		Reference ref = new Reference.Builder()
				.setBible(bible)
				.parseReference(reference.attr("content").substring(18))
				.create();

		Passage passage = new Passage(ref);
					Document verseDoc = Download.bibleChapter(APIKey, passage.getReference());
		passage.getVerseInfo(verseDoc);
		return passage;
	}
}
