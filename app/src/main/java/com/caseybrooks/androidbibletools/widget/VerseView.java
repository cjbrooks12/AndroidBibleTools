package com.caseybrooks.androidbibletools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caseybrooks.androidbibletools.R;
import com.caseybrooks.androidbibletools.basic.AbstractVerse;
import com.caseybrooks.androidbibletools.basic.Reference;
import com.caseybrooks.androidbibletools.data.Downloadable;
import com.caseybrooks.androidbibletools.data.OnResponseListener;

public class VerseView extends TextView implements OnReferenceCreatedListener {
	public static int DISPLAY_FORMATTED = 0x1;
	public static int DISPLAY_UNFORMATTED = 0x2;
	public static int DISPLAY_NORMAL = 0x4;
	public static int DISPLAY_HTML = 0x8;
	public static int DISPLAY_DEFAULT = 0x5; //formatted and normal display

	Class<? extends AbstractVerse> verseClass;

	AbstractVerse verse;
	int displayFlags;

//Constructors and Initialization
//--------------------------------------------------------------------------------------------------
	public VerseView(Context context) {
		super(context);

		initialize(null);
	}

	public VerseView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize(attrs);
	}

	public void initialize(AttributeSet attrs) {
		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.abt_verseview, 0, 0);
			setDisplayFlags(a.getInt(R.styleable.abt_verseview_verseDisplay, DISPLAY_DEFAULT));

			try {
				setVerseClass((Class<? extends AbstractVerse>) Class.forName(a.getString(R.styleable.abt_verseview_verseClass)));
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			a.recycle();
		}
	}

	@Override
	public void onReferenceCreated(Reference.Builder reference) {
		if(verseClass != null) {
			try {
				setVerse(verseClass.getConstructor(Reference.class).newInstance(reference.create()));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setVerseClass(Class<? extends AbstractVerse> verseClass) {
		this.verseClass = verseClass;
	}

	public AbstractVerse getVerse() {
		return verse;
	}

	public void setVerse(final AbstractVerse verse) {
		this.verseClass = verse.getClass();
		this.verse = verse;

		if(verse instanceof Downloadable) {
			((Downloadable) verse).download(new OnResponseListener() {
				@Override
				public void responseFinished() {
					displayText();
				}
			});
		}
		else {
			displayText();
		}
	}

	private void displayText() {
		String text;

		if(checkFlag(DISPLAY_FORMATTED)) {
			text = verse.getFormattedText();
		}
		else if(checkFlag(DISPLAY_UNFORMATTED)) {
			text = verse.getText();
		}
		else {
			text = verse.getFormattedText();
		}

		if(checkFlag(DISPLAY_HTML)) {
			setText(Html.fromHtml(text));
		}
		else if(checkFlag(DISPLAY_NORMAL)) {
			setText(text);
		}
		else {
			setText(text);
		}
	}

	public void setDisplayFlags(int flags) {
		displayFlags = flags;
	}

	private boolean checkFlag(int flag) {
		return ((displayFlags & flag) == flag);
	}
}
