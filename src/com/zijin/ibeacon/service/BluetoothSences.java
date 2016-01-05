package com.zijin.ibeacon.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.slidingmenu.AddBeaconActivity;
import com.example.slidingmenu.IbeaconApplication;
import com.zijin.dao.IbeaconAreaDao;
import com.zijin.dao.IbeaconDao;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.util.BaiduUtils;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.Tracker;
import com.zjjin.entity.TrackerSafityArea;
import com.zjjin.entity.iBeaconClass;
import com.zjjin.entity.iBeaconClass.iBeacon;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

@SuppressLint("NewApi")
public class BluetoothSences extends Thread {
	private String TAG = BluetoothSences.class.getSimpleName();
	private Handler mHanlder = new Handler();
	private BluetoothAdapter mBluetoothAdapter;
//	private static Map<String,Long> mTimestmp=Collections.synchronizedMap(new HashMap<String,Long>());//定时场景阀值
	private static List<String> mSences =new ArrayList<String>();
//	private static boolean mIsInScene=false;
	private CallBack callback;
	private IbeaconDao mIbeaconDao;
	private IbeaconAreaDao mSafetyAreaDao;
	private ArrayList<Tracker> mTrackers = new ArrayList<Tracker>();//启用的设备
	private Map<Tracker, ArrayList<TrackerSafityArea>> mMapAreas = new HashMap<Tracker,ArrayList<TrackerSafityArea>>(); //启用的设备对应的安全区域
//	private Map<String, ArrayList<TrackerSafityArea>> mMapAddrAreas = new HashMap<String, ArrayList<TrackerSafityArea>>();//每个设备地址对应的安全区域集合
	public static boolean mIsScan=true;
	private static final int SCAN_PERIOD = 3000;//每隔1200毫秒扫描一次周边场景
	private static final int SCAN_TIME=3000;
	private Handler handler;
	private boolean tagNet = false;
	//百度地图获取当前位置
	private Context context;
	private Activity activity;
	private LatLng currentLocation;
	private BaiduUtils mBaUtils;
	public void setCallback(CallBack callb){
		callback = callb;
	}
	
	/**
	 * 场景线程
	 * @param mAdapter
	 * @param context
	 */
	public  BluetoothSences(BluetoothAdapter mAdapter, Activity activity, Handler handler){
//		usersp = context.getSharedPreferences(ConstsUser.USERSPNAME, 0);
		this.mBluetoothAdapter = mAdapter;
		this.context = activity;
		this.activity = activity;
		mIbeaconDao = IbeaconDao.getInstance(context);
		mSafetyAreaDao = IbeaconAreaDao.getInstance(context);
		mTrackers = getIbeacons();
		mMapAreas = getSafetyArea();
//		initTimesMap();
		this.handler = handler;
	}
	static{
		mSences.add("D0:5F:B8:32:2E:41");//添加场景
	}
	/** 
	 * 为所有设备初始化计时
	 * 存储设备 计时用
	 */
	/*private void initTimesMap() {
		for(Tracker tracker :mTrackers){
			mTimestmp.put(tracker.getDevice_addr(), System.currentTimeMillis());
		}
	}*/
	/**
	 * 从数据库获取所有开启的设备
	 * 从数据库获取数据 getIbeacons
	 */
	private ArrayList<Tracker> getIbeacons() {
		ArrayList<Tracker> mTrackers = null;// = new ArrayList<Tracker>();
		mTrackers = mIbeaconDao.findBeaconsByEnable("1");//获取所有开启设备的
		if(mTrackers != null){
			Log.i(TAG, "获取到开启的设备数:"+mTrackers.size());
			/*for(Tracker tracker :mTrackers){
				mTimestmp.put(tracker.getDevice_addr(), System.currentTimeMillis());
			}*/
		}
		return mTrackers;
	}
	/**
	 * 获取开启的安全区域？
	 * 从数据库获取数据 getLocation
	 */
	private Map<Tracker, ArrayList<TrackerSafityArea>> getSafetyArea() {
//		ArrayList<TrackerSafityArea> areas = new ArrayList<TrackerSafityArea>();
		Map<String, ArrayList<TrackerSafityArea>> mMapAddrAreas = new HashMap<String, ArrayList<TrackerSafityArea>>();//每个address对应的安全区域
		Map<Tracker, ArrayList<TrackerSafityArea>> mMapAreas = new HashMap<Tracker,ArrayList<TrackerSafityArea>>();
		for(Tracker tracker:mTrackers){
			ArrayList<TrackerSafityArea> areatemp = new ArrayList<TrackerSafityArea>();
			areatemp = mSafetyAreaDao.getAreasByBeaconID(tracker.getId());
			mMapAreas.put(tracker, areatemp);
			mMapAddrAreas.put(tracker.getDevice_addr(), areatemp);
		}
		Log.i(TAG, "获取到的area:"+mMapAreas.size());
//		this.mMapAddrAreas = mMapAddrAreas;
		return mMapAreas;
	}
	private void ScanDeivce(boolean enable) {
		try {
			Log.i(TAG, "开始查找周边场景：enable = "+enable+", ThreadId:"+getThreadGroup());
			if(mBluetoothAdapter!=null){
				if (enable) {
					mHanlder.postDelayed(new Runnable() {
						@Override
						public void run() {
							mBluetoothAdapter.stopLeScan(mLeScanCallback);
						}
					}, SCAN_TIME);
					mBluetoothAdapter.startLeScan(mLeScanCallback);
				}else{
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}else{
				final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
				mBluetoothAdapter = bluetoothManager.getAdapter();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	public BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecords) {
			final iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecords);
        	if(!ibeacon.proximityUuid.equalsIgnoreCase(Consts.UUID)){
        		return;
        	}
			String device_mac = device.getAddress();
			if(device_mac == null || device_mac.length() == 0)	return;
/*			首先，要先判断和大beacon的距离，
			如果在大beacon的控制范围内的话，防丢模式自动切换成全自动模式，并且根据大beacon设置的防丢距离来判断是否需要报警；
			如果不在大beacon的控制范围内，那么就根据手动设置的防丢距离来判断是否需要报警
*/			
			//扫描到场景，模式主动切换到自动，并且根据大beacon设置的防丢器距离来判断是否报警
			//判断为非场景，模式自主切换到手动，并且根据手动设置的距离来判断是否报警
			if(tagNet){
				checkSenceOnNet(device_mac, ibeacon.rssi);
			}
			
			/*if(checkSence(device_mac)){//D0:5F:B8:32:32:91
				Log.i(TAG, "已进入场景: " + device_mac);
				long ss = System.currentTimeMillis();
				mTimestmp.put(device_mac, ss);//更新计时
				mIsInScene=true; 
				mIsScan = true;
			}else{
				mIsInScene = false;
			}*/
		}

	};
	/**
	 * 联网检查设备是否场景，并获取距离
	 * @param device_mac
	 */
	private void checkSenceOnNet(String deviceMac, int rssi) {
		double distanceFact = Utils.calculateDistance(rssi);
		// 需要发送到后台
		Message msg = new Message();
		msg.obj = deviceMac;
		msg.arg1 = rssi;
		msg.what = Consts.MESSAGE_CHECE_SENCE_ON_NET;
		boolean result = this.handler.sendMessage(msg); 
		Log.i(TAG, "场景检测，发送到服务器——————"+result);
	}
	/**
	 * device场景扫描线程
	 */
	@Override
	public void run() {
		while(mIsScan){//更改为可以停止的线程
			 try {
				Thread.sleep(SCAN_PERIOD);
				mTrackers = getIbeacons();
				mMapAreas = getSafetyArea();
				checkSafetyArea(mMapAreas);
			 } catch (InterruptedException e) {
				e.printStackTrace();
			 }
			 //怎么解释？
			 /*if(mTrackers == null || mTrackers.size() == 0){
				 return;
			 }*/
			 if(mTrackers!=null && mTrackers.size()>0 && !Utils.isScanAddOrHelp){
				 ScanDeivce(true);
				 tagNet = true;
			 }else{
				 ScanDeivce(false);
				 tagNet = false;
			 }
			 /*if(mTimestmp != null || mTimestmp.size() != 0){
				 for(String deviceAddr:mTimestmp.keySet()){
					 long getIbeaconTime = mTimestmp.get(deviceAddr);
					 if(getIbeaconTime == 0)return;
					 HashMap<String,String> result = new HashMap<String,String>();
					 if(mIsInScene){
						 result.put("result","1");//?有场景
						 result.put("type", "device");//?设备场景
						 result.put(Consts.DEVICE_MAC, deviceAddr);
						 callback.onResult(result);
					 }else{
						 result.put("type", "device");//?设备场景
						 result.put("result","0");//?无场景
						 result.put(Consts.DEVICE_MAC, deviceAddr);
						 callback.onResult(result);
					 }
				 }
			 }*/
			 /*long currentTime = System.currentTimeMillis();
			 for(String key:mTimestmp.keySet()){
				 long getIbeaconTime = mTimestmp.get(key);
					if(getIbeaconTime == 0)return;
					HashMap<String,String> result = new HashMap<String,String>();
					if((currentTime-getIbeaconTime)>3000){//想不通！D0:5F:B8:32:2E:41
						result.put("result","1");//?有场景
						result.put("type", "device");//?设备场景
						callback.onResult(result);
					}
					else{
						result.put("type", "device");//?设备场景
						result.put("result","0");//?无场景
						callback.onResult(result);
					}
			 }*/
		 }
	}
	/**
	 * 安全区域
	 * @param mMapAreas2
	 */
	private void checkSafetyArea(Map<Tracker, ArrayList<TrackerSafityArea>> mMapAreas2) {
		for(Tracker key:mMapAreas2.keySet()){
			ArrayList<TrackerSafityArea> tempAreas = mMapAreas2.get(key);
			String deviceAddr = key.getDevice_addr();
			if(tempAreas ==null)return;
			HashMap<String,String> result = new HashMap<String,String>();
//			int distance = 10;//key.getDistance();//设定距离
			for(TrackerSafityArea area:tempAreas){
				if(area.getSouthWest() == null || area.getNorthEast() == null)return;
				//构造当前矩形安全区域
				String southWest = area.getSouthWest();
				String[] southWestD = southWest.split(",");
				LatLng southWestP = new LatLng(Double.parseDouble(southWestD[0]), Double.parseDouble(southWestD[1]));
				String northEast = area.getNorthEast();
				String[] northEastD = northEast.split(",");
				LatLng northEastP = new LatLng(Double.parseDouble(northEastD[0]), Double.parseDouble(northEastD[1]));
				LatLngBounds bounds = new LatLngBounds.Builder().include(southWestP).include(northEastP).build();
				//获取当前位置
				getCurrentLocation();
				//看当前地理位置是否包含在矩形区域内
				if(currentLocation == null)return;
				boolean in = bounds.contains(currentLocation);
//				int factDistance= (int) mBaiduUtils.getInstance(lat);
//				Log.i(TAG, "当前场景，距离:"+factDistance);
				if(in){//(factDistance<distance)
					result.put("result","1");//?在安全区域
					result.put("type", "area");//安全区域
					result.put(Consts.DEVICE_MAC, deviceAddr);
					callback.onResult(result);
				}else{
					result.put("result","0");//?不在安全区域
					result.put("type", "area");//安全区域
					result.put(Consts.DEVICE_MAC, deviceAddr);
					callback.onResult(result);
				}
			}
		}
		
	}
	
	private void getCurrentLocation() { 
		if(mBaUtils != null)return;
		mBaUtils = new BaiduUtils(context);
		mBaUtils.setCallback(new BaiduUtils.Callback() {
			@Override
			public void onResult(BDLocation location) {
				if(location ==null){
					return;
				}
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				currentLocation = new LatLng(latitude, longitude);
				Log.i(TAG, "current address: " + location.getAddrStr());
			}
		});
		mBaUtils.start();
	}
	/**
	 * 检查该设备是否是场景设备
	 * @param device_mac
	 * @return
	 */
	protected boolean checkSence(String device_mac) {
		for(String senceMac:mSences){
			if(senceMac.equalsIgnoreCase(device_mac))
				return true;
		}
		return false;
	}
	public interface CallBack{
		public void onResult(HashMap<String,String> result);
	}
	
	/*public void stopScan(){
		mIsScan = false;
	}*/
}
