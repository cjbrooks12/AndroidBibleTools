package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.Bible;

public class BiblePickerPreference extends DialogPreference implements OnBibleSelectedListener {
	OnBibleSelectedListener listener;
	BiblePicker picker;

    String apiKey;

	public BiblePickerPreference(Context context, AttributeSet attrs) {
		//"hack" to ensure custom Preferences all look the same
		this(context, attrs, Resources.getSystem().getIdentifier("dialogPreferenceStyle", "attr", "android"));
	}

	public BiblePickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.abt_biblepicker, 0, 0);
        apiKey = a.getString(R.styleable.abt_biblepicker_bible_api_key);
        a.recycle();

		Bible bible = BiblePickerSettings.getSelectedBible(context);

		setTitle("Preferred Bible");
		setSummary(bible.getAbbreviation());
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setNegativeButton(null, null);
		builder.setNeutralButton(null, null);
        builder.setPositiveButton(null, null);
	}


	@Override
	protected View onCreateDialogView() {
		picker = new BiblePicker(getContext());
		picker.setListener(this);
        picker.setApiKey(apiKey);
        picker.setPreferenceKey(getKey());
		return picker;
	}

	@Override
	public void onBibleSelected() {
		if(listener != null)
			listener.onBibleSelected();

        this.getDialog().setCancelable(false);

		setSummary(BiblePickerSettings.getSelectedBible(getContext()).getName());
	}

	@Override
	public void onBibleDownloaded(boolean successfullyDownloaded) {
		if(listener != null)
			listener.onBibleDownloaded(successfullyDownloaded);

        this.getDialog().setCancelable(true);
    }

	public OnBibleSelectedListener getListener() {
		return listener;
	}

	public void setListener(OnBibleSelectedListener listener) {
		this.listener = listener;
	}
}
