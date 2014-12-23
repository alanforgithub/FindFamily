package com.alan.findfamily.ui;

import cn.bmob.v3.listener.SaveListener;

import com.alan.findfamily.R;
import com.alan.findfamily.model.Location;
import com.alan.findfamily.model.User;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jumei.findfamily.utils.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 使用定位功能之前必须在清单文件中注册此服务 <service android:name="com.baidu.location.f"
 * android:enabled="true" android:process=":remote" > </service>
 * 
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 * @author guangbingw
 * 
 */
public class LocationActivity extends Activity implements
		OnGetGeoCoderResultListener {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位

	private Location mCurrentPoint;// 用户的当前信息

	private GeoCoder mSearch; // 地图的搜索模块

	private static String TAG = "position";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton = (Button) findViewById(R.id.button1);
		requestLocButton.setText("普通");

		requestLocButton.setOnClickListener(btnClickListener);

		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioButtonListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.defaulticon) {
					// 传入null则，恢复默认图标
					mCurrentMarker = null;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, null));
				}
				if (checkedId == R.id.customicon) {
					// 修改为自定义marker
					mCurrentMarker = BitmapDescriptorFactory
							.fromResource(R.drawable.icon_geo);
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));

				}
			}
		};
		group.setOnCheckedChangeListener(radioButtonListener);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		initMap();
	}

	private void initMap() {
		// 初始化地图
		mBaiduMap = mMapView.getMap();

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 初始化搜索模块
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

	}

	OnClickListener btnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (mCurrentMode) {
			case NORMAL:
				requestLocButton.setText("跟随");
				mCurrentMode = LocationMode.FOLLOWING;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			case COMPASS:
				requestLocButton.setText("普通");
				mCurrentMode = LocationMode.NORMAL;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			case FOLLOWING:
				requestLocButton.setText("罗盘");
				mCurrentMode = LocationMode.COMPASS;
				mBaiduMap
						.setMyLocationConfigeration(new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker));
				break;
			}
		}
	};

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}

			if (null == mCurrentPoint) {
				mCurrentPoint = new Location();
			}

			mCurrentPoint.setLongitude(location.getLongitude());
			mCurrentPoint.setLatitude(location.getLatitude());

			// 经纬度信息
			Log.i(TAG, "当前信息 = " + mCurrentPoint.toString()+",,"+location.getCity());

			
			
			searchWithLatLng(new LatLng(39.904965,
					116.327764));

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mSearch.destroy();
		super.onDestroy();
	}

	/**
	 * 通过经纬度查找信息
	 * 
	 * @param ptCenter
	 *            经纬度信息
	 */
	private void searchWithLatLng(LatLng ptCenter) {

		// LatLng ptCenter = new LatLng(locationPoint.getLongitude(),
		// locationPoint.getLatitude());
		// 反Geo搜索
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}

	/**
	 * 通过位置名称进行搜索
	 * 
	 * @param geoCodeOption
	 *            位置信息
	 */
	private void searchWithPosition(GeoCodeOption geoCodeOption) {

		// GeoCodeOption mGeoCodeOption = new GeoCodeOption();
		// mGeoCodeOption.city(editCity.getText().toString()); //城市信息，北京
		// mGeoCodeOption.address(editGeoCodeKey.getText().toString());
		// //位置信息，海淀区上地十街11号

		mSearch.geocode(geoCodeOption);
	}

	/**
	 * 通过位置搜索位置，并得到经纬度信息
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		// mBaiduMap.clear();
		// mBaiduMap.addOverlay(new
		// MarkerOptions().position(result.getLocation())
		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.icon_marka)));
		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
		// .getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);

		Log.i(TAG, "通过位置名称搜索位置信息==" + strInfo);

	}

	/**
	 * 通过经纬度搜素位置，并得到位置信息
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		// mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
		// .getLocation()));

		Log.i(TAG,
				"通过经纬度搜索位置信息==" + result.getAddress() + ",,"
						+ result.getAddressDetail().city + ",,,,"
						+ result.getLocation().toString());
		
		
		User user = new User();
		user.setUserId("123456");
		user.setUserName("alan");
		Location loc = new Location();
		loc.setLatitude(result.getLocation().longitude);
		loc.setLongitude(result.getLocation().longitude);
		loc.setLocationName(result.getAddress());
		//user.setCurrentLocation(loc);
		
		user.save(LocationActivity.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ToastUtil.showToast(getApplicationContext(), "成功");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ToastUtil.showToast(getApplicationContext(), "失败");
			}
		});
		

	}
}
