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

import java.util.ArrayList;

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
public class ReferencePicker extends AutoCompleteTextView implements IReferencePicker, IReferencePickerListener {
//Data Members
//------------------------------------------------------------------------------
	Context context;

	ArrayAdapter<String> suggestionsAdapter;
	IReferencePickerListener listener;

	ReferenceWorker worker;

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

		worker = new ReferenceWorker(context);
		worker.setListener(this);

		suggestionsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
		setAdapter(suggestionsAdapter);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					if(suggestionsAdapter.getCount() > 0) {
						showDropDown();
					}
				}

				return false;
			}
		});
	}

	@Override
	public void loadSelectedBible() {
		worker.loadSelectedBible();
	}

	@Override
	public Reference getReference() {
		return worker.getReference();
	}

	public IReferencePickerListener getListener() {
		return listener;
	}

	public void setListener(IReferencePickerListener listener) {
		this.listener = listener;
	}

	@Override
	public void checkReference(String referenceText) {
		worker.checkReference(referenceText);
	}

	public void checkReference() {
		this.checkReference(getText().toString());
	}

	@Override
	public boolean onBibleLoaded(Bible bible, LoadState state) {
		boolean handled = false;

		if(listener != null)
			handled = listener.onBibleLoaded(bible, state);

		if(!handled) {
			ArrayList<String> items = new ArrayList<>();
			for(Book book : bible.getBooks()) {
				items.add(book.getName());
			}

			suggestionsAdapter.clear();
			for(String item : items) {
				suggestionsAdapter.add(item);
			}
			suggestionsAdapter.notifyDataSetChanged();
		}

		return true;
	}

	@Override
	public boolean onReferenceParsed(final Reference parsedReference, boolean wasSuccessful) {
		boolean handled = false;

		if(listener != null)
			handled = listener.onReferenceParsed(parsedReference, wasSuccessful);

		if(!handled) {
			if(wasSuccessful) {
				post(new Runnable() {
					@Override
					public void run() {
						setText(parsedReference.toString());
					}
				});
			}
			else {
				post(new Runnable() {
					@Override
					public void run() {
						setError("Cannot parse reference");
					}
				});
			}
		}

		return true;
	}
}

