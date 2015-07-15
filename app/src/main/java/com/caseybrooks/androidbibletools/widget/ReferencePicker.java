package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.widget.biblepicker.BiblePickerSettings;

/**
 * A simple widget that makes it easy to turn user input into References that can
 * be included as part of the various Verses and widgets in this library. A
 * Reference is essentially a locating object for Bible verses, and this creates
 * well-formed References with knowledge of the real location in the Bible from
 * potentially messy user input.
 *
 * By default, this uses an AutoCompleteTextView to suggest Books available in
 * the cached Bible, and the user then enters the chapter and optional verses to
 * use. This input String is parsed to determine the real Bible location with
 * respect to the cached Bible.
 *
 * An alternative layout for this class provides dropdown spinners to select the
 * Book and the chapter in that book. Assuming the user is wanting to view larger
 * groups of verses, this mode will try to create a Reference for that entire
 * chapter of the Bible, so that the entire chapter could be displayed, for example
 * as part of a Bible reader app.
 */
public class ReferencePicker extends AutoCompleteTextView {
//Data Members
//------------------------------------------------------------------------------
	Context context;

	ArrayAdapter<String> suggestionsAdapter;

	Bible selectedBible;
	Reference reference;

	ReferencePickerListener listener;
	WorkerThread workerThread;

//Constructors and Initialization
//------------------------------------------------------------------------------
	public ReferencePicker(Context context) {
		super(context);
		this.context = context;

		initialize(context, null);
	}

	public ReferencePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public void initialize(Context context, AttributeSet attrs) {
		this.context = context;

		workerThread = new WorkerThread();

		suggestionsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
		setAdapter(suggestionsAdapter);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					showDropDown();
				}

				return false;
			}
		});

		getSelectedBible();
	}

	public void getSelectedBible() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				selectedBible = BiblePickerSettings.getCachedBible(context);

				post(new Runnable() {
					@Override
					public void run() {
						suggestionsAdapter.clear();

						for(Book book : selectedBible.getBooks()) {
							suggestionsAdapter.add(book.getName());
						}

						suggestionsAdapter.notifyDataSetChanged();
					}
				});
			}
		});
	}

	public void checkReference() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				if(getText().length() > 0) {
					post(new Runnable() {
						 @Override
						 public void run() {
							 if(listener != null)
								 listener.onPreParse(getText().toString());
						 }
					 });

					Reference.Builder builder = new Reference.Builder();
					builder.setBible(selectedBible);
					builder.parseReference(getText().toString());
					reference = builder.create();

					//check flags for defaults. If the book is default, then don't post
					//the reference, just prompt the user to try with default or edit it

					if(!builder.checkFlag(Reference.Builder.DEFAULT_BOOK_FLAG)) {
						post(new Runnable() {
							@Override
							public void run() {
								setText(reference.toString());
								if(listener != null)
									listener.onParseCompleted(reference, true);
							}
						});
					}
					else {
						post(new Runnable() {
							@Override
							public void run() {
								if(listener != null)
									listener.onParseCompleted(reference, false);
								setError("Cannot parse reference");
							}
						});
					}
				}
			}
		});
	}

	/**
	 * Always call checkReference() before this to ensure the most up-to-date
	 * parse of the reference, and be sure that the reference is even something
	 * that can be worked with further.
	 *
	 * @return the most recently parsed reference, regardless of whether it was
	 * a fully successful parse.
	 */
	public Reference getReference() {
		return reference;
	}

	public ReferencePickerListener getListener() {
		return listener;
	}

	public void setListener(ReferencePickerListener listener) {
		this.listener = listener;
	}

	public void setSelectedBible(Bible selectedBible) {
		this.selectedBible = selectedBible;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public WorkerThread getWorkerThread() {
		return workerThread;
	}
}

