package com.caseybrooks.androidbibletools.providers.openbible;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.CachingStringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

//TODO: Fix this class
public class TopicalSearch implements Downloadable, Response.Listener<String>, Response.ErrorListener {
	//Data Members
//--------------------------------------------------------------------------------------------------
	private ArrayList<OpenBiblePassage> passages;
	private String searchTerm;
	private OnResponseListener listener;

	//Constructors
//--------------------------------------------------------------------------------------------------
	public TopicalSearch() {
		passages = new ArrayList<>();
		searchTerm = "";
	}

//Getters and Setters
//--------------------------------------------------------------------------------------------------
	public ArrayList<OpenBiblePassage> getPassages() {
		return passages;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

//Interface Implementations
//--------------------------------------------------------------------------------------------------
	@Override
	public void download(OnResponseListener listener) {
		this.listener = listener;
		String tag = "TopicalSearch";
		String url = "http://www.openbible.info/topics/" + searchTerm.trim().replaceAll("\\s+", "_");

		CachingStringRequest htmlObjReq = new CachingStringRequest(Request.Method.GET, url, this, this);
		htmlObjReq.setShouldCache(false);

		ABT.getInstance().addToRequestQueue(htmlObjReq, tag);
	}

	@Override
	public void onResponse(String response) {
		if(TextUtils.isEmpty(response)) {
			onErrorResponse(new VolleyError("Empty response"));
			return;
		}

		Document doc = Jsoup.parse(response);

		String w = "";
		String db = "";

		for(Element element : doc.select("#vote input")) {
			String name = element.attr("name");
			if(name.equals("w"))
				w = element.val();
			else if(name.equals("db"))
				db = element.val();
		}

		this.passages.clear();

		for(Element element : doc.select(".verse")) {
			Reference ref = new Reference.Builder()
					.parseReference(element.select(".bibleref").first().ownText())
					.create();

			OpenBiblePassage passage = new OpenBiblePassage(ref);
			passage.setText(element.select("p").text());

			String postUpvote = element.select("button[id^=vote_u]").first().id();
			String postDownvote = element.select("button[id^=vote_d]").first().id();

			String notesString = element.select(".note").first().ownText();
			passage.getMetadata().putInt("UPVOTES", Integer.parseInt(notesString.replaceAll("\\D", "")));
			passage.getMetadata().putString("SEARCH_TERM", searchTerm.trim());
			passage.getMetadata().putString("POST_UPVOTE", postUpvote);
			passage.getMetadata().putString("POST_DOWNVOTE", postDownvote);
			passage.getMetadata().putString("W", w);
			passage.getMetadata().putString("DB", db);

			passages.add(passage);
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
