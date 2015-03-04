package com.caseybrooks.androidbibletools;

import com.caseybrooks.androidbibletools.data.Reference;
import com.caseybrooks.androidbibletools.enumeration.Book;

import junit.framework.TestCase;

public class ReferenceTest extends TestCase {
	public void testReferenceParser() throws Throwable {
		String[] references = new String[] {
				"John 3:16",
				"1 John 3:16",
				"Philippians 4:11",
				"Eph 1:1-8",
				"Eph. 1:1 through 8",
				"Eph 1:1 to 8",
				"Eph 1:1 and 8",
				"Ecc 4:1, 4",
				"Ecc 4:1 and 4",
				"Gen 1:1-3",
				"Psalm 125",
				"Galatians 2: 1-5, 19-21, 4-8",
				"1 Timothy 4 5-8"
		};

		Reference[] refObjects = new Reference[] {
				new Reference(Book.John, 3, 16),
				new Reference(Book.FirstJohn, 3, 16),
				new Reference(Book.Philippians, 4, 11),
				new Reference(Book.Ephesians, 1, 1, 2, 3, 4, 5, 6, 7, 8),
				new Reference(Book.Ephesians, 1, 1, 2, 3, 4, 5, 6, 7, 8),
				new Reference(Book.Ephesians, 1, 1, 2, 3, 4, 5, 6, 7, 8),
				new Reference(Book.Ephesians, 1, 1, 8),
				new Reference(Book.Ecclesiastes, 4, 1, 4),
				new Reference(Book.Ecclesiastes, 4, 1, 4),
				new Reference(Book.Genesis, 1, 1, 2, 3),
				new Reference(Book.Psalms, 125, 1, 2, 3, 4, 5),
				new Reference(Book.Galatians, 2, 1, 2, 3, 4, 5, 19, 20, 21, 6, 7, 8),
				new Reference(Book.FirstTimothy, 4, 5, 6, 7, 8)
		};

		for(int i = 0; i < references.length; i++) {
			Reference ref = Reference.parseReference(references[i]);

			//should never return null Reference, but rather throw exception. Check here just to be sure
			assertNotNull(ref);

			//test for equality among parsed and entered References by all possible
			// measures. Also test to make sure that a.equals(b) is no different from
			// b.equals(a) for each comparison
			assertTrue(ref.equals(refObjects[i]));
			assertTrue(refObjects[i].equals(ref));

			assertEquals(0, ref.compareTo(refObjects[i]));
			assertEquals(0, refObjects[i].compareTo(ref));

			assertEquals(ref.hashCode(), refObjects[i].hashCode());
			assertEquals(refObjects[i].hashCode(), ref.hashCode());

			assertEquals(refObjects[i], ref);
			assertEquals(ref, refObjects[i]);

			//ensure that we can read in a string that the class prints just the same
			String s = ref.toString();
			Reference ref2 = Reference.parseReference(s);

			assertEquals(ref, ref2);
		}
	}

//	//this test shows that for any book except Judges, Jude, Philippians, and
//	//Philemon, I can successfully parse its name based on the first three letters
//	//of the word that was unput. For Judges and Jude, I need a fourth letter to
//	//determine which book it is, and I need 5 letters for Philippians and Philemon
	public void testBookNameLikeness() throws Throwable {
		String[] bookNames = Book.getList();

		for(int i = 0; i < bookNames.length; i++) {
			Book book;
			String pre, post;

			//test parsing the names based on the fewest letters of full name
			pre = bookNames[i].toLowerCase().trim().replaceAll("\\s", "");
			if(pre.equals("judges") || pre.equals("jude")) {
				book = Book.parseBook(pre.substring(0, 4));
			}
			else if(pre.equals("philemon") || pre.equals("philippians")) {
				book = Book.parseBook(pre.substring(0, 5));
			}
			else {
				book = Book.parseBook(pre.substring(0, 3));
			}
			post = book.getName().toLowerCase().trim().replaceAll("\\s", "");
			assertEquals(pre, post);

			//test parsing the names based on their code. we have already found
			//the book, so just get its code and parse
			pre = book.getCode().toLowerCase().trim().replaceAll("\\s", "");
			book = Book.parseBook(pre);

			post = book.getCode().toLowerCase().trim().replaceAll("\\s", "");
			assertEquals(pre, post);
		}
	}

	public void testPrintingReferences() throws Throwable {
		String refStringManual1 = "John 3:16-19,     24, 27-29, 31, 33";
		Reference ref1 = new Reference(Book.John, 3,
											  16, 17, 18, 19, 24, 27, 28, 29, 31, 33);

		String refStringManual2 = "Mark 1:1-7, 14, 19, 22, 29-32, 34-35";
		Reference ref2 = new Reference(Book.Mark, 1,
											  1, 2, 3, 4, 5, 6, 7, 14, 19, 22, 29, 30, 31, 32, 34, 35);

		assertEquals(refStringManual1.replaceAll("\\s+", " "), ref1.toString());
		assertEquals(refStringManual2.replaceAll("\\s+", " "), ref2.toString());

		Reference ref3 = Reference.parseReference(ref1.toString());
		Reference ref4 = Reference.parseReference(ref2.toString());

		assertEquals(ref1.toString(), ref3.toString());
		assertEquals(ref2.toString(), ref4.toString());
	}

	public void testExtractVerse() throws Throwable {
		String[] references = new String[] {
				"Lets see if I can find John 3:16-18, 22-24",
				"Now how about finding it in http://bible.com/111/gen.3.1.niv Now the serpent was more crafty",
				"\"Now the serpent was more crafty\"\n\n http://ref.ly/r/niv2011/Ge3.1 via the FaithLife",
				"http://www.biblestudytools.com/kjv/romans/14-1.html"
		};

		Reference[] refObjects = new Reference[] {
				new Reference(Book.John, 3, 16, 17, 18, 22, 23, 24),
				new Reference(Book.Genesis, 3, 1),
				new Reference(Book.Genesis, 3, 1),
				new Reference(Book.Romans, 14, 1)
		};

		for(int i = 0; i < references.length; i++) {
			Reference ref = Reference.extractReference(references[i]);

			//should never return null Reference, but rather throw exception. Check here just to be sure
			assertNotNull(ref);
			assertEquals(ref, refObjects[i]);
		}
	}

//	public void testStringInitializationAndSorting() throws Throwable {
//		Verse verse1 = new Verse("Ephesians 2:19");
//		Verse verse2 = new Verse("Ephesians 2:20");
//		Verse verse3 = new Verse("Ephesians 2:21");
//		Passage passage = new Passage("Ephesians 2:19-21");
//	}
}
