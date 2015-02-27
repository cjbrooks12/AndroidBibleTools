package com.caseybrooks.androidbibletools;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.enumeration.Version;

public class IOTest extends ApplicationTestCase<Application> {

	public IOTest() { super(Application.class); }

	public void testXMLCreation() throws Throwable {
		final Passage passage = new Passage("John 1:1-5");
		passage.setVersion(Version.ESV);
		passage.retrieve();

		String xmlExpected =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "" +
					"<passage reference=\"John 1:1-5\" version=\"ESV\">" +
						"<verse reference=\"1\">In the beginning was the Word, and the Word was with God, and the Word was God.</verse>" +
						"<verse reference=\"2\">He was in the beginning with God.</verse>" +
						"<verse reference=\"3\">All things were made through him, and without him was not any thing made that was made.</verse>" +
						"<verse reference=\"4\">In him was life, and the life was the light of men.</verse>" +
						"<verse reference=\"5\">The light shines in the darkness, and the darkness has not overcome it.</verse>" +
					"</passage>";
		String xmlActual= passage.toXMLString();
		assertEquals(xmlExpected, xmlActual);
	}
}
