package com.androidbibletools.abttestapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.abs.ABSBibleList;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;
import com.caseybrooks.androidbibletools.widget.BiblePickerDialog;
import com.caseybrooks.androidbibletools.widget.VersePickerDialog;

public class FragmentThree extends Fragment {
	Button biblePickerButton, versePickerButton, downloadButton;
	TextView verseView;

	BiblePickerDialog biblePickerDialog;
	VersePickerDialog versePickerDialog;

	public static Fragment newInstance() {
		Fragment fragment = new FragmentThree();
		Bundle extras = new Bundle();
		fragment.setArguments(extras);
		return fragment;
	}

//Lifecycle and Initialization
//--------------------------------------------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_fragment_three, container, false);

		biblePickerButton = (Button) view.findViewById(R.id.biblePickerButton);
		versePickerButton = (Button) view.findViewById(R.id.versePickerButton);
		downloadButton = (Button) view.findViewById(R.id.downloadButton);

		verseView = (TextView) view.findViewById(R.id.verseView);

		biblePickerDialog = new BiblePickerDialog();
		biblePickerDialog.setBibleListClass(ABSBibleList.class, "frag3");
		versePickerDialog = new VersePickerDialog();
		versePickerDialog.setSelectedBibleTag("frag3");

		biblePickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				biblePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		versePickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				versePickerDialog.show(getActivity().getSupportFragmentManager(), "Sample Fragment");
			}
		});

		downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Reference reference = versePickerDialog.getVersePicker().getReferenceBuilder().create();

				final ABSPassage passage = new ABSPassage(reference);
				passage.download(new OnResponseListener() {
					@Override
					public void responseFinished() {
						verseView.setText(Html.fromHtml(passage.getFormattedText()));
					}
				});
			}
		});

		return view;
	}

}
