package com.androidbibletools.abttestapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment4 extends Fragment {

	public static Fragment newInstance() {
		Fragment fragment = new Fragment4();
		Bundle extras = new Bundle();
		fragment.setArguments(extras);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_fragment_4, container, false);
	}
}
