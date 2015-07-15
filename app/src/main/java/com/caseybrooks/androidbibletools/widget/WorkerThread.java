package com.caseybrooks.androidbibletools.widget;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * A simple class that allows all async calls done with the widgets to be executed
 * sequentially on a single non-UI thread
 */
public class WorkerThread extends HandlerThread {
	Handler mHandler = null;

	public WorkerThread() {
		super("WorkerThread");
		start();
		mHandler = new Handler(getLooper());
	}

	public boolean post(Runnable r) {
		return mHandler.post(r);
	}
}
