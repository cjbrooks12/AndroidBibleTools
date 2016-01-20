package com.caseybrooks.androidbibletools.providers.cjb;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.CachingStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CJBPassage extends Passage<CJBVerse> implements Downloadable, Response.Listener<String>, Response.ErrorListener {
	OnResponseListener listener;

	public CJBPassage(Reference reference) {
		super(reference);
	}

	@Override
	public void download(OnResponseListener listener) {
		this.listener = listener;

		String tag = "CJBBible";
		String url = "https://heroku-website-cjbrooks12.c9.io/verse_api/verses?";
		url += "service=" + ((CJBBible) reference.getBible()).getService();
		url += "&bibleId=" + reference.getBible().getId();
		url += "&bookId=" + ((CJBBook) reference.getBook()).getBookId();
		url += "&chapterId=" + reference.getChapter();

		CachingStringRequest jsonObjReq = new CachingStringRequest(Request.Method.GET, url, this, this);
		ABT.getInstance().addToRequestQueue(jsonObjReq, tag);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		if(listener != null) {
			listener.responseFinished();
		}
	}

	@Override
	public void onResponse(String response) {
		if(TextUtils.isEmpty(response)) {
			onErrorResponse(new VolleyError("Empty response"));
		}

		try {
			JSONArray versesJSON = new JSONArray(response);

			//add all verses to a map from which we can pick the individual verses we want
			HashMap<Integer, CJBVerse> verseMap = new HashMap<>();
			for(int i = 0; i < versesJSON.length(); i++) {
				Reference verseReference = new Reference.Builder()
						.setBible(reference.getBible())
						.setBook(reference.getBook())
						.setChapter(reference.getChapter())
						.setVerses(i)
						.create();
				CJBVerse verse = new CJBVerse(verseReference);

				JSONObject verseJSON = versesJSON.getJSONObject(i);
				String text = verseJSON.getString("text");

				verse.setText(text);

				verseMap.put(verseJSON.getInt("verseNum"), verse);
			}

			verses.clear();
			for(int i = 0; i < reference.getVerses().size(); i++) {
				CJBVerse verseFromMap = verseMap.get(reference.getVerses().get(i));
				verses.add(verseFromMap);
			}

			if(listener != null) {
				listener.responseFinished();
			}
		}
		catch(JSONException e) {
			e.printStackTrace();
			onErrorResponse(new VolleyError("Error parsing JSON", e));
		}
	}
}
