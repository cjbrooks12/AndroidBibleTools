package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;

public class ABSPassage extends Passage {
	public ABSPassage(Reference reference) {
		super(reference);
	}
//	protected final String APIKey;
//	protected final String id;
//
//	public String getId() {
//		return id;
//	}
//
//	public String getData() throws IOException {
//		return null;
//	}
//
//	public boolean parseData(String data) {
//		return false;
//	}
//
//	public String getAPIKey() {
//		return APIKey;
//	}
//
//	public ABSPassage(String APIKey, Reference reference) {
//		super(reference);
//		this.APIKey = APIKey;
//
//		if(reference.book instanceof ABSBook) {
//			ABSBook absBook = (ABSBook) reference.book;
//			this.id = absBook.getId() + "." + reference.chapter;
//		}
//		else {
//			this.id = "Matt.1";
//		}
//	}
//
//	public boolean isAvailable() {
//		return (APIKey != null) && (id != null);
//	}
//
//	public Document getDocument() throws IOException {
//		if(reference != null) {
//			String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/chapters/" +
//					id + "/verses.xml?include_marginalia=false";
//
//			String header = APIKey + ":x";
//			String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
//
//			return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
//		}
//		else {
//			return null;
//		}
//	}
//
//	public boolean parseDocument(Document doc) {
//		String id_base = doc.select("verse").attr("id");
//		String[] id_split = id_base.split("\\.");
//
//		if(id_split.length == 3) {
//			id_base = id_split[0] + "." + id_split[1];
//		}
//		else {
//			return false;
//		}
//
//		String passageRawText = "";
//
//		for(int i = 0; i < verses.size(); i++) {
//			Verse verse = verses.get(i);
//
//			Elements scripture = doc.select(
//					"verse[id=" + id_base + "." + verse.getReference().verses.get(0) + "]"
//			);
//
//			String verseRawText = scripture.select("text").text();
//			verse.setRawText(verseRawText);
//			passageRawText += verseRawText + " ";
//
//			Document textHTML = Jsoup.parse(verseRawText);
//			textHTML.select("sup").remove();
//			textHTML.select("h3").remove();
//
//			verse.setText(textHTML.text());
//		}
//
//		allText = null;
//		rawText = passageRawText;
//
//		return true;
//	}
}
