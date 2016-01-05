package com.example.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.utils.Consts;
import com.zjjin.utils.LogUtil;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class BaiduMapActivity extends Activity implements OnClickListener{
//应用位置：1，主页“查看位置”
	private static final String TAG = BaiduMapActivity.class.getSimpleName();
	// 定位相关
	LocationClient mLocClient;//定位SDK的核心类
	public MyLocationListenner myListener = new MyLocationListenner();//定义监听类
//	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button confirmButton;
	ImageView requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位
	
	private double mBaiduLat=39.963175;//39.963175, 116.400244
	private double mBaiduLot=116.400244;
	
	public static final int REQUEST_BAIDU_LOCATION=1;
	private String locationStr="";
	private GeoCoder mPoiSearch;
	private String type;
	private String addr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		IbeaconApplication.getInstance().addActivity(this);
		confirmButton = (Button) findViewById(R.id.button1);
		requestLocButton = (ImageView) findViewById(R.id.btnGpsLocation);
		confirmButton.setText("返回");
		confirmButton.setOnClickListener(this);
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		getData();
		initLocation();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1://定位确定OK返回
			if ("".equals(locationStr) || locationStr.length() == 0) {
				Toast.makeText(getApplicationContext(), "正在定位请稍后...", Toast.LENGTH_SHORT).show();
			} else {
				Intent data = new Intent();
				data.putExtra("locationStr", locationStr);
				data.putExtra("lat",mBaiduLat);
				data.putExtra("lot",mBaiduLot);
				setResult(REQUEST_BAIDU_LOCATION, data);
				finish();
			}
			break;
		case R.id.btnGpsLocation: //GPS定位
//			initLocation();
			break;
		}
	}
	
	private void getData() {
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		mBaiduLat = intent.getDoubleExtra("mBaiduLat", 39.963175);
		mBaiduLot = intent.getDoubleExtra("mBaiduLot", 116.400244);
		addr = intent.getStringExtra("addr");
		if(Consts.EXTRA_BAIDUMAP_TYPE_VIEW_POSITION.equals(type)){//查看位置
			mBaiduLat = intent.getDoubleExtra("mBaiduLat", 39.963175);
			mBaiduLot = intent.getDoubleExtra("mBaiduLot", 116.400244);
			addr = intent.getStringExtra("addr");
			LatLng ptCenter = new LatLng(mBaiduLat, mBaiduLot);
			searchByMap(ptCenter,addr);
		}
		else{
			if(isFirstLoc){
//				initLocation();//开启定位
//				confirmButton.setVisibility(View.VISIBLE);
			}else{
				LatLng ptCenter = new LatLng(mBaiduLat, mBaiduLot);
//				confirmButton.setVisibility(View.GONE);
				searchByMap(ptCenter,addr);
			}
		}
	}

	/**
	 * 地理编码与反地理编码
	 * @param ptCenter 要检索的地理编码
	 * @param addr 要检索的地址
	 */
	private void searchByMap(LatLng ptCenter,String addr) {
		// 创建地理编码检索实例 
		mPoiSearch = GeoCoder.newInstance();
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
			
			// 地理编码查询结果回调函数
		    public void onGetGeoCodeResult(GeoCodeResult result) {  
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
		        	// 没有检测到结果
		        	Utils.showMsg(BaiduMapActivity.this, "没有检索到结果 ");
		        	return;
		        }  
		        mBaiduMap.clear();
				mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
						 .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
		    }  
		    // 反地理编码查询结果回调函数
		    @Override  
		    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
		        	Utils.showMsg(BaiduMapActivity.this, "没有找到检索结果");  
		        	return;
		        } 
		        mBaiduMap.clear();
				mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
						 .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
		        //获取反向地理编码结果  
		    }  
		};
		// 设置地理编码检索监听者
		mPoiSearch.setOnGetGeoCodeResultListener(listener);
		if(ptCenter!=null ){
			mPoiSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
		}
		if(addr!=null)
			mPoiSearch.geocode(new GeoCodeOption().city("北京").address(addr));
		
	}

	private void initLocation() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(16);
		mBaiduMap.animateMapStatus(status);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1200);//设置时间间隔
		option.setIsNeedAddress(true);
		
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

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
					.direction(100) //方向
					.latitude(location.getLatitude()) //
					.longitude(location.getLongitude())
					.build();
			// 设置定位数据
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
			Toast.makeText(getApplicationContext(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				locationStr = location.getAddrStr();
				mBaiduLat = location.getLatitude();
				mBaiduLot = location.getLongitude();
			}
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
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.unRegisterLocationListener(myListener);  
		if(mLocClient!=null)
			mLocClient.stop();
		// 退出时销毁定位
		mMapView.onDestroy();
		mMapView = null;
	}
	
	/**
	 * 开始定位
	 */
	private void startLocation() {
		mLocClient.start();
	}

}
