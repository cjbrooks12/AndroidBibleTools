package com.caseybrooks.androidbibletools.providers.abs;


import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.AuthFailureError;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ABSBible extends Bible<ABSBook> implements Response.Listener<String>, Response.ErrorListener {
//Data Members
//--------------------------------------------------------------------------------------------------
	protected String APIKey;

	protected String nameEnglish;
	protected String copyright;

	private OnResponseListener listener;

//Constructors
//--------------------------------------------------------------------------------------------------
	public ABSBible() {
		super();
		this.id = "eng-ESV";

		this.abbreviation = "ESV";
		this.name = "English Standard Version";
		this.nameEnglish = "English Standard Version";
		this.language = "English";
		this.languageEnglish = "English";
		this.copyright = "Scripture quotations marked (ESV) are from The Holy Bible, English Standard Version®, copyright © 2001 by Crossway Bibles, a publishing ministry of Good News Publishers. Used by permission. All rights reserved.";

		books = new ArrayList<>();
		for(int i = 0; i < DefaultBible.defaultBookName.length; i++) {
			ABSBook book = new ABSBook();
			book.setId(this.id + ":" + DefaultBible.defaultBookAbbr[i]);
			book.setName(DefaultBible.defaultBookName[i]);
			book.setAbbreviation(DefaultBible.defaultBookAbbr[i]);
			book.setChapters(DefaultBible.defaultBookVerseCount[i]);
			book.setLocation(i + 1);

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

//Downloadable Interface Implementation
//--------------------------------------------------------------------------------------------------
	public boolean isAvailable() {
		return APIKey != null && id != null;
	}

	public void download(OnResponseListener listener) {
		APIKey = ABT.getInstance().getMetadata().getString("ABS_ApiKey", null);

		if(TextUtils.isEmpty(APIKey)) {
			throw new IllegalStateException(
					"API key not set in ABT metadata. Please add 'ABS_ApiKey' key to metadata."
			);
		}

		this.listener = listener;

		String tag = "ABSBible";
		String url = "http://" + APIKey + ":x@bibles.org/v2/versions/" + id + "/books.js?include_chapters=true";

		CachingStringRequest jsonObjReq = new CachingStringRequest(
				Request.Method.GET,
				url,
				this,
				this
		) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				try {
					String encodedHeader = Base64.encodeToString(
							(APIKey + ":x").getBytes("UTF-8"),
							Base64.DEFAULT
					);
					headers.put("Authorization", "Basic " + encodedHeader);
				}
				catch(UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				return headers;
			}
		};

		ABT.getInstance().addToRequestQueue(jsonObjReq, tag);
	}

	@Override
	public void onResponse(String response) {
		if(TextUtils.isEmpty(response)) {
			onErrorResponse(new VolleyError("Empty response"));
		}

		try {
			this.abbreviation = null;
			this.name = null;
			this.nameEnglish = null;
			this.copyright = null;

			this.books = new ArrayList<>();

			JSONArray booksJSON = new JSONObject(response).getJSONObject("response")
			                                              .getJSONArray("books");
			for(int i = 0; i < booksJSON.length(); i++) {
				JSONObject bookJSON = booksJSON.getJSONObject(i);

				if(abbreviation == null) {
					this.abbreviation = bookJSON.getJSONObject("parent")
					                            .getJSONObject("version")
					                            .getString("id")
					                            .replaceAll(".*-", "");
				}
				if(name == null) {
					this.name = bookJSON.getJSONObject("parent")
					                    .getJSONObject("version")
					                    .getString("name");
					this.nameEnglish = bookJSON.getJSONObject("parent")
					                           .getJSONObject("version")
					                           .getString("name");
				}
				if(copyright == null) {
					this.copyright = bookJSON.getString("copyright");
				}

				ABSBook book = new ABSBook();
				book.setId(bookJSON.getString("id"));

				book.setName(bookJSON.getString("name"));
				book.setAbbreviation(bookJSON.getString("abbr"));
				book.setLocation(Integer.parseInt(bookJSON.getString("ord")));

				JSONArray chapters = bookJSON.getJSONArray("chapters");
				int[] chapterVerseCounts = new int[chapters.length()];
				for(int j = 0; j < chapters.length(); j++) {
					JSONObject chapterJSON = chapters.getJSONObject(j);

					String osis_end = chapterJSON.getString("osis_end");
					Matcher m = Pattern.compile(".*\\.\\d+\\.(\\d+)").matcher(osis_end);

					if(m.find()) {
						chapterVerseCounts[j] = Integer.parseInt(m.group(1));
					}
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

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || !(o instanceof ABSBible)) {
			return false;
		}

		ABSBible bible = (ABSBible) o;

		if(getId().equalsIgnoreCase(bible.getId())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (id != null) ? id.hashCode() : 0;
	}

	@Override
	public String serialize() {
		try {
			JSONObject bibleJSON = new JSONObject();
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
			this.id = bibleJSON.getString("id");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
