package com.alan.findfamily.ui;

import com.alan.findfamily.R;
import com.alan.findfamily.utils.ToastUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 此页面用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制 同时展示如何进行节点浏览并弹出泡泡
 * 
 */
public class RoutePlanAactivity extends Activity implements
		OnGetRoutePlanResultListener, BaiduMap.OnMapClickListener,
		OnClickListener {

	private EditText startEdit;
	private EditText destEdit;
	private Button searchBut;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private RoutePlanSearch mRoutePlanSearch;
	private static String TAG = "tag";
	int nodeIndex = -1;//节点索引,供浏览节点时使用
	private DrivingRouteLine route;
	OverlayManager routeOverlay = null;
	boolean useDefaultIcon = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_map);
		setTitle("路线规划");
		initView();

		// 获取地图
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapClickListener(this);

		/**
		 * 初始化搜索模块，注册监听事件
		 */
		mRoutePlanSearch = RoutePlanSearch.newInstance();
		mRoutePlanSearch.setOnGetRoutePlanResultListener(this);

	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.map);
		startEdit = (EditText) findViewById(R.id.startPoint_edit);
		destEdit = (EditText) findViewById(R.id.destPoint_edit);
		searchBut = (Button) findViewById(R.id.search);
		searchBut.setOnClickListener(this);
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.showToast(getApplicationContext(), "抱歉未找到结果");
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		Log.i(TAG, "onGetTransitRouteResult = "+arg0.toString());

	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		Log.i(TAG, "onGetWalkingRouteResult = "+arg0.toString());

	}
	
    //定制驾车路线的RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            
			if (useDefaultIcon ) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

	@Override
	public void onMapClick(LatLng arg0) {
		Log.i(TAG, "onMapClick = "+arg0.toString());

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		Log.i(TAG, "onMapPoiClick = "+arg0.toString());
		return false;
	}

	/**
	 * 规划路线
	 */
	private void planRoute() {
		String start = startEdit.getText().toString();
		String end = destEdit.getText().toString();

		if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
			ToastUtil.showToast(RoutePlanAactivity.this, "起点或终点为null");
			return;
		}

		// 设置起点和终点信息，对于公交搜索 来说城市名无意义
		PlanNode startNode = PlanNode.withCityNameAndPlaceName("北京", start);
		PlanNode endNode = PlanNode.withCityNameAndPlaceName("北京", end);

		// 以驾车模式进行搜索
		mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(
				startNode).to(endNode));

		// 步行模式搜索
		// mRoutePlanSearch.walkingSearch(new
		// WalkingRoutePlanOption().from(startNode).to(endNode));

		// 步行模式进行搜索
		// mRoutePlanSearch.transitSearch(new
		// TransitRoutePlanOption().from(startNode).to(endNode).city("北京"));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:// 搜索
			mBaiduMap.clear();
			planRoute();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mRoutePlanSearch.destroy();
		mMapView.onDestroy();
		super.onDestroy();
	}
}
