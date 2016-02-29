package com.caseybrooks.androidbibletools.data;

//TODO: Make this interface return the object that finished downloading
//TODO: Decide whether I should make it return an AbstractVerse/Bible, etc, or a generic Object, or template it
public interface OnResponseListener {
	void responseFinished(boolean success);
}
