package com.caseybrooks.androidbibletools.basic;

import java.util.HashMap;

/**
 * A base class for a collection of Bibles, typically offered by an online Bible text provider.
 * A BibleList allows a user to choose their desired Bible version to download verses from. Providers
 * looking to add their own web services should extend this class to implement the necessary
 * functionality. A BibleList class can be used by a
 * {@link com.caseybrooks.androidbibletools.widget.BiblePicker} to expose the list of Bibles to users
 * to be set as a preference.
 *
 * @param <T>  the type of Bible contained in this list
 *
 * @see Bible
 * @see com.caseybrooks.androidbibletools.widget.BiblePicker
 */
public abstract class BibleList<T extends Bible> {
	protected HashMap<String, T> bibles;

	/**
	 * No-arg constructor is necessary to allow compatibility with
	 * {@link com.caseybrooks.androidbibletools.widget.BiblePicker}
	 */
	public BibleList() {
	}

	/**
	 * Get a HashMap containing the available Bibles, with their primary ID as a map key.
	 *
	 * @return map of available Bibles
	 */
	public HashMap<String, T> getBibles() {
		return bibles;
	}

	/**
	 * Manually set the list of Bibles. Useful if you need to create the BibleList from another class,
	 * but still want to use it with this class for its compatibility with
	 * {@link com.caseybrooks.androidbibletools.widget.BiblePicker}
	 *
	 * @param bibles  the map of keys to Bibles to set
	 */
	public void setBibles(HashMap<String, T> bibles) {
		this.bibles = bibles;
	}
}
