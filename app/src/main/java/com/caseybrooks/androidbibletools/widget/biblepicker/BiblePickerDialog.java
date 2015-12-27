package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.app.AlertDialog;
import android.content.Context;

public class BiblePickerDialog extends AlertDialog {
	BiblePicker picker;

	public static BiblePickerDialog create(Context context, String apiKey, String preferenceKey) {
		BiblePickerDialog dialog = new BiblePickerDialog(context);
        dialog.setApiKey(apiKey);
        dialog.setPreferenceKey(preferenceKey);
        dialog.loadBibleList();
        return dialog;
	}

	protected BiblePickerDialog(Context context) {
		super(context);

		picker = new BiblePicker(context);
		setView(picker);
	}

	public OnBibleSelectedListener getListener() {
        return picker.getListener();
	}

	public void setListener(OnBibleSelectedListener listener) {
        picker.setListener(listener);
	}

    public String getApiKey() {
        return picker.getApiKey();
    }

    public void setApiKey(String apiKey) {
        picker.setApiKey(apiKey);
    }

    public String getPreferenceKey() {
        return picker.getPreferenceKey();
    }

    public void setPreferenceKey(String preferenceKey) {
        picker.setPreferenceKey(preferenceKey);
    }

    public void loadBibleList() {
        picker.loadBibleList();
    }
}
