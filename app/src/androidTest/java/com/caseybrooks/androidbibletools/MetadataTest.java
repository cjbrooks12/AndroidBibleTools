package com.caseybrooks.androidbibletools;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.basic.Metadata;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Verse;

import java.util.ArrayList;
import java.util.Collections;

public class MetadataTest extends ApplicationTestCase<Application> {
//Dummy classes to ensure only Comparable types are allowed in the
//------------------------------------------------------------------------------
	private class ComparableClass implements Comparable<ComparableClass> {
		public int value;

		public ComparableClass(int value) { this.value = value; }


		@Override
		public int compareTo(ComparableClass rhs) {
			return this.value - rhs.value;
		}
	}

	private class NonComparableClass {
		public int value;

		public NonComparableClass(int value) { this.value = value; }

		public int compareTo(ComparableClass rhs) {
			return this.value - rhs.value;
		}
	}

	public MetadataTest() {
		super(Application.class);
	}


	public void testAddingKeys() throws Throwable {
		Verse verseA = new Verse(
				new Reference.Builder()
						.setBook(new Book())
						.setChapter(3)
						.setVerses(16)
						.create());
		Metadata metadataA = new Metadata();
		metadataA.put("STRING", "value a"); //String is a comparable type
		metadataA.put("INTEGER", 0); //int is comparable
		metadataA.put("LONG", 0l); //long is comparable
		metadataA.put("BOOLEAN", true); //boolean is comparable
		metadataA.put("COMPARABLE_CLASS", new ComparableClass(0));
		try {
			//I expect this to throw an exception. If it does, catch it and continue.
			//If if does not, throw something that cannot be caught to indicate an error
			metadataA.put("NON_COMPARABLE_CLASS", new NonComparableClass(5));
			throw new Throwable();
		}
		catch(IllegalArgumentException iae) {

		}
		verseA.setMetadata(metadataA);

		//create a second Metadata object to test comparison of all types
		Verse verseB = new Verse(new Reference.Builder()
				.setBook(new Book())
				.setChapter(3)
				.setVerses(17)
				.create());
		Metadata metadataB = new Metadata();
		metadataB.putString("STRING", "value b"); //String is a comparable type
		metadataB.putInt("INTEGER", 1); //int is comparable
		metadataB.putLong("LONG", 1l); //long is comparable
		metadataB.putBoolean("BOOLEAN", true); //boolean is comparable
		metadataB.put("COMPARABLE_CLASS", new ComparableClass(1));
		verseB.setMetadata(metadataB);


		//create a third Metadata object which has the same keys but different classes with those keys
		//to ensure that it won't compare Objects of different type
		Verse verseC = new Verse(new Reference.Builder()
				.setBook(new Book())
				.setChapter(3)
				.setVerses(18)
				.create());
		Metadata metadataC = new Metadata();
		metadataC.putString("COMPARABLE_CLASS", "value c"); //String is a comparable type
		metadataC.putInt("STRING", 2); //int is comparable
		metadataC.putLong("INTEGER", 2l); //long is comparable
		metadataC.putBoolean("LONG", true); //boolean is comparable
		metadataC.put("BOOLEAN", new ComparableClass(2));
		verseC.setMetadata(metadataC);


		//Test comparison of these verses to see if it works correctly with arbitrary keys
		int testValue;

		testValue = new Metadata.Comparator("STRING").compare(verseA, verseB);
		if(testValue >= 0) throw new Throwable();
		testValue = new Metadata.Comparator("INTEGER").compare(verseA, verseB);
		if(testValue >= 0) throw new Throwable();
		testValue = new Metadata.Comparator("LONG").compare(verseA, verseB);
		if(testValue >= 0) throw new Throwable();
		testValue = new Metadata.Comparator("BOOLEAN").compare(verseA, verseB);
		if(testValue != 0) throw new Throwable();
		testValue = new Metadata.Comparator("COMPARABLE_CLASS").compare(verseA, verseB);
		if(testValue >= 0) throw new Throwable();

		try {
			//STRING key exists in both, but are of different types. I expect an exception to be
			//thrown, but if it doesn't, throw Throwable to ensure I detect the error
			testValue = new Metadata.Comparator("STRING").compare(verseA, verseC);
			throw new Throwable();
		}
		catch(ClassCastException cce) {

		}
		try {
			testValue = new Metadata.Comparator("INTEGER").compare(verseA, verseC);
			throw new Throwable();
		}
		catch(ClassCastException cce) {

		}
		try {
			testValue = new Metadata.Comparator("LONG").compare(verseA, verseC);
			throw new Throwable();
		}
		catch(ClassCastException cce) {

		}
		try {
			testValue = new Metadata.Comparator("BOOLEAN").compare(verseA, verseC);
			throw new Throwable();
		}
		catch(ClassCastException cce) {

		}
		try {
			testValue = new Metadata.Comparator("COMPARABLE_CLASS").compare(verseA, verseC);
			throw new Throwable();
		}
		catch(ClassCastException cce) {

		}
	}

	public void testMultiComparator() {
		//sort items by these 5 criteria in this order
		ArrayList<Metadata.Comparator> comparators = new ArrayList<>();
		comparators.add(new Metadata.Comparator("IS_MENS"));
		comparators.add(new Metadata.Comparator("SIZE"));
		comparators.add(new Metadata.Comparator("COLOR"));
		comparators.add(new Metadata.Comparator("POS"));

		Metadata.MultiComparator multiComparator = new Metadata.MultiComparator(comparators);

		ArrayList<Verse> verses = new ArrayList<>();
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(10).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(9).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(8).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(7).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(6).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(5).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(4).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(3).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(2).create()));
		verses.add(new Verse(
				new Reference.Builder()
					.setBook(new Book())
					.setChapter(1)
					.setVerses(1).create()));

		verses.get(0).getMetadata().putBoolean("IS_MENS", false);
		verses.get(1).getMetadata().putBoolean("IS_MENS", false);
		verses.get(2).getMetadata().putBoolean("IS_MENS", false);
		verses.get(3).getMetadata().putBoolean("IS_MENS", false);
		verses.get(4).getMetadata().putBoolean("IS_MENS", false);
		verses.get(5).getMetadata().putBoolean("IS_MENS", false);
		verses.get(6).getMetadata().putBoolean("IS_MENS", true);
		verses.get(7).getMetadata().putBoolean("IS_MENS", true);
		verses.get(8).getMetadata().putBoolean("IS_MENS", true);
		verses.get(9).getMetadata().putBoolean("IS_MENS", true);

		verses.get(0).getMetadata().putInt("SIZE", 0);
		verses.get(1).getMetadata().putInt("SIZE", 0);
		verses.get(2).getMetadata().putInt("SIZE", 0);
		verses.get(6).getMetadata().putInt("SIZE", 0);
		verses.get(7).getMetadata().putInt("SIZE", 0);
		verses.get(3).getMetadata().putInt("SIZE", 1);
		verses.get(4).getMetadata().putInt("SIZE", 1);
		verses.get(5).getMetadata().putInt("SIZE", 1);
		verses.get(8).getMetadata().putInt("SIZE", 1);
		verses.get(9).getMetadata().putInt("SIZE", 1);

		verses.get(1).getMetadata().putString("COLOR", "blue");
		verses.get(3).getMetadata().putString("COLOR", "blue");
		verses.get(5).getMetadata().putString("COLOR", "blue");
		verses.get(7).getMetadata().putString("COLOR", "blue");
		verses.get(9).getMetadata().putString("COLOR", "blue");
		verses.get(0).getMetadata().putString("COLOR", "red");
		verses.get(2).getMetadata().putString("COLOR", "red");
		verses.get(4).getMetadata().putString("COLOR", "red");
		verses.get(6).getMetadata().putString("COLOR", "red");
		verses.get(8).getMetadata().putString("COLOR", "red");

		verses.get(0).getMetadata().putInt("POS", 0);
		verses.get(1).getMetadata().putInt("POS", 1);
		verses.get(2).getMetadata().putInt("POS", 2);
		verses.get(3).getMetadata().putInt("POS", 3);
		verses.get(4).getMetadata().putInt("POS", 4);
		verses.get(5).getMetadata().putInt("POS", 5);
		verses.get(6).getMetadata().putInt("POS", 6);
		verses.get(7).getMetadata().putInt("POS", 7);
		verses.get(8).getMetadata().putInt("POS", 8);
		verses.get(9).getMetadata().putInt("POS", 9);


		//start by sorting into position
		Collections.sort(verses, new Metadata.Comparator("POS"));
		//sort according to whether item IS MENS or not
		Collections.sort(verses, new Metadata.Comparator("IS_MENS"));
		String simpleSort1 = "";
		for(int i = 0; i < verses.size(); i++) {
			simpleSort1 += verses.get(i).getMetadata().getInt("POS");
		}
		assertEquals("0123456789", simpleSort1);

		//start by sorting into position
		Collections.sort(verses, new Metadata.Comparator("POS"));
		//sort by SIZE
		Collections.sort(verses, new Metadata.Comparator("SIZE"));
		String simpleSort2 = "";
		for(int i = 0; i < verses.size(); i++) {
			simpleSort2 += verses.get(i).getMetadata().getInt("POS");
		}
		assertEquals("0126734589", simpleSort2);

		//start by sorting into position
		Collections.sort(verses, new Metadata.Comparator("POS"));
		//sort by COLOR
		Collections.sort(verses, new Metadata.Comparator("COLOR"));
		String simpleSort3 = "";
		for(int i = 0; i < verses.size(); i++) {
			simpleSort3 += verses.get(i).getMetadata().getInt("POS");
		}
		assertEquals("1357902468", simpleSort3);

//Reference tests won't pass because Reference comparison function does not work yet for single-verse objects

		//start by sorting into position
//		Collections.sort(verses, new Metadata.Comparator("POS"));
//		//sort by reference
//		Collections.sort(verses, new Metadata.Comparator(Metadata.Comparator.KEY_REFERENCE));
//		String simpleSort4 = "";
//		for(int i = 0; i < verses.size(); i++) {
//			simpleSort4 += verses.get(i).getMetadata().getInt("POS");
//		}
//		assertEquals("9876543210", simpleSort4);

		//start by sorting into position
		Collections.sort(verses, new Metadata.Comparator("POS"));
		//sort using multicomparator
		Collections.sort(verses, multiComparator);
		String multiSort = "";
		for(int i = 0; i < verses.size(); i++) {
			multiSort += verses.get(i).getMetadata().getInt("POS");
		}
		assertEquals("1023547698", multiSort);
	}
}

