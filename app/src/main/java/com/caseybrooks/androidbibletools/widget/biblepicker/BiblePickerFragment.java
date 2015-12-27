package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BiblePickerFragment extends Fragment {
	BiblePicker picker;

    public static BiblePickerFragment newInstance(String apiKey, String preferenceKey) {
        BiblePickerFragment fragment = new BiblePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("apiKey", apiKey);
        bundle.putString("preferenceKey", preferenceKey);
        fragment.setArguments(bundle);
        return fragment;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);

		picker = new BiblePicker(getActivity());
        if(getArguments() != null) {
            picker.setApiKey(getArguments().getString("apiKey"));
            picker.setPreferenceKey(getArguments().getString("preferenceKey"));
        }
        picker.loadBibleList();

        return picker;
	}

	public OnBibleSelectedListener getListener() {
		return picker.getListener();
	}

	public void setListener(OnBibleSelectedListener listener) {
	    picker.setListener(listener);
    }
}

