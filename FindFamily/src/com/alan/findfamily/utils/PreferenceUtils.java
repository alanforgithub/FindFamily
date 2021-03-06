package com.alan.findfamily.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

public class PreferenceUtils {
	
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORD = "user_password";
	public static final String IS_REMEMBER = "is_remember_user";
	
	private static void paraCheck(SharedPreferences sp, String key) {
		if (sp == null) {
			throw new IllegalArgumentException();
		}
		if (TextUtils.isEmpty(key)) {
			throw new IllegalArgumentException();
		}
	}

	public static boolean putBitmap(SharedPreferences sp, String key,
			Bitmap bitmap) {
		paraCheck(sp, key);
		if (bitmap == null || bitmap.isRecycled()) {
			return false;
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, baos);
			String imageBase64 = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			Editor e = sp.edit();
			e.putString(key, imageBase64);
			return e.commit();
		}
	}

	public static Bitmap getBitmap(SharedPreferences sp, String key,
			Bitmap defaultValue) {
		paraCheck(sp, key);
		String imageBase64 = sp.getString(key, "");
		byte[] base64Bytes = Base64.decode(imageBase64.getBytes(),
				Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		Bitmap ret = BitmapFactory.decodeStream(bais);
		if (ret != null) {
			return ret;
		} else {
			return defaultValue;
		}
	}

	public static boolean putDrawable(SharedPreferences sp, String key,
			Drawable d) {
		paraCheck(sp, key);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		((BitmapDrawable) d).getBitmap()
				.compress(CompressFormat.JPEG, 50, baos);
		String imageBase64 = new String(Base64.encode(baos.toByteArray(),
				Base64.DEFAULT));
		Editor e = sp.edit();
		e.putString(key, imageBase64);
		return e.commit();
	}

	public static Drawable getDrawable(SharedPreferences sp, String key,
			Drawable defaultValue) {
		paraCheck(sp, key);
		String imageBase64 = sp.getString(key, "");
		byte[] base64Bytes = Base64.decode(imageBase64.getBytes(),
				Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		Drawable ret = Drawable.createFromStream(bais, "");
		if (ret != null) {
			return ret;
		} else {
			return defaultValue;
		}
	}

	public static boolean putObject(SharedPreferences sp, String key, Object obj) {
		paraCheck(sp, key);
//		if (obj == null) {
//			Editor e = sp.edit();
//			e.putString(key, "");
//			return e.commit();
//		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(obj);
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			String objectBase64 = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			Editor e = sp.edit();
			e.putString(key, objectBase64);
			return e.commit();
		//}
	}

	public static Object getObject(SharedPreferences sp, String key,
			Object defaultValue) {
		paraCheck(sp, key);
		String objectBase64 = sp.getString(key, "");
		byte[] base64Bytes = Base64.decode(objectBase64.getBytes(),
				Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return defaultValue;
		} catch (IOException e) {
			e.printStackTrace();
			return defaultValue;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	public static boolean isObjectEqual(SharedPreferences sp, String key,
			Object newValue) {
		paraCheck(sp, key);
		if (newValue == null) {
			return false;
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(newValue);
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			String newValueBase64 = new String(Base64.encode(
					baos.toByteArray(), Base64.DEFAULT));
			String oldValueBase64 = sp.getString(key, "");
			return newValueBase64.equals(oldValueBase64);
		}
	}
}
