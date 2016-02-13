package com.caseybrooks.androidbibletools.datastore;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Reference;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferenceDatastoreDelegate extends DatastoreHelper {
    private static final String PREFERENCES = "ABT_preferences";
    private static final String PREF_CLASS_NAME = "className";
    private static final String PREF_SELECTED_BIBLE = "ABT_selectedBible";
    private static final String PREF_BIBLE = "bible";

    private static final String PREF_SAVED_VERSE = "ABT_savedVerse";
    private static final String PREF_VERSE = "verse";
    private static final String PREF_REFERENCE = "reference";

    public SharedPreferenceDatastoreDelegate(Context context) {
        super(context);
    }

    private SharedPreferences getSharedPreferences() {
        if(mContext == null) {
            throw new IllegalStateException(
                    "SharedPreferenceDatastoreDelegate does not have a Context (when initializing ABT did you use the correct getInstance() method?)"
            );
        }

        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void saveBible(Bible bible, @Nullable String tag) {
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

    @Override
    public Bible getSavedBible(@Nullable String tag) {
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

    @Override
    public void saveVerse(AbstractVerse verse, @Nullable String tag) {
        String prefKey = (!TextUtils.isEmpty(tag))
                ? PREF_SAVED_VERSE + tag
                : PREF_SAVED_VERSE;

        try {
            JSONObject verseJSON = new JSONObject();
            verseJSON.put(PREF_CLASS_NAME, verse.getClass().getName());
            verseJSON.put(PREF_VERSE, verse.serialize());
            verseJSON.put(PREF_REFERENCE, verse.getReference().toString());
            getSharedPreferences().edit().putString(prefKey, verseJSON.toString()).commit();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractVerse getSavedVerse(@Nullable String tag) {
        AbstractVerse verse;
        String prefKey = (!TextUtils.isEmpty(tag))
                ? PREF_SAVED_VERSE + tag
                : PREF_SAVED_VERSE;

        if(getSharedPreferences().contains(prefKey)) {
            try {
                JSONObject serializedVerse = new JSONObject(
                        getSharedPreferences().getString(
                                prefKey,
                                null
                        )
                );

                String verseClassName = serializedVerse.optString(PREF_CLASS_NAME);
                String reference = serializedVerse.optString(PREF_REFERENCE);

                if(!TextUtils.isEmpty(reference)) {
                    Reference ref = new Reference.Builder().parseReference(reference).create();

                    if(!TextUtils.isEmpty(verseClassName)) {
                        Class<? extends AbstractVerse> verseClass = (Class<? extends AbstractVerse>) Class.forName(
                                verseClassName
                        );
                        verse = verseClass.getConstructor(Reference.class).newInstance(ref);
                        verse.deserialize(serializedVerse.getString(PREF_VERSE));
                    }
                    else {
                        verse = null;
                    }
                }
                else {
                    verse = null;
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                verse = null;
            }
        }
        else {
            verse = null;
        }

        return verse;
    }
}
