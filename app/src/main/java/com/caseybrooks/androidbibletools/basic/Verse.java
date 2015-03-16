package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.enumeration.BookEnum;
import com.caseybrooks.androidbibletools.enumeration.VersionEnum;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/** The simplest unit of data in this data structure. Each verse contains one
 *  and only one Bible verse, its corresponding Book, Chapter, and Verse Number,
 *  and the version this specific Verse is.
 *
 *  Verse objects are immutable. The necessary core information, the
 *  verse reference and its getText, cannot be modified once set, as the Verse should
 *  always point to the same Verse, and the getText should correspond directly to
 *  this verse, not anything the user wants. Should the user be unable to connect
 *  to the internet at the time of creating a Verse, they may manually set the
 *  getText in the constructor, but it cannot be modified after this.
 *
 *  The display flags and Version may be changed at will, so that the user may
 *  view the same verse in different translations and formats. Think of a Verse
 *  object as a reference to a particular Verse, not the getText of the verse. In
 *  this way, it makes sense that a Verse can be displayed in multiple versions
 *  and in different formats.
 */
public class Verse extends AbstractVerse {
////Verse implementation of a Reference
////------------------------------------------------------------------------------
//	public static class Reference extends com.caseybrooks.androidbibletools.data.Reference {
//		public Reference(Book book, int chapter, int... verses) {
//			super(book, chapter, verses);
//		}
//
//		public Reference(Book book, int chapter, ArrayList<Integer> verses) {
//			super(book, chapter, verses);
//		}
//	}

//Data Members
//------------------------------------------------------------------------------
    //Data members that make up the actual verse
    protected String verseText;

//Constructors
//------------------------------------------------------------------------------
    public Verse(Reference reference) {
        super(reference);
    }

    public Verse(String reference) throws ParseException {
        super(reference);
    }

//Getters and Setters
//------------------------------------------------------------------------------
    //Verse Text is mutable, should be set when a user manually inputs a verse,
    //or when downloading the verse in a new Version.
    public Verse setText(String verseText) {
        this.verseText = verseText;
        return this;
    }

	public Verse next() {
		if(reference.verses.get(0) != reference.book.numVersesInChapter(reference.chapter)) {
            Reference nextRef = new Reference(reference.book, reference.chapter, reference.verses.get(0) + 1);
			return new Verse(nextRef);
		}
		else {
			if(reference.chapter != reference.book.numChapters()) {
                Reference nextRef = new Reference(reference.book, reference.chapter + 1, reference.verses.get(0));
                return new Verse(nextRef);
			}
			else {
				for(int i = 0; i < BookEnum.values().length; i++) {
					if((reference.book == BookEnum.values()[i]) && (i != BookEnum.values().length - 1)) {
                        Reference nextRef = new Reference(BookEnum.values()[i+1], 1, 1);
						return new Verse(nextRef);
					}
				}
                Reference nextRef = new Reference(BookEnum.values()[0], 1, 1);
				return new Verse(nextRef);
			}
		}
	}

	public Verse previous() {
		if(reference.verses.get(0) != 1) {
            Reference previousRef = new Reference(reference.book, reference.chapter, reference.verses.get(0) - 1);
            return new Verse(previousRef);
		}
		else {
			if(reference.chapter != 1) {
                Reference previousRef = new Reference(reference.book, reference.chapter - 1, reference.book.numVersesInChapter(reference.chapter - 1));
                return new Verse(previousRef);
			}
			else {
				BookEnum newBook;
				for(int i = 0; i < BookEnum.values().length; i++) {
					if((reference.book == BookEnum.values()[i]) && (i != 0)) {
						newBook = BookEnum.values()[i-1];
                        Reference previousRef = new Reference(newBook, newBook.numChapters(), newBook.lastVerseInBook());
                        return new Verse(previousRef);
					}
				}
				newBook = BookEnum.values()[BookEnum.values().length - 1];
                Reference previousRef = new Reference(newBook, newBook.numChapters(), newBook.lastVerseInBook());
                return new Verse(previousRef);
			}
		}
	}

//Print the formatted String
//------------------------------------------------------------------------------
    @Override
    public String getText() {
        String text = "";

        text += formatter.onPreFormat(reference);
        text += formatter.onFormatNumber(reference.verses.get(0));
        text += formatter.onFormatText(verseText);
        text += formatter.onPostFormat();

        return text.trim();
    }

//Turn this Verse into an XML object
//------------------------------------------------------------------------------

	/**The structure for a Verse object will be like the following
	 *
	 * <verse reference="John 1:5" version="KJV">
	 *     The light shines in the darkness, and the darkness has not overcome<footnote>Or understood</footnote> it.
	 * </verse>
	 */

	@Override
	public Element toXML(org.w3c.dom.Document doc) {
		org.w3c.dom.Element root = doc.createElement("verse");

		root.setAttribute("reference", reference.toString());
		root.setAttribute("version", version.getCode().toUpperCase());
		root.appendChild(doc.createTextNode(verseText));

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

	public static Verse fromXML(org.jsoup.nodes.Element passageRoot) {
		try {
			Verse verse = new Verse(passageRoot.attr("reference"));
			verse.setVersion(VersionEnum.parseVersion(passageRoot.attr("version")));
			verse.setText(passageRoot.text());

			return verse;
		}
		catch(ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}


//Comparison methods for sorting
//------------------------------------------------------------------------------

    //Compares two Verses with respect to classical reference order
    //RETURN VALUES (negative indicates lhs is less than rhs)
    //
    //  0: Verses are equal, since they point to the same verse
    //  1: Verses are adjacent
    //  2: Verses are not adjacent, but are in the same chapter
    //  3: Verses are not adjacent, but are in different chapters of the same Book
    //  4: Verses are not adjacent, and aren't even in the same Book
    @Override
    public int compareTo(@NonNull AbstractVerse verse) {
        Verse lhs = this;
        Verse rhs = (Verse) verse;

        //get the position of each book as an integer so we can work with it
        int aBook = -1, bBook = -1;
        for(int i = 0; i < BookEnum.values().length; i++) {
            if(BookEnum.values()[i] == lhs.reference.book) aBook = i;
            if(BookEnum.values()[i] == rhs.reference.book) bBook = i;
        }

        if(aBook - bBook == 1) {
            if((lhs.reference.chapter == 1 && lhs.reference.verses.get(0) == 1) &&
               (rhs.reference.chapter == rhs.reference.book.numChapters() &&
                 (rhs.reference.verses.get(0) == rhs.reference.book.numVersesInChapter(rhs.reference.chapter)))) return 1;
            else return 4;
        }
        else if(aBook - bBook == -1) {
            if((rhs.reference.chapter == 1 && rhs.reference.verses.get(0) == 1) &&
               (lhs.reference.chapter == lhs.reference.book.numChapters() &&
                 (lhs.reference.verses.get(0) == lhs.reference.book.numVersesInChapter(lhs.reference.chapter)))) return -1;
            else return -4;
        }
        else if(aBook > bBook) return 4;
        else if(aBook < bBook) return -4;
        else {
            //same book
            if(lhs.reference.chapter - rhs.reference.chapter == 1) {
                if((lhs.reference.verses.get(0) == 1) &&
                   (rhs.reference.verses.get(0) == rhs.reference.book.numVersesInChapter(rhs.reference.chapter))) return 1;
                else return 3;
            }
            if(lhs.reference.chapter - rhs.reference.chapter == -1) {
                if((rhs.reference.verses.get(0) == 1) &&
                   (lhs.reference.verses.get(0) == lhs.reference.book.numVersesInChapter(lhs.reference.chapter))) return -1;
                else return -3;
            }
            else if(lhs.reference.chapter > rhs.reference.chapter) return 3;
            else if(lhs.reference.chapter < rhs.reference.chapter) return -3;
            else {
                //same chapter
                if(lhs.reference.verses.get(0) - rhs.reference.verses.get(0) == 1) return 1;
                else if(lhs.reference.verses.get(0) - rhs.reference.verses.get(0) == -1) return -1;
                else if(lhs.reference.verses.get(0) > rhs.reference.verses.get(0)) return 2;
                else if(lhs.reference.verses.get(0) < rhs.reference.verses.get(0)) return -2;
                else return 0; //lhs.reference.verses.get(0) == rhs.reference.verses.get(0)
            }
        }
    }

	@Override
    public boolean equals(Object verse) {
		if(verse instanceof Verse) {
			Verse lhs = this;
			Verse rhs = (Verse) verse;

			return lhs.reference.equals(rhs.reference);
		}
		else {
			return false;
		}
    }

//Retrieve verse from the internet
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
					reference.book.getCode() +
					reference.chapter + "." +
					reference.verse;
		default:
			return null;
		}
	}

	@Override
	public void retrieve(String APIKey) throws IOException {
		if(listener != null) listener.onPreDownload();

		String verseID = "eng-" +
				version.getCode() + ":" +
				reference.book.getCode() +"." +
				reference.chapter;

		String url = "http://" + APIKey + ":x@bibles.org/v2/chapters/" +
				verseID + "/verses.xml?include_marginalia=false";

		String header = APIKey + ":x";
		String encodedHeader = Base64.encodeToString(header.getBytes("UTF-8"), Base64.DEFAULT);

		Document doc = Jsoup.connect(url).header("Authorization", "Basic " + encodedHeader).get();

		Elements scripture = doc.select("verse[id=" + verseID + "." + reference.verse + "]");
		Document textHTML = Jsoup.parse(scripture.select("text").text());
		textHTML.select("sup").remove();

		setText(textHTML.text());

		if(listener != null) listener.onDownloadSuccess();
	}
}
