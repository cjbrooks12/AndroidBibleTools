package com.caseybrooks.androidbibletools.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.caseybrooks.androidbibletools.basic.BibleList;

public class BiblePickerFragment extends DialogFragment {
	BiblePicker picker;
	Class<? extends BibleList> bibleListClass;
	String tag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		picker = new BiblePicker(getActivity());
		picker.setBibleListClass(bibleListClass, tag);
		picker.loadBibleList();

		Log.i("selected", "tag=" + tag);
		return picker;
	}

	@Override
	public
	@NonNull
	Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public void setBibleListClass(Class<? extends BibleList> bibleListClass, String tag) {
		this.bibleListClass = bibleListClass;
		this.tag = tag;
	}

	public void setBiblePicker(BiblePicker picker) {
		this.picker = picker;
	}
}
