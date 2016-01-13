package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Verse;

public class ABSVerse extends Verse {
	public ABSVerse(Reference reference) {
		super(reference);
	}
//	protected final String id;
//	protected final String APIKey;
//
//	public ABSVerse(String APIKey, Reference reference) {
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
//	public boolean isAvailable() {
//		return APIKey != null && id != null;
//	}
//
//	public Document getDocument() throws IOException {
//		String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/chapters/" +
//				id + "/verses.xml?include_marginalia=false";
//
//		String header = APIKey + ":x";
//		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);
//
//		return Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();
//	}
//
//	public boolean parseDocument(Document doc) {
//		String id_base = doc.select("verse").attr("id");
//		String[] id_split = id_base.split("\\.");
//
//		if(id_split.length == 3) {
//			id_base = id_split[0] + "." + id_split[1] + "." + reference.verses.get(0);
//		}
//		else {
//			String s = "";
//			for(String ss : id_split) {
//				s += ss + " ";
//			}
//			setText("split not correct " + s);
//			return false;
//		}
//
//		Elements scripture = doc.select("verse[id=" + id_base + "]");
//		rawText = scripture.select("text").text();
//
//		//TODO: instead of simply stripping out these areas of the text, try to determine if they are actually common across the API and use it as part of the Formatter
//		Document textHTML = Jsoup.parse(scripture.select("text").text());
//		textHTML.select("sup").remove();
//		textHTML.select("h3").remove();
//		String text = textHTML.text();
//
//		if(text != null && text.length() > 0) {
//			setText(text);
//			return true;
//		}
//		else {
//			setText("");
//
//			return false;
//		}
//	}
}
