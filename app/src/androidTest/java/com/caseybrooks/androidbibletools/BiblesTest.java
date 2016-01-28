package com.caseybrooks.androidbibletools;

import android.test.AndroidTestCase;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.abs.ABSBible;
import com.caseybrooks.androidbibletools.providers.abs.ABSBibleList;
import com.caseybrooks.androidbibletools.providers.abs.ABSBook;
import com.caseybrooks.androidbibletools.providers.cjb.CJBBible;
import com.caseybrooks.androidbibletools.providers.cjb.CJBBibleList;
import com.caseybrooks.androidbibletools.providers.cjb.CJBBook;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BiblesTest extends AndroidTestCase {
//test getting info about a single Bible from various services
//--------------------------------------------------------------------------------------------------
	public void testCJBBible_dbp() throws Throwable{
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.createInstance(getContext());

		//verify that a Bible and required components will all extend the correct classes
		final CJBBible bible = new CJBBible();
		assertTrue(bible.getClass() == CJBBible.class);
		assertTrue(bible.getBooks().get(0).getClass() == CJBBook.class);

		//verify that this Bible's data is downloaded and parsed correctly using DBP service
		bible.setId("ENGKJVN1ET");
		bible.setService("dbp");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertTrue(bible.getName().equals("King James Version"));
				assertEquals(bible.getBooks().size(), 27);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testCJBBible_abs() throws Throwable{
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.createInstance(getContext());

		//verify that a Bible and required components will all extend the correct classes
		final CJBBible bible = new CJBBible();
		assertTrue(bible.getClass() == CJBBible.class);
		assertTrue(bible.getBooks().get(0).getClass() == CJBBook.class);

		//verify that this Bible's data is downloaded and parsed correctly using abs service
		bible.setId("eng-NASB");
		bible.setService("abs");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertTrue(bible.getName().equals("New American Standard Bible"));
				assertEquals(bible.getBooks().size(), 66);
				Book matthew = bible.getBooks().get(39);

				assertEquals(matthew.getName(), "Matthew");
				assertEquals(matthew.numChapters(), 28);
				assertEquals(matthew.numVersesInChapter(28), 20);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testABSBible() throws Throwable{
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.getInstance(getContext())
				.getMetadata().putString("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

		//verify that a Bible and required components will all extend the correct classes
		final ABSBible bible = new ABSBible();
		assertTrue(bible.getClass() == ABSBible.class);
		assertTrue(bible.getBooks().get(0).getClass() == ABSBook.class);

		//verify that this Bible's data is downloaded and parsed correctly using abs service
		bible.setId("eng-NASB");
		bible.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertTrue(bible.getName().equals("New American Standard Bible"));
				assertTrue(bible.getAbbreviation().equals("NASB"));
				assertEquals(bible.getBooks().size(), 66);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

//test getting lists of Bibles from the various services
//--------------------------------------------------------------------------------------------------
	public void testCJBBibleList() throws Throwable{
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.createInstance(getContext());

		final CJBBibleList bibleList = new CJBBibleList();
		bibleList.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertEquals(bibleList.getBibles().size(), 1418);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testABSBibleList() throws Throwable{
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.createInstance(getContext());

		final ABSBibleList bibleList = new ABSBibleList();
		bibleList.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertEquals(bibleList.getBibles().size(), 303);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}
	
//Test serializing and deserializing selected Bibles
//--------------------------------------------------------------------------------------------------
	public void testSelectingBible() throws Throwable {
		ABT abt = ABT.getInstance(getContext());

		//create two Bibles and save them with different tags
		final CJBBible bible1 = new CJBBible();
		bible1.setService("abs");
		bible1.setId("eng-NASB");
		abt.setSelectedBible(bible1, "bible1");

		final ABSBible bible2 = new ABSBible();
		bible2.setId("eng-NASB");
		abt.setSelectedBible(bible2, "bible2");

		//verify the class that is saved is correct
		Class<? extends Bible> selectedBibleType1 = abt.getSelectedBibleType("bible1");
		Class<? extends Bible> selectedBibleType2 = abt.getSelectedBibleType("bible2");
		assertEquals(selectedBibleType1, CJBBible.class);
		assertEquals(selectedBibleType2, ABSBible.class);

		//verify the Bible that is saved is correct
		final CJBBible selectedBible1 = (CJBBible) abt.getSelectedBible("bible1");
		final ABSBible selectedBible2 = (ABSBible) abt.getSelectedBible("bible2");
		assertNotNull(bible1);
		assertNotNull(bible2);
		assertNotNull(selectedBible1);
		assertNotNull(selectedBible2);
		assertEquals(bible1, selectedBible1);
		assertEquals(bible2, selectedBible2);

		//selectedBible1 type should be CJBBible, so attempting to load it as an ABSBible should fail
		try {
			final ABSBible nonSelectedBible = (ABSBible) abt.getSelectedBible("bible1");
			Assert.fail("Should have thrown ClassCastException");
		}
		catch (ClassCastException e) {
		}

		//selectedBible2 type should be ABSBible, so attempting to load it as an CJBBible should fail
		try {
			final CJBBible nonSelectedBible = (CJBBible) abt.getSelectedBible("bible2");
			Assert.fail("Should have thrown ClassCastException");
		}
		catch (ClassCastException e) {
		}
	}
}
