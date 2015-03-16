package com.caseybrooks.androidbibletools.data;

public interface OnDownloadListener {
	public void onPreDownload();
	public void onDownloadSuccess();
	public void onDownloadFail();
}
