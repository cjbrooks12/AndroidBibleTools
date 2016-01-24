package com.caseybrooks.androidbibletools.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class VersePickerDialog extends DialogFragment {
	VersePicker picker;
	String tag;
	OnReferenceCreatedListener listener;

	@Override
	public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		picker = new VersePicker(getActivity());
		builder.setView(picker);
		picker.setOnReferenceCreatedListener(listener);
		picker.setSelectedBibleTag(tag);
		picker.loadBible();

		return builder.create();
	}

	public void setSelectedBibleTag(String tag) {
		this.tag = tag;
	}

	public VersePicker getVersePicker() {
		return picker;
	}

	public void setOnReferenceCreatedListener(OnReferenceCreatedListener listener) {
		this.listener = listener;
	}
}
