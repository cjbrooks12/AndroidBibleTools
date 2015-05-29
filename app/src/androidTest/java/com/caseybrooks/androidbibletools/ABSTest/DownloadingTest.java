package com.caseybrooks.androidbibletools.ABSTest;

import android.util.Log;

import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.io.PrivateKeys;
import com.caseybrooks.androidbibletools.providers.abs.ABSBible;
import com.caseybrooks.androidbibletools.providers.abs.ABSPassage;
import com.caseybrooks.androidbibletools.providers.abs.ABSVerse;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public class DownloadingTest extends TestCase {
	public void testDownloadingVerse() throws Throwable {
		ABSBible baseBible = new ABSBible(PrivateKeys.API_KEY, "eng-ESV");
		assertTrue(baseBible.isAvailable());
//		baseBible.parseDocument(baseBible.getDocument());

		for(Book book : baseBible.getBooks()) {
			Log.e("", book.toString());
		}

		ABSVerse verse = new ABSVerse(
				PrivateKeys.API_KEY,
				new Reference.Builder()
						.setBible(baseBible)
						.parseReference("Galatians 2:19")
						.create());
		assertNotNull(verse);
		assertTrue(verse.isAvailable());
		verse.parseDocument(verse.getDocument());
		assertEquals("For through the law I died to the law, so that I might live to God.", verse.getText());

		ABSPassage passage = new ABSPassage(
				PrivateKeys.API_KEY,
				new Reference.Builder()
						.setBible(baseBible)
						.parseReference("Galatians 2:19-21")
						.create());
		assertNotNull(passage);
		assertTrue(passage.isAvailable());
		passage.parseDocument(passage.getDocument());
		assertEquals("For through the law I died to the law, so that I might live to God. I have been crucified with Christ. It is no longer I who live, but Christ who lives in me. And the life I now live in the flesh I live by faith in the Son of God, who loved me and gave himself for me. I do not nullify the grace of God, for if righteousness were through the law, then Christ died for no purpose.", passage.getText());

		//test downloading verses in other languages, like Spanish. First download
		//the appropriate version text, then download the verse after the reference
		//has been properly parsed in that language
		ABSBible DHH = new ABSBible(PrivateKeys.API_KEY, "spa-DHH");
		DHH.parseDocument(DHH.getDocument());
		ABSPassage spanishPassage = new ABSPassage(
				PrivateKeys.API_KEY,
				new Reference.Builder()
						.setBible(DHH)
						.parseReference("Gálatas 2:19-21")
						.create());
		assertTrue(spanishPassage.isAvailable());
		spanishPassage.parseDocument(spanishPassage.getDocument());
		assertEquals("Porque por medio de la ley yo he muerto a la ley, a fin de vivir para Dios. Con Cristo he sido crucificado, y ya no soy yo quien vive, sino que es Cristo quien vive en mí. Y la vida que ahora vivo en el cuerpo, la vivo por mi fe en el Hijo de Dios, que me amó y se entregó a la muerte por mí. No quiero rechazar la bondad de Dios; pues si se obtuviera la justicia por medio de la ley, Cristo habría muerto inútilmente.", spanishPassage.getText());
	}

	public void testGettingVersionsList() throws Throwable {
		Document availableVersions = ABSBible.availableVersionsDoc(PrivateKeys.API_KEY, null);
		HashMap<String, String> availableLanguages = ABSBible.getAvailableLanguages(availableVersions);

		assertNotNull(availableLanguages);
		Log.i("No. Available Langs", availableLanguages.size() + " languages");

		String langKey = "English (US)";
		if(availableLanguages.containsKey(langKey)) {
			HashMap<String, Bible> versionsList = ABSBible.parseAvailableVersions(availableVersions);

			assertNotNull(versionsList);
			Log.i("No. Versions", versionsList.size() + " versions in " + availableLanguages.get(langKey));

			ABSBible esv = (ABSBible) versionsList.get("ESV");
			assertNotNull(esv);

			esv.parseDocument(esv.getDocument());

			Book galatians = esv.parseBook("Galatians");
			assertNotNull(galatians);
			assertEquals("Galatians", galatians.getName());
			assertEquals("Gal", galatians.getAbbreviation());
			assertEquals(48, galatians.getLocation());
		}
	}
}
