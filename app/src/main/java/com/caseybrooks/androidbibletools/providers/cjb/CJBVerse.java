package com.caseybrooks.androidbibletools.providers.cjb;

import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Verse;
import com.caseybrooks.androidbibletools.providers.abs.ABSBook;

public class CJBVerse extends Verse {
	public CJBVerse(Reference reference) {
		super(reference);

		if(reference.getBook() instanceof ABSBook) {
			ABSBook absBook = (ABSBook) reference.getBook();
			this.id = absBook.getId() + "." + reference.getChapter();
		}
		else {
			this.id = "Matt.1";
		}
	}
}
