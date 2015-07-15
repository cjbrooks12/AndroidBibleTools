package com.caseybrooks.androidbibletools.widget;

import com.caseybrooks.androidbibletools.basic.Reference;

public interface ReferencePickerListener {
	void onPreParse(String textToParse);
	void onParseCompleted(Reference parsedReference, boolean wasSuccessful);
}
