package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Html;
import android.util.AttributeSet;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;

/**
 * An extension of the Android EditText that exactly parallels the VerseView. As
 * an EditText is little more than a TextView whose text can be edited, so an
 * EditVerse is a VerseView that can be edited. By default it does not support the
 * advanced formatting of the VerseView (because the text of the Verse should not
 * include formatting tags as they will be considered part of the verse's text),
 * but does support the same kind of automatic downloading and caching.
 */
public class EditVerse extends AppCompatEditText implements IVerseView, IVerseViewListener {
//Data Members
//------------------------------------------------------------------------------
	Context context;

	boolean displayAsHtml;
	boolean displayRawText;

	VerseWorker worker;
	IVerseViewListener listener;

//Constructors and Initialization
//------------------------------------------------------------------------------
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

		worker = new VerseWorker(context);
		worker.setListener(this);
	}

	public void loadSelectedBible() {
		worker.loadSelectedBible();
	}

	public void showText() {
		String textToShow = displayRawText ? worker.getVerse().getRawText() : worker.getVerse().getText();

		if(displayAsHtml) {
			setText(Html.fromHtml(textToShow));
		}
		else {
			setText(textToShow);
		}
	}

	public void displayCachedText() {
		worker.displayCachedText();
	}

	public boolean hasCachedText() {
		return worker.hasCachedText();
	}

	public void displayDownloadedText() {
		worker.displayDownloadedText();
	}

	public void tryCacheOrDownloadText() {
		worker.tryCacheOrDownloadText();
	}

	public ABSPassage getVerse() {
		return worker.getVerse();
	}

	public void setVerse(ABSPassage verse) {
		worker.setVerse(verse);
	}

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

	@Override
	public boolean onBibleLoaded(Bible bible, LoadState state) {
		return false;
	}

	@Override
	public boolean onVerseLoaded(AbstractVerse verse, LoadState state) {
		boolean handled = false;
		if(listener != null) {
			handled = listener.onVerseLoaded(verse, state);
		}

		if(!handled) {
			if(state != LoadState.Failed) {
				showText();
			}
			else {
				post(new Runnable() {
					@Override
					public void run() {
						setText("Error displaying verse");
					}
				});
			}
		}

		return false;
	}

	public IVerseViewListener getListener() {
		return listener;
	}

	public void setListener(IVerseViewListener listener) {
		this.listener = listener;
	}
}
