package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import com.caseybrooks.androidbibletools.data.Formatter;
import com.caseybrooks.androidbibletools.defaults.DefaultFormatter;

import java.util.TreeSet;

/**
 * An abstract implementation of a Verse in the Bible. A verse represents a location and its text,
 * and is considered immutable in that the location the verse points to is fixed. A verse contains
 * several peripheral classes to give a verse metadata, provide intelligent sorting, and create a
 * consistent IO pattern.
 */
public abstract class AbstractVerse implements Comparable<AbstractVerse> {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected Bible bible;
	protected final Reference reference;
	protected Formatter formatter;
	protected Metadata metadata;
	protected TreeSet<Tag> tags;
	protected String id;

	/**
	 * Constructor for AbstractVerse that takes a Reference object as initialization
	 *
	 * @param reference the Reference that this verse points to in the Bible
	 *
	 * @see Reference
	 */
	public AbstractVerse(Reference reference) {
		this.reference = reference;
		this.formatter = new DefaultFormatter();
		this.metadata = new Metadata();
		this.tags = new TreeSet<>();
	}

//Defined methods
//--------------------------------------------------------------------------------------------------

	/**
	 * Get the currently set Bible of this verse
	 *
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return bible;
	}

	/**
	 * Set the Bible of this verse
	 *
	 * @param bible the desired Bible translation
	 *
	 * @see Bible
	 */
	public void setBible(Bible bible) {
		this.bible = bible;
	}

	/**
	 * Get the Reference this verse points to
	 *
	 * @return {@link Reference}
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
	 * @return {@link Metadata}
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * Set the metadata to be associated with this verse
	 *
	 * @param metadata the MetaData object to set
	 *
	 * @see Metadata
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	//Tags
//--------------------------------------------------------------------------------------------------
	public AbstractVerse removeAllTags() {
		tags.clear();

		return this;
	}

	public AbstractVerse removeTag(Tag tag) {
		if(tags.contains(tag)) {
			tags.remove(tag);
		}

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
//--------------------------------------------------------------------------------------------------

	/**
	 * Get a the text of this verse
	 *
	 * @return {@link java.lang.String} unformatted text of the verse
	 *
	 * @see #setFormatter(com.caseybrooks.androidbibletools.data.Formatter)
	 */
	public abstract String getText();

	/**
	 * Get a the formatted text of this verse
	 *
	 * @return {@link java.lang.String} formatted text of the verse
	 *
	 * @see #setFormatter(com.caseybrooks.androidbibletools.data.Formatter)
	 */
	public abstract String getFormattedText();

//Comparison methods
//--------------------------------------------------------------------------------------------------

	public abstract int compareTo(@NonNull AbstractVerse verse);

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
}