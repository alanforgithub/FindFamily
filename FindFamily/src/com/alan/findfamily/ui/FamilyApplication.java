package com.alan.findfamily.ui;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FamilyApplication extends Application {
	private static FamilyApplication sInst;
	private SharedPreferences mPrefs;

	@Override
	public void onCreate() {
		super.onCreate();
		setInst(this);
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	private void setInst(FamilyApplication ints) {
		sInst = ints;
	}

	public static FamilyApplication getInst() {
		return sInst;
	}

	public SharedPreferences getSharedPreferences() {
		if (null == mPrefs) {
			mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		}

		return mPrefs;

	}

}
