package com.caseybrooks.androidbibletools.basic;

import android.support.annotation.NonNull;

import com.caseybrooks.androidbibletools.data.Formatter;
import com.caseybrooks.androidbibletools.defaults.DefaultFormatter;

import java.util.TreeSet;

/**
 * An abstract implementation of a Verse in the Bible. A verse represents a location and its text,
 * and is considered immutable in that the location the verse points to is fixed. A verse contains
 * several peripheral classes to give a verse metadata, provide intelligent sorting, and create a
 * consistent IO pattern that enables use with the various UI widgets contained in this library.
 *
 * Each verse must be initialized with the Reference that refers to its location in the Bible.
 * References are immutable, and so this verse will always describe the same verse in the same Bible.
 * The actual text and other metadata associated with the verse may change, so that the same verse
 * text can be modified by a user or displayed differently.
 *
 * @see Reference
 */
public abstract class AbstractVerse implements Comparable<AbstractVerse> {
	protected final Reference reference;
	protected Formatter formatter;
	protected Metadata metadata;
	protected TreeSet<Tag> tags;
	protected String id;

	/**
	 * Required constructor for this verse. Accepts just the Reference, and all other values are set
	 * to their default empty states.
	 *
	 * @param reference  the Reference that this verse points to in the Bible
	 *
	 * @see Reference
	 */
	public AbstractVerse(Reference reference) {
		this.reference = reference;
		this.formatter = new DefaultFormatter();
		this.metadata = new Metadata();
		this.tags = new TreeSet<>();
	}

	/**
	 * Get the Reference this verse points to.
	 *
	 * @return the reference
	 */
	public Reference getReference() {
		return reference;
	}

	/**
	 * Get the Formatter used to display this verse's text.
	 *
	 * @return the formatter
	 */
	public Formatter getFormatter() {
		return formatter;
	}

	/**
	 * Set the Formatter to be used when printing this verse with {@link AbstractVerse#getFormattedText()}
	 *
	 * @param formatter  the Formatter to be used
	 *
	 * @see com.caseybrooks.androidbibletools.defaults.DefaultFormatter
	 */
	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * Get the metadata associated with this verse.
	 *
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * Set the metadata for this verse. Can be useful when trying to sync the state of multiple verses.
	 *
	 * @param metadata  the MetaData to set
	 *
	 * @see Metadata
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Get this verses's id.
	 *
	 * @return this verse's id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Most implementations of verses have some concept of an Id. This may be a key needed to retrieve
	 * its text from a webservice, its primary key to look it up in a local database, or simply the
	 * text of its Reference. Regardless of how it is used, it is simpler to keep this in the base class
	 * since it is such a common use case.
	 *
	 * @param id  the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Convenience method for removing all tags from this verse.
	 *
	 * @return this verse, for chaining
	 */
	public AbstractVerse removeAllTags() {
		tags.clear();

		return this;
	}

	/**
	 * Convenience method for removing the specified tag from this verse, if it exists.
	 *
	 * @param tag  the tag to remove
	 * @return this verse, for chaining
	 */
	public AbstractVerse removeTag(Tag tag) {
		if(tags.contains(tag)) {
			tags.remove(tag);
		}

		return this;
	}

	/**
	 * Convenience method for adding many tags to a verse.
	 *
	 * @param tags  the tags to set
	 * @return this verse, for chaining
	 */
	public AbstractVerse setTags(Tag... tags) {
		for(Tag item : tags) {
			this.tags.add(item);
		}
		return this;
	}

	/**
	 * Convenience method for adding a tag to a verse.
	 *
	 * @param tag  the tags to set
	 * @return this verse, for chaining
	 */
	public AbstractVerse addTag(Tag tag) {
		this.tags.add(tag);
		return this;
	}

	/**
	 * Convenience method for checking to see if this verse contains the given tag.
	 *
	 * @param tag  the tag to check
	 * @return this verse, for chaining
	 */
	public boolean containsTag(Tag tag) {
		return this.tags.contains(tag);
	}

	/**
	 * Get the list of tags on this verse.
	 *
	 * @return the list of tags
	 */
	public Tag[] getTags() {
		Tag[] tags = new Tag[this.tags.size()];
		this.tags.toArray(tags);
		return tags;
	}

	/**
	 * Get the unformatted text of this verse. In addition to not using the Formatter for output, it
	 * if likely that the text of implementing classes will contain markup that will be given with
	 * this method. This might be useful for display if that markup is simple HTML markup.
	 *
	 * @return unformatted text of the verse
	 */
	public abstract String getText();

	/**
	 * Get the formatted text of this verse, using the set Formatter.
	 *
	 * @return the formatted text of the verse
	 *
	 * @see #setFormatter(com.caseybrooks.androidbibletools.data.Formatter)
	 */
	public abstract String getFormattedText();

	/**
	 * Serialize this verse into a string that can be used to restore this verse from persistent
	 * memory. Should serialize only that which is necessary to be able to identify this verse and
	 * download the rest of the information. This would typically be the fully qualified class name
	 * of this AbstractVerse, and any IDs/API keys necessary.
	 *
	 * @return serialized String representation
	 */
	public String serialize() {
		return "";
	}

	/**
	 * deserialize this verse from String.
	 *
	 * @see AbstractVerse#serialize()
	 */
	public void deserialize(String string) {

	}

	/**
	 * Compare two AbstractVerses. Each implementation is responsible for deciding whether that
	 * AbstractVerse implementation can be sorted with any other implementation, and if so, how that
	 * comparison should work. Refer to the documentation of any implementing classes to see how it
	 * is handled.
	 *
	 * @param verse  the verse to compare with
	 * @return the result of comparison
	 */
	public abstract int compareTo(@NonNull AbstractVerse verse);

	/**
	 * Compare two AbstractVerses for equality. Each implementation is responsible for deciding
	 * whether that AbstractVerse implementation can be equal to any other implementation, and if
	 * so, how that comparison should work. Refer to the documentation of any implementing classes
	 * to see how it is handled.
	 *
	 * @param o the object to compare with
	 * @return
	 */
	@Override
	public abstract boolean equals(Object o);

	/**
	 * get the hashcode for this verse.
	 *
	 * @return the hashcode
	 */
	@Override
	public abstract int hashCode();
}