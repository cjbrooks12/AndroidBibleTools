package com.caseybrooks.androidbibletools;

import android.test.AndroidTestCase;

import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.openbible.OpenBiblePassage;
import com.caseybrooks.androidbibletools.providers.openbible.TopicalSearch;
import com.caseybrooks.androidbibletools.providers.openbible.Topics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class OpenBibleTest extends AndroidTestCase {
	public void testTopicsList() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.createInstance(getContext());

		final Topics topics = new Topics();
		topics.setSearchCharacter('h');

		topics.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertEquals(topics.getTopics().size(), 395);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

	public void testTopicalSearch() throws Throwable {
		final CountDownLatch signal = new CountDownLatch(1);
		ABT.createInstance(getContext());

		final TopicalSearch topics = new TopicalSearch();
		topics.setSearchTerm("pie");

		topics.download(new OnResponseListener() {
			@Override
			public void responseFinished() {
				assertEquals(topics.getPassages().size(), 33);

				signal.countDown();
			}
		});

		signal.await(120, TimeUnit.SECONDS);
	}

//	public void testVoting() throws Throwable {
//		final CountDownLatch signal = new CountDownLatch(1);
//		ABT.createInstance(getContext());
//
//		final TopicalSearch topics = new TopicalSearch();
//		topics.setSearchTerm("Life After Death");
//
//		topics.download(new OnResponseListener() {
//			@Override
//			public void responseFinished() {
//				assertEquals(topics.getPassages().size(), 90);
//				OpenBiblePassage passage = topics.getPassages().get(0);
//				final int upvotes_initial = passage.getMetadata().getInt("UPVOTES");
//				assertEquals(upvotes_initial, 365);
//
//				passage.upvote(new OnResponseListener() {
//					@Override
//					public void responseFinished() {
//						topics.download(new OnResponseListener() {
//							@Override
//							public void responseFinished() {
//								assertEquals(topics.getPassages().size(), 90);
//								OpenBiblePassage passage = topics.getPassages().get(0);
//								final int upvotes_after = passage.getMetadata().getInt("UPVOTES");
//								assertEquals((upvotes_initial + 1), upvotes_after);
//
//								passage.downvote(new OnResponseListener() {
//									@Override
//									public void responseFinished() {
//										topics.download(new OnResponseListener() {
//											@Override
//											public void responseFinished() {
//												assertEquals(topics.getPassages().size(), 90);
//												OpenBiblePassage passage = topics.getPassages().get(0);
//												final int upvotes_after = passage.getMetadata().getInt("UPVOTES");
//												assertEquals(upvotes_initial, upvotes_after);
//
//												signal.countDown();
//											}
//										});
//									}
//								});
//							}
//						});
//					}
//				});
//			}
//		});
//
//		signal.await(120, TimeUnit.SECONDS);
//	}
}
