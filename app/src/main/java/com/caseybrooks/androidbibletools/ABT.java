package com.caseybrooks.androidbibletools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Metadata;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A singleton class that provides the ABT library with much of the Android application logic necessary
 * for more advanced uses. By default, all providers in this library use Volley for networking for
 * its caching capabilities, but Volley is directly connected to an Application by requiring a
 * Context, so a Context is something that is included here and consumed by the providers when they
 * need it. This makes it so you only need to initialize this singleton once, and then forget about
 * it. Likewise, API keys are set here so that they do not need to be specified every time a provider
 * needs an API key for accessing its web service. This class also makes it easy to save a user's
 * preferred Bible, and the widgets in the library are dependant upon the user's preference being
 * set here as opposed to being stored elsewhere.
 */
public class ABT {
	private static final String PREFERENCES = "ABT_preferences";
	private static final String PREF_SELECTED_BIBLE = "ABT_selectedBible";
	private static final String PREF_CLASS_NAME = "className";
	private static final String PREF_BIBLE = "bible";

	private static ABT ourInstance;

	/**
	 * DO NOT USE THIS METHOD! ABT requires an application Context to do any kind of data
	 * persistence or networking. This is only used internally when there is no direct handle to
	 * such a Context, at which point it can be assumed that the Context was already supplied to the
	 * singleton via getInstance(Context context) or setContext(Context context).
	 *
	 * @return the singleton of ABT
	 */
	@Deprecated
	public static ABT getInstance() {
		if(ourInstance == null) {
			ourInstance = new ABT(null);
		}

		return ourInstance;
	}

	/**
	 * Initialize this library with the application Context and get the singleton instance for ABT.
	 * If no instance has been created yet, this will create a new instance. Otherwise, it will set
	 * the instance's to this new Context.
	 *
	 * @param context  the application Context
	 * @return the singleton of ABT
	 */
	public static ABT getInstance(Context context) {
		if(ourInstance == null) {
			ourInstance = new ABT(context);
		}

		if(context != null) {
			ourInstance.setContext(context);
		}

		return ourInstance;
	}

	/**
	 * Initialize this library with the application Context. If no instance has been created yet,
	 * this will create a new instance. Otherwise, it will set the instance's to this new Context.
	 *
	 * @param context  the application Context
	 */
	public static void createInstance(Context context) {
		if(ourInstance == null) {
			ourInstance = new ABT(context);
		}

		if(ourInstance.getContext() == null) {
			ourInstance.setContext(context);
		}
	}

	private Context mContext;
	private Metadata mMetadata;
	private RequestQueue mRequestQueue;

	private ABT(Context context) {
		this.mContext = context;
		this.mMetadata = new Metadata();
	}

	/**
	 * Providers should use Volley for all HTTP requests, and ABT has its own instance of
	 * RequestQueue. This returns the instance of RequestQueue used by ABT
	 *
	 * @return an instance of a Volley RequestQueue
	 */
	public RequestQueue getRequestQueue() {
		if(mContext == null) {
			throw new IllegalStateException(
					"ABT does not have a Context (did you use the correct getInstance() method?)"
			);
		}

		if(mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext);
		}

		return mRequestQueue;
	}

	/**
	 * Add a Request to Volley's RequestQueue
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? "" : tag);
		getRequestQueue().add(req);
	}

	/**
	 * Add a Request to Volley's RequestQueue with a default tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		addToRequestQueue(req, null);
	}

	/**
	 * Cancel all Requests in Volley's RequestQueue with the given tag
	 */
	public void cancelPendingRequests(String tag) {
		if(mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	public Metadata getMetadata() {
		return mMetadata;
	}

	private SharedPreferences getSharedPreferences() {
		if(mContext == null) {
			throw new IllegalStateException(
					"ABT does not have a Context (did you use the correct getInstance() method?)"
			);
		}

		String prefsFile = (mMetadata.containsKey(PREFERENCES))
		                   ? mMetadata.getString(PREFERENCES)
		                   : PREFERENCES;
		return mContext.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
	}

	/**
	 * An instance of a Bible is often required to download Verse data. The user-selected Bible can
	 * be stored and loaded at a later time. Multiple Bibles can be saved at the same time by
	 * specifying a tag. This method loads the Bible that has been stored with the specified tag.
	 *
	 * @param tag unique tag that identified this instance of a Bible to persist
	 *
	 * @return Generic Bible object representing the Bible that was stored. It maintains the same
	 * Type of the object that was saved
	 */
	public Bible getSelectedBible(@Nullable String tag) {
		Bible bible;
		String prefKey = (!TextUtils.isEmpty(tag))
		                 ? PREF_SELECTED_BIBLE + tag
		                 : PREF_SELECTED_BIBLE;

		if(getSharedPreferences().contains(prefKey)) {
			try {
				JSONObject serializedBible = new JSONObject(
						getSharedPreferences().getString(
								prefKey,
								null
						)
				);

				String bibleClassName = serializedBible.optString(PREF_CLASS_NAME);
				if(!TextUtils.isEmpty(bibleClassName)) {
					Class<? extends Bible> bibleClass = (Class<? extends Bible>) Class.forName(
							bibleClassName
					);
					bible = bibleClass.newInstance();
					bible.deserialize(serializedBible.getString(PREF_BIBLE));
				}
				else {
					bible = null;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				bible = null;
			}
		}
		else {
			bible = null;
		}

		return bible;
	}

	/**
	 * An instance of a Bible is often required to download Verse data. The user-selected Bible can
	 * be stored and loaded at a later time. Multiple Bibles can be saved at the same time by
	 * specifying a tag. This method returns the Class representing the Bible that has been stored
	 * with the specified key.
	 *
	 * @param tag unique tag that identified this instance of a Bible to persist
	 *
	 * @return Class object representing the type of Bible that was stored
	 */
	public Class<? extends Bible> getSelectedBibleType(@Nullable String tag) {
		String prefKey = (!TextUtils.isEmpty(tag))
		                 ? PREF_SELECTED_BIBLE + tag
		                 : PREF_SELECTED_BIBLE;

		if(getSharedPreferences().contains(prefKey)) {
			try {
				JSONObject serializedBible = new JSONObject(
						getSharedPreferences().getString(
								prefKey,
								null
						)
				);

				String bibleClassName = serializedBible.optString(PREF_CLASS_NAME);
				if(!TextUtils.isEmpty(bibleClassName)) {
					return (Class<? extends Bible>) Class.forName(bibleClassName);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * An instance of a Bible is often required to download Verse data. The user-selected Bible can
	 * be stored and loaded at a later time. Multiple Bibles can be saved at the same time by
	 * specifying a tag. This method saves a Bible to be loaded later.
	 * <p>
	 * The Bible's Type is saved along with the result of Bible.serialize() so that any generic
	 * Bible subclass can fully save and load whatever data is necessary in whatever format. All
	 * serialization and deserialization is left entirely up to the Bible class.
	 *
	 * @param tag unique tag that identified this instance of a Bible to persist
	 */
	public void setSelectedBible(Bible bible, @Nullable String tag) {
		String prefKey = (!TextUtils.isEmpty(tag))
		                 ? PREF_SELECTED_BIBLE + tag
		                 : PREF_SELECTED_BIBLE;

		try {
			JSONObject bibleJSON = new JSONObject();
			bibleJSON.put(PREF_CLASS_NAME, bible.getClass().getName());
			bibleJSON.put(PREF_BIBLE, bible.serialize());
			getSharedPreferences().edit().putString(prefKey, bibleJSON.toString()).commit();
		}
		catch(JSONException e) {
			e.printStackTrace();
		}
	}
}
