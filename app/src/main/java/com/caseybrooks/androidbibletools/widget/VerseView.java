package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * An extension of the Android TextView which makes displaying Verses in this library incredibly
 * simple. It allows for simple yet rich display formatting, because it displays text from the
 * Verses as HTML (using 'Html.fromHtml()'). It also supports automatic verse downloading and
 * caching for downloadable passages, plugging directly into the BiblePicker widget.
 * <p/>
 * By default, this VerseView assumes that the BiblePicker is used to select the user's preferred
 * Bible, and pulls the Bible to download from those classes, which is persisted automatically into
 * SharedPreferences.
 */
public class VerseView extends TextView {
//Data Members
//--------------------------------------------------------------------------------------------------
	Context context;

	boolean displayAsHtml;
	boolean displayRawText;

//Constructors and Initialization
//--------------------------------------------------------------------------------------------------
	public VerseView(Context context) {
		super(context);
		this.context = context;

		initialize();
	}

	public VerseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initialize();
	}

	public void initialize() {
		displayAsHtml = true;
		displayRawText = false;
	}

//	public void showText() {
//		post(new Runnable() {
//			@Override
//			public void run() {
//				String textToShow = displayRawText ? worker.getVerse().getRawText() : worker.getVerse().getText();
//
//				if(displayAsHtml) {
//					setText(Html.fromHtml(textToShow));
//				}
//				else {
//					setText(textToShow);
//				}
//			}
//		});
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
