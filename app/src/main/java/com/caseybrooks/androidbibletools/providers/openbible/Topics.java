package com.caseybrooks.androidbibletools.providers.openbible;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.CachingStringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Topics implements Downloadable, Response.Listener<String>, Response.ErrorListener {
//Data Members
//--------------------------------------------------------------------------------------------------
	private ArrayList<String> topics;
	private char searchCharacter;
	private OnResponseListener listener;

//Constructors
//--------------------------------------------------------------------------------------------------
	public Topics() {
		topics = new ArrayList<>();
		searchCharacter = ' ';
	}

//Getters and Setters
//--------------------------------------------------------------------------------------------------


	public ArrayList<String> getTopics() {
		return topics;
	}

	public char getSearchCharacter() {
		return searchCharacter;
	}

	public void setSearchCharacter(char searchCharacter) {
		if(Character.isLetter(searchCharacter))
			this.searchCharacter = searchCharacter;
	}

//Interface Implementations
//--------------------------------------------------------------------------------------------------
	@Override
	public void download(OnResponseListener listener) {
		this.listener = listener;
		String tag = "Topics";
		String url = "http://www.openbible.info/topics/" + searchCharacter;

		CachingStringRequest htmlObjReq = new CachingStringRequest(Request.Method.GET, url, this, this);

		ABT.getInstance().addToRequestQueue(htmlObjReq, tag);
	}

	@Override
	public void onResponse(String response) {
		if(TextUtils.isEmpty(response)) {
			onErrorResponse(new VolleyError("Empty response"));
			return;
		}

		Elements passages = Jsoup.parse(response).select("li");

		if(passages == null) {
			onErrorResponse(new VolleyError("No valid data available in response"));
			return;
		}

		topics.clear();
		for(Element element : passages) {
			topics.add(element.text());
		}

		if(listener != null) {
			listener.responseFinished();
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		if(listener != null) {
			listener.responseFinished();
		}
	}
}
