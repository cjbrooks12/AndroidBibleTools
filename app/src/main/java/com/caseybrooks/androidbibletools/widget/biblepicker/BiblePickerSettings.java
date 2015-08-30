package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.io.ABTUtility;
import com.caseybrooks.androidbibletools.providers.abs.ABSBible;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;

public class BiblePickerSettings {
	private static final String PREFIX = "BIBLEPICKER_";

	public static final String NAME = "NAME";
	public static final String ABBR = "ABBR";
	public static final String LANG = "LANG";

	public static final String ID = "ID";

	public static void setSelectedBible(Context context, Bible bible) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(PREFIX + NAME, bible.getName())
				.putString(PREFIX + LANG, bible.getLanguage())
				.putString(PREFIX + ABBR, bible.getAbbreviation()).commit();

		if(bible instanceof Downloadable) {
			Downloadable downloadableBible = (Downloadable) bible;

			PreferenceManager.getDefaultSharedPreferences(context).edit()
					.putString(PREFIX + ID, downloadableBible.getId()).commit();
		}
	}

	public static Bible getSelectedBible(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		String name = prefs.getString(PREFIX + NAME, "") + "";
		String abbreviation = prefs.getString(PREFIX + ABBR, "") + "";
		String language = prefs.getString(PREFIX + LANG, "") + "";
		String id = prefs.getString(PREFIX + ID, "") + "";

		if(name.equalsIgnoreCase("") ||
				abbreviation.equalsIgnoreCase("") ||
				language.equalsIgnoreCase("")) {
			return new ABSBible(context.getResources().getString(R.string.bibles_org_key), null);
		}
		else {
			if(!id.equalsIgnoreCase("")) {
				ABSBible bible = new ABSBible(context.getResources().getString(R.string.bibles_org_key), id);
				bible.setName(name);
				bible.setLanguage(language);
				bible.setAbbreviation(abbreviation);
				return bible;
			}
			else {
				Bible bible = new Bible();
				bible.setName(name);
				bible.setLanguage(language);
				bible.setAbbreviation(abbreviation);
				return bible;
			}
		}
	}

	public static Bible getCachedBible(Context context) {
		Bible bible = getSelectedBible(context);

		if(bible instanceof Downloadable) {
			String filename = "selectedBible.xml";
			Document doc = ABTUtility.getChachedDocument(context, filename);

			if(doc != null) {
				((Downloadable) bible).parseDocument(doc);

				//if the bible is more than 5 days old, its data is still valid so
				//we can return the Bible we just pulled from the cache. However,
				//in order to ensure it is always valid, lets redownload it again
				//anyway in the background so next time it is the most up to date
				long cachedTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(filename, 0);
				long cacheTimeout = ABTUtility.CacheTimeout.OneDay.millis*5; //5 days
				long now = Calendar.getInstance().getTimeInMillis();

				if((cachedTime != 0) && ((now - cachedTime) >= cacheTimeout)) {
					redownloadBible(context);
				}

				return bible;
			}
		}

		return new ABSBible(context.getResources().getString(R.string.bibles_org_key, ""), null);
	}

	public static void redownloadBible(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bible bible = getSelectedBible(context);
				if(bible instanceof Downloadable) {
					try {
						Document doc = ((Downloadable) bible).getDocument();

						if(doc != null) {
							ABTUtility.cacheDocument(context, doc, "selectedBible.xml");
							((Downloadable) bible).parseDocument(doc);

							PreferenceManager.getDefaultSharedPreferences(context)
									.edit()
									.putInt("TIMES_BIBLE_REDOWNLOADED",
											PreferenceManager.getDefaultSharedPreferences(context).getInt("TIMES_BIBLE_REDOWNLOADED", 0) + 1)
									.commit();
						}
					}
					catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		}).start();
	}
}
