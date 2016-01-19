package com.caseybrooks.androidbibletools.providers.basic;

import com.caseybrooks.androidbibletools.basic.Book;

public class SimpleBook extends Book {

	@Override
	public void setName(String name) {
		super.setName(name);
		if(name.length() > 3)
			setAbbreviation(name.substring(0, 3));
		else
			setAbbreviation(name);
	}
}
