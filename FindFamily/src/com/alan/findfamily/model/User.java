package com.alan.findfamily.model;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
	
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "passWord";
	public static final String REGISTER_TIME = "registerTime";
	public static final String LOGIN_TIME = "loginTime";
	public static final String LOGOUT_TIME = "logoutTime";
	
	private String userName;
	private String passWord;
	private String registerTime;
	private String loginTime;// 登录时间
	private String logoutTime;// 退出时间

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(String logoutTime) {
		this.logoutTime = logoutTime;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", passWord=" + passWord
				+ ", registerTime=" + registerTime + ", loginTime=" + loginTime
				+ ", logoutTime=" + logoutTime + "]";
	}

}
