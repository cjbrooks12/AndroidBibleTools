package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * An extension of the Android EditText that exactly parallels the VerseView. As an EditText is
 * little more than a TextView whose text can be edited, so an EditVerse is a VerseView that can be
 * edited. By default it does not support the advanced formatting of the VerseView (because the text
 * of the Verse should not include formatting tags as they will be considered part of the verse's
 * text), but does support the same kind of automatic downloading and caching.
 */
public class EditVerse extends EditText {
//Data Members
//--------------------------------------------------------------------------------------------------
	Context context;

	boolean displayAsHtml;
	boolean displayRawText;

//Constructors and Initialization
//--------------------------------------------------------------------------------------------------
	public EditVerse(Context context) {
		super(context);
		this.context = context;

		initialize();
	}

	public EditVerse(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initialize();
	}

	public void initialize() {
		displayAsHtml = false;
		displayRawText = false;
	}


//	public void showText() {
//		String textToShow = displayRawText ? worker.getVerse().getRawText() : worker.getVerse().getText();
//
//		if(displayAsHtml) {
//			setText(Html.fromHtml(textToShow));
//		}
//		else {
//			setText(textToShow);
//		}
//	}

	public boolean isDisplayingAsHtml() {
		return displayAsHtml;
	}

	public void setDisplayingAsHtml(boolean displayAsHtml) {
		this.displayAsHtml = displayAsHtml;
	}

	public boolean isDisplayingRawText() {
		return displayRawText;
	}

	public void setDisplayingRawText(boolean displayRawText) {
		this.displayRawText = displayRawText;
	}

}
