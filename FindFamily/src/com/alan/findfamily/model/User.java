package com.alan.findfamily.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
	
	private String userId;
	private String userName;
	/**
	 * 现在的位置
	 */
	private Location currentLocation;
	/**
	 * 上一次的位置
	 */
	private Location lastLocation;

	private List<Friend> friends;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	
	
	
}
