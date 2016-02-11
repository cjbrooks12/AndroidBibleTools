package com.androidbibletools.abttestapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.providers.abs.ABSBibleList;
import com.caseybrooks.androidbibletools.widget.BiblePickerDialog;
import com.caseybrooks.androidbibletools.widget.OnBibleSelectedListener;
import com.caseybrooks.androidbibletools.widget.VersePicker;
import com.caseybrooks.androidbibletools.widget.VersePickerDialog;
import com.caseybrooks.androidbibletools.widget.VerseView;

public class Fragment3 extends Fragment {
	Button biblePickerButton, versePickerButton;
	VerseView verseView;

	BiblePickerDialog biblePickerDialog;
	VersePickerDialog versePickerDialog;

	public static Fragment newInstance() {
		Fragment fragment = new Fragment3();
		Bundle extras = new Bundle();
		fragment.setArguments(extras);
		return fragment;
	}

//Lifecycle and Initialization
//--------------------------------------------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_fragment_3, container, false);

		biblePickerButton = (Button) view.findViewById(R.id.biblePickerButton);
		versePickerButton = (Button) view.findViewById(R.id.versePickerButton);

		verseView = (VerseView) view.findViewById(R.id.verseView);

		biblePickerDialog = new BiblePickerDialog();
		biblePickerDialog.setBibleListClass(ABSBibleList.class, "frag3");

		versePickerDialog = new VersePickerDialog();
		versePickerDialog.setSelectedBibleTag("frag3");
		versePickerDialog.setSelectionMode(VersePicker.SELECTION_WHOLE_CHAPTER);
		versePickerDialog.setAllowSelectionModeChange(false);

		//hit button to show Bible picker
		biblePickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				biblePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		//Selecting a Bible automatically opens the verse picker dialog
		biblePickerDialog.setOnBibleSelectedListener(new OnBibleSelectedListener() {
			@Override
			public void onBibleSelected(Bible bible) {
				biblePickerDialog.dismiss();
				versePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		//can also manually open versepicker
		versePickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				versePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		//selecting verses automatically downloads them into the verseview
		versePickerDialog.setOnReferenceCreatedListener(verseView);

		return view;
	}

}
