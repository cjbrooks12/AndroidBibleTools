package com.caseybrooks.androidbibletools.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.BibleList;

public class BiblePickerPreference extends DialogPreference {
	BiblePicker picker;
	Class<? extends BibleList> bibleListClass;

	public BiblePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs, 0);
	}

	public BiblePickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs, defStyle);
	}

	private void initialize(Context context, AttributeSet attrs, int defstyle) {
		TypedArray a = getContext().obtainStyledAttributes(
				attrs,
				R.styleable.abt_biblepicker,
				0,
				0
		);
		try {
			bibleListClass = (Class<? extends BibleList>) Class.forName(a.getString(R.styleable.abt_biblepicker_bp_bibleList));
		}
		catch(ClassNotFoundException e) {
			bibleListClass = null;
		}
		a.recycle();
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton(null, null);
		builder.setNeutralButton(null, null);
		builder.setPositiveButton(null, null);
		builder.setTitle(null);
	}

	@Override
	protected View onCreateDialogView() {
		picker = new BiblePicker(getContext());
		picker.setBibleListClass(bibleListClass, getKey());
		picker.loadBibleList();
		return picker;
	}
}
