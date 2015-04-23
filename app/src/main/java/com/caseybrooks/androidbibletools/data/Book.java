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

	/**
	 * Two Books are considered the same if they represent the same location in
	 * the Bible. In other words, if they have the same Order. Books may be called
	 * something different in other languages, but they will be the same Book if
	 * they have the same Order.
	 *
	 * @param o the other book to compare to
	 * @return whether the two books represent the same Book in the Bible
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Book)) return false;

		Book other = (Book) o;

		if(this.order != other.order) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return order;
	}
}
