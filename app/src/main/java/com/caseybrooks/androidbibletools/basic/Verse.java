package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

/** The simplest unit of data in this data structure. Each verse contains one
 *  and only one Bible verse, its corresponding Book, Chapter, and Verse Number,
 *  and the bible this specific Verse is.
 *
 *  Verse objects are immutable. The necessary core information, the
 *  verse reference and its getText, cannot be modified once set, as the Verse should
 *  always point to the same Verse, and the getText should correspond directly to
 *  this verse, not anything the user wants. Should the user be unable to connect
 *  to the internet at the time of creating a Verse, they may manually set the
 *  getText in the constructor, but it cannot be modified after this.
 *
 *  The display flags and Bible may be changed at will, so that the user may
 *  view the same verse in different translations and formats. Think of a Verse
 *  object as a reference to a particular Verse, not the getText of the verse. In
 *  this way, it makes sense that a Verse can be displayed in multiple versions
 *  and in different formats.
 */
public class Verse extends AbstractVerse {
//Data Members
//------------------------------------------------------------------------------
    protected String verseText;

//Constructors
//------------------------------------------------------------------------------
    public Verse(Reference reference) {
        super(reference);
    }

//Getters and Setters
//------------------------------------------------------------------------------
    //Verse Text is mutable, should be set when a user manually inputs a verse,
    //or when downloading the verse in a new Bible.
    public Verse setText(String verseText) {
        this.verseText = verseText;
        return this;
    }

//	public Verse next() {
//		if(reference.verses.get(0) != reference.book.numVersesInChapter(reference.chapter)) {
//            Reference nextRef = new Reference(reference.book, reference.chapter, reference.verses.get(0) + 1);
//			return new Verse(nextRef);
//		}
//		else {
//			if(reference.chapter != reference.book.numChapters()) {
//                Reference nextRef = new Reference(reference.book, reference.chapter + 1, reference.verses.get(0));
//                return new Verse(nextRef);
//			}
//			else {
//				for(int i = 0; i < BookEnum.values().length; i++) {
//					if((reference.book == BookEnum.values()[i]) && (i != BookEnum.values().length - 1)) {
//                        Reference nextRef = new Reference(BookEnum.values()[i+1], 1, 1);
//						return new Verse(nextRef);
//					}
//				}
//                Reference nextRef = new Reference(BookEnum.values()[0], 1, 1);
//				return new Verse(nextRef);
//			}
//		}
//	}

//	public Verse previous() {
//		if(reference.verses.get(0) != 1) {
//            Reference previousRef = new Reference(reference.book, reference.chapter, reference.verses.get(0) - 1);
//            return new Verse(previousRef);
//		}
//		else {
//			if(reference.chapter != 1) {
//                Reference previousRef = new Reference(reference.book, reference.chapter - 1, reference.book.numVersesInChapter(reference.chapter - 1));
//                return new Verse(previousRef);
//			}
//			else {
//				BookEnum newBook;
//				for(int i = 0; i < BookEnum.values().length; i++) {
//					if((reference.book == BookEnum.values()[i]) && (i != 0)) {
//						newBook = BookEnum.values()[i-1];
//                        Reference previousRef = new Reference(newBook, newBook.numChapters(), newBook.lastVerseInBook());
//                        return new Verse(previousRef);
//					}
//				}
//				newBook = BookEnum.values()[BookEnum.values().length - 1];
//                Reference previousRef = new Reference(newBook, newBook.numChapters(), newBook.lastVerseInBook());
//                return new Verse(previousRef);
//			}
//		}
//	}

//Print the formatted String
//------------------------------------------------------------------------------
    @Override
    public String getText() {
        String text = "";

        text += formatter.onPreFormat(reference);
        text += formatter.onFormatVerseStart(reference.verses.get(0));
        text += formatter.onFormatText(verseText);
        text += formatter.onPostFormat();

        return text.trim();
    }

//Turn this Verse into an XML object
//------------------------------------------------------------------------------

//	/**The structure for a Verse object will be like the following
//	 *
//	 * <verse reference="John 1:5" bible="KJV">
//	 *     The light shines in the darkness, and the darkness has not overcome<footnote>Or understood</footnote> it.
//	 * </verse>
//	 */
//
//	@Override
//	public Element toXML(org.w3c.dom.Document doc) {
//		org.w3c.dom.Element root = doc.createElement("verse");
//
//		root.setAttribute("reference", reference.toString());
//		root.setAttribute("bible", bible.id);
//		root.appendChild(doc.createTextNode(verseText));
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
//	public static Verse fromXML(org.jsoup.nodes.Element passageRoot) {
//		try {
//			Bible bible = new Bible(passageRoot.attr("bible"));
//			Verse verse = new Verse(Reference.parseReference(passageRoot.attr("reference"), bible));
//			verse.setText(passageRoot.text());
//
//			return verse;
//		}
//		catch(ParseException pe) {
//			pe.printStackTrace();
//			return null;
//		}
//	}


//Comparison methods for sorting
//------------------------------------------------------------------------------

    @Override
    public int compareTo(@NonNull AbstractVerse verse) {
        Verse lhs = this;
        Verse rhs = (Verse) verse;

		return lhs.reference.compareTo(rhs.getReference());
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
}
