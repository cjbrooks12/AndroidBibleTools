package com.caseybrooks.androidbibletools.basic;

/**
 * Represents a tag the user can set on a verse. Really, is just an arbitrary bit of metadata with
 * nothing clever about it, and will eventually be removed once I figure out how to better handle
 * something like this, or how to make it more useful. As it is, this is mostly a class that adds
 * functionality that needs to be baked into Scripture Now!, but is not so useful elsewhere.
 */
@Deprecated
public class Tag implements Comparable<Tag> {
	public String name;
	public int id;
	public int color;
	public int count;

	/**
	 * Initialize this Tag to its default values.
	 */
	public Tag() {
		this.name = null;
		this.id = 0;
		this.color = 0;
		this.count = 0;
	}

	/**
	 * Initialize this tag with a name, and all other values as defaults. Useful for allowing a user
	 * to create a new Tag, but they don't have the information exposed to give it an id or color, or
	 * to know how many other verses also have this tag.
	 *
	 * @param name  the name of this Tag
	 */
	public Tag(String name) {
		if(name != null && name.length() > 0) {
			this.name = name;
		}
		this.id = 0;
		this.color = 0;
		this.count = 0;
	}

	/**
	 * Initialize this tag with all its properties set to custom values. Useful for extracting tags
	 * data from a database.
	 *
	 * @param name  the name of this Tag
	 * @param id  the unique id by which this Tag can be found in the database
	 * @param color  the color displayed that represents this Tag
	 * @param count  the number of verses in the database that share this Tag
	 */
	public Tag(String name, int id, int color, int count) {
		if(name != null && name.length() > 0) {
			this.name = name;
		}
		this.id = id;
		this.color = color;
		this.count = count;
	}

	/**
	 * Compare this Tag to another by name.
	 *
	 * @param another  the Tag to compare, for sorting
	 * @return result of name.compareToIgnoreCase(another.name)
	 */
	@Override
	public int compareTo(Tag another) {
		return this.name.compareToIgnoreCase(another.name);
	}
}
