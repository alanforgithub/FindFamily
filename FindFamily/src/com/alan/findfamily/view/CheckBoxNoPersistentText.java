package com.alan.findfamily.view;

import com.alan.findfamily.R;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CheckBoxNoPersistentText extends CheckBox {
	public CheckBoxNoPersistentText(Context context) {
		super(context);
		init();
	}

	public CheckBoxNoPersistentText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CheckBoxNoPersistentText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setButtonDrawable(R.drawable.checkbox);
	}

	@Override
	public void onRestoreInstanceState(final Parcelable state) {

		final CharSequence text = getText(); // the text has been resolved anew

		super.onRestoreInstanceState(state); // this restores the old text

		setText(text); // this overwrites the restored text with the newly
						// resolved text

	}
}
