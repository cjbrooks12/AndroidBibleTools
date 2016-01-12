package com.caseybrooks.androidbibletools.basic;

import java.util.HashMap;

public class BibleList<T extends Bible> {
	protected HashMap<String, T> bibles;

	public BibleList() {
	}

	public HashMap<String, T> getBibles() {
		return bibles;
	}

	public void setBibles(HashMap<String, T> bibles) {
		this.bibles = bibles;
	}
}
