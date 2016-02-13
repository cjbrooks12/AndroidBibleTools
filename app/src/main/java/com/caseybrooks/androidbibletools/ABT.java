package com.caseybrooks.androidbibletools;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Metadata;
import com.caseybrooks.androidbibletools.datastore.DatastoreHelper;
import com.caseybrooks.androidbibletools.datastore.SharedPreferenceDatastoreDelegate;

//TODO: Create delegation pattern for how we save Bibles and Verses. I want to allow users to be able to save data however they want, like in a database, shared preferences, over a network, etc.
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
	private static ABT ourInstance;

	private Context mContext;
	private Metadata mMetadata;
	private DatastoreHelper mDatastore;

	private RequestQueue mRequestQueue;

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

	private ABT(Context context) {
		this.mContext = context;
		this.mMetadata = new Metadata();
		this.mDatastore = new SharedPreferenceDatastoreDelegate(context);
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
		if(mDatastore != null)
			mDatastore.setContext(mContext);
	}

	public Metadata getMetadata() {
		return mMetadata;
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
	public Bible getSavedBible(@Nullable String tag) {
		return mDatastore.getSavedBible(tag);
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
	public void saveBible(Bible bible, @Nullable String tag) {
		mDatastore.saveBible(bible, tag);
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
	public AbstractVerse getSavedVerse(@Nullable String tag) {
		return mDatastore.getSavedVerse(tag);
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
	public void saveVerse(AbstractVerse verse, @Nullable String tag) {
		mDatastore.saveVerse(verse, tag);
	}
}
