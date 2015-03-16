package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.enumeration.VersionEnum;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

    public Passage(String reference) throws ParseException {
		super(reference);

        Collections.sort(this.reference.verses);
        this.verses = new ArrayList<Verse>();
		for(Integer verseNum : this.reference.verses) {
			this.verses.add(new Verse(new Reference(this.reference.book, this.reference.chapter, verseNum)));
		}
	}

//Setters and Getters
//------------------------------------------------------------------------------


    @Override
    public void setVersion(VersionEnum version) {
        super.setVersion(version);
        for(Verse verse : verses) {
            verse.setVersion(this.version);
        }
    }

    public Passage setText(String text) {
		this.verses.clear();


		//attempt to parse the text as if it were marked up

		if(text.contains("\\{/num (\"\\d+\")?\\}")) {
			String[] parsedVerses = text.split("\\{/num (\"\\d+\")?\\}");

			if(parsedVerses.length == reference.verses.size()) {
				for(int i = 0; i < this.reference.verses.size(); i++) {
					this.verses.add(
					   new Verse(new Reference(this.reference.book,
						  this.reference.chapter,
						  this.reference.verses.get(i))));
					verses.get(i).setVersion(this.version);
					verses.get(i).setText(parsedVerses[i]);
				}
			}
		}

		//if the text is not marked up, try to extract as much verse information
		//as we can. Get hashtags and verse numbers, and leave the rest alone
		else {
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
//			this.allText = text.replaceAll(markup_num, "").replaceAll(markup_ref, "").replaceAll(markup_tag, "");

		}

		return this;
	}

	@Override
	public String getText() {
        if(verses.size() > 0) {
            String text = "";

            text += formatter.onPreFormat(reference);

            for (int i = 0; i < verses.size(); i++) {
                Verse verse = verses.get(i);

                text += formatter.onFormatNumber(verse.reference.verse);
                text += formatter.onFormatText(verse.verseText);

                if (i < verses.size() - 1) {
                    text += formatter.onFormatNewVerse();
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
	@Override
	public Element toXML(org.w3c.dom.Document doc) {
		org.w3c.dom.Element root = doc.createElement("passage");

		root.setAttribute("reference", reference.toString());
		root.setAttribute("version", version.getCode().toUpperCase());

		if(verses.size() > 0) {
			for(Verse verse : verses) {
				Element child = verse.toXML(doc);
				child.removeAttribute("version");
				child.removeAttribute("reference");
				child.setAttribute("reference", Integer.toString(verse.getReference().verse));

				root.appendChild(child);
			}
		}
		else {
			root.appendChild(doc.createTextNode(allText));
		}

		return root;
	}

	@Override
	public String toXMLString() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.newDocument();

			Element root = toXML(doc);
			doc.appendChild(root);

			StringWriter writer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.toString();
		}
		catch(TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
		catch(ParserConfigurationException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Passage fromXML(org.jsoup.nodes.Element passageRoot) {
		try {
			if(passageRoot.tagName().equals("passage")) {
				Passage passage = new Passage(passageRoot.attr("reference"));
				if(passageRoot.childNodeSize() == passage.reference.verses.size()) {
					passage.setVersion(VersionEnum.parseVersion(passageRoot.attr("version")));
					passage.setText(passageRoot.text());
					passage.verses.clear();

					for(org.jsoup.nodes.Element childVerse : passageRoot.children()) {
						Verse verse = new Verse(new Reference(
								passage.reference.book,
								passage.reference.chapter,
								Integer.parseInt(childVerse.attr("reference"))));

						verse.setVersion(passage.version);
						verse.setText(childVerse.text());
						passage.verses.add(verse);
					}
					return passage;
				}
			}

			return null;
		}
		catch(ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}

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
    public String getURL(OnlineBible service) {
		switch(service) {
		case BibleGateway:
		case BibleStudyTools:
		case BlueLetterBible:
		case YouVersion:
		case Biblia:
			return "http://biblia.com/books/" +
					version.getCode() + "/" +
					reference.book.getCode() + reference.chapter;
		default:
			return null;
		}
    }

	@Override
	public void retrieve(String APIKey) throws IOException {
		if(listener != null) listener.onPreDownload();
		allText = null;

		String verseID = "eng-" +
				version.getCode() + ":" +
				reference.book.getCode() +"." +
				reference.chapter;

		String url = "http://" + APIKey + ":x@bibles.org/v2/chapters/" +
				verseID + "/verses.xml?include_marginalia=false";

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		Document doc = Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();

		for(int i = 0; i < verses.size(); i++) {
			Verse verse = verses.get(i);

			Elements scripture = doc.select("verse[id=" + verseID + "." + verse.getReference().verse + "]");

			Document textHTML = Jsoup.parse(scripture.select("text").text());
			textHTML.select("sup").remove();

			verse.setText(textHTML.text());
		}

		if(listener != null) listener.onDownloadSuccess();
	}
}
