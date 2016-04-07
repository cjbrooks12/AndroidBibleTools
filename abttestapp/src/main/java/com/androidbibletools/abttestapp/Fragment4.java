package com.androidbibletools.abttestapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.joshuaproject.JoshuaProject;

public class Fragment4 extends Fragment {

	public static Fragment newInstance() {
		Fragment fragment = new Fragment4();
		Bundle extras = new Bundle();
		fragment.setArguments(extras);
		return fragment;
	}

	JoshuaProject jp;
	ImageView joshuaProjectImage;
	TextView joshuaProjectPGName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fragment_4, container, false);

		joshuaProjectImage = (ImageView) view.findViewById(R.id.joshuaProjectImage);
		joshuaProjectPGName = (TextView) view.findViewById(R.id.joshuaProjectPGName);

		jp = new JoshuaProject();
		jp.download(new OnResponseListener() {
			@Override
			public void responseFinished(boolean success) {
				if(success) {
					joshuaProjectImage.setImageBitmap(jp.getPhoto());
					joshuaProjectPGName.setText(jp.getPeopleNameInCountry());
				}
			}
		});

		return view;
	}
}
