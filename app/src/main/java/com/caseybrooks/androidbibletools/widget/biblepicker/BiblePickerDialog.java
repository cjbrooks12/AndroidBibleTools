package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.app.AlertDialog;
import android.content.Context;

public class BiblePickerDialog extends AlertDialog implements OnBibleSelectedListener {
	OnBibleSelectedListener listener;
	BiblePicker picker;

	public static BiblePickerDialog create(Context context) {
		return new BiblePickerDialog(context);
	}

	protected BiblePickerDialog(Context context) {
		super(context);

		picker = new BiblePicker(context);
		picker.setListener(this);
		setView(picker);
	}

	@Override
	public void onBibleSelected() {
		if(listener != null)
			listener.onBibleSelected();
	}

	@Override
	public void onBibleDownloaded(boolean successfullyDownloaded) {
		if(listener != null)
			listener.onBibleDownloaded(successfullyDownloaded);
	}

	public OnBibleSelectedListener getListener() {
		return listener;
	}

	public void setListener(OnBibleSelectedListener listener) {
		this.listener = listener;
	}
}
