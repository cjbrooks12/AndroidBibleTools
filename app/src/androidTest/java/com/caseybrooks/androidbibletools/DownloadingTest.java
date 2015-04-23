package com.caseybrooks.androidbibletools;

import android.util.Log;

import com.caseybrooks.androidbibletools.data.Bible;
import com.caseybrooks.androidbibletools.data.Book;
import com.caseybrooks.androidbibletools.io.Download;
import com.caseybrooks.androidbibletools.io.PrivateKeys;

import junit.framework.TestCase;

import org.jsoup.nodes.Document;

import java.util.HashMap;

public class DownloadingTest extends TestCase {
//	public void testDownloadingVerse() throws Throwable {
//		Verse verse = Verse.parseVerse("Galatians 2:19", null);
//		assertNotNull(verse);
//
//		Document doc1 = Download.bibleChapter(
//				PrivateKeys.API_KEY,
//				verse.getReference());
//		verse.getVerseInfo(doc1);
//		assertEquals("For through the law I died to the law, so that I might live to God.", verse.getText());
//
//		Passage passage = Passage.parsePassage("Galatians 2:19-21", null);
//		Document doc2 = Download.bibleChapter(
//				PrivateKeys.API_KEY,
//				passage.getReference());
//		passage.getVerseInfo(doc2);
//		assertEquals("For through the law I died to the law, so that I might live to God. I have been crucified with Christ. It is no longer I who live, but Christ who lives in me. And the life I now live in the flesh I live by faith in the Son of God, who loved me and gave himself for me. I do not nullify the grace of God, for if righteousness were through the law, then Christ died for no purpose.", passage.getText());
//
//		//test downloading verses in other languages, like Spanish. First download
//		//the appropriate verseion text, then download the verse after the reference
//		//has been properly parsed in that language
//		Bible DHH = new Bible("spa-DHH");
//		DHH.getVersionInfo(Download.versionInfo(PrivateKeys.API_KEY, DHH.getVersionId()));
//		Passage spanishPassage = Passage.parsePassage("Gálatas 2:19-21", DHH);
//		Document doc3 = Download.bibleChapter(
//				PrivateKeys.API_KEY,
//				spanishPassage.getReference());
//		spanishPassage.getVerseInfo(doc3);
//		assertEquals("Porque por medio de la ley yo he muerto a la ley, a fin de vivir para Dios. Con Cristo he sido crucificado, y ya no soy yo quien vive, sino que es Cristo quien vive en mí. Y la vida que ahora vivo en el cuerpo, la vivo por mi fe en el Hijo de Dios, que me amó y se entregó a la muerte por mí. No quiero rechazar la bondad de Dios; pues si se obtuviera la justicia por medio de la ley, Cristo habría muerto inútilmente.", spanishPassage.getText());
//	}

	public void testGettingVersionsList() throws Throwable {
		Document availableVersions = Download.availableVersions(PrivateKeys.API_KEY, null);
		HashMap<String, String> availableLanguages = Bible.getAvailableLanguages(availableVersions);

		assertNotNull(availableLanguages);
		Log.i("No. Available Langs", availableLanguages.size() + " languages");

		String langKey = "English (US)";
		if(availableLanguages.containsKey(langKey)) {
			HashMap<String, Bible> versionsList = Bible.getAvailableVersions(availableVersions);

			assertNotNull(versionsList);
			Log.i("No. Versions", versionsList.size() + " versions in " + availableLanguages.get(langKey));

			Bible esv = versionsList.get("ESV");
			assertNotNull(esv);

			esv.getVersionInfo(Download.versionInfo(PrivateKeys.API_KEY, esv.getVersionId()));

			Book galatians = esv.parseBook("Galatians");
			assertNotNull(galatians);
			assertEquals("Galatians", galatians.getName());
			assertEquals("Gal", galatians.getAbbr());
			assertEquals(48, galatians.getOrder());
		}
	}
}
