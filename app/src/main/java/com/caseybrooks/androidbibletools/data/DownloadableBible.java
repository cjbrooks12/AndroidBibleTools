package com.caseybrooks.androidbibletools.data;

import com.caseybrooks.androidbibletools.basic.Bible;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

public interface DownloadableBible extends Downloadable {
    Document getAvailableVersions(String APIKey, @Optional String languageAbbr) throws IOException;
    HashMap<String, Bible> parseAvailableVersions(Document doc);
    HashMap<String, String> getAvailableLanguages(Document doc);
}
