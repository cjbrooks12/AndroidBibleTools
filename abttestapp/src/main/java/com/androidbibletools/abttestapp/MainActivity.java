package com.androidbibletools.abttestapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment fragment;

		switch(position) {
		case 0:
			fragment = FragmentOne.newInstance();
			break;
		case 1:
			fragment = FragmentTwo.newInstance();
			break;
		case 2:
			fragment = FragmentThree.newInstance();
			break;
		default:
			fragment = FragmentOne.newInstance();
			break;
		}

		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
		onSectionAttached(position);
	}

	public void onSectionAttached(int number) {
		switch(number) {
		case 0:
			mTitle = "Section 1";
			break;
		case 1:
			mTitle = "Bible";
			break;
		case 2:
			mTitle = "Section 3";
			break;
		default:
			mTitle = "Default";
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
}
