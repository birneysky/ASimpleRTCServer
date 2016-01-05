package com.zjjin.utils.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.slidingmenu.R;

/**
 * 此demo用来展示如何在地图上用GraphicsOverlay添加点、线、多边形、圆 
 * 同时展示如何在地图上用TextOverlay添加文字
 * 
 */
public class GeometryActivity extends Activity implements OnGetGeoCoderResultListener{

	// 地图相关
	MapView mMapView;
	BaiduMap mBaiduMap;
	// UI相关
	private EditText etSetArea;
	private Button btnSetArea, btnBack;
	private int setRadius = 500;
	private LatLng newLatLng = new LatLng(39.914773, 116.403766);
	private GeoCoder mSearch;
	private String address;
	public static boolean isAdd = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geometry);
		 
		// UI初始化
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		
		etSetArea = (EditText)findViewById(R.id.et_set_area);
		btnSetArea = (Button)findViewById(R.id.btn_set_area);
		btnBack = (Button)findViewById(R.id.btn_back);
		OnClickListener setAreaListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				setArea();
			}
		};
		OnClickListener backListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isAdd){
					Intent data = new Intent();
					data.putExtra("radius", setRadius);
					data.putExtra("lat", newLatLng.latitude);
					data.putExtra("lng", newLatLng.longitude);
					data.putExtra("address", address);
					setResult(RESULT_OK, data);
					finish();
				}else{
					finish();
				}
			}
		};
		btnSetArea.setOnClickListener(setAreaListener);
		btnBack.setOnClickListener(backListener);
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
			@Override
			public void onMapClick(LatLng latLng) {
				newLatLng = latLng;
			}
		});
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		if(!isAdd){
			//让某个控件隐藏
			btnSetArea.setVisibility(View.INVISIBLE);
			etSetArea.setVisibility(View.INVISIBLE);
		}
		getData();
		// 界面加载时添加绘制图层
		addCustomElementsDemo();
	}

	private void getData() {
		Intent intent = getIntent();
		setRadius = intent.getIntExtra("radius", 500);
		double lat = intent.getDoubleExtra("lat", 39.914773);
		double lng = intent.getDoubleExtra("lng", 116.403766);
		newLatLng = new LatLng(lat, lng);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(newLatLng);
		mBaiduMap.animateMapStatus(u);
		if(isAdd){
			// 反Geo搜索
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(newLatLng));
		}
	}

	public static void actionStart(Context context, int radius, LatLng point){
		Intent intent = new Intent(context, GeometryActivity.class);
		intent.putExtra("radius", radius);
		intent.putExtra("lat", point.latitude);
		intent.putExtra("lng", point.longitude);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		isAdd = false;
		context.startActivity(intent);
	}
	
	protected void setArea() {
		try {
			String radius = etSetArea.getText().toString();
//			if(radius == null)return;
//			if(radius.length() == 0)return;
			if(radius != null && radius.length() != 0){
				setRadius = Integer.parseInt(radius);
			}
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(newLatLng);
			mBaiduMap.animateMapStatus(u);
			// 反Geo搜索
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(newLatLng));
			mMapView.getMap().clear();
			addCustomElementsDemo();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加点、线、多边形、圆、文字
	 */
	public void addCustomElementsDemo() {
		// 添加折线
		/*LatLng p1 = new LatLng(39.97923, 116.357428);
		LatLng p2 = new LatLng(39.94923, 116.397428);
		LatLng p3 = new LatLng(39.97923, 116.437428);
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
		OverlayOptions ooPolyline = new PolylineOptions().width(10)
				.color(0xAAFF0000).points(points);
		mBaiduMap.addOverlay(ooPolyline);*/
		// 添加弧线
		/*OverlayOptions ooArc = new ArcOptions().color(0xAA00FF00).width(4)
				.points(p1, p2, p3);
		mBaiduMap.addOverlay(ooArc);*/
		// 添加圆
//		LatLng llCircle = new LatLng(39.90923, 116.447428);
		OverlayOptions ooCircle = new CircleOptions().fillColor(0x550000FF)
				.center(newLatLng).stroke(new Stroke(5, 0x33020eff))
				.radius(setRadius);
		mBaiduMap.addOverlay(ooCircle);
		// 添加圆点
		/*LatLng llDot = new LatLng(39.98923, 116.397428);
		OverlayOptions ooDot = new DotOptions().center(llDot).radius(5)
				.color(0xFF0000FF);
		mBaiduMap.addOverlay(ooDot);*/
		// 添加多边形
		LatLng pt1 = new LatLng(39.93923, 116.357428);
		LatLng pt2 = new LatLng(39.91923, 116.327428);
		LatLng pt3 = new LatLng(39.89923, 116.347428);
		LatLng pt4 = new LatLng(39.89923, 116.367428);
//		LatLng pt5 = new LatLng(39.91923, 116.387428);
		List<LatLng> pts = new ArrayList<LatLng>();
		pts.add(pt1);
		pts.add(pt2);
		pts.add(pt3);
		pts.add(pt4);
//		pts.add(pt5);
		OverlayOptions ooPolygon = new PolygonOptions().points(pts)
				.stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
		mBaiduMap.addOverlay(ooPolygon);
		// 添加文字
		/*LatLng llText = new LatLng(39.86923, 116.397428);
		OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
				.fontSize(24).fontColor(0xFFFF00FF).text("百度地图SDK").rotate(-30)
				.position(llText);
		mBaiduMap.addOverlay(ooText);*/
	}

	public void resetClick() {
		// 添加绘制元素
		addCustomElementsDemo();
	}

	public void clearClick() {
		// 清除所有图层
		mMapView.getMap().clear();
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
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	/**
	 * Geo搜索回调
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			Toast.makeText(GeometryActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}
//		mBaiduMap.clear();
//		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//				.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
//		String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
//		Toast.makeText(GeometryActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	/**
	 * 反Geo搜索回调
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			Toast.makeText(GeometryActivity.this, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
			return;
		}
//		mBaiduMap.clear();
//		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.icon_marka)));
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
//		Toast.makeText(GeometryActivity.this, result.getAddress(), Toast.LENGTH_SHORT).show();
		address = result.getAddress();
	}

}
