package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.data.Reference;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//A Passage is a group of Verse objects that are in a sequence (i.e. Galatians 2:19-21)
//	A Passage is a basic type, and so its reference is non-modifiable. In addition,
//	a Passage can only contain verses that are all adjacent in the same Book. It ensures
//	this by setting the start and end verses, then populating everything between them.
//	Passage objects can parse an input string for the reference, and will optionally
//	parse a string for the individual verse text. When parsing the verse text, if it
//  cannot determine where to split it for the different verses, it will store
//  the whole text locally and keep the Verse objects just as markers. That being
//  said, if the verse text to be parsed was passed in from anywhere in this
//  library or read from a file created by the library, then the verse text
//  will be marked up to be able to be parsed.
public class Passage extends AbstractVerse {
//Data Members
//------------------------------------------------------------------------------
	//Data that makes up the Passage
	private ArrayList<Verse> verses;
	private String allText;

	private static Pattern hashtag = Pattern.compile("#((\\w+)|(\"[\\w ]+\"))");

	//Constructors
//------------------------------------------------------------------------------
    public Passage(Reference reference) {
        super(reference);

        Collections.sort(this.reference.verses);
        this.verses = new ArrayList<>();
		for(Integer verseNum : this.reference.verses) {
			this.verses.add(new Verse(new Reference(this.reference.book, this.reference.chapter, verseNum)));
		}
    }

	public static Passage parsePassage(String reference, Bible bible) throws ParseException {
		Bible ver = (bible == null) ? new Bible("eng-ESV") : bible;

		Reference ref = Reference.parseReference(reference, ver);
		Passage passage = new Passage(ref);
		passage.setBible(ver);
		return passage;
	}

//Setters and Getters
//------------------------------------------------------------------------------

    @Override
    public void setBible(Bible bible) {
        super.setBible(bible);
        for(Verse verse : verses) {
            verse.setBible(this.bible);
        }
    }

    public Passage setText(String text) {
		this.verses.clear();

		//parse input string and extract any tags, denoted as standard hastags
		Matcher m = hashtag.matcher(text);

		while(m.find()) {
			String match = m.group(1);
			if(match.charAt(0) == '\"') {
				addTag(new Tag(match.substring(1, match.length() - 1)));
			}
			else {
				addTag(new Tag(match));
			}
		}

		this.allText = m.replaceAll("");

		return this;
	}

	@Override
	public String getText() {
        if(verses.size() > 0) {
            String text = "";

            text += formatter.onPreFormat(reference);

            for (int i = 0; i < verses.size(); i++) {
                Verse verse = verses.get(i);

                text += formatter.onFormatVerseStart(verse.reference.verse);
                text += formatter.onFormatText(verse.verseText);

                if (i < verses.size() - 1) {
                    text += formatter.onFormatVerseEnd();
                }
            }

			text += formatter.onPostFormat();

			return text.trim();
        }
        else {
            String text = "";

            text += formatter.onPreFormat(reference);
            text += formatter.onFormatText(allText);
            text += formatter.onPostFormat();

            return text;
        }
	}


//get the XML representation of this object
//------------------------------------------------------------------------------
//	@Override
//	public Element toXML(org.w3c.dom.Document doc) {
//		org.w3c.dom.Element root = doc.createElement("passage");
//
//		root.setAttribute("reference", reference.toString());
//		root.setAttribute("bible", bible.id.toUpperCase());
//
//		if(verses.size() > 0) {
//			for(Verse verse : verses) {
//				Element child = verse.toXML(doc);
//				child.removeAttribute("bible");
//				child.removeAttribute("reference");
//				child.setAttribute("reference", Integer.toString(verse.getReference().verse));
//
//				root.appendChild(child);
//			}
//		}
//		else {
//			root.appendChild(doc.createTextNode(allText));
//		}
//
//		return root;
//	}
//
//	@Override
//	public String toXMLString() {
//		try {
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			org.w3c.dom.Document doc = builder.newDocument();
//
//			Element root = toXML(doc);
//			doc.appendChild(root);
//
//			StringWriter writer = new StringWriter();
//			Transformer transformer = TransformerFactory.newInstance().newTransformer();
//			transformer.transform(new DOMSource(doc), new StreamResult(writer));
//			return writer.toString();
//		}
//		catch(TransformerException ex) {
//			ex.printStackTrace();
//			return null;
//		}
//		catch(ParserConfigurationException ex) {
//			ex.printStackTrace();
//			return null;
//		}
//	}
//
//	public static Passage fromXML(org.jsoup.nodes.Element passageRoot) {
//		try {
//			if(passageRoot.tagName().equals("passage")) {
//				Bible bible = new Bible(passageRoot.attr("bible"));
//				Passage passage = new Passage(Reference.parseReference(passageRoot.attr("reference"), bible));
//				if(passageRoot.childNodeSize() == passage.reference.verses.size()) {
//					passage.setText(passageRoot.text());
//					passage.verses.clear();
//
//					for(org.jsoup.nodes.Element childVerse : passageRoot.children()) {
//						Verse verse = new Verse(new Reference(
//								passage.reference.book,
//								passage.reference.chapter,
//								Integer.parseInt(childVerse.attr("reference"))));
//
//						verse.setBible(passage.bible);
//						verse.setText(childVerse.text());
//						passage.verses.add(verse);
//					}
//					return passage;
//				}
//			}
//
//			return null;
//		}
//		catch(ParseException pe) {
//			pe.printStackTrace();
//			return null;
//		}
//	}

//------------------------------------------------------------------------------
	public Verse[] getVerses() {
		Verse[] versesArray = new Verse[verses.size()];
		verses.toArray(versesArray);
		return versesArray;
	}

	@Override
	public int compareTo(@NonNull AbstractVerse verse) {
		Verse lhs = this.verses.get(0);
		Verse rhs = ((Passage) verse).verses.get(0);

		return lhs.compareTo(rhs);
	}

	@Override
	public boolean equals(Object passage) {
		if(passage instanceof Passage) {
			Passage lhs = this;
			Passage rhs = ((Passage) passage);

			return lhs.reference.equals(rhs.reference);
		}
		else return false;
	}

//Retrieve verse from the Internet
//------------------------------------------------------------------------------

	@Override
	public void getVerseInfo(Document doc) {
		if(listener != null) listener.onPreDownload();
		allText = null;

		String verseID = reference.book.getId() +"." + reference.chapter;

		for(int i = 0; i < verses.size(); i++) {
			Verse verse = verses.get(i);

			Elements scripture = doc.select("verse[id=" + verseID + "." + verse.getReference().verse + "]");

			Document textHTML = Jsoup.parse(scripture.select("text").text());
			textHTML.select("sup").remove();

			verse.setText(textHTML.text());
		}

		if(listener != null) listener.onPostDownload();
	}
}
