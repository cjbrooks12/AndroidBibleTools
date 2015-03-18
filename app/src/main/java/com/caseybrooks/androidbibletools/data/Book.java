package com.caseybrooks.androidbibletools.data;

public class Book {
	private final String id;
	private String name;
	private String abbr;
	private int order;
	private int[] chapters;

	public Book(String id) {
		this.id = id;
	}

	public String getId() { return id; }
	public String getName() { return name; }
	public String getAbbr() { return abbr; }
	public int getOrder() { return order; }

	public void setName(String name) { this.name = name; }
	public void setAbbr(String abbr) { this.abbr = abbr; }
	public void setOrder(int order) { this.order = order; }

	public int[] getChapters() { return chapters; }
	public void setChapters(int[] chapters) { this.chapters = chapters; }

	/**
	 * Get the number of chapters in this Book
	 *
	 * @return the number of chapters in this Book
	 */
	public int numChapters() {
		return chapters.length;
	}

	/**
	 * Get the number of verses in the specified chapter of this Book
	 *
	 * @param chapter the specified chapter
	 * @return the number of verses in this chapter
	 */
	public int numVersesInChapter(int chapter) {
		if(chapter <= chapters.length && chapter != 0) {
			return chapters[chapter - 1];
		}
		else return -1;
	}

	/**
	 * Get the last verse in this Book. Equivalent to numVersesinChapter(numChapters() - 1)
	 *
	 * @return the last verse in this book
	 */
	public int lastVerseInBook() {
		return chapters[chapters.length - 1];
	}

}
