package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.data.Formatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ABSFormatter implements Formatter {
    protected AbstractVerse verse;

    @Override
    public String onPreFormat(AbstractVerse verse) {
        if(!(verse instanceof ABSVerse || verse instanceof ABSPassage)) {
            throw new IllegalArgumentException("ABSFormatter expects a verse of type ABSVerse or ABSPassage");
        }

        this.verse = verse;
        return "";
    }

    @Override
    public String onFormatVerseStart(int i) {
        return "";
    }

    @Override
    public String onFormatText(String s) {
        Document textHTML = Jsoup.parse(s);
        textHTML.select("sup").remove();
        textHTML.select("h3").remove();

        return textHTML.text();
    }

    @Override
    public String onFormatVerseEnd() {
        return " ";
    }

    @Override
    public String onPostFormat() {
        return "";
    }
}
