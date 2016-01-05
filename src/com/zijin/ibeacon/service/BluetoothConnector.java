package com.zijin.ibeacon.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

import com.zijin.ibeacon.service.BluetoothLeService;
import com.example.slidingmenu.ForgetPasswordActivity;
import com.example.slidingmenu.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothConnector extends Thread{
	private final String TAG=BluetoothConnector.class.getSimpleName();
    private Context context;
    private boolean mCheckTimeout=true;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mService;
//    private String address;
    private BluetoothDevice device;//?
    private BluetoothGatt gatt;
//    private Timer mRssiTimer;
    private static final int GET_RSSID_TIME=2000;
//    private int mElapsed=0;//设备连接次数
	private static final int REQUEST_TIMEOUT = 30 * 100; // 10秒钟？total timeout =
	private static String REASON;
	private TimeoutRunnable mTimeoutRunnable;
	private Thread mRequestTimeout;//超时连接线程
	public static boolean isScanning = true;
//	public static boolean stopAlarm = false;
	//每个不同的地址，对应一个gatt
	private Map<String,BluetoothGatt> gatts = Collections.synchronizedMap(new HashMap<String,BluetoothGatt>());
	private Map<String, Integer> deviceAddrList = new HashMap<String, Integer>();//记录设备对应超时时间
	
	
	/**
	 * 蓝牙设备扫描线程
	 * @author birney
	 */
	public BluetoothConnector(Context context,BluetoothAdapter mBluetoothAdapter,BluetoothLeService mBle) {
		this.context = context;
		this.mService=mBle;
		this.mBluetoothAdapter = mBluetoothAdapter;
		REASON = context.getResources().getString(R.string.connecttimeout);
		Log.i(TAG, "Connector启动");
	}
/*	public void disconnectGatt(){
		if(gatt!=null)gatt.disconnect();
	}*/
	public boolean connectGatt(String addr){
		/*if(mBluetoothAdapter == null){
			final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}*/
		try {
			BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(addr);
			if(device1==null){
				return false;
			}
			BluetoothGatt gatt1 = device1.connectGatt(context, true, mGattCallback);
			if(gatt1==null){
				return false;
			}
			if(gatts.containsKey(device1.getAddress()))	return true;
			gatts.put(device1.getAddress(), gatt1);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 超时线程是什么逻辑 ？
	 * @author birney
	 *
	 */
	private class TimeoutRunnable implements Runnable{
		private String device_addr;
		
		public TimeoutRunnable(String device_addr) {
			super();
			this.device_addr = device_addr;
		}
		@Override
		public void run() {
			Log.e(TAG, "monitoring thread start");
//			mElapsed = 0;
			try {
				while (mCheckTimeout) {
					Thread.sleep(100);
//					mElapsed += 100;
					for(String address : gatts.keySet()){
						BluetoothGatt gatt = gatts.get(address);
						if(gatt == null)	continue;
						Integer integ = deviceAddrList.get(address);
						if(integ == null)	continue;
						int time = deviceAddrList.get(address).intValue();
						if(time != 0){
							time += 100;
							deviceAddrList.put(address, Integer.valueOf(time));
							final String str = address;
							if (time > REQUEST_TIMEOUT) {// && !gatt.connect()
//								mService.bleRequestFailed(address,REASON);
								Log.e(TAG, device_addr+"———连接失败————连接超时————"+time+"——"+REASON);
								if(time == REQUEST_TIMEOUT + 101){
									boolean result = gatt.connect();//发现丢失则重新连接
									mService.bleMissAlarmAlert(str);
									deviceAddrList.remove(str);
									if(deviceAddrList.size() == 0){
										mCheckTimeout = false;
										Log.e(TAG, "mCheckTimeout = false");
									}else{
										mCheckTimeout = false;
									}
								}
							}
						}
					}
					/*Integer integ = deviceAddrList.get(device_addr);
					if(integ == null)	return;
					int time = deviceAddrList.get(device_addr).intValue();
					if(time != 0){
						if(deviceAddrList.containsKey(device_addr)){
							Log.d(TAG, "address = "+device_addr+", time = "+time+", 数量："+gatts.size());
							time += 100;
							deviceAddrList.put(device_addr, Integer.valueOf(time));
							final String str = device_addr;
							if (time > REQUEST_TIMEOUT) {// && !gatt.connect()
								Log.e(TAG, device_addr+"———连接失败————连接超时————"+time+"——"+REASON);
//								mService.bleRequestFailed(address,REASON);
								if(time == REQUEST_TIMEOUT + 101){
									boolean result = gatt.connect();//发现丢失则重新连接
									mService.bleMissAlarmAlert(str);
									mCheckTimeout = false;
								}
							}
						}
					}*/
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				Log.e(TAG, "monitoring thread exception");
			}
			Log.e(TAG, "monitoring thread stop");
		}
		
	};
	/**
	 * 开始超时连接线程
	 */
	private void startTimeoutThread(String addr) {
		if(deviceAddrList.containsKey(addr)){
			return;
		}else{
			int count = deviceAddrList.size();
			deviceAddrList.put(addr, Integer.valueOf(1));
			if(count == 0){
				mCheckTimeout = true;
				mTimeoutRunnable = new TimeoutRunnable(addr);
				mRequestTimeout = new Thread(mTimeoutRunnable, "TiemoutThread： "+addr);
				mRequestTimeout.start();
			}
		}
	}
	
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		public void onConnectionStateChange(final BluetoothGatt gatt1, int status, int newState) {
			String address = gatt1.getDevice().getAddress();
			Log.d(TAG, "onConnectionStateChange:" + address + ", status:"+ status + ", newState:" + newState);
			/*if(status == 133){
				//解决办法就是要重新连接同一个蓝牙设备的时候，记得调用BluetoothGatt的.close() 方法来关闭当前的蓝牙连接并清掉已使用过的蓝牙连接。
				if (bluetoothDevice != null) { 
					if (mBluetoothGatt != null) { 
						mBluetoothGatt.close(); 
						mBluetoothGatt.disconnect(); 
						mBluetoothGatt = null; 
					} 
					mBluetoothGatt = bluetoothDevice.connectGatt(this.context, false, gattCallback); 
				}
			}*/
			if (status != BluetoothGatt.GATT_SUCCESS) {
//				gatt.disconnect();//qinwq del
					return;
			}
			if(gatts.get(address) == null) return;
			
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.i(TAG, newState+"----已经连接！");	//2
				mService.bleGattConnected(gatts.get(address).getDevice());//gatt.getDevice()空指针
				//设备连接上，为设备开启一个超时线程计时器 lllv
				startTimeoutThread(address);
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.i(TAG, newState+"----已经断开！");	//0
				if(deviceAddrList.containsKey(address)){
					deviceAddrList.remove(address);
				}
				if(deviceAddrList.size() == 0 || gatts.size() == 0){
					mCheckTimeout = false;
				}
				mService.bleGattDisConnected(gatts.get(address).getDevice().getAddress());
//				startTimeoutThread(address);//原先注释了 wqqin
			}
		}
		
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {//发现服务的回调
			Log.d(TAG, "mGattCallback onServicesDiscovered: ");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d(TAG,"设备连接成功！");
			}
		}
//		public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharactSeristic characteristic, int status) {
//			Log.d(TAG,"---------------------读取数据-----------------------------");
//		}
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			Log.d(TAG,"---------------------读取描述-----------------------------");
		}
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		}
		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			if(deviceAddrList.containsKey(gatt.getDevice().getAddress())){
				deviceAddrList.put(gatt.getDevice().getAddress(), Integer.valueOf(1));
			}
			Log.d(TAG,"---------------------读取RSSI------------"+gatt.getDevice().getAddress()+", rssI:"+rssi);  
			mService.bleReadRssi(gatt.getDevice(), rssi, status);//mService.bleReadRssi(device, rssi, status);
		}
	};

	
	/*public void setStatus(boolean stauts){
		mCheckTimeout= stauts;
	}*/

	@SuppressWarnings("static-access")
	@Override
	public void run() {
//		Log.d(TAG,"--------------------开始连接设备---------------------------");
//		connectAllGatt();
		while(isScanning){
            try {
            	Log.d(TAG,"--------------------准备rssi值---------------------------"+isScanning);
				Thread.currentThread().sleep(GET_RSSID_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            /* if(gatt==null || !gatt.connect()){
            	 connectGatt();
            	 return;  由于执行了stopRun方法，还是自动连接
            	 }*/
//             gatt.readRemoteRssi();
             for(String addr:gatts.keySet()){
            	 if(deviceAddrList.containsKey(addr)){
            		 BluetoothGatt gatt = gatts.get(addr);
            		 if(gatt==null){
            			 return;
            		 }
            		 boolean rssi = gatt.readRemoteRssi();
            		 Log.i(TAG, addr+": readRemoteRssi: " + rssi);
            	 }
             }
		}
	}
	
	
	private void connectAllGatt() {
		 try {
			for(String addr:gatts.keySet()){
				 device = mBluetoothAdapter.getRemoteDevice(addr);
				 gatt = device.connectGatt(context, true, mGattCallback);
					if(gatt==null){
						return ;
					}
					gatts.put(device.getAddress(), gatt);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void removeRun(String addr){
		gatt = gatts.get(addr);
		gatts.remove(addr);
		if(gatt!=null){
			gatt.disconnect();
		}
		if(deviceAddrList.containsKey(addr)){
			deviceAddrList.remove(addr);
		}
		if(deviceAddrList.size() == 0){
			mCheckTimeout = false;
			Log.e(TAG, "mCheckTimeout = false");
		}
	}
		
	
	public void stopRun(){
		 for(String addr:gatts.keySet()){
			 gatts.remove(addr);
			 if(gatt!=null){
//				 gatt.close();
				 gatt.disconnect();
			 }
		 }
		 isScanning = false;
		 mCheckTimeout = false;
		 Log.e(TAG, "mCheckTimeout = false");
	}
	
	public void addMinitor(String addr){
		boolean result = connectGatt(addr);
	}
	
	public boolean checkDevice(String addr){
		for(String addrs:gatts.keySet()){
			if(addr.equals(addrs)){
				return true;
			}
		}
		return false;
	}


}
