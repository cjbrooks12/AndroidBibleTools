package com.caseybrooks.androidbibletools.data;

import com.caseybrooks.androidbibletools.basic.AbstractVerse;

/**
 * Class to format the text output of a Verse object. Rather than setting flags on the Verse itself,
 * set flags on this Formatter and assign the formatter to the Verse. This makes it easier to have
 * consistent output, because the same Formatter can be assigned to multiple Verses
 */
public interface Formatter {
	//called before we begin formatting the actual verses.
	//i.e. to print the reference before all its text
	String onPreFormat(AbstractVerse verse);

	//called when about to format a new ver
	//i.e. change how the numbers will be shown
	String onFormatVerseStart(int verseNumber);

	//called when formatting the main text of a verse
	//i.e. print only first letters of words, random words, etc.
	String onFormatText(String verseText);

	//called when we have finished formatting one verse and are moving to the next
	//i.e. to insert a newline between all verses
	String onFormatVerseEnd();

	//called when we have finished all other formatting
	//i.e. to print the reference at the end of all text, or a URL, or copyright info
	String onPostFormat();
}
