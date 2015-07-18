package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.util.Log;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.io.ABTUtility;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;
import com.caseybrooks.androidbibletools.widget.biblepicker.BiblePickerSettings;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class VerseWorker implements IVerseView {
	Context context;
	WorkerThread workerThread;

	Bible selectedBible;
	ABSPassage verse;

	IVerseViewListener listener;

	public VerseWorker(Context context) {
		this.context = context;
		this.workerThread = new WorkerThread();
	}

	@Override
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
				if(listener != null)
					listener.onBibleLoaded(selectedBible, LoadState.Cached);
			}
		});
	}

	@Override
	public boolean hasCachedText() {
		return ABTUtility.getChachedDocument(context, verse.getId()) != null;
	}

	@Override
	public void displayCachedText() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		Log.i("VerseWorker", "try cache for " + verse.getReference().toString());

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				Document doc = ABTUtility.getChachedDocument(context, verse.getId());

				if(doc != null) {
					verse.parseDocument(doc);

					if(listener != null)
						listener.onVerseLoaded(verse, LoadState.Cached);
				}
				else {
					verse.setText("Error displaying cached text");
					verse.setRawText("Error displaying cached text");
					if(listener != null)
						listener.onVerseLoaded(verse, LoadState.Failed);
				}
			}
		});
	}

	@Override
	public void displayDownloadedText() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		Log.i("VerseWorker", "try download for " + verse.getReference().toString());

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				try {
					Log.i("VerseWorker", "VerseId: " + verse.getId());
					Document doc = verse.getDocument();
					if(doc != null) {
						ABTUtility.cacheDocument(context, doc, verse.getId());
						verse.parseDocument(doc);
						if(listener != null)
							listener.onVerseLoaded(verse, LoadState.Downloaded);
					}
				}
				catch(IOException ioe) {
					ioe.printStackTrace();
					verse.setText("Error displaying downloaded text: " + ioe.getMessage());
					verse.setRawText("Error displaying downloaded text: " + ioe.getMessage());

					if(listener != null)
						listener.onVerseLoaded(verse, LoadState.Failed);
				}
			}
		});
	}

	@Override
	public void tryCacheOrDownloadText() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				if(hasCachedText()) {
					displayCachedText();
				}
				else {
					displayDownloadedText();
				}
			}
		});
	}

	@Override
	public void setVerse(ABSPassage verse) {
		this.verse = verse;
		this.verse.setBible(selectedBible);
	}

	@Override
	public ABSPassage getVerse() {
		return verse;
	}

	public IVerseViewListener getListener() {
		return listener;
	}

	public void setListener(IVerseViewListener listener) {
		this.listener = listener;
	}
}
