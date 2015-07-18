package com.caseybrooks.androidbibletools.widget;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Reference;

public interface IReferencePickerListener {
	boolean onBibleLoaded(Bible bible, LoadState state);
	boolean onReferenceParsed(Reference parsedReference, boolean wasSuccessful);
}
