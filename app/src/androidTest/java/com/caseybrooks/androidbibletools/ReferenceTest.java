package com.caseybrooks.androidbibletools;

import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.data.Reference;

import junit.framework.TestCase;

public class ReferenceTest extends TestCase {
	Bible bible;

	public ReferenceTest() {
		bible = new Bible("eng-ESV");
	}

	public void testReferenceParser() throws Throwable {
		String[] references = new String[] {
				"John 3:16",
				"1 John 3:16",
				"2 John 3:16",
				"3 John 3:16",
				"Philippians 4:11",
				"Eph 1:1-8",
				"Eph. 1:1 through 8",
				"Eph 1:1 to 8",
				"Eph 1:1 and 8",
				"Ecc 4:1, 4",
				"Ecc 4:1 and 4",
				"Gen 1:1-3",
				"Psalm 125",
				"Psalms 125",
				"Galatians 2: 1-5, 19-21, 4-8",
				"1 Timothy 4 5-8",
		};

		Reference.Builder[] refObjects = new Reference.Builder[] {
				new Reference.Builder().setBook("John").setChapter(3).setVerses(16),
				new Reference.Builder().setBook("1 John").setChapter(3).setVerses(16),
				new Reference.Builder().setBook("2 John").setChapter(3).setVerses(16),
				new Reference.Builder().setBook("3 John").setChapter(3).setVerses(16),
				new Reference.Builder().setBook("Philippians").setChapter(4).setVerses(11),
				new Reference.Builder().setBook("Ephesians").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
				new Reference.Builder().setBook("Ephesians").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
				new Reference.Builder().setBook("Ephesians").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
				new Reference.Builder().setBook("Ephesians").setChapter(1).setVerses(1, 8),
				new Reference.Builder().setBook("Ecclesiastes").setChapter(4).setVerses(1, 4),
				new Reference.Builder().setBook("Ecclesiastes").setChapter(4).setVerses(1, 4),
				new Reference.Builder().setBook("Genesis").setChapter(1).setVerses(1, 2, 3),
				new Reference.Builder().setBook("Psalm").setChapter(125).setVerses(1, 2, 3, 4, 5),
				new Reference.Builder().setBook("Psalm").setChapter(125).setVerses(1, 2, 3, 4, 5),
				new Reference.Builder().setBook("Galatians").setChapter(2).setVerses(1, 2, 3, 4, 5, 19, 20, 21, 6, 7, 8),
				new Reference.Builder().setBook("1 Timothy").setChapter(4).setVerses(5, 6, 7, 8),
		};

		for(int i = 0; i < references.length; i++) {
			Reference ref1 = new Reference.Builder()
					.setBible(bible)
					.parseReference(references[i])
					.create();
			Reference ref2 = refObjects[i].create();

			//should never return null Reference, but rather throw exception. Check here just to be sure
			assertNotNull(ref1);
			assertNotNull(ref2);

			//test for equality among parsed and entered References by all possible
			// measures. Also test to make sure that a.equals(b) is no different from
			// b.equals(a) for each comparison
			assertEquals(ref1, ref2);
			assertEquals(ref2, ref1);

			assertTrue(ref1.equals(ref2));
			assertTrue(ref2.equals(ref1));

			assertEquals(0, ref1.compareTo(ref2));
			assertEquals(0, ref2.compareTo(ref1));

			assertEquals(ref1.hashCode(), ref2.hashCode());
			assertEquals(ref2.hashCode(), ref1.hashCode());

			//ensure that we can read in a string that the class prints just the same
			String s = ref1.toString();
			Reference ref3 = new Reference.Builder()
					.setBible(bible)
					.parseReference(s)
					.create();

			assertEquals(ref1, ref3);
		}
	}

//	//this test shows that for any book except Judges, Jude, Philippians, and
//	//Philemon, I can successfully parse its name based on the first three letters
//	//of the word that was unput. For Judges and Jude, I need a fourth letter to
//	//determine which book it is, and I need 5 letters for Philippians and Philemon
//	public void testBookNameLikeness() throws Throwable {
//
//		for(int i = 0; i < DefaultBible.defaultBookName.length; i++) {
//			Book book;
//			String pre, post;
//
//			//test parsing the names based on the fewest letters of full name
//			pre = DefaultBible.defaultBookName[i].toLowerCase().trim().replaceAll("\\s", "");
//			if(pre.equals("judges") || pre.equals("jude")) {
//				book = bible.parseBook(pre.substring(0, 4));
//			}
//			else if(pre.equals("philemon") || pre.equals("philippians")) {
//				book = bible.parseBook(pre.substring(0, 5));
//			}
//			else {
//				book = bible.parseBook(pre.substring(0, 3));
//			}
//			post = book.getName().toLowerCase().trim().replaceAll("\\s", "");
//			assertEquals(pre, post);
//
//			//test parsing the names based on their code. we have already found
//			//the book, so just get its code and parse
//			pre = book.getAbbr().toLowerCase().trim().replaceAll("\\s", "");
//			book = bible.parseBook(pre);
//
//			post = book.getAbbr().toLowerCase().trim().replaceAll("\\s", "");
//			assertEquals(pre, post);
//		}
//	}
//
	public void testPrintingReferences() throws Throwable {
		String refStringManual1 = "John 3:16-19,     24, 27-29, 31, 33";
		Reference ref1 = new Reference.Builder()
				.setBook("John")
				.setChapter(3)
				.setVerses(16, 17, 18, 19, 24, 27, 28, 29, 31, 33)
				.create();

		String refStringManual2 = "Mark 1:1-7, 14, 19, 22, 29-32, 34-35";
		Reference ref2 = new Reference.Builder()
				.setBook("Mark")
				.setChapter(1)
				.setVerses(1, 2, 3, 4, 5, 6, 7, 14, 19, 22, 29, 30, 31, 32, 34, 35)
				.create();

		assertEquals(refStringManual1.replaceAll("\\s+", " "), ref1.toString());
		assertEquals(refStringManual2.replaceAll("\\s+", " "), ref2.toString());

		Reference ref3 = new Reference.Builder()
				.setBible(bible)
				.parseReference(ref1.toString())
				.create();
		Reference ref4 = new Reference.Builder()
				.setBible(bible)
				.parseReference(ref2.toString())
				.create();

		assertEquals(ref1.toString(), ref3.toString());
		assertEquals(ref2.toString(), ref4.toString());
	}
//
//	public void testExtractVerse() throws Throwable {
//		String[] references = new String[] {
//				"Lets see if I can find John 3:16-18, 22-24",
//				"Now how about finding it in http://bible.com/111/gen.3.1.niv Now the serpent was more crafty",
//				"\"Now the serpent was more crafty\"\n\n http://ref.ly/r/niv2011/Ge3.1 via the FaithLife",
//				"http://www.biblestudytools.com/kjv/romans/14-1.html"
//		};
//
//		Reference.Builder[] refObjects = new Reference.Builder[] {
//				new Reference.Builder().setBook("John").setChapter(3).setVerses(16, 17, 18, 22, 23, 24),
//				new Reference.Builder().setBook("Gen").setChapter(3).setVerses(1),
//				new Reference.Builder().setBook("Gen").setChapter(3).setVerses(1),
//				new Reference.Builder().setBook("Rom").setChapter(14).setVerses(1)
//		};
//
//		for(int i = 0; i < references.length; i++) {
//			Reference ref = Reference.extractReference(references[i], bible);
//
//			//should never return null Reference, but rather throw exception. Check here just to be sure
//			assertNotNull(ref);
//			assertEquals(ref, refObjects[i]);
//		}
//	}
}
