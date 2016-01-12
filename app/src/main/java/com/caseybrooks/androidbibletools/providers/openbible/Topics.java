package com.caseybrooks.androidbibletools.providers.openbible;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.TreeSet;

public class Topics {
	private TreeSet<String> topics;
	private char c;

	public Topics() {
		topics = new TreeSet<>();
		c = ' ';
	}

	//set a specific letter to get topics from. If character is not a letter, then
	//get everything
	public Topics(char c) {
		topics = new TreeSet<>();
		this.c = c;
	}

	public boolean isAvailable() {
		return Character.isLetter(c);
	}

	public String getData() throws IOException {
		return null;
	}

	public boolean parseData(String data) {
		return false;
	}

	public Document getDocument() throws IOException {
		String query = "http://www.openbible.info/topics/" + c;

		return Jsoup.connect(query).get();
	}

	public boolean parseDocument(Document doc) {
		Elements passages = doc.select("li");

		for(Element element : passages) {
			topics.add(element.text());
		}

		return true;
	}

	public TreeSet<String> getTopics() {
		return topics;
	}

	public void setC(char c) {
		this.c = c;
	}
}
