package com.caseybrooks.androidbibletools.providers.abs;

import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.basic.Verse;

public class ABSVerse extends Verse {
	public ABSVerse(Reference reference) {
		super(reference);
		this.formatter = new ABSFormatter();

		if(reference.getBook() instanceof ABSBook) {
			ABSBook absBook = (ABSBook) reference.getBook();
			this.id = absBook.getId() + "." + reference.getChapter();
		}
		else {
			this.id = "Matt.1";
		}
	}
}
