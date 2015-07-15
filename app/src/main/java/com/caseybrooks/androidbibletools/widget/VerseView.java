package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.io.ABTUtility;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;
import com.caseybrooks.androidbibletools.widget.biblepicker.BiblePickerSettings;

import org.jsoup.nodes.Document;

import java.io.IOException;

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
public class VerseView extends TextView {
//Data Members
//------------------------------------------------------------------------------
	Context context;

	Bible selectedBible;
	ABSPassage verse;

	boolean displayAsHtml;
	boolean displayRawText;

	WorkerThread workerThread;

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
		workerThread = new WorkerThread();
		displayAsHtml = true;
		displayRawText = false;

		loadSelectedBible();
	}

	public void loadSelectedBible() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				selectedBible = BiblePickerSettings.getCachedBible(context);
				if(verse != null) {
					verse.setBible(selectedBible);
				}
			}
		});
	}

	public void showText() {
		String textToShow = displayRawText ? verse.getRawText() : verse.getText();

		if(displayAsHtml) {
			setText(Html.fromHtml(textToShow));
		}
		else {
			setText(textToShow);
		}
	}

	/**
	 * Tries to get the text of the given verse from the cache to display. If
	 * a cached file exists, it will be parsed and displayed as HTML inside
	 * this TextView
	 *
	 * @return true if a cached verse was found and could be displayed
	 */
	public void displayCachedText() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				Document doc = ABTUtility.getChachedDocument(context, verse.getId());

				if(doc != null) {
					verse.parseDocument(doc);
					post(new Runnable() {
						@Override
						public void run() {
							showText();
						}
					});
				}
			}
		});
	}

	public boolean hasCachedDocument() {
		return ABTUtility.getChachedDocument(context, verse.getId()) != null;
	}

	/**
	 * Tries to download the text of this verse from the internet. It does not
	 * try to get the text from the cache, it simply downloads it new. Useful
	 * for forcing a redownload. If the verse is downloaded successfully, it
	 * will display the parsed text as HTML
	 */
	public void displayDownloadedText() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				try {
					Document doc = verse.getDocument();
					if(doc != null) {
						ABTUtility.cacheDocument(context, doc, verse.getId());
						verse.parseDocument(doc);
						post(new Runnable() {
							@Override
							public void run() {
								showText();
							}
						});
					}
				}
				catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
	}

	/**
	 * Try to get the verse text and display it. It first checks the cache, and
	 * failing to display text from the cache, will try to download it from the
	 * internet.
	 */
	public void tryCacheOrDownloadText() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				if(hasCachedDocument()) {
					displayCachedText();
				}
				else {
					displayDownloadedText();
				}
			}
		});
	}

	public Bible getSelectedBible() {
		return selectedBible;
	}

	public void setSelectedBible(Bible selectedBible) {
		this.selectedBible = selectedBible;
	}

	public ABSPassage getVerse() {
		return verse;
	}

	public void setVerse(ABSPassage verse) {
		this.verse = verse;
		this.verse.setBible(selectedBible);
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

	public WorkerThread getWorkerThread() {
		return workerThread;
	}
}
