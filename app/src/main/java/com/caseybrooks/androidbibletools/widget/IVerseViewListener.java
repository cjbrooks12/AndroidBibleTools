package com.caseybrooks.androidbibletools.widget;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Bible;

/**
 * Since a VerseView is intended to work asynchronously, this interface listens
 * for when the various events finish
 */
public interface IVerseViewListener {
	boolean onBibleLoaded(Bible bible, LoadState state);
	boolean onVerseLoaded(AbstractVerse verse, LoadState state);
}
