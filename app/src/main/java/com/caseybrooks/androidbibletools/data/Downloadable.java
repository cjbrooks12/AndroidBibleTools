package com.caseybrooks.androidbibletools.data;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface Downloadable {
	/**
	 * Determines whether this object is downloadable
	 *
	 * @return if this object has data that can be downloaded
	 */
	public boolean isAvailable();

	/**
	 * Downloadable items typically have some kind of unique identifier which is
	 * used to get data about that object. This returns that identifier assuming
	 * that it exists and the item is available to download.
	 *
	 * @return the unique identifier
	 */
	public String getId();

	/**
	 * Download the data for this object
	 *
	 * @return a Jsoup Document with the data to be parsed
	 */
	public Document getDocument() throws IOException;

	/**
	 * Extracts the data from within this Document
	 *
	 * @return true on a successful parse, false otherwise
	 */
	public boolean parseDocument(Document doc);
}
