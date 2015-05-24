package com.caseybrooks.androidbibletools.providers.dbp;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.data.Downloadable;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class DBPBible extends Bible implements Downloadable {
	public DBPBible() {
		super();
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public Document getDocument() throws IOException {
		return null;
	}

	@Override
	public boolean parseDocument(Document doc) {
		return false;
	}
}
