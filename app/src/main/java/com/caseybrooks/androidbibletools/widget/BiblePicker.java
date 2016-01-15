package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.BibleList;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BiblePicker extends LinearLayout {
//Data Members
//--------------------------------------------------------------------------------------------------
	Context context;

	EditText filter;
	TextView bibleCount;
	BibleListAdapter adapter;
	RecyclerView bibleListView;

	TextView progressText;
	ProgressBar progressBar;

	int colorPrimary, colorAccent, textColor;

	Class<? extends BibleList> bibleListClass;
	String tag;

	Bible selectedBible;

//Constructors and Initialization
//--------------------------------------------------------------------------------------------------
	public BiblePicker(Context context) {
		super(context);
		this.context = context;

		initialize(null);
	}

	public BiblePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initialize(attrs);
	}

	public void initialize(AttributeSet attrs) {
		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(
					attrs,
					R.styleable.abt_biblepicker,
					0,
					0
			);
			try {
				bibleListClass = (Class<? extends BibleList>) Class.forName(a.getString(R.styleable.abt_biblepicker_bp_bibleList));
			}
			catch(ClassNotFoundException e) {
				bibleListClass = null;
			}

			this.tag = a.getString(R.styleable.abt_biblepicker_bp_tag);

			a.recycle();
		}

		selectedBible = ABT.getInstance(context).getSelectedBible(tag);

		TypedArray a = context.getTheme().obtainStyledAttributes(
				new int[] {
						android.R.attr.textColor,
						R.attr.colorPrimaryDark,
						R.attr.colorAccent
				}
		);
		textColor = a.getColor(0, Color.BLACK);
		colorPrimary = a.getColor(1, Color.DKGRAY);
		colorAccent = a.getColor(2, Color.LTGRAY);
		a.recycle();

		LayoutInflater.from(context).inflate(R.layout.bible_picker, this);

		filter = (EditText) findViewById(R.id.bible_list_filter);
		filter.addTextChangedListener(filterTextChanged);

		bibleCount = (TextView) findViewById(R.id.bible_list_count);

		bibleListView = (RecyclerView) findViewById(R.id.bible_list);
		bibleListView.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		bibleListView.setLayoutManager(llm);
		bibleListView.addItemDecoration(
				new DividerItemDecoration(
						context,
						DividerItemDecoration.LIST_VERTICAL
				)
		);

		progressText = (TextView) findViewById(R.id.progress_text);
		progressBar = (ProgressBar) findViewById(R.id.progress);

		if(bibleListClass != null) {
			loadBibleList();
		}
	}

//Getters and Setters
//--------------------------------------------------------------------------------------------------
	public Class<? extends BibleList> getBibleListClass() {
		return bibleListClass;
	}

	public void setBibleListClass(Class<? extends BibleList> bibleListClass, String tag) {
		this.bibleListClass = bibleListClass;
		this.tag = tag;
	}

//Data retrieval and manipulation
//--------------------------------------------------------------------------------------------------
	public void loadBibleList() {
		selectedBible = ABT.getInstance(context).getSelectedBible(tag);

		if(selectedBible != null)
			Log.i("BiblePicker", "tag=[" + tag + "] bible=[" + selectedBible.getClass().getName() + ", " + selectedBible.getId() + "]");

		progressText.setText("Retrieving Bible list");
		progressText.setVisibility(View.VISIBLE);
		bibleListView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);

		try {
			final BibleList bibleList = bibleListClass.newInstance();

			if(bibleList instanceof Downloadable) {
				((Downloadable) bibleList).download(new OnResponseListener() {
					@Override
					public void responseFinished() {
						adapter = new BibleListAdapter(
								context,
								bibleList.getBibles().values()
						);

						adapter.filterBy(filter.getText().toString());
						bibleListView.setAdapter(adapter);
						bibleListView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
						progressText.setVisibility(View.GONE);
					}
				});
			}
			else {
				progressBar.setVisibility(View.GONE);
				progressText.setText("cannot display Bible list class [" + bibleListClass.getName() + "]");
				progressText.setVisibility(View.VISIBLE);
			}
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		catch(InstantiationException e) {
			e.printStackTrace();
		}
	}

	private class BibleListViewHolder extends RecyclerView.ViewHolder {
		View root;
		TextView name;
		TextView language;
		TextView abbreviation;

		public BibleListViewHolder(View itemView) {
			super(itemView);

			root = itemView;
			name = (TextView) itemView.findViewById(R.id.bible_name);
			language = (TextView) itemView.findViewById(R.id.bible_language);
			abbreviation = (TextView) itemView.findViewById(R.id.bible_abbreviation);
		}

		public void bind(final Bible bible) {
			if(TextUtils.isEmpty(bible.getName())) {
				name.setText(bible.getLanguage());
			}
			else {
				name.setText(bible.getName());
			}
			language.setText(bible.getLanguage() + " (" + bible.getLanguageEnglish() + ")");
			abbreviation.setText(bible.getAbbreviation());

			if(bible.equals(selectedBible)) {
				name.setTextColor(colorAccent);
				abbreviation.setTextColor(textColor);
			}
			else {
				name.setTextColor(textColor);
				abbreviation.setTextColor(colorPrimary);
			}

			root.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							ABT.getInstance(context).setSelectedBible(bible, tag);
							selectedBible = bible;
							adapter.resort();
						}
					}
			);
		}
	}

	private class BibleListAdapter extends RecyclerView.Adapter<BibleListViewHolder> {
		private Comparator<Bible> bibleComparator = new Comparator<Bible>() {
			@Override
			public int compare(Bible lhs, Bible rhs) {
				if(lhs.equals(selectedBible)) {
					return Integer.MIN_VALUE;
				}
				else if(rhs.equals(selectedBible)) {
					return Integer.MAX_VALUE;
				}
				return lhs.getName().compareTo(rhs.getName());
			}
		};

		ArrayList<Bible> allData;
		ArrayList<Bible> filteredData;

		Context context;

		public BibleListAdapter(Context context, Collection<Bible> data) {
			super();

			this.allData = new ArrayList<>();
			this.allData.addAll(data);
			Collections.sort(this.allData, bibleComparator);

			this.filteredData = new ArrayList<>();
			this.filteredData.addAll(allData);

			this.context = context;

			HashMap<String, String> languages = new HashMap<>();

			for(Bible item : allData) {
				languages.put(item.getLanguage(), item.getLanguage());
			}

			String text = "";

			if(allData.size() == 1) {
				text += allData.size() + " Bible in ";
			}
			else {
				text += allData.size() + " Bibles in ";
			}

			if(languages.size() == 1) {
				text += languages.size() + " language";
			}
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
					if(item.getAbbreviation().toLowerCase().contains(query.toLowerCase()) ||
							item.getLanguage().toLowerCase().contains(query.toLowerCase()) ||
							item.getName().toLowerCase().contains(query.toLowerCase())) {
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
		public BibleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.
					                              from(context).
					                              inflate(
							                              R.layout.bible_picker_item,
							                              parent,
							                              false
					                              );

			return new BibleListViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(BibleListViewHolder holder, int position) {
			holder.bind(filteredData.get(position));
		}

		@Override
		public int getItemCount() {
			return filteredData.size();
		}
	}

	TextWatcher filterTextChanged = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(adapter != null) {
				adapter.filterBy(filter.getText().toString());
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};
}
