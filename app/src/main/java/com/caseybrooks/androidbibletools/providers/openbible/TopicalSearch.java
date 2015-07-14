package com.caseybrooks.androidbibletools.providers.openbible;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.basic.Reference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class TopicalSearch implements Downloadable {
	private String topic;
	private ArrayList<Passage> verses;

	public TopicalSearch(String topic) {
		this.topic = topic;
		verses = new ArrayList<>();
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public Document getDocument() throws IOException {
		String query = "http://www.openbible.info/topics/" + topic.trim().replaceAll(" ", "_");
		return Jsoup.connect(query).get();
	}

	@Override
	public boolean parseDocument(Document doc) {
		Elements passages = doc.select(".verse");

		for(Element element : passages) {
			Reference ref = new Reference.Builder()
					.parseReference(element.select(".bibleref").first().ownText())
					.create();

			Passage passage = new Passage(ref);
			passage.setText(element.select("p").text());

			String notesString = element.select(".note").first().ownText();
			passage.getMetadata().putInt("UPVOTES", Integer.parseInt(notesString.replaceAll("\\D", "")));
			passage.getMetadata().putString("SEARCH_TERM", topic.trim());

			verses.add(passage);
		}

		return true;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
		verses.clear(); //we have changed the topic, so prepare for new verses
	}

	public ArrayList<Passage> getVerses() {
		return verses;
	}
}
