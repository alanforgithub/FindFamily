package com.alan.findfamily.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.alan.findfamily.R;
import com.alan.findfamily.model.User;
import com.alan.findfamily.utils.Config;
import com.alan.findfamily.utils.DialogHelper;
import com.alan.findfamily.utils.PreferenceUtils;
import com.alan.findfamily.utils.ToastUtil;
import com.alan.findfamily.view.CheckBoxNoPersistentText;
import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private SDKReceiver mReceiver;

	private static String TAG = "tag";

	private EditText mUserNameEdit;

	private EditText mUserPwd;

	private CheckBoxNoPersistentText autoLoginCheckBox;

	private TextView forgetPwd;

	private Button loginBtn;

	private TextView registerBtn;

	private Dialog loginDialog;

	/**
	 * 是否记住密码
	 */
	private boolean isRememberPwd = false;

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
		mUserNameEdit = (EditText) findViewById(R.id.username);
		mUserPwd = (EditText) findViewById(R.id.password);
		autoLoginCheckBox = (CheckBoxNoPersistentText) findViewById(R.id.autoLogin);
		forgetPwd = (TextView) findViewById(R.id.forgetPassword);
		forgetPwd.setOnClickListener(this);
		loginBtn = (Button) findViewById(R.id.loginButton);
		loginBtn.setOnClickListener(this);
		registerBtn = (TextView) findViewById(R.id.registerButton);
		registerBtn.setOnClickListener(this);

		User user = getUserInfo();
		if (null != user && null != user.getUserName()
				&& null != user.getPassWord()) {
			mUserNameEdit.setText(user.getUserName());
			mUserPwd.setText(user.getPassWord());
		}

		autoLoginCheckBox.setChecked(isRememberPwd);

	}

	/**
	 * 登录
	 */
	private void login() {

		String userName = mUserNameEdit.getText().toString().trim();
		String pwd = mUserPwd.getText().toString().trim();

		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
			ToastUtil.showToast(getApplicationContext(), "账号或密码为空!!!");
			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (null == loginDialog)
					loginDialog = DialogHelper.showProgressDialog(
							LoginActivity.this, getString(R.string.logining));
				loginDialog.show();
			}
		});

		final User user = new User();
		user.setUserName(userName);
		user.setPassWord(pwd);
		user.setLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date()));

		if (autoLoginCheckBox.isChecked()) {
			saveUserInfo(user);
		} else {
			saveUserInfo(null);
		}
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo(User.USER_NAME, userName);
		query.addWhereEqualTo(User.PASSWORD, pwd);
		query.findObjects(LoginActivity.this, new FindListener<User>() {

			@Override
			public void onSuccess(List<User> list) {
				if (null == list || list.size() <= 0) {
					ToastUtil.showToast(getApplicationContext(), "账号或密码错误");
					loginDialog.cancel();
				} else {
					loginAccout(list.get(0).getObjectId(), user);
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				ToastUtil.showToast(getApplicationContext(), "账号或密码错误");
				loginDialog.cancel();
			}
		});
	}

	/**
	 * 记住用户信息
	 */
	private void saveUserInfo(User user) {

		if (null != user) {
			PreferenceUtils.putObject(FamilyApplication.getInst()
					.getSharedPreferences(), PreferenceUtils.USER_NAME, user
					.getUserName());
			PreferenceUtils.putObject(FamilyApplication.getInst()
					.getSharedPreferences(), PreferenceUtils.USER_PASSWORD,
					user.getPassWord());

		} else {
			PreferenceUtils.putObject(FamilyApplication.getInst()
					.getSharedPreferences(), PreferenceUtils.USER_NAME, null);
			PreferenceUtils.putObject(FamilyApplication.getInst()
					.getSharedPreferences(), PreferenceUtils.USER_PASSWORD,
					null);
		}
		PreferenceUtils.putObject(FamilyApplication.getInst()
				.getSharedPreferences(), PreferenceUtils.IS_REMEMBER,
				autoLoginCheckBox.isChecked());
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	private User getUserInfo() {
		User user = new User();
		user.setUserName((String) PreferenceUtils.getObject(FamilyApplication
				.getInst().getSharedPreferences(), PreferenceUtils.USER_NAME,
				null));

		user.setPassWord((String) PreferenceUtils.getObject(FamilyApplication
				.getInst().getSharedPreferences(),
				PreferenceUtils.USER_PASSWORD, null));

		isRememberPwd = (Boolean) PreferenceUtils.getObject(FamilyApplication
				.getInst().getSharedPreferences(), PreferenceUtils.IS_REMEMBER,
				false);

		return user;
	}

	/**
	 * 登录账号
	 * 
	 * @param user
	 */
	private void loginAccout(String objectId, User user) {
		user.update(getApplicationContext(), objectId, new UpdateListener() {

			@Override
			public void onSuccess() {
				ToastUtil.showToast(getApplicationContext(), "登录成功");
				loginDialog.cancel();
				Intent intent = new Intent(LoginActivity.this,
						LocationActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ToastUtil.showToast(getApplicationContext(), "登录失败,请重新登录");
				loginDialog.cancel();
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.loginButton:// 登录
			login();
			break;
		case R.id.registerButton: // 注册
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.forgetPassword: // 忘记密码
			break;

		default:
			break;
		}
	}
}
