package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class VersePicker extends LinearLayout {
//Data Members
//--------------------------------------------------------------------------------------------------
	Context context;

	Bible bible;
	String tag;
	EditText editReference;

	ViewPager viewPager;
	TabLayout tabLayout;
	VersePickerPagerAdapter adapter;

	RecyclerView bookList;
	RecyclerView chapterList;
	RecyclerView verseList;
	LinearLayout verseListLayout;

	Reference.Builder builder;

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
		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.abt_versepicker, 0, 0);
			this.tag = a.getString(R.styleable.abt_versepicker_vp_tag);
			a.recycle();
		}

		bible = ABT.getInstance(context).getSelectedBible(tag);
		builder = new Reference.Builder();
		builder.setFlag(Reference.Builder.PREVENT_AUTO_ADD_VERSES_FLAG);

		LayoutInflater.from(context).inflate(R.layout.verse_picker, this);

		editReference = (EditText) findViewById(R.id.editReference);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

		bookList = (RecyclerView) findViewById(R.id.book_list);
		chapterList = (RecyclerView) findViewById(R.id.chapter_list);
		verseList = (RecyclerView) findViewById(R.id.verse_list);
		verseListLayout = (LinearLayout) findViewById(R.id.verse_list_layout);

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
		chapterList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.GRID_STROKE));
		verseList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.GRID_STROKE));

		if(bible != null) {
			loadBible();
		}
	}

	public String getSelectedBibleTag() {
		return tag;
	}

	public void setSelectedBibleTag(String tag) {
		this.tag = tag;
	}

	public void loadBible() {
		bible = ABT.getInstance(context).getSelectedBible(tag);

		if(bible == null)
			return;

		if(bible instanceof Downloadable) {
			((Downloadable) bible).download(
				new OnResponseListener() {
					@Override
					public void responseFinished() {
						adapter = new VersePickerPagerAdapter();
						viewPager.setAdapter(adapter);
						tabLayout.setupWithViewPager(viewPager);

						bookList.getAdapter().notifyDataSetChanged();
						chapterList.getAdapter().notifyDataSetChanged();
						verseList.getAdapter().notifyDataSetChanged();
					}
				}
			);
		}
	}

///TabLayout and ViewPagerAdapter
//--------------------------------------------------------------------------------------------------

	class VersePickerPagerAdapter extends PagerAdapter {
		boolean[] isRemoved = {true, true, true};
		View[] views = {bookList, chapterList, verseListLayout};
		String[] titles = {"Books", "Chapters", "Verses"};

		public VersePickerPagerAdapter() {
			ViewGroup parent = (ViewGroup) views[0].getParent();
			parent.removeView(views[0]);
			parent.removeView(views[1]);
			parent.removeView(views[2]);
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
			return 3;
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
					editReference.setText(book.getName());

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

					editReference.setText(builder.getBook().getName() + " " + (chapter));

					chapterList.getAdapter().notifyDataSetChanged();
					verseList.getAdapter().notifyDataSetChanged();
					tabLayout.getTabAt(2).select();
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

						if(!builder.getVerses().contains(Integer.valueOf(verse)))
							builder.getVerses().add(Integer.valueOf(verse));
						else
							builder.getVerses().remove(Integer.valueOf(verse));

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
