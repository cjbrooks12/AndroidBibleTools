package com.caseybrooks.androidbibletools.datastore;

import android.content.Context;
import android.support.annotation.Nullable;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Bible;

public class DatastoreHelper {
    Context mContext;

    public DatastoreHelper(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
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
    public void saveBible(Bible bible, @Nullable String tag) {

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
    public void saveVerse(AbstractVerse verse, @Nullable String tag) {

    }
}
