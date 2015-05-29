package com.caseybrooks.androidbibletools.io;

/**
 * A helper class to facilitate the sharing of verses on Android. It defines a
 * new Action for Intent-Filter to be usable by any given app, and creates an
 * Intent given a list of Verses that will be shared according to this format.
 * It also can receive an Intent sent by this method and return a list of verses
 * to the caller, but the Intent extras contain lots of additional information
 * that may be useful to the receiver, such as the name of the sending app.
 */
public class Send {
//	public static String ACTION_SEND_VERSES = "ACTION_SEND_VERSES";
//	public static String SENDER = "ABT_SENDER";
//	public static String VERSE_XML_DATA = "VERSE_XML_DATA";
//
//	public static Element toXML(ArrayList<AbstractVerse> verses) {
//		try {
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			org.w3c.dom.Document doc = builder.newDocument();
//			org.w3c.dom.Element root = doc.createElement("verses");
//
//			if(verses.size() > 0) {
//				for(AbstractVerse verse : verses) {
//					root.appendChild(verse.toXML(doc));
//				}
//			}
//			return root;
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public static String toXMLString(ArrayList<AbstractVerse> verses) {
//		try {
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			org.w3c.dom.Document doc = builder.newDocument();
//			org.w3c.dom.Element root = doc.createElement("verses");
//
//			if(verses.size() > 0) {
//				for(AbstractVerse verse : verses) {
//					root.appendChild(verse.toXML(doc));
//				}
//			}
//
//			StringWriter writer = new StringWriter();
//			Transformer transformer = TransformerFactory.newInstance().newTransformer();
//			transformer.transform(new DOMSource(doc), new StreamResult(writer));
//			return writer.toString();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public static ArrayList<AbstractVerse> parseXML(String XML, Bible bible) {
//		ArrayList<AbstractVerse> verses = new ArrayList<>();
//		Document doc = Jsoup.parse(XML);
//
//		Elements children = doc.children();
//
//		for(org.jsoup.nodes.Element child : children) {
//			if(child.tagName().equals("passage")) {
//				Passage passage = Passage.fromXML(child);
//				verses.add(passage);
//			}
//			else if(child.tagName().equals("verse")) {
//				try {
//					Verse verse = new Verse(Reference.parseReference(child.attr("reference"), bible));
//					verse.setBible(Bible.parseVersion(child.attr("version")));
//					verse.setText(child.text());
//					verses.add(verse);
//				}
//				catch(ParseException pe) {
//					pe.printStackTrace();
//				}
//			}
//		}
//
//
//		return verses;
//	}
//
//	public static Intent getShareIntent(Context context, ArrayList<AbstractVerse>verses) {
//		Intent intent = new Intent(ACTION_SEND_VERSES);
//		Bundle extras = new Bundle();
//
//		final PackageManager pm = context.getPackageManager();
//		ApplicationInfo ai;
//
//		try {
//			ai = pm.getApplicationInfo(context.getPackageName(), 0);
//		}
//		catch (final PackageManager.NameNotFoundException e) {
//			ai = null;
//		}
//		final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
//
//		extras.putString(SENDER, applicationName);
//		extras.putString(VERSE_XML_DATA, toXMLString(verses));
//
//		intent.putExtras(extras);
//		return intent;
//	}
//
//	public static ArrayList<AbstractVerse> getVersesFromIntent(Context context, Intent intent) {
//
//		return null;
//	}

}