package com.caseybrooks.androidbibletools.providers.dbp;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.basic.Reference;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class DBPPassage extends Passage implements Downloadable {
	public DBPPassage(Reference reference) {
		super(reference);
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
