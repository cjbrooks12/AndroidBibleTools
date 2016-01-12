package com.caseybrooks.androidbibletools.providers.cjb;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.defaults.DefaultBible;
import com.caseybrooks.androidbibletools.io.CachingStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CJBBible extends Bible<CJBBook> implements Response.Listener<String>, Response.ErrorListener {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected String service;

	protected String nameEnglish;
	protected String copyright;

	private OnResponseListener listener;

//Constructors
//--------------------------------------------------------------------------------------------------
	public CJBBible() {
		super();
		this.id = "eng-ESV";
		this.service = "abs";

		this.abbreviation = "ESV";
		this.name = "English Standard Version";
		this.nameEnglish = "English Standard Version";
		this.language = "English";
		this.languageEnglish = "English";
		this.copyright = "Scripture quotations marked (ESV) are from The Holy Bible, English Standard Version®, copyright © 2001 by Crossway Bibles, a publishing ministry of Good News Publishers. Used by permission. All rights reserved.";

		books = new ArrayList<>();
		for(int i = 0; i < DefaultBible.defaultBookName.length; i++) {

			CJBBook book = new CJBBook();
			book.setService(service);
			book.setBibleId(id);
			book.setBookId(this.id + ":" + DefaultBible.defaultBookAbbr[i]);

			book.setName(DefaultBible.defaultBookName[i]);
			book.setAbbreviation(DefaultBible.defaultBookAbbr[i]);
			book.setLocation(i + 1);
			book.setChapters(DefaultBible.defaultBookVerseCount[i]);

			books.add(book);
		}
	}

//Getters and Setters
//--------------------------------------------------------------------------------------------------
	public String getNameEnglish() {
		return nameEnglish;
	}

	public void setNameEnglish(String nameEnglish) {
		this.nameEnglish = nameEnglish;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public OnResponseListener getListener() {
		return listener;
	}

	@Override
	public String serialize() {
		try {
			JSONObject bibleJSON = new JSONObject();
			bibleJSON.put("service", this.service);
			bibleJSON.put("id", this.id);
			return bibleJSON.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void deserialize(String string) {
		try {
			JSONObject bibleJSON = new JSONObject(string);
			this.service = bibleJSON.getString("service");
			this.id = bibleJSON.getString("id");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || !(o instanceof CJBBible)) {
			return false;
		}

		CJBBible bible = (CJBBible) o;

		if(getId().equalsIgnoreCase(bible.getId()) &&
				getService().equalsIgnoreCase(bible.getService())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (service != null ? service.hashCode() : 0);
		return result;
	}

//Downloadable Interface Implementation
//--------------------------------------------------------------------------------------------------
	public boolean isAvailable() {
		return service != null && id != null;
	}

	public void download(OnResponseListener listener) {
		this.listener = listener;

		String tag = "CJBBible";
		String url = "https://heroku-website-cjbrooks12.c9.io/verse_api/bible?";
		url += "service=" + service;
		url += "&id=" + id;

		CachingStringRequest jsonObjReq = new CachingStringRequest(
				Request.Method.GET,
				url,
				this,
				this
		);

		ABT.getInstance().addToRequestQueue(jsonObjReq, tag);
	}

	@Override
	public void onResponse(String response) {
		if(TextUtils.isEmpty(response)) {
			onErrorResponse(new VolleyError("Empty response"));
		}

		try {
			JSONObject jsonObject = new JSONObject(response);
			this.name = jsonObject.getString("name");
			this.nameEnglish = jsonObject.getString("nameEnglish");
			this.language = jsonObject.getString("language");
			this.languageEnglish = jsonObject.getString("languageEnglish");
			this.copyright = jsonObject.getString("copyright");
			this.books = new ArrayList<>();

			JSONArray booksJSON = jsonObject.getJSONArray("books");
			for(int i = 0; i < booksJSON.length(); i++) {
				JSONObject bookJSON = booksJSON.getJSONObject(i);

				CJBBook book = new CJBBook();
				book.setService(service);
				book.setBibleId(id);
				book.setBookId(bookJSON.getString("id"));

				book.setName(bookJSON.getString("name"));
				book.setAbbreviation(bookJSON.getString("abbreviation"));
				book.setLocation(bookJSON.getInt("order"));

				JSONArray chapters = bookJSON.getJSONArray("chapters");
				int[] chapterVerseCounts = new int[chapters.length()];
				for(int j = 0; j < chapters.length(); j++) {
					chapterVerseCounts[j] = chapters.getInt(j);
				}
				book.setChapters(chapterVerseCounts);

				this.books.add(book);
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

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		if(listener != null) {
			listener.responseFinished();
		}
	}
}
