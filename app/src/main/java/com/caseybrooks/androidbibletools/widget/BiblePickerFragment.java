package com.caseybrooks.androidbibletools.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caseybrooks.androidbibletools.basic.BibleList;

public class BiblePickerFragment extends Fragment {
	private static final String KEY_CLASS = "KEY_CLASS";
	private static final String KEY_TAG = "KEY_TAG";

	BiblePicker picker;

	public static BiblePickerFragment newInstance(Class<? extends BibleList> bibleListClass, String tag) {
		BiblePickerFragment fragment = new BiblePickerFragment();
		Bundle extras = new Bundle();
		extras.putString(KEY_CLASS, bibleListClass.getName());
		extras.putString(KEY_TAG, tag);
		fragment.setArguments(extras);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String tag = null;
		Class<? extends BibleList> bibleListClass = null;

		if(getArguments() != null) {
			try {
				tag = getArguments().getString(KEY_TAG);
				bibleListClass = (Class<? extends BibleList>) Class.forName(getArguments().getString(KEY_CLASS));
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		picker = new BiblePicker(getActivity());
		picker.setBibleListClass(bibleListClass, tag);
		picker.loadBibleList();
		return picker;
	}
}
