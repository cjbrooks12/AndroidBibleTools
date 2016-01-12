package com.androidbibletools.abttestapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.caseybrooks.androidbibletools.widget.ReferencePicker;
import com.caseybrooks.androidbibletools.widget.VerseView;

public class FragmentThree extends Fragment {
	Context context;

	ReferencePicker referencePicker;
	VerseView verseView;

	private static final String settings_file = "my_settings";
	private static final String PREFIX = "BIBLE_";

	private static final String PROGRESS = "PROGRESS";

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
		View view = inflater.inflate(R.layout.fragment_bible_reader, container, false);

		this.context = getActivity();
		setHasOptionsMenu(true);

		referencePicker = (ReferencePicker) view.findViewById(R.id.reference_picker);
		verseView = (VerseView) view.findViewById(R.id.verse_view);

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		restoreProgress();
	}

//Menu
//--------------------------------------------------------------------------------------------------
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_show_bibles) {
			return true;
		}
		if(item.getItemId() == R.id.action_refresh) {
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}

//Preferences
//--------------------------------------------------------------------------------------------------
	public void saveProgress(String progress) {
		context.getSharedPreferences(settings_file, 0).edit().putString(PREFIX + PROGRESS, progress).commit();
	}

	public void restoreProgress() {
		String progress = context.getSharedPreferences(settings_file, 0).getString(PREFIX + PROGRESS, "Matthew 1:1");

		referencePicker.setText(progress);
	}
}
