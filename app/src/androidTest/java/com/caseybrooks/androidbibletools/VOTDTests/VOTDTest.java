package com.caseybrooks.androidbibletools.VOTDTests;

import android.util.Log;

import com.caseybrooks.androidbibletools.providers.votd.VerseOfTheDay;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;

public class VOTDTest extends TestCase {
	public void testTodaysVerse() throws Throwable {
		VerseOfTheDay votd = new VerseOfTheDay();
		if(votd.isAvailable()) {
			Document doc = votd.getDocument();
			assertNotNull(doc);

			votd.parseDocument(doc);
			assertNotNull(votd.getPassage());
			assertNotNull(votd.getPassage().getReference());
			assertNotNull(votd.getPassage().getText());

			Log.e("VOTD RESPONSE", votd.getPassage().getReference() + " " + votd.getPassage().getText());


		}
	}
}
