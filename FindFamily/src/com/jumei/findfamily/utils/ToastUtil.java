package com.jumei.findfamily.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	private static Toast mToast = null;

	public static void showToast(Context context, int Stringid) {
		showToast(context, context.getString(Stringid));
	}

	public static void showToast(Context context, String string) {
		if (TextUtils.isEmpty(string))
			return;
		try {
			if (null == mToast) {
				mToast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
			} else {
				mToast.setText(string);
			}
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}