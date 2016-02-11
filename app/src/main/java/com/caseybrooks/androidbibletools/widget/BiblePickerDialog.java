package com.caseybrooks.androidbibletools.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.ViewGroup;

import com.caseybrooks.androidbibletools.basic.BibleList;

public class BiblePickerDialog extends AppCompatDialogFragment {
	BiblePicker picker;
	Class<? extends BibleList> bibleListClass;
	String tag;
	OnBibleSelectedListener listener;

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
			picker = new BiblePicker(getActivity());
			picker.setOnBibleSelectedListener(listener);
			picker.setBibleListClass(bibleListClass, tag);
			picker.loadBibleList();
		}
		else {
			((ViewGroup) picker.getParent()).removeView(picker);
		}

		builder.setView(picker);

		return builder.create();
	}

	public void setBibleListClass(Class<? extends BibleList> bibleListClass, String tag) {
		this.bibleListClass = bibleListClass;
		this.tag = tag;
	}

	public void setOnBibleSelectedListener(OnBibleSelectedListener listener) {
		this.listener = listener;
	}
}
