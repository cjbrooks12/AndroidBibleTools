package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;

/**
 * An extension of the Android TextView which makes displaying Verses in this
 * library incredibly simple. It allows for simple yet rich display formatting,
 * because it displays text from the Verses as HTML (using 'Html.fromHtml()'). It
 * also supports automatic verse downloading and caching for downloadable passages,
 * plugging directly into the BiblePicker widget.
 *
 * By default, this VerseView assumes that the BiblePicker is used to select
 * the user's preferred Bible, and pulls the Bible to download from those classes,
 * which is persisted automatically into SharedPreferences.
 */
public class VerseView extends TextView implements IVerseView, IVerseViewListener {
//Data Members
//------------------------------------------------------------------------------
	Context context;

	boolean displayAsHtml;
	boolean displayRawText;

	VerseWorker worker;
	IVerseViewListener listener;

//Constructors and Initialization
//------------------------------------------------------------------------------
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

		worker = new VerseWorker(context);
		worker.setListener(this);
	}

	@Override
	public void loadSelectedBible() {
		worker.loadSelectedBible();
	}

	public void showText() {
		post(new Runnable() {
			@Override
			public void run() {
				String textToShow = displayRawText ? worker.getVerse().getRawText() : worker.getVerse().getText();

				if(displayAsHtml) {
					setText(Html.fromHtml(textToShow));
				}
				else {
					setText(textToShow);
				}
			}
		});
	}

	@Override
	public void displayCachedText() {
		worker.displayCachedText();
	}

	@Override
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
	public boolean onVerseLoaded(final AbstractVerse verse, LoadState state) {
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
						setText("Error displaying verse" + verse.getText());
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
