package com.caseybrooks.androidbibletools.io;

import android.content.Context;
import android.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class ABTUtility {

	public enum CacheTimeout {
		TwoWeeks(14*24*60*60*1000),
		OneDay(24*60*60*1000),
		Never(Long.MAX_VALUE);

		public long millis;

		CacheTimeout(long millis) {
			this.millis = millis;
		}
	}

	/**
	 * Writes the given Jsoup document to the applications internal cache,
	 * overwriting any file that already has the given name
	 */
	public static boolean cacheDocument(Context context, Document doc, String filename) {
		try {
			File cacheFile = new File(context.getCacheDir(), filename);

			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(cacheFile), "UTF-8"));
			bufferedWriter.write(doc.toString());
			bufferedWriter.close();

			PreferenceManager.getDefaultSharedPreferences(context)
					.edit()
					.putLong(filename, Calendar.getInstance().getTimeInMillis())
					.commit();

			return true;
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
	}

	public static Document getChachedDocument(Context context, String filename, long timeout) {
		try {

			long cachedTime =
					PreferenceManager.getDefaultSharedPreferences(context)
					.getLong(filename, 0);

			File cacheFile = new File(context.getCacheDir(), filename);

			if(cachedTime != 0 && cacheFile.exists()) {
				//if this file was cached more than 2 weeks ago, delete the file
				//and remove the preference timestamp. Data is stale and should
				//should not be used.
				if(Calendar.getInstance().getTimeInMillis() - cachedTime >= timeout) {
					boolean deletedCorrectly = cacheFile.delete();
					if(deletedCorrectly) {
						PreferenceManager.getDefaultSharedPreferences(context)
								.edit().remove(filename).commit();
					}
					return null;
				}
				else {
					return Jsoup.parse(cacheFile, "UTF-8");
				}
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	public static Document getChachedDocument(Context context, String filename) {
		return getChachedDocument(context, filename, CacheTimeout.TwoWeeks.millis);
	}
}
