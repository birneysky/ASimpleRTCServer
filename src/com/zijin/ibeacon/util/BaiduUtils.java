package com.zijin.ibeacon.util;

import java.text.DecimalFormat;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.slidingmenu.IbeaconApplication;

public class BaiduUtils {
	
	private Context context;
	private LatLng mCurrentLat;
	private Callback mCallback;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";
	/**
	 * @param context
	 * @param lat 目标坐标
	 */
	public BaiduUtils(Context context) {
		this.context = context;
		init();
	}
	
	private void init() {
		mLocationClient = ((IbeaconApplication)context.getApplicationContext()).mLocationClient;
		InitLocation();
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1200);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
//		mGeofenceClient = new GeofenceClient(context.getApplicationContext());
	}
	/**
     * 定位
     */
	public  LocationClient mLocationClient;
//	public  GeofenceClient mGeofenceClient;
	public  MyLocationListener mMyLocationListener;
	public  Vibrator mVibrator;
	
	/**
	 * 启动定位查询
	 */
	public void start(){
		mLocationClient.start();
	}
	public void stop(){
		mLocationClient.stop();
	}
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			mCurrentLat = new LatLng(location.getLatitude(),location.getLongitude());
			onResult(location);
//			mLocationClient.stop();
		}
	}
	public static abstract class Callback {
        /**
         * 获取当前位置信息
         * location.getLatitude();
		   mBaiduLong = location.getLongitude();
		      中文location.getAddrStr()
         * @param location
         */
        public abstract void onResult(BDLocation location);
    }
	
	public void setCallback(Callback call){
		mCallback = call;
	}
	
   public void onResult(BDLocation location) {
	   if(mCallback==null)return;
	   mCallback.onResult(location);
    }
   
   private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02;国测局经纬度坐标系(gcj02)，百度墨卡托坐标系(bd09)，百度经纬度坐标系(bd09ll)
		int span=1000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
   
   public double getInstance(LatLng lat){
	   if(lat==null)return 0.0;
	   DecimalFormat df = new DecimalFormat("###.000");
	   double distance = DistanceUtil.getDistance(lat, mCurrentLat);
	   String result="0";
	   if(distance>0){
		   result= df.format(distance);
	   }
	   return Double.valueOf(result);
   }
   /**
    * 获取当前坐标与目标坐标值
    * LatLng mEndLat = new LatLng(39.987628,116.314362);
        		BaiduUtils mBaidu = new BaiduUtils(MainActivity.this,mEndLat);
        		mBaidu.setCallback(new Callback() {
					@Override
					public void onResult(double distance) {
						Toast.makeText(MainActivity.this, "我们的距离:"+distance, Toast.LENGTH_LONG).show();
					}
				});
        		mBaidu.numDistance();
    */
	
}
