package com.alan.findfamily.utils;

import com.alan.findfamily.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class DialogHelper {

	public static Dialog showProgressDialog(Context context, String message) {
		Dialog dialog = null;
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_animation_progressbar, null);
		dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(view);
		TextView messageText = (TextView) view.findViewById(R.id.widget197);
		if (message != null) {
			messageText.setText(message);
		}
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

}
