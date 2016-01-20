package com.caseybrooks.androidbibletools;

import android.test.AndroidTestCase;

import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.abs.ABSBible;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;
import com.caseybrooks.androidbibletools.providers.cjb.CJBBible;
import com.caseybrooks.androidbibletools.providers.cjb.CJBPassage;
import com.caseybrooks.androidbibletools.providers.simple.SimpleBook;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class VersesTest extends AndroidTestCase {
//Test classes to ensure correct extensibility of Passage subclasses and its type parameters
//--------------------------------------------------------------------------------------------------
	public static class TestVerse extends Verse {

		public TestVerse(Reference reference) {
			super(reference);
		}
	}

	public static class TestPassage extends Passage<TestVerse> {
		public TestPassage(Reference reference) {
			super(reference);
		}
	}

//Actual Tests
//--------------------------------------------------------------------------------------------------
	public void testPassageGenerics() throws Throwable {
		Book book = new SimpleBook();
		book.setName("Matthew");

		TestPassage passage = new TestPassage(
				new Reference.Builder()
						.setBook(book)
						.setChapter(1)
						.setVerses(1)
						.create()
		);

		assertEquals(passage.getVerses().get(0).getClass(), TestVerse.class);
	}

	public void testABSPassage() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final ABSBible bible = new ABSBible();
		bible.setId("eng-NASB");

		bible.download(
				new OnResponseListener() {
					@Override
					public void responseFinished() {
						assertTrue(bible.getName().equals("New American Standard Bible"));
						assertTrue(bible.getAbbreviation().equals("NASB"));
						assertEquals(bible.getBooks().size(), 66);

						Book book = bible.getBooks().get(0);
						assertEquals(book.getName(), "Genesis");
						assertEquals(book.numChapters(), 50);
						assertEquals(book.numVersesInChapter(10), 32);

						Reference reference = new Reference.Builder()
								.setBible(bible)
								.setBook(book)
								.setChapter(1)
								.setVerses(1, 2, 3, 4, 6)
								.create();

						assertEquals(reference.toString(), "Genesis 1:1-4, 6");

						final ABSPassage passage = new ABSPassage(reference);
						passage.download(
								new OnResponseListener() {
									@Override
									public void responseFinished() {
										assertEquals(passage.getVerses().get(0).getText(), "<h3 class=\"s\">The Creation</h3>\n<p class=\"p\"><sup id=\"Gen.1.1\" class=\"v\">1</sup>In the beginning God created the heavens and the earth.</p>");
										assertEquals(passage.getVerses().get(1).getText(), "<p class=\"p\"><sup id=\"Gen.1.2\" class=\"v\">2</sup>The earth was formless and void, and darkness was over the surface of the deep, and the Spirit of God was moving over the surface of the waters.</p>");
										assertEquals(passage.getVerses().get(2).getText(), "<p class=\"p\"><sup id=\"Gen.1.3\" class=\"v\">3</sup>Then God said, “Let there be light”; and there was light.</p>");
										assertEquals(passage.getVerses().get(3).getText(), "<p class=\"p\"><sup id=\"Gen.1.4\" class=\"v\">4</sup>God saw that the light was good; and God separated the light from the darkness.</p>");
										assertEquals(passage.getVerses().get(4).getText(), "<p class=\"p\"><sup id=\"Gen.1.6\" class=\"v\">6</sup>Then God said, “Let there be an expanse in the midst of the waters, and let it separate the waters from the waters.”</p>");

										signal.countDown();
									}
								}
						);
					}
				}
		);

		signal.await(120, TimeUnit.SECONDS);
	}


	public void testCJBPassage() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		final CJBBible bible = new CJBBible();
		bible.setService("abs");
		bible.setId("eng-NASB");

		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertTrue(bible.getName().equals("New American Standard Bible"));
				assertTrue(bible.getAbbreviation().equals("NASB"));
				assertEquals(bible.getBooks().size(), 66);

				Book book = bible.getBooks().get(0);
				assertEquals(book.getName(), "Genesis");
				assertEquals(book.numChapters(), 50);
				assertEquals(book.numVersesInChapter(10), 32);

				Reference reference = new Reference.Builder()
						.setBible(bible)
						.setBook(book)
						.setChapter(1)
						.setVerses(1, 2, 3, 4, 6)
						.create();

				assertEquals(reference.toString(), "Genesis 1:1-4, 6");

				final CJBPassage passage = new CJBPassage(reference);
				passage.download(new OnResponseListener() {
					@Override
					public void responseFinished() {
						assertEquals(passage.getVerses().get(0).getText(), "<h3 class=\"s\">The Creation</h3>\n<p class=\"p\"><sup id=\"Gen.1.1\" class=\"v\">1</sup>In the beginning God created the heavens and the earth.</p>");
						assertEquals(passage.getVerses().get(1).getText(), "<p class=\"p\"><sup id=\"Gen.1.2\" class=\"v\">2</sup>The earth was formless and void, and darkness was over the surface of the deep, and the Spirit of God was moving over the surface of the waters.</p>");
						assertEquals(passage.getVerses().get(2).getText(), "<p class=\"p\"><sup id=\"Gen.1.3\" class=\"v\">3</sup>Then God said, “Let there be light”; and there was light.</p>");
						assertEquals(passage.getVerses().get(3).getText(), "<p class=\"p\"><sup id=\"Gen.1.4\" class=\"v\">4</sup>God saw that the light was good; and God separated the light from the darkness.</p>");
						assertEquals(passage.getVerses().get(4).getText(), "<p class=\"p\"><sup id=\"Gen.1.6\" class=\"v\">6</sup>Then God said, “Let there be an expanse in the midst of the waters, and let it separate the waters from the waters.”</p>");

						signal.countDown();
					}
				});
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}
}
