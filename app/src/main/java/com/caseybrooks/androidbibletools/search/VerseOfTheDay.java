package com.caseybrooks.androidbibletools.search;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.io.Download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;

public class VerseOfTheDay {
	public static Passage retrieve(Bible bible, String APIKey) throws IOException {
		Document votdDoc = Jsoup.connect("http://verseoftheday.com").get();

		Elements reference = votdDoc.select("meta[property=og:title]");

        try {
            Passage passage = Passage.parsePassage(reference.attr("content").substring(18), bible);
			Document verseDoc = Download.bibleChapter(APIKey,
					passage.getReference().book.getId(),
					passage.getReference().chapter);
            passage.getVerseInfo(verseDoc);
            return passage;
        }
        catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
	}
}
