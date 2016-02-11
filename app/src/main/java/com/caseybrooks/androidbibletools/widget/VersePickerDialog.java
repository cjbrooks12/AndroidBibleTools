package com.caseybrooks.androidbibletools.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;

public class VersePickerDialog extends DialogFragment {
	VersePicker picker;
	String tag;
	OnReferenceCreatedListener listener;
	boolean allowSelectionModeChange;
	int selectionMode;

	@Override
	public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		if(picker == null) {
			picker = new VersePicker(getActivity());
			picker.setSelectedBibleTag(tag);
			picker.loadBible();
			picker.setOnReferenceCreatedListener(listener);
			picker.setSelectionMode(selectionMode);
			picker.setAllowSelectionModeChange(allowSelectionModeChange);
		}
		else {
			((ViewGroup) picker.getParent()).removeView(picker);
		}

		builder.setView(picker);

		return builder.create();
	}

	public void setSelectedBibleTag(String tag) {
		this.tag = tag;
		if(picker != null) {
			picker.setSelectedBibleTag(tag);
			picker.loadBible();
		}
	}

	public VersePicker getVersePicker() {
		return picker;
	}

	public void setOnReferenceCreatedListener(OnReferenceCreatedListener listener) {
		this.listener = listener;
		if(picker != null) {
			picker.setOnReferenceCreatedListener(listener);
		}
	}

	public void setAllowSelectionModeChange(boolean allowSelectionModeChange) {
		this.allowSelectionModeChange = allowSelectionModeChange;
	}

	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
	}
}
