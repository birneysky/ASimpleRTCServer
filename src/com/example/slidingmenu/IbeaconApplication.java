package com.example.slidingmenu;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.zjjin.entity.User;
import com.zjjin.utils.Consts;

public class IbeaconApplication extends Application {

	private List<Activity> activityList=new LinkedList<Activity>();
	private static IbeaconApplication instance;
	public boolean isSupport=true;
	//baidu location
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public Vibrator mVibrator;
	private Handler handler = null;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(getApplicationContext());
		AVOSCloud.initialize(IbeaconApplication.this,Consts.SMS_AppId,Consts.SMS_AppKey); 
		checkBLe();
		initLocation();
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public Handler getHandler(){
		return this.handler;
	}
	/**
	 * 百度地图定位 
	 */
	 private void initLocation() {
		 mLocationClient = new LocationClient(this.getApplicationContext());
		 mGeofenceClient = new GeofenceClient(getApplicationContext());
	     mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
	}
	/**
	 * 判断设备是否支持BLE
	 */
	 private void checkBLe() {
		 if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			 Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			 isSupport=false;
//	         	IbeaconApplication.this.exit();
		 }
	 }
	
	 public IbeaconApplication()
     {
     }
     /**
      * 单例模式中获取唯一的ExitApplication 实例
      * @return
      */
     public static IbeaconApplication getInstance(){
	     if(null == instance){
	    	 instance = new IbeaconApplication();
	     }
	     return instance;
     }
     /**
      * 添加Activity 到容器中
      * @param activity
      */
     public void addActivity(Activity activity){
    	 if(!activityList.contains(activity)){
    		 activityList.add(activity);
    	 }
     }
     /**
      *  移除某一个Activity
      * @param activity
      */
     public void removeAcitvity(Activity activity){
    	 if(activityList.contains(activity)){
    		 activityList.remove(activityList.indexOf(activity));
    	 }
     }
     /**
      * 退出应用程序：遍历所有Activity 并finish
      */
     public void exit(){
	     for(Activity activity:activityList){
	    	 activity.finish();
	     }
	     System.exit(0);
     }
}