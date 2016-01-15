package com.caseybrooks.androidbibletools.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VersePickerFragment extends Fragment {
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

	public void setSelectedBibleTag(String tag) {
		this.tag = tag;
	}

	public VersePicker getVersePicker() {
		return picker;
	}
}
