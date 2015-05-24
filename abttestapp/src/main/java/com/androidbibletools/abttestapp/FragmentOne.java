package com.androidbibletools.abttestapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.data.Formatter;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.io.Download;
import com.caseybrooks.androidbibletools.io.PrivateKeys;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FragmentOne extends Fragment {
	View view;

	Spinner versionSpinner;
	Spinner bookSpinner;
	Spinner chapterSpinner;
	Spinner verseSpinner;

	EditText referenceEditText;
	TextView verseTextView;
	Button parseButton;

	CheckBox showNumbersCheckbox;
	CheckBox newLinesCheckBox;

	HashMap<String, Bible> availableBibles;
	String selectedBible;
	Bible selectedBibleObject;

	Passage passage;

	Book selectedBook;
	int selectedChapter;
	int selectedVerse;

	public static FragmentOne newInstance() {
		FragmentOne fragment = new FragmentOne();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_fragment_one, container, false);

		initialize();
		return view;
	}

	private void initialize() {
		versionSpinner = (Spinner) view.findViewById(R.id.versionSpinner);
		bookSpinner = (Spinner) view.findViewById(R.id.bookSpinner);
		chapterSpinner = (Spinner) view.findViewById(R.id.chapterSpinner);
		verseSpinner = (Spinner) view.findViewById(R.id.verseSpinner);

		referenceEditText = (EditText) view.findViewById(R.id.referenceEditText);
		verseTextView = (TextView) view.findViewById(R.id.verseTextView);
		parseButton = (Button) view.findViewById(R.id.parseButton);
		parseButton.setOnClickListener(parseButtonClick);

		showNumbersCheckbox = (CheckBox) view.findViewById(R.id.showNumbersCheckbox);
		showNumbersCheckbox.setOnCheckedChangeListener(checkedChange);

		newLinesCheckBox = (CheckBox) view.findViewById(R.id.newlinesCheckbox);
		newLinesCheckBox.setOnCheckedChangeListener(checkedChange);

		new PopulateVersions().execute();
	}

	private class PopulateVersions extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Document doc = Download.availableVersions(PrivateKeys.API_KEY, null);
				availableBibles = Bible.getAvailableVersions(doc);
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			Bible[] biblesList = new Bible[availableBibles.size()];
			availableBibles.values().toArray(biblesList);

			String[] biblesArray = new String[biblesList.length];

			for(int i = 0; i < biblesList.length; i++) {
				biblesArray[i] = biblesList[i].getVersionId();
			}

			Arrays.sort(biblesArray);
			versionSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, biblesArray));
			versionSpinner.setOnItemSelectedListener(versionSpinnerCallback);
		}
	}

	private AdapterView.OnItemSelectedListener versionSpinnerCallback = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedBible = (String) parent.getAdapter().getItem(position);

			new PopulateBooks().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private class PopulateBooks extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Document doc = Download.versionInfo(PrivateKeys.API_KEY, selectedBible);

				selectedBibleObject = new Bible(selectedBible);
				selectedBibleObject.getVersionInfo(doc);
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			ArrayList<Book> booksList = selectedBibleObject.books;

			String[] booksArray = new String[booksList.size()];

			for(int i = 0; i < booksList.size(); i++) {
				booksArray[i] = booksList.get(i).getName();
			}

			bookSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, booksArray));
			bookSpinner.setOnItemSelectedListener(bookSpinnerCallback);
		}
	}

	private AdapterView.OnItemSelectedListener bookSpinnerCallback = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedBook = selectedBibleObject.parseBook((String) parent.getAdapter().getItem(position));

			String[] chaptersArray = new String[selectedBook.getChapters().length];

			for(int i = 0; i < chaptersArray.length; i++) {
				chaptersArray[i] = Integer.toString(i+1);
			}

			chapterSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, chaptersArray));
			chapterSpinner.setOnItemSelectedListener(chapterSpinnerCallback);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private AdapterView.OnItemSelectedListener chapterSpinnerCallback = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedChapter = position + 1;

			String[] versesArray = new String[selectedBook.getChapters()[position]];

			for(int i = 0; i < versesArray.length; i++) {
				versesArray[i] = Integer.toString(i+1);
			}

			verseSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, versesArray));
			verseSpinner.setOnItemSelectedListener(verseSpinnerCallback);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private AdapterView.OnItemSelectedListener verseSpinnerCallback = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedVerse = position + 1;

			Reference reference = new Reference(selectedBook, selectedChapter, selectedVerse);
			referenceEditText.setText(reference.toString());
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private class DownloadVerse extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				String referenceString = referenceEditText.getText().toString();

				Reference reference = Reference.parseReference(referenceString, selectedBibleObject);
				passage = new Passage(reference);
				passage.setFormatter(formatter);

				Document doc = Download.bibleChapter(PrivateKeys.API_KEY,
						reference.book.getId(),
						reference.chapter);

				passage.getVerseInfo(doc);
			}
			catch(ParseException | IOException pe) {
				pe.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			verseTextView.setText(passage.getText());
			referenceEditText.setText(passage.getReference().toString());
		}
	}

	private View.OnClickListener parseButtonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new DownloadVerse().execute();
		}
	};

	private CompoundButton.OnCheckedChangeListener checkedChange = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
			verseTextView.setText(Html.fromHtml(passage.getText()));
		}
	};

	Formatter formatter = new Formatter() {
		@Override
		public String onPreFormat(Reference reference) {
			return "";
		}

		@Override
		public String onFormatVerseStart(int verseNumber) {
			return (showNumbersCheckbox.isChecked()) ? "<sup><small>" + verseNumber + "</small></sup>" : "";
		}

		@Override
		public String onFormatText(String verseText) {
			return verseText;
		}

		@Override
		public String onFormatSpecial(String special) {
			return special;
		}

		@Override
		public String onFormatVerseEnd() {
			return (newLinesCheckBox.isChecked()) ? "<br/>" : "";
		}

		@Override
		public String onPostFormat() {
			return "";
		}
	};
}
