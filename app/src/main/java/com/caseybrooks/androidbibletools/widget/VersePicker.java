package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.ABT;
import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.Bible;
import com.caseybrooks.androidbibletools.basic.Book;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;
import com.caseybrooks.androidbibletools.io.DividerItemDecoration;

//TODO: Make an EditReference that does the actual input parsing logic, and connect it here instead of a TextView
public class VersePicker extends LinearLayout implements OnBibleSelectedListener {
	public static int SELECTION_ONE_VERSE = 0;
	public static int SELECTION_MANY_VERSES = 1;
	public static int SELECTION_WHOLE_CHAPTER = 2;

//Data Members
//--------------------------------------------------------------------------------------------------
	Context context;

	ColorStateList bookTextColor, circleTextColor, circleBackgroundColor;

	Bible bible;
	String tag;
	TextView selectedBibleName;
	TextView editReference;
	Button verseSelectionModeButton;

	ViewPager viewPager;
	TabLayout tabLayout;
	VersePickerPagerAdapter adapter;

	RecyclerView bookList;
	RecyclerView chapterList;
	RecyclerView verseList;

	Reference.Builder builder;

	OnReferenceCreatedListener listener;
	int connectedViewId;
	int selectionMode;

//Constructors and Initialization
//--------------------------------------------------------------------------------------------------
	public VersePicker(Context context) {
		super(context);
		this.context = context;

		initialize(null);
	}

	public VersePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initialize(attrs);
	}

	public void initialize(AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.verse_picker, this);

		int attrSelectionMode = SELECTION_MANY_VERSES;

		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.abt_versepicker, 0, 0);
			attrSelectionMode = a.getInt(R.styleable.abt_versepicker_selectionMode, SELECTION_MANY_VERSES);
			setSelectedBibleTag(a.getString(R.styleable.abt_versepicker_tag));
			connectedViewId = a.getResourceId(R.styleable.abt_biblepicker_connectTo, 0);
			a.recycle();
		}

		loadColors();

		bible = ABT.getInstance(context).getSelectedBible(tag);
		builder = new Reference.Builder();
		builder.setFlag(Reference.Builder.PREVENT_AUTO_ADD_VERSES_FLAG);

		selectedBibleName = (TextView) findViewById(R.id.selected_bible_name);
		editReference = (TextView) findViewById(R.id.editReference);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

		bookList = (RecyclerView) findViewById(R.id.book_list);
		chapterList = (RecyclerView) findViewById(R.id.chapter_list);
		verseList = (RecyclerView) findViewById(R.id.verse_list);

		bookList.setHasFixedSize(true);
		chapterList.setHasFixedSize(true);
		verseList.setHasFixedSize(true);

		bookList.setAdapter(new BooksAdapter());
		chapterList.setAdapter(new ChaptersAdapter());
		verseList.setAdapter(new VersesAdapter());

		bookList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		chapterList.setLayoutManager(new GridLayoutManager(getContext(), 4));
		verseList.setLayoutManager(new GridLayoutManager(getContext(), 4));

		bookList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.LIST_VERTICAL));

		verseSelectionModeButton = (Button) findViewById(R.id.selection_mode_button);
		verseSelectionModeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(
						getContext(),
						android.R.layout.simple_list_item_single_choice,
						getResources().getStringArray(R.array.versepicker_selection_titles)) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);

								if(position == selectionMode)
									view.setChecked(true);
								else
									view.setChecked(false);

								return view;
							}
						};

				new AlertDialog.Builder(getContext())
						.setTitle("Select...")
						.setAdapter(itemsAdapter,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									setSelectionMode(which);
								}
							})
						.create()
						.show();

			}
		});

		setSelectionMode(attrSelectionMode);

		if(bible != null) {
			loadBible();
		}
	}

	private void loadColors() {
		TypedArray a = context.getTheme().obtainStyledAttributes(
				new int[] {
						android.R.attr.textColor,
						R.attr.colorAccent
				}
		);
		int textColor = a.getColor(0, Color.BLACK);
		int colorAccent = a.getColor(1, Color.LTGRAY);
		int pressedColor = Color.parseColor("#15000000");
		a.recycle();

		bookTextColor = new ColorStateList(
				new int[][]{
						new int[] { android.R.attr.state_pressed },
						new int[] { android.R.attr.state_selected },
						new int[] { }
				},
				new int[] {
						darker(colorAccent, 0.8f),
						colorAccent,
						textColor
				}
		);

		circleTextColor = new ColorStateList(
				new int[][]{
						new int[] { android.R.attr.state_selected },
						new int[] { }
				},
				new int[] {
						Color.WHITE,
						textColor,
				}
		);

		circleBackgroundColor = new ColorStateList(
				new int[][]{
						new int[] { android.R.attr.state_pressed, android.R.attr.state_selected },
						new int[] { android.R.attr.state_pressed },
						new int[] { android.R.attr.state_selected },
						new int[] { }
				},
				new int[] {
						darker(colorAccent, 0.8f),
						pressedColor,
						colorAccent,
						Color.TRANSPARENT
				}
		);
	}

	private static int darker (int color, float factor) {
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);

		return Color.argb(
				a,
				Math.max((int) (r * factor), 0),
				Math.max((int) (g * factor), 0),
				Math.max((int) (b * factor), 0));
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if(connectedViewId != 0 && getRootView() != null) {
			View connectedView = getRootView().findViewById(connectedViewId);

			if(connectedView != null && connectedView instanceof OnReferenceCreatedListener) {
				setOnReferenceCreatedListener((OnReferenceCreatedListener) connectedView);
			}
		}
	}

	public String getSelectedBibleTag() {
		return tag;
	}

	public void setSelectedBibleTag(String tag) {
		this.tag = tag;
	}

	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;

		if(adapter != null) {
			adapter.notifyDataSetChanged();
			tabLayout.setupWithViewPager(viewPager);
			tabLayout.getTabAt(0).select();
		}

		builder.getVerses().clear();
		verseList.getAdapter().notifyDataSetChanged();
		editReference.setText(builder.create().toString());
	}

	public void loadBible() {
		bible = ABT.getInstance(context).getSelectedBible(tag);

		if(bible == null)
			return;

		builder.setBible(bible);

		if(bible instanceof Downloadable) {
			((Downloadable) bible).download(new OnResponseListener() {
				@Override
				public void responseFinished() {
					selectedBibleName.setText("From " + bible.getName());

					adapter = new VersePickerPagerAdapter();
					viewPager.setAdapter(adapter);
					tabLayout.setupWithViewPager(viewPager);

					bookList.getAdapter().notifyDataSetChanged();
					chapterList.getAdapter().notifyDataSetChanged();
					verseList.getAdapter().notifyDataSetChanged();
				}
			});
		}
	}

	public Reference.Builder getReferenceBuilder() {
		return builder;
	}

	@Override
	public void onBibleSelected(Bible bible) {
		this.bible = bible;

		loadBible();
	}

	public void setOnReferenceCreatedListener(OnReferenceCreatedListener listener) {
		this.listener = listener;
	}

//TabLayout and ViewPagerAdapter
//--------------------------------------------------------------------------------------------------

	class VersePickerPagerAdapter extends PagerAdapter {
		boolean[] isRemoved = {true, true, true};
		View[] views = {bookList, chapterList, verseList};
		String[] titles = {"Books", "Chapters", "Verses"};

		public VersePickerPagerAdapter() {
			for(int i = 0; i < views.length; i++) {
				if(views[i].getParent() != null) {
					((ViewGroup) views[i].getParent()).removeAllViews();
					break;
				}
			}
		}

		public Object instantiateItem(ViewGroup collection, int position) {
			if(isRemoved[position]) {
				collection.addView(views[position]);
				isRemoved[position] = false;
			}

			return views[position];
		}

		@Override
		public int getCount() {
			return (selectionMode == SELECTION_WHOLE_CHAPTER) ? 2 : 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			isRemoved[position] = true;
		}
	}

	//Books RecyclerView
//--------------------------------------------------------------------------------------------------
	public class BookViewHolder extends RecyclerView.ViewHolder {
		Book book;

		View root;
		TextView tv;

		public BookViewHolder(View itemView) {
			super(itemView);

			root = itemView;
			tv = (TextView) itemView.findViewById(R.id.book_name);
			tv.setTextColor(bookTextColor);
		}

		public void bind(final Book book) {
			this.book = book;

			if(book == builder.getBook())
				root.setSelected(true);
			else
				root.setSelected(false);

			tv.setText(book.getName());

			root.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					builder.setBook(book);
					builder.setDefaultChapter();
					builder.setDefaultVerses();
					editReference.setText(builder.create().toString());

					bookList.getAdapter().notifyDataSetChanged();
					chapterList.getAdapter().notifyDataSetChanged();
					tabLayout.getTabAt(1).select();
				}
			});
		}
	}

	public class BooksAdapter extends RecyclerView.Adapter<BookViewHolder> {
		@Override
		public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.verse_picker_book_item, parent, false);
			return new BookViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(BookViewHolder holder, int position) {
			holder.bind((Book) bible.getBooks().get(position));
		}

		@Override
		public int getItemCount() {
			return (bible != null) ? bible.getBooks().size() : 0;
		}
	}

//Chapters RecyclerView
//--------------------------------------------------------------------------------------------------
	public class ChapterViewHolder extends RecyclerView.ViewHolder {
		int chapter;

		View root;
		TextView tv;

		public ChapterViewHolder(View itemView) {
			super(itemView);

			root = itemView;
			tv = (TextView) itemView.findViewById(R.id.chapter_name);
			tv.setTextColor(circleTextColor);
			ViewCompat.setBackgroundTintList(tv, circleBackgroundColor);
		}

		public void bind(final int chapter) {
			this.chapter = chapter;
			tv.setText(Integer.toString(chapter));

			if(chapter == builder.getChapter())
				root.setSelected(true);
			else
				root.setSelected(false);

			root.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					builder.setChapter(chapter);

					if(selectionMode == SELECTION_WHOLE_CHAPTER) {
						builder.addAllVersesInChapter();
						editReference.setText(builder.create().toString());
						if(listener != null)
							listener.onReferenceCreated(builder);
					}
					else {
						builder.setDefaultVerses();
						tabLayout.getTabAt(2).select();
						editReference.setText(builder.create().toString());
					}

					chapterList.getAdapter().notifyDataSetChanged();
					verseList.getAdapter().notifyDataSetChanged();
				}
			});
		}
	}

	public class ChaptersAdapter extends RecyclerView.Adapter<ChapterViewHolder> {
		@Override
		public ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.verse_picker_chapter_item, parent, false);
			return new ChapterViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(ChapterViewHolder holder, int position) {
			holder.bind(position+1);
		}

		@Override
		public int getItemCount() {
			return (builder.getBook() != null) ? builder.getBook().numChapters() : 0;
		}
	}

//Verses RecyclerView
//--------------------------------------------------------------------------------------------------
	public class VerseViewHolder extends RecyclerView.ViewHolder {
		int verse;

		View root;
		TextView tv;

		public VerseViewHolder(View itemView) {
			super(itemView);

			root = itemView;
			tv = (TextView) itemView.findViewById(R.id.verse_name);
			tv.setTextColor(circleTextColor);
			ViewCompat.setBackgroundTintList(tv, circleBackgroundColor);
		}

		public void bind(final int verse) {
			this.verse = verse;
			tv.setText(Integer.toString(verse));

			if(builder.getVerses().contains(Integer.valueOf(verse)))
				root.setSelected(true);
			else
				root.setSelected(false);

			root.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(selectionMode == SELECTION_ONE_VERSE) {
							builder.getVerses().clear();
							builder.getVerses().add(Integer.valueOf(verse));
							if(listener != null)
								listener.onReferenceCreated(builder);
						}
						if(selectionMode == SELECTION_MANY_VERSES) {
							if(!builder.getVerses().contains(Integer.valueOf(verse)))
								builder.getVerses().add(Integer.valueOf(verse));
							else
								builder.getVerses().remove(Integer.valueOf(verse));

							if(listener != null)
								listener.onReferenceCreated(builder);
						}

						editReference.setText(builder.create().toString());
						verseList.getAdapter().notifyDataSetChanged();
					}
				}
			);
		}
	}

	public class VersesAdapter extends RecyclerView.Adapter<VerseViewHolder> {
		@Override
		public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.verse_picker_verse_item, parent, false);
			return new VerseViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(VerseViewHolder holder, int position) {
			holder.bind(position+1);
		}

		@Override
		public int getItemCount() {
			return (builder.getBook() != null && builder.getChapter() >= 0) ? builder.getBook().numVersesInChapter(builder.getChapter()) : 0;
		}
	}
}
