package com.caseybrooks.androidbibletools;

import android.test.AndroidTestCase;

import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.abs.ABSBible;
import com.caseybrooks.androidbibletools.providers.abs.ABSBook;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReferenceTest extends AndroidTestCase {
	public ReferenceTest() {
	}

	public void testReferenceParser() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final ABSBible bible = new ABSBible();

		bible.setId("eng-NASB");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertTrue(bible.getName().equals("New American Standard Bible"));
				assertEquals(bible.getBooks().size(), 66);

				ABSBook foDaGalatiaPeopo = new ABSBook();
				foDaGalatiaPeopo.setName("Fo Da Galatia Peopo");
				foDaGalatiaPeopo.setAbbreviation("Gal");
				foDaGalatiaPeopo.setChapters(10, 10, 10, 10, 10);
				bible.getBooks().add(foDaGalatiaPeopo);

				ABSBook aabbccddeeffgg = new ABSBook();
				aabbccddeeffgg.setName("1 AA BB CC DD EE FF GG");
				aabbccddeeffgg.setAbbreviation("abc");
				aabbccddeeffgg.setChapters(10, 10, 10, 10, 10);
				bible.getBooks().add(aabbccddeeffgg);

				ABSBook belAndtheDragon = new ABSBook();
				belAndtheDragon.setName("Bel And The Dragon");
				belAndtheDragon.setAbbreviation("Bel");
				belAndtheDragon.setChapters(10, 10, 10, 10, 10);
				bible.getBooks().add(belAndtheDragon);

				assertEquals(bible.getBooks().size(), 69);

				//potentailly poorly formatted input we're trying to parse
				String[] references = new String[] {
						"John 3:16",
						"1 John 3:16",
						"2 John 1:2",
						"3 John 1:2",
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
						"Fo Da Galatia Peopo 4:2",
						"Bel And The Dragon 4:2",
						"1 AA BB CC DD EE FF GG 4:2",
						"Proverbs 7:4",
						"Proverb 7:4",
						"James 2 2"
				};

				//Verified correct objects to check against
				Reference.Builder[] refObjects = new Reference.Builder[] {
						new Reference.Builder().setBible(bible).setBook("John").setChapter(3).setVerses(16),
						new Reference.Builder().setBible(bible).setBook("1 John").setChapter(3).setVerses(16),
						new Reference.Builder().setBible(bible).setBook("2 John").setChapter(1).setVerses(2),
						new Reference.Builder().setBible(bible).setBook("3 John").setChapter(1).setVerses(2),
						new Reference.Builder().setBible(bible).setBook("Philippians").setChapter(4).setVerses(11),
						new Reference.Builder().setBible(bible).setBook("Ephesians").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
						new Reference.Builder().setBible(bible).setBook("Ephesians").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
						new Reference.Builder().setBible(bible).setBook("Ephesians").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
						new Reference.Builder().setBible(bible).setBook("Ephesians").setChapter(1).setVerses(1, 8),
						new Reference.Builder().setBible(bible).setBook("Ecclesiastes").setChapter(4).setVerses(1, 4),
						new Reference.Builder().setBible(bible).setBook("Ecclesiastes").setChapter(4).setVerses(1, 4),
						new Reference.Builder().setBible(bible).setBook("Genesis").setChapter(1).setVerses(1, 2, 3),
						new Reference.Builder().setBible(bible).setBook("Psalm").setChapter(125).setVerses(1, 2, 3, 4, 5),
						new Reference.Builder().setBible(bible).setBook("Psalm").setChapter(125).setVerses(1, 2, 3, 4, 5),
						new Reference.Builder().setBible(bible).setBook("Galatians").setChapter(2).setVerses(1, 2, 3, 4, 5, 19, 20, 21, 6, 7, 8),
						new Reference.Builder().setBible(bible).setBook("1 Timothy").setChapter(4).setVerses(5, 6, 7, 8),
						new Reference.Builder().setBible(bible).setBook("Fo Da Galatia Peopo").setChapter(4).setVerses(2),
						new Reference.Builder().setBible(bible).setBook("Bel And The Dragon").setChapter(4).setVerses(2),
						new Reference.Builder().setBible(bible).setBook("1 AA BB CC DD EE FF GG").setChapter(4).setVerses(2),
						new Reference.Builder().setBible(bible).setBook("Proverbs").setChapter(7).setVerses(4),
						new Reference.Builder().setBible(bible).setBook("Proverbs").setChapter(7).setVerses(4),
						new Reference.Builder().setBible(bible).setBook("James").setChapter(2).setVerses(2),
				};

				assertEquals(references.length, refObjects.length);

				for(int i = 0; i < references.length; i++) {
					Reference.Builder builder = new Reference.Builder();
					builder.setBible(bible);
					builder.parseReference(references[i]);

					Reference parsedReference = builder.create();
					Reference objectReference = refObjects[i].create();

					assertTrue(builder.checkFlag(Reference.Builder.PARSED));
					assertTrue(builder.checkFlag(Reference.Builder.PARSE_SUCCESS));
					assertFalse(builder.checkFlag(Reference.Builder.PARSE_FAILURE));

					assertNotNull(parsedReference);
					assertNotNull(objectReference);

					assertEquals(parsedReference.toString(), objectReference.toString());
					assertEquals(objectReference.toString(), parsedReference.toString());

					assertEquals(parsedReference, objectReference);
					assertEquals(objectReference, parsedReference);

					assertTrue(parsedReference.equals(objectReference));
					assertTrue(objectReference.equals(parsedReference));

					assertEquals(parsedReference.hashCode(), objectReference.hashCode());
					assertEquals(objectReference.hashCode(), parsedReference.hashCode());

					//ensure that we can read in a string that the class prints just the same
					String s = parsedReference.toString();
					Reference reparsedReference = new Reference.Builder()
							.setBible(bible)
							.parseReference(s)
							.create();

					assertEquals(reparsedReference.toString(), objectReference.toString());
					assertEquals(objectReference.toString(), reparsedReference.toString());
				}

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testBadInput() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final ABSBible bible = new ABSBible();

		bible.setId("eng-NASB");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertTrue(bible.getName().equals("New American Standard Bible"));
				assertEquals(bible.getBooks().size(), 66);

				String[] references = new String[] {
						"Xyz 3:16",
						"Jonny",
				};

				for(int i = 0; i < references.length; i++) {
					Reference.Builder builder = new Reference.Builder();
					builder.setBible(bible);
					builder.parseReference(references[i]);

					Reference parsedReference = builder.create();

					assertTrue(builder.checkFlag(Reference.Builder.PARSED));
					assertTrue(builder.checkFlag(Reference.Builder.PARSE_FAILURE));
					assertFalse(builder.checkFlag(Reference.Builder.PARSE_SUCCESS));
				}

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testPrintingReferences() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final ABSBible bible = new ABSBible();

		bible.setId("eng-NASB");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
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

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}


	public void testNextVerses() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final ABSBible bible = new ABSBible();

		bible.setId("eng-NASB");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				Book ephesiansBook = bible.parseBook("Ephesians");
				assertNotNull(ephesiansBook);

				//test a relatively simple case first, where only the chapter rolls over
				Reference gal_2_19 = new Reference.Builder()
						.setBible(bible)
						.parseReference("Galatians 2:19")
						.create();

				Reference gal_2_20 = gal_2_19.next(Reference.TYPE_VERSE).create();
				assertEquals(gal_2_20.getChapter(), 2);
				assertEquals(gal_2_20.getFinalVerse(), 20);

				Reference gal_2_21 = gal_2_20.next(Reference.TYPE_VERSE).create();
				assertEquals(gal_2_21.getChapter(), 2);
				assertEquals(gal_2_21.getFinalVerse(), 21);

				Reference gal_3_1 = gal_2_21.next(Reference.TYPE_VERSE).create();
				assertEquals(gal_3_1.getChapter(), 3);
				assertEquals(gal_3_1.getFinalVerse(), 1);

				//next, test a more complex case where the book also rolls over
				Book markBook = bible.parseBook("Mark");
				assertNotNull(markBook);

				Reference matthew_28_20 = new Reference.Builder()
						.setBible(bible)
						.parseReference("Matthew 28:20")
						.create();

				Reference mark_1_1 = matthew_28_20.next(Reference.TYPE_VERSE).create();
				assertEquals(mark_1_1.getBook(), markBook);
				assertEquals(mark_1_1.getChapter(), 1);
				assertEquals(mark_1_1.getFinalVerse(), 1);

				//finally, test most complex case where the book also rolls over to beginning of Bible
				Book genesisBook = bible.parseBook("Genesis");
				assertNotNull(markBook);

				Reference revelation_22_21 = new Reference.Builder()
						.setBible(bible)
						.parseReference("Revelation 22:21")
						.create();

				Reference genesis_1_1 = revelation_22_21.next(Reference.TYPE_VERSE).create();
				assertEquals(genesis_1_1.getBook(), genesisBook);
				assertEquals(genesis_1_1.getChapter(), 1);
				assertEquals(genesis_1_1.getFinalVerse(), 1);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testPreviousVerses() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final ABSBible bible = new ABSBible();

		bible.setId("eng-NASB");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				Book galatiansBook = bible.parseBook("Galatians");
				assertNotNull(galatiansBook);

				//test a relatively simple case first, where only the chapter rolls over
				Reference galatians_3_2 = new Reference.Builder()
						.setBible(bible)
						.parseReference("Galatians 3:2")
						.create();
				assertEquals(galatians_3_2.getBook(), galatiansBook);
				assertEquals(galatians_3_2.getChapter(), 3);
				assertEquals(galatians_3_2.getFinalVerse(), 2);

				Reference galatians_3_1 = galatians_3_2.previous(Reference.TYPE_VERSE).create();
				assertEquals(galatians_3_1.getBook(), galatiansBook);
				assertEquals(galatians_3_1.getChapter(), 3);
				assertEquals(galatians_3_1.getFinalVerse(), 1);

				Reference galatians_2_21 = galatians_3_1.previous(Reference.TYPE_VERSE).create();
				assertEquals(galatians_2_21.getBook(), galatiansBook);
				assertEquals(galatians_2_21.getChapter(), 2);
				assertEquals(galatians_2_21.getFinalVerse(), 21);

				//next, test a more complex case where the book also rolls over
				Book matthewBook = bible.parseBook("Matthew");
				assertNotNull(matthewBook);

				Reference mark_1_1 = new Reference.Builder()
						.setBible(bible)
						.parseReference("Mark 1:1")
						.create();

				Reference matthew_28_20 = mark_1_1.previous(Reference.TYPE_VERSE).create();

				assertEquals(matthew_28_20.getBook(), matthewBook);
				assertEquals(matthew_28_20.getChapter(), 28);
				assertEquals(matthew_28_20.getFinalVerse(), 20);

				//finally, test most complex case where the book also rolls over to end of Bible
				Book revelationBook = bible.parseBook("Revelation");
				assertNotNull(revelationBook);

				Reference genesis_1_1 = new Reference.Builder()
						.setBible(bible)
						.parseReference("Genesis 1:1")
						.create();

				Reference revelation_22_21 = genesis_1_1.previous(Reference.TYPE_VERSE).create();
				assertEquals(revelation_22_21.getBook(), revelationBook);
				assertEquals(revelation_22_21.getChapter(), 22);
				assertEquals(revelation_22_21.getFinalVerse(), 21);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}
}
