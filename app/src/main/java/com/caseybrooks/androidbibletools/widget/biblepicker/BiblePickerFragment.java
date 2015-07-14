package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BiblePickerFragment extends Fragment implements OnBibleSelectedListener {
	OnBibleSelectedListener listener;
	BiblePicker picker;

	public static Fragment newInstance() {
		return new BiblePickerFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);

		picker = new BiblePicker(getActivity());
		picker.setListener(this);

		return picker;
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

