package com.alan.findfamily.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.alan.findfamily.R;
import com.alan.findfamily.model.User;
import com.alan.findfamily.utils.NetworkUtils;
import com.alan.findfamily.utils.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 注册页面
 * 
 * @author guangbingw
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener {
	private EditText userName;
	private EditText passWord;
	private EditText confirmPwd;
	private Button register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		userName = (EditText) findViewById(R.id.username);
		passWord = (EditText) findViewById(R.id.password);
		confirmPwd = (EditText) findViewById(R.id.checkPassword);
		register = (Button) findViewById(R.id.registerButton);
		register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.registerButton) { // 注册
			checkAccout();
		}
	}

	/**
	 * 检查注册信息
	 */
	private void checkAccout() {

		if (!NetworkUtils.isNetworkAvaliable(RegisterActivity.this)) {
			ToastUtil
					.showToast(getApplicationContext(), R.string.network_error);
			return;
		}

		String inputUserName = userName.getText().toString().trim();
		String inputPwd = passWord.getText().toString().trim();
		String inputConfirmPwd = confirmPwd.getText().toString().trim();

		// 用户名为null
		if (TextUtils.isEmpty(inputUserName)) {
			ToastUtil.showToast(getApplicationContext(),
					R.string.user_name_null);
			return;
		}

		// 密码为null
		if (TextUtils.isEmpty(inputPwd)) {
			ToastUtil
					.showToast(getApplicationContext(), R.string.password_null);
			return;
		}

		// 确认密码为null
		if (TextUtils.isEmpty(inputConfirmPwd)) {
			ToastUtil.showToast(getApplicationContext(),
					R.string.input_confirm_pwd);
			return;
		}

		// 密码不一致
		if (!inputPwd.equals(inputConfirmPwd)) {
			ToastUtil.showToast(getApplicationContext(),
					R.string.pwd_inconformity);
			return;
		}

		final User user = new User();
		user.setUserName(inputUserName);
		user.setPassWord(inputPwd);
		user.setRegisterTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date()));

		BmobQuery<User> bmobQuery = new BmobQuery<User>();
		bmobQuery.addWhereEqualTo(User.USER_NAME, inputUserName);

		bmobQuery.findObjects(getApplicationContext(),
				new FindListener<User>() {

					@Override
					public void onSuccess(List<User> userList) {
						if (null == userList || userList.size() <= 0) {
							registerAccout(user);
						} else {
							ToastUtil.showToast(getApplicationContext(),
									"用户名已被占用！！！");
						}

					}

					@Override
					public void onError(int arg0, String arg1) {
						registerAccout(user);
					}
				});
	}

	// 注册用户
	private void registerAccout(User user) {
		user.save(getApplicationContext(), new SaveListener() {

			@Override
			public void onSuccess() {
				ToastUtil.showToast(getApplicationContext(), "注册成功");

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ToastUtil.showToast(getApplicationContext(), "注册失败，请重新注册");

			}
		});
	}

}
