package com.caseybrooks.androidbibletools.io;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CachingStringRequest extends StringRequest {
	public enum Timeout {
		TwoWeeks(1000 * 60 * 60 * 24 * 14),
		OneWeek(1000 * 60 * 60 * 24 * 7),
		OneDay(1000 * 60 * 60 * 24),
		OneHour(1000 * 60 * 60),
		Never(Long.MAX_VALUE);

		public long millis;

		Timeout(long millis) {
			this.millis = millis;
		}
	}

	private long refresh = Timeout.OneWeek.millis;
	private long expire = Timeout.TwoWeeks.millis;

	public CachingStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	public CachingStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
		super(url, listener, errorListener);
	}

	public long getRefreshMillis() {
		return refresh;
	}

	public void setRefreshMillis(long refresh) {
		this.refresh = refresh;
	}

	public long getExpireMillis() {
		return expire;
	}

	public void setExpireMillis(long expire) {
		this.expire = expire;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		}
		catch(UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, parseIgnoreCacheHeaders(response));
	}

	public Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
		long now = System.currentTimeMillis();

		Map<String, String> headers = response.headers;
		long serverDate = 0;
		String serverEtag = null;
		String headerValue;

		headerValue = headers.get("Date");
		if(headerValue != null) {
			serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
		}

		serverEtag = headers.get("ETag");

		final long softExpire = now + refresh;
		final long ttl = now + expire;

		Cache.Entry entry = new Cache.Entry();
		entry.data = response.data;
		entry.etag = serverEtag;
		entry.softTtl = softExpire;
		entry.ttl = ttl;
		entry.serverDate = serverDate;
		entry.responseHeaders = headers;

		return entry;
	}
}
