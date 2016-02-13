package com.androidbibletools.abttestapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.caseybrooks.androidbibletools.ABT;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	public Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawer,
				toolbar,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		ABT.getInstance(this)
			.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		navigationView.setCheckedItem(R.id.nav_verseview_pickers);
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container, Fragment3.newInstance())
				.commit();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if(drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();

		Fragment fragment;

		if(id == R.id.nav_bibles) {
			fragment = Fragment1.newInstance();
		}
		else if(id == R.id.nav_verses) {
			fragment = Fragment2.newInstance();
		}
		else if(id == R.id.nav_verseview_pickers) {
			fragment = Fragment3.newInstance();
		}
		else if(id == R.id.nav_verseview_views) {
			fragment = Fragment4.newInstance();
		}
		else if(id == R.id.nav_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		else {
			return false;
		}

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.container, fragment)
				.addToBackStack(null)
				.commit();

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
