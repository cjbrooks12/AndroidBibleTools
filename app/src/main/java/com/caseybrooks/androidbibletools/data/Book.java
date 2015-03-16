package com.caseybrooks.androidbibletools.data;

import java.util.ArrayList;

public class Book {
	public String id;
	public ArrayList<Integer> chapters;
	public String name;
	public String abbr;


	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getAbbr() { return abbr; }
	public void setAbbr(String abbr) { this.abbr = abbr; }

	/** Returns the number of chapters in the book */
	public int numChapters() {
		return chapters.size();
	}

	/** Returns the number of verses in the specific chapter of the book */
	public int numVersesInChapter(int chapter) {
		if(chapter <= chapters.size() && chapter != 0) {
			return chapters.get(chapter - 1);
		}
		else return -1;
	}

	public int lastVerseInBook() {
		return chapters.get(chapters.size() - 1);
	}

}
