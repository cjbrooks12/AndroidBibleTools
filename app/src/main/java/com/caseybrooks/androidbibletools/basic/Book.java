package com.caseybrooks.androidbibletools.basic;

public class Book {
	private String name;
	private String abbreviation;
	private int location;
	private int[] chapters;

	public String getName() { return name; }
	public String getAbbreviation() { return abbreviation; }
	public int getLocation() { return location; }

	public void setName(String name) { this.name = name; }
	public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
	public void setLocation(int location) { this.location = location; }

	public int[] getChapters() { return chapters; }
	public void setChapters(int... chapters) { this.chapters = chapters; }

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

		if(this.location != other.location) return false;

		return true;
	}

	/**
	 * The hashcode is simply the exact location in the Bible this Book exists.
	 * In the case of two hashcodes being equal, it is assumed that the Books
	 * are the same, even if the books are of different languages.
	 *
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		return location;
	}
}
