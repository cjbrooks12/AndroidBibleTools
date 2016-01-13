package com.caseybrooks.androidbibletools.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class VersePickerFragment extends DialogFragment {
	VersePicker picker;
	String tag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		picker = new VersePicker(getActivity());
		picker.setSelectedBibleTag(tag);
		picker.loadBible();

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

	public void setSelectedBibleTag(String tag) {
		this.tag = tag;
	}
}
