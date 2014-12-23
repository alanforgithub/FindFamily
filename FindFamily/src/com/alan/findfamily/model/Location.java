package com.alan.findfamily.model;

import cn.bmob.v3.BmobObject;

/**
 * 此类用于描述位置的经纬度信息
 * 
 * @author guangbingw
 * 
 */
public class Location extends BmobObject{

	// 经度信息
	private double longitude;

	// 纬度信息
	private double latitude;

	/**
	 * 位置名称
	 */
	private String locationName;

	/**
	 * 位置获取的时间
	 */
	private String locationTime;

	
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationTime() {
		return locationTime;
	}

	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "Point [longitude=" + longitude + ", latitude=" + latitude + "]";
	}

}
