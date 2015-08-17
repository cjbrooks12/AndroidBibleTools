package com.caseybrooks.androidbibletools.widget.biblepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.io.ABTUtility;
import com.caseybrooks.androidbibletools.providers.abs.ABSBible;
import com.caseybrooks.androidbibletools.widget.WorkerThread;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BiblePicker extends LinearLayout {
//Data Members
//------------------------------------------------------------------------------
	Context context;
	Bible selectedBible;
	OnBibleSelectedListener listener;

	EditText filter;
	TextView bibleCount;
	BibleListAdapter adapter;
	ListView bibleList;

	TextView progressText;
	ProgressBar progressBar;

	int colorPrimary, colorAccent, textColor;

	WorkerThread workerThread;

	//Constructors and Initialization
//------------------------------------------------------------------------------
	public BiblePicker(Context context) {
		super(context);
		this.context = context;

		initialize();
	}

	public BiblePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initialize();
	}

	public void initialize() {
		workerThread = new WorkerThread();

		TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{
				android.R.attr.textColor,
				R.attr.colorAccent,
				R.attr.colorPrimaryDark
		});
		textColor = a.getColor(0, Color.BLACK);
		colorAccent = a.getColor(1, Color.LTGRAY);
		colorPrimary = a.getColor(2, Color.DKGRAY);
		a.recycle();

		LayoutInflater.from(context).inflate(R.layout.bible_picker, this);

		filter = (EditText) findViewById(R.id.bible_list_filter);
		filter.addTextChangedListener(filterTextChanged);

		bibleCount = (TextView) findViewById(R.id.bible_list_count);

		bibleList = (ListView) findViewById(R.id.bible_list);
		bibleList.setOnItemClickListener(itemClickListener);

		progressText = (TextView) findViewById(R.id.progress_text);
		progressBar = (ProgressBar) findViewById(R.id.progress);

		loadBibleList();
	}

//Data retrieval and manipulation
//------------------------------------------------------------------------------

	private void loadBibleList() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		post(new Runnable() {
			@Override
			public void run() {
				progressText.setText("Retrieving Bible list");
				progressText.setVisibility(View.VISIBLE);
				bibleList.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				selectedBible = BiblePickerSettings.getSelectedBible(context);

				try {
					final HashMap<String, Bible> bibles = ABSBible.parseAvailableVersions(
							ABSBible.availableVersionsDoc(
									context.getResources().getString(R.string.bibles_org_key)
									, null
							));
					post(new Runnable() {
						@Override
						public void run() {
							adapter = new BibleListAdapter(context, bibles.values());
						}
					});
				}
				catch(IOException ioe) {
					//assuming we have previously selected a bible, only show that
					//one in the list if we can't connect to the server right now.
					//In the case that we haven't selected a Bible yet, the settings
					//will return a default Bible that we can use for the time being
					final HashMap<String, Bible> bibles = new HashMap<>();
					bibles.put(selectedBible.getAbbreviation(), selectedBible);
					post(new Runnable() {
						@Override
						public void run() {
							adapter = new BibleListAdapter(context, bibles.values());
						}
					});
				}

				post(new Runnable() {
					@Override
					public void run() {
						adapter.filterBy(filter.getText().toString());
						bibleList.setAdapter(adapter);
						bibleList.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
						progressText.setVisibility(View.GONE);
					}
				});
			}
		});
	}

	private void loadSelectedBibleInfo() {
		if(workerThread.getState() == Thread.State.NEW)
			workerThread.start();

		post(new Runnable() {
			@Override
			public void run() {
				progressText.setText("Downloading Bible Info");
				progressText.setVisibility(View.VISIBLE);
				bibleList.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		workerThread.post(new Runnable() {
			@Override
			public void run() {
				if(selectedBible instanceof Downloadable) {
					Downloadable downloadableBible = (Downloadable) selectedBible;

					try {
						Document doc = downloadableBible.getDocument();

						if(doc != null) {
							ABTUtility.cacheDocument(context, doc, "selectedBible.xml");
							post(new Runnable() {
								@Override
								public void run() {
									progressText.setVisibility(View.GONE);
									bibleList.setVisibility(View.VISIBLE);
									progressBar.setVisibility(View.GONE);

									if(listener != null) {
										listener.onBibleDownloaded(true);
									}
								}
							});
						}
						else {
							post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(context, "Could not cache bible, try again later", Toast.LENGTH_SHORT).show();
									progressText.setVisibility(View.GONE);
									bibleList.setVisibility(View.VISIBLE);
									progressBar.setVisibility(View.GONE);

									if(listener != null) {
										listener.onBibleDownloaded(false);
									}
								}
							});
						}
					}
					catch(IOException ioe) {
						ioe.printStackTrace();
						post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(context, "Could not cache bible, try again later", Toast.LENGTH_SHORT).show();
								progressText.setVisibility(View.GONE);
								bibleList.setVisibility(View.VISIBLE);
								progressBar.setVisibility(View.GONE);

								if(listener != null) {
									listener.onBibleDownloaded(false);
								}
							}
						});
					}
				}
			}
		});
	}

	private class BibleListAdapter extends BaseAdapter {
		private Comparator<Bible> bibleComparator = new Comparator<Bible>() {
			@Override
			public int compare(Bible lhs, Bible rhs) {
				if(lhs.equals(selectedBible)) return Integer.MIN_VALUE;
				else if(rhs.equals(selectedBible)) return Integer.MAX_VALUE;
				else return lhs.getName().compareTo(rhs.getName());
			}
		};

		ArrayList<Bible> allData;
		ArrayList<Bible> filteredData;

		Context context;
		LayoutInflater layoutInflater;

		public BibleListAdapter(Context context, Collection<Bible> data) {
			super();
			this.allData = new ArrayList<>();
			this.allData.addAll(data);
			Collections.sort(this.allData, bibleComparator);

			this.filteredData = new ArrayList<>();
			this.filteredData.addAll(allData);

			this.context = context;
			layoutInflater = LayoutInflater.from(context);

			HashMap<String, String> languages = new HashMap<>();

			for(Bible item : allData) {
				languages.put(item.getLanguage(), item.getLanguage());
			}

			String text = "";

			if(allData.size() == 1)
				text += allData.size() + " Bible in ";
			else {
				text += allData.size() + " Bibles in ";
			}

			if(languages.size() == 1)
				text += languages.size() + " language";
			else {
				text += languages.size() + " languages";
			}

			bibleCount.setVisibility(View.VISIBLE);
			bibleCount.setText(text);
		}

		public void resort() {
			Collections.sort(this.allData, bibleComparator);
			filterBy(filter.getText().toString());
		}

		public void filterBy(String query) {
			filteredData.clear();

			if(query != null && query.length() > 0) {
				for(Bible item : allData) {
					if(	item.getAbbreviation().toLowerCase().contains(query.toLowerCase()) ||
							item.getLanguage().toLowerCase().contains(query.toLowerCase()) ||
							item.getName().toLowerCase().contains(query.toLowerCase()))
					{
						filteredData.add(item);
					}
				}
			}
			else {
				filteredData.addAll(allData);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return filteredData.size();
		}

		@Override
		public Bible getItem(int position) {
			return filteredData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = layoutInflater.inflate(R.layout.bible_picker_item, null);

			Bible item = getItem(position);

			TextView name = (TextView) convertView.findViewById(R.id.bible_name);
			name.setText(item.getName());

			TextView language = (TextView) convertView.findViewById(R.id.bible_language);
			language.setText(item.getLanguage());

			TextView abbreviation = (TextView) convertView.findViewById(R.id.bible_abbreviation);
			abbreviation.setText(item.getAbbreviation());

			if(item.equals(selectedBible)) {
				name.setTextColor(colorAccent);
				abbreviation.setTextColor(textColor);
			}
			else {
				name.setTextColor(textColor);
				abbreviation.setTextColor(colorPrimary);
			}

			return convertView;
		}
	}

	TextWatcher filterTextChanged = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(adapter != null)	adapter.filterBy(filter.getText().toString());
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Bible bible = adapter.getItem(position);
			if(listener != null)
				listener.onBibleSelected();

			BiblePickerSettings.setSelectedBible(context, bible);
			selectedBible = BiblePickerSettings.getSelectedBible(context);
			adapter.resort();
			adapter.notifyDataSetChanged();

			loadSelectedBibleInfo();
		}
	};

//Getters and Setters
//------------------------------------------------------------------------------

	public OnBibleSelectedListener getListener() {
		return listener;
	}

	public void setListener(OnBibleSelectedListener listener) {
		this.listener = listener;
	}
}
