package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import com.caseybrooks.androidbibletools.data.Formatter;
import com.caseybrooks.androidbibletools.data.Metadata;
import com.caseybrooks.androidbibletools.data.OnDownloadListener;
import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.defaults.DefaultFormatter;

import org.jsoup.nodes.Document;

import java.util.TreeSet;

/**An abstract implementation of a Verse in the Bible. A verse represents a location
 * and its text, and is considered immutable in that the location the verse
 * points to is fixed. A verse contains several peripheral classes to give a
 * verse metadata, provide intelligent sorting, and create a consistent IO pattern.
 */
public abstract class AbstractVerse implements Comparable<AbstractVerse> {
//Data Members
//------------------------------------------------------------------------------
	protected Bible bible;
    protected final Reference reference; //always points to one single verse
    protected Formatter formatter;
    protected Metadata metadata;
	protected TreeSet<Tag> tags;
	protected OnDownloadListener listener;

	/**
	 * Constructor for AbstractVerse that takes a Reference object as initialization
	 *
	 * @param reference the Reference that this verse points to in the Bible
	 *
	 * @see com.caseybrooks.androidbibletools.data.Reference
	 */
	public AbstractVerse(Reference reference) {
		this.bible = new Bible("eng-esv");
        this.reference = reference;
        this.formatter = new DefaultFormatter();
        this.metadata = new Metadata();
		this.tags = new TreeSet<>();
	}

//Defined methods
//------------------------------------------------------------------------------

	/**
	 * Get the currently set Bible of this verse
	 * @return {@link com.caseybrooks.androidbibletools.data.Bible}
	 *
	 */
	public Bible getBible() {
		return bible;
	}

	/**
	 * Set the Bible of this verse
	 * @param bible the desired Bible translation
	 *
	 * @see com.caseybrooks.androidbibletools.data.Bible
	 */
    public void setBible(Bible bible) {
		this.bible = bible;
	}

	/**
	 * Get the Reference this verse points to
	 *
	 * @return {@link com.caseybrooks.androidbibletools.data.Reference}
	 */
    public Reference getReference() {
		return reference;
	}

	/**
	 * Get the Formatter used to display this verse's text
	 *
	 * @return {@link com.caseybrooks.androidbibletools.data.Formatter}
	 *
	 * @see com.caseybrooks.androidbibletools.defaults.DefaultFormatter
	 */
    public Formatter getFormatter() {
		return formatter;
	}

	/**
	 * Set the Formatter to be used when printing this verse with @link getText()
	 *
	 * @param formatter the Formatter to be used
	 *
	 * @see com.caseybrooks.androidbibletools.data.Formatter
	 * @see com.caseybrooks.androidbibletools.defaults.DefaultFormatter
	 */
    public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * Get the HashMap of arbitrary metadata associated with this verse
	 *
	 * @return {@link com.caseybrooks.androidbibletools.data.Metadata}
	 */
    public Metadata getMetadata() { return metadata; }

	/**
	 * Set the metadata to be associated with this verse
	 *
	 * @param metadata the MetaData object to set
	 *
	 * @see com.caseybrooks.androidbibletools.data.Metadata
	 */
    public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Register a callback to notify the start and end of downloading the text
	 * of this verse
	 * @param listener the callback that will run
	 *
	 * @see com.caseybrooks.androidbibletools.data.OnDownloadListener
	 */
	public void setOnDownloadListener(OnDownloadListener listener) {
		this.listener = listener;
	}

//Tags
//TODO: make Tags its own class
//------------------------------------------------------------------------------
	public AbstractVerse removeAllTags() {
        tags.clear();

        return this;
    }

    public AbstractVerse removeTag(Tag tag) {
        if(tags.contains(tag)) tags.remove(tag);

        return this;
    }

	public AbstractVerse setTags(Tag... tags) {
		for(Tag item : tags) {
			this.tags.add(item);
		}
		return this;
	}

	public AbstractVerse addTag(Tag tag) {
		this.tags.add(tag);
		return this;
	}

	public boolean containsTag(Tag tag) {
		return this.tags.contains(tag);
	}

	public Tag[] getTags() {
		Tag[] tags = new Tag[this.tags.size()];
		this.tags.toArray(tags);
		return tags;
	}

//Abstract Methods
//------------------------------------------------------------------------------

	/**
	 * Get a human-readable text representation of this verse using the set
	 * {@link com.caseybrooks.androidbibletools.data.Formatter}. Default formatters
	 * can display the text in various ways designed for memorization, but custom
	 * Formatters can be configured for any kind of output, including HTML, XML, or JSON
	 *
	 * @return {@link java.lang.String}
	 *
	 * @see #setFormatter(com.caseybrooks.androidbibletools.data.Formatter)
	 */
	public abstract String getText();

//	/**
//	 * Enumeration for the several online bibles to provide links to
//	 */
//	public enum OnlineBible {
//		Biblia,
//		BibleStudyTools,
//		BibleGateway,
//		BlueLetterBible,
//		YouVersion,
//	}
//	/**
//	 * Get a URL to this verse on Biblia.com by Logos Bible Software
//	 *
//	 * @return {@link java.lang.String} the URL of this verse on Biblia.com
//	 */
//    public abstract String getURL(OnlineBible service);

	/**
	 * Load data from the Bibles.org API response into this Verse. Uses either
	 * the response just downloaded, or a previously cached and parsed file.
	 *
	 * @param doc a Jsoup Document containing the XML response to be parsed
	 */
	public abstract void getVerseInfo(Document doc);

//	/**
//	 * Returns a String XML representation of this verse. Is equivalent to calling
//	 * #toXML() and transforming the result to a String.
//	 * @return
//	 */
//	public abstract String toXMLString();
//
//	/**
//	 * Generates an XML representation of this verse, designed to create a consistent
//	 * IO sharing pattern
//	 *
//	 * @param doc the base Document to add this verse as a Node to
//	 * @return {@link org.w3c.dom.Element} a single element of an XML file. May
//	 * be kept as the root or added to another element
//	 */
//	public abstract Element toXML(Document doc);


//Comparison methods
//------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 *
	 * @param verse the verse to compare this to
	 * @return the int result of comparison
	 */
	public abstract int compareTo(@NonNull AbstractVerse verse);

	/**
	 * {@inheritDoc}
	 * @param o the verse to compare this to
	 * @return the boolean result of checking for equality
	 */
	@Override
	public abstract boolean equals(Object o);
}