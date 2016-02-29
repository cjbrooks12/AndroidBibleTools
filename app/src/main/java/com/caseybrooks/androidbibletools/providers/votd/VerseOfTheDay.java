package com.caseybrooks.androidbibletools.providers.votd;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Tag;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.CachingStringRequest;
import com.caseybrooks.androidbibletools.providers.simple.SimplePassage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//TODO: make a method to coerce into whatever Bible we want, to attempt to download VOTD in any given Bible
//TODO: add functionality for viewing archived VOTD verses, as the constructor suggests we can do
public class VerseOfTheDay implements Downloadable, Response.Listener<String>, Response.ErrorListener {
	private SimplePassage passage;
	private long date;
	OnResponseListener listener;

	public VerseOfTheDay() {
		this.date = 0l;
	}

	public VerseOfTheDay(long date) {
		this.date = date;
	}

	public Passage getPassage() {
		return passage;
	}

	@Override
	public void download(OnResponseListener listener) {
		this.listener = listener;
		String tag = "TopicalSearch";
		String url = "http://verseoftheday.com";

		CachingStringRequest htmlObjReq = new CachingStringRequest(Request.Method.GET, url, this, this);
		htmlObjReq.setExpireMillis(CachingStringRequest.Timeout.OneDay.millis);
		htmlObjReq.setRefreshMillis(CachingStringRequest.Timeout.OneHour.millis);

		ABT.getInstance().addToRequestQueue(htmlObjReq, tag);
	}

	@Override
	public void onResponse(String response) {
		if(TextUtils.isEmpty(response)) {
			onErrorResponse(new VolleyError("Empty response"));
			return;
		}

		Document doc = Jsoup.parse(response);

		Elements scripture = doc.select(".scripture");
		Elements verseReference = scripture.select("a");

		passage = new SimplePassage(
				new Reference.Builder()
						.parseReference(verseReference.text())
						.create()
		);

		scripture.select(".reference").remove();

		passage.setText(scripture.text());
		passage.setTags(new Tag("VOTD"));

		if(listener != null) {
			listener.responseFinished(true);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		if(listener != null) {
			listener.responseFinished(false);
		}
	}
}
