package com.caseybrooks.androidbibletools.widget;

import android.content.Context;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.widget.biblepicker.BiblePickerSettings;

public class ReferenceWorker implements IReferencePicker {
	Bible selectedBible;
	Reference reference;

	WorkerThread workerThread;
	IReferencePickerListener listener;

	public IReferencePickerListener getListener() {
		return listener;
	}

	public void setListener(IReferencePickerListener listener) {
		this.listener = listener;
	}

	Context context;

	public ReferenceWorker(Context context) {
		this.context = context;

		initialize();
	}

	public void initialize() {
		workerThread = new WorkerThread();
	}

	@Override
	public void loadSelectedBible() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				selectedBible = BiblePickerSettings.getCachedBible(context);

				if(listener != null)
					listener.onBibleLoaded(selectedBible, LoadState.Cached);
			}
		});
	}

	@Override
	public Reference getReference() {
		return reference;
	}

	@Override
	public void checkReference(final String referenceText) {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				if(referenceText.length() > 0) {
					Reference.Builder builder = new Reference.Builder();
					builder.setBible(selectedBible);
					builder.parseReference(referenceText);
					reference = builder.create();

					//check flags for defaults. If the book is default, then don't post
					//the reference, just prompt the user to try with default or edit it

					if(!builder.checkFlag(Reference.Builder.DEFAULT_BOOK_FLAG)) {
						if(listener != null)
							listener.onReferenceParsed(reference, true);
					}
					else {
						if(listener != null)
							listener.onReferenceParsed(reference, false);
					}
				}
			}
		});
	}
}
