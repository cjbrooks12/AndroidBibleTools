package com.caseybrooks.androidbibletools;

import android.test.AndroidTestCase;
import android.util.Log;

import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.providers.votd.VerseOfTheDay;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class VOTDTest extends AndroidTestCase {
    public void testVOTD() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        ABT.createInstance(getContext());

        final VerseOfTheDay votd = new VerseOfTheDay();

        votd.download(new OnResponseListener() {
            @Override
            public void responseFinished() {
                assertNotNull(votd.getPassage());
                assertNotNull(votd.getPassage().getText());

                Log.i("VOTDTEst", "votd=[" +  votd.getPassage().getReference().toString() + "]: " + votd.getPassage().getText());

                signal.countDown();
            }
        });

        signal.await(120, TimeUnit.SECONDS);
    }
}
