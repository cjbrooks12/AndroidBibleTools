package com.caseybrooks.androidbibletools.widget;

import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;

/**
 * An interface that prepares and manages a downloadable verse for displaying to
 * a user.
 */
public interface IVerseView {
	void loadSelectedBible();
	boolean hasCachedText();
	void displayCachedText();
	void displayDownloadedText();
	void tryCacheOrDownloadText();

	void setVerse(ABSPassage verse);
	ABSPassage getVerse();
}
