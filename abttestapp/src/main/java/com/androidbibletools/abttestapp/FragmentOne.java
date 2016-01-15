package com.androidbibletools.abttestapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caseybrooks.androidbibletools.providers.cjb.CJBBibleList;
import com.caseybrooks.androidbibletools.widget.BiblePickerDialog;
import com.caseybrooks.androidbibletools.widget.BiblePickerFragment;

public class FragmentOne extends Fragment {
	public static Fragment newInstance() {
		Fragment fragment = new FragmentOne();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	Button showDialogButton, showFragmentButton;

	BiblePickerDialog biblePickerDialog;
	BiblePickerFragment biblePickerFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fragment_one, container, false);

		biblePickerDialog = new BiblePickerDialog();
		biblePickerDialog.setBibleListClass(CJBBibleList.class, null);

		biblePickerFragment = new BiblePickerFragment();
		biblePickerFragment.setBibleListClass(CJBBibleList.class, null);

		showDialogButton = (Button) view.findViewById(R.id.biblepickerButtonDialog);
		showDialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				biblePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		showFragmentButton = (Button) view.findViewById(R.id.biblepickerButtonFragment);
		showFragmentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.container, biblePickerFragment)
						.addToBackStack(null)
						.commit();
			}
		});

		return view;
	}
}
