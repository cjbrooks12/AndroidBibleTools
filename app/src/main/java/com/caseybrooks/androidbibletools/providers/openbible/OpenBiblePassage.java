package com.caseybrooks.androidbibletools.providers.openbible;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.simple.SimplePassage;
import com.caseybrooks.androidbibletools.providers.simple.SimpleVerse;

import java.util.HashMap;
import java.util.Map;

public class OpenBiblePassage extends SimplePassage implements Response.Listener<String>, Response.ErrorListener {
	OnResponseListener listener;
	String text;

	public OpenBiblePassage(Reference reference) {
		super(reference);
	}

	public void upvote(OnResponseListener listener) {
		this.listener = listener;

		String tag = "OpenBiblePassagePost";
		String url = "http://www.openbible.info/topics/" + metadata.getString("SEARCH_TERM").trim().replaceAll("\\s+", "_");

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, this, this) {
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<>();
				params.put("w", getMetadata().getString("W"));
				params.put("db", getMetadata().getString("DB"));
				params.put(getMetadata().getString("POST_UPVOTE"), "1");

				Log.e("OpenBiblePassage", "Post Params: w=" + getMetadata().getString("W") + " db=" + getMetadata().getString("DB") + " " + getMetadata().getString("POST_UPVOTE") + "=1");

				return params;
			}

			@Override
			public String getBodyContentType() {
				return "application/x-www-form-urlencoded; charset=UTF-8";
			}
		};

		ABT.getInstance().addToRequestQueue(postRequest, tag);
	}

	public void downvote(OnResponseListener listener) {
		this.listener = listener;

		String tag = "OpenBiblePassagePost";
		String url = "http://www.openbible.info/topics/" + metadata.getString("SEARCH_TERM").trim().replaceAll("\\s+", "_");

		StringRequest postRequest = new StringRequest(Request.Method.POST, url, this, this) {
			@Override
			protected Map<String,String> getParams(){
				Map<String,String> params = new HashMap<>();
				params.put("w", getMetadata().getString("W"));
				params.put("db", getMetadata().getString("DB"));
				params.put(getMetadata().getString("POST_DOWNVOTE"), "-11");

				return params;
			}

			@Override
			public String getBodyContentType() {
				return "application/x-www-form-urlencoded; charset=UTF-8";
			}
		};

		ABT.getInstance().addToRequestQueue(postRequest, tag);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if(listener != null) {
			listener.responseFinished();
		}
	}

	@Override
	public void onResponse(String response) {
		if(listener != null) {
			listener.responseFinished();
		}
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
