package com.alan.findfamily.ui;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

import com.alan.findfamily.R;
import com.alan.findfamily.utils.Config;
import com.alan.findfamily.utils.ToastUtil;
import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private SDKReceiver mReceiver;

	private static String TAG = "tag";

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(TAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ToastUtil.showToast(context,
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ToastUtil.showToast(context, "网络出错");
			} else {
				ToastUtil.showToast(context, "验证成功");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		initBmobInfo();
		initView();

	}

	private void initBmobInfo() {
		// 初始化Bmob，，第二个参数是Application ID
		Bmob.initialize(this, Config.APP_ID);
		BmobInstallation.getCurrentInstallation(this).save();// 初始化消息推送的sdk
		BmobPush.startWork(this, Config.APP_ID);// 启动推动服务
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != mReceiver) {
			unregisterReceiver(mReceiver);
		}

	}

	private void initView() {
		findViewById(R.id.rootPlan).setOnClickListener(this);
		findViewById(R.id.location).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.rootPlan:
			intent.setClass(MainActivity.this, RoutePlanAactivity.class);
			startActivity(intent);
			break;
		case R.id.location:
			intent.setClass(MainActivity.this, LocationActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}
