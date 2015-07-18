package com.caseybrooks.androidbibletools.widget;

import com.caseybrooks.androidbibletools.basic.Reference;

public interface IReferencePicker {
	void loadSelectedBible();

	Reference getReference();
	void checkReference(String referenceText);
}
