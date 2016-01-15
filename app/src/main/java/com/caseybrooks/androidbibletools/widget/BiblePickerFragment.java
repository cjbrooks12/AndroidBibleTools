package com.caseybrooks.androidbibletools.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caseybrooks.androidbibletools.basic.BibleList;

public class BiblePickerFragment extends Fragment {
	BiblePicker picker;
	Class<? extends BibleList> bibleListClass;
	String tag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		picker = new BiblePicker(getActivity());
		picker.setBibleListClass(bibleListClass, tag);
		picker.loadBibleList();
		return picker;
	}

	public void setBibleListClass(Class<? extends BibleList> bibleListClass, String tag) {
		this.bibleListClass = bibleListClass;
		this.tag = tag;
	}
}
