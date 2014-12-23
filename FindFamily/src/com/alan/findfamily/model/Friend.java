package com.alan.findfamily.model;

import cn.bmob.v3.BmobObject;

public class Friend extends BmobObject {

	private String Uid;
	private String name;

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Friend [Uid=" + Uid + ", name=" + name + "]";
	}

}
