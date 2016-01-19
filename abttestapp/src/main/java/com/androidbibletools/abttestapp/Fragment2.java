package com.androidbibletools.abttestapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caseybrooks.androidbibletools.widget.VersePickerDialog;
import com.caseybrooks.androidbibletools.widget.VersePickerFragment;

public class Fragment2 extends Fragment {
	public static Fragment newInstance() {
		Fragment fragment = new Fragment2();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	Button showDialogButton, showFragmentButton;

	VersePickerDialog versePickerDialog;
	VersePickerFragment versePickerFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fragment_2, container, false);

		versePickerDialog = new VersePickerDialog();
		versePickerDialog.setSelectedBibleTag(null);
		versePickerFragment = new VersePickerFragment();
		versePickerFragment.setSelectedBibleTag(null);

		showDialogButton = (Button) view.findViewById(R.id.versepickerButtonDialog);
		showDialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				versePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		showFragmentButton = (Button) view.findViewById(R.id.versepickerButtonFragment);
		showFragmentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.container, versePickerFragment)
						.addToBackStack(null)
						.commit();
			}
		});

		return view;
	}
}