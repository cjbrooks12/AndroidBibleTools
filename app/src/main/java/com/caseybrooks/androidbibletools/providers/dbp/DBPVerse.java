package com.caseybrooks.androidbibletools.providers.dbp;

import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.basic.Reference;

import org.jsoup.nodes.Document;

import java.io.IOException;

public class DBPVerse extends Verse implements Downloadable {
	public DBPVerse(Reference reference) {
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
