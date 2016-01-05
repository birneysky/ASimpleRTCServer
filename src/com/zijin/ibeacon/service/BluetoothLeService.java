package com.zijin.ibeacon.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.slidingmenu.IbeaconApplication;
import com.zijin.ibeacon.util.Utils;

@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
	private final static String TAG = BluetoothLeService.class.getSimpleName();
	/** Intent for broadcast */
	public static final String BLE_NOT_SUPPORTED = "com.xtremeprog.sdk.ble.not_supported";
	public static final String BLE_NO_BT_ADAPTER = "com.xtremeprog.sdk.ble.no_bt_adapter";
	public static final String BLE_STATUS_ABNORMAL = "com.xtremeprog.sdk.ble.status_abnormal";
	/**
	 * @see BleService#bleRequestFailed
	 */
	public static final String BLE_DEVICE_LOSTED = "com.xtremeprog.sdk.ble.device_lost";
	/**
	 * @see BleService#bleRequestFailed
	 */
	public static final String BLE_REQUEST_FAILED = "com.xtremeprog.sdk.ble.request_failed";
	/**
	 * @see BleService#bleDeviceFound
	 */
	public static final String BLE_DEVICE_FOUND = "com.xtremeprog.sdk.ble.device_found";
	/**
	 * @see BleService#bleGattConnected
	 */
	public static final String BLE_GATT_CONNECTED = "com.xtremeprog.sdk.ble.gatt_connected";
	/**
	 * @see BleService#bleGattDisConnected
	 */
	public static final String BLE_GATT_DISCONNECTED = "com.xtremeprog.sdk.ble.gatt_disconnected";
	/**
	 * @see BleService#bleServiceDiscovered
	 */
	public static final String BLE_SERVICE_DISCOVERED = "com.xtremeprog.sdk.ble.service_discovered";
	/**
	 * @see BleService#bleCharacteristicRead
	 */
	public static final String BLE_CHARACTERISTIC_READ = "com.xtremeprog.sdk.ble.characteristic_read";
	/**
	 * @see BleService#bleCharacteristicNotification
	 */
	public static final String BLE_CHARACTERISTIC_NOTIFICATION = "com.xtremeprog.sdk.ble.characteristic_notification";
	/**
	 * @see BleService#bleCharacteristicIndication
	 */
	public static final String BLE_CHARACTERISTIC_INDICATION = "com.xtremeprog.sdk.ble.characteristic_indication";
	/**
	 * @see BleService#bleCharacteristicWrite
	 */
	public static final String BLE_CHARACTERISTIC_WRITE = "com.xtremeprog.sdk.ble.characteristic_write";
	/**
	 * @see BleService#bleCharacteristicChanged
	 */
	public static final String BLE_CHARACTERISTIC_CHANGED = "com.xtremeprog.sdk.ble.characteristic_changed";
	
	//qinwq 
	public static final String BLE_RSSI_READED="com.zijin.rssi_readed";
	//qinwq 警报
	public static final String BLE_RSSI_ALARM="com.zijin.rssi_alarm";
	public static final String BLE_MISSED_ALARM = "com.zijin.ble_missed_alarm";//发消息取消铃声
	public static final String BLE_MISSED_ALARM_ALERT = "com.zijin.ble_missed_alarm_alert";//发消息弹框提示
	
	//qinwq 配对广播 
	public static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";  

	/** Intent extras */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_SCAN_RECORD = "SCAN_RECORD";
	public static final String EXTRA_SOURCE = "SOURCE";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";
	public static final String EXTRA_REQUEST = "REQUEST";
	public static final String EXTRA_REASON = "REASON";

	/** Source of device entries in the device list */
	public static final int DEVICE_SOURCE_SCAN = 0;
	public static final int DEVICE_SOURCE_BONDED = 1;
	public static final int DEVICE_SOURCE_CONNECTED = 2;
	private final IBinder mBinder = new LocalBinder();
	private BluetoothAdapter mBluetoothAdapter;
    private static MediaPlayer mMediaPlayer=null;//警报
	
	/**线程管理*/
	private Map<String,String> devices =Collections.synchronizedMap(new HashMap<String,String>());
	private  BluetoothConnector connector;

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BLE_NOT_SUPPORTED);
		intentFilter.addAction(BLE_NO_BT_ADAPTER);
		intentFilter.addAction(BLE_STATUS_ABNORMAL);
		intentFilter.addAction(BLE_REQUEST_FAILED);
		intentFilter.addAction(BLE_DEVICE_FOUND);
		intentFilter.addAction(BLE_GATT_CONNECTED);
		intentFilter.addAction(BLE_GATT_DISCONNECTED);
		intentFilter.addAction(BLE_SERVICE_DISCOVERED);
		intentFilter.addAction(BLE_CHARACTERISTIC_READ);
		intentFilter.addAction(BLE_CHARACTERISTIC_NOTIFICATION);
		intentFilter.addAction(BLE_CHARACTERISTIC_WRITE);
		intentFilter.addAction(BLE_CHARACTERISTIC_CHANGED);
		intentFilter.addAction(BLE_RSSI_READED);//qinwq add
		intentFilter.addAction(ACTION_PAIRING_REQUEST);//qinwq add
		intentFilter.addAction(BLE_MISSED_ALARM);//取消设备报警广播
		intentFilter.addAction(BLE_MISSED_ALARM_ALERT);
		return intentFilter;
	}


	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}
	
	/**
	 * 开启蓝牙线程
	 * Initializes a reference to the local Bluetooth adapter.
	 */
	private void initialize() {
		IbeaconApplication application = (IbeaconApplication) getApplication();
		if(application.isSupport){
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			connector = new BluetoothConnector(getApplicationContext(), mBluetoothAdapter, BluetoothLeService.this);
			connector.setName("connectorThread");
			connector.start();
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		initialize();
	}
	/**
	 * 
	 */
	public void stopConnectors(){
		connector.stopRun();
	}
	/**
	 * 停止监控
	 */
	public void stopConnector(String address){
		if(connector ==null)return;
		connector.removeRun(address);
	}

	public void addMonitor(String device_addr){
//		if(checkDevice(device_addr)){
		if(connector ==null){
			Utils.showMsg(getApplicationContext(), "打开失败");
			return;
		}
		if(BluetoothConnector.isScanning == false){
			BluetoothConnector.isScanning = true;
		}
//		BluetoothSences.mIsScan = true;
			connector.addMinitor(device_addr);
//		}
	}
	
	@Override
	public void onDestroy() {
		stopConnectors();//service销毁时，销毁线程
		super.onDestroy();
	}
	
	/**
	 * 发现设备的回调
	 *//*
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecords) {
			System.out.println("scanRecords:"+scanRecords);
			f(!checkDevice(device.getAddress())){
				BluetoothConnector connector = new BluetoothConnector(getApplicationContext(), mBluetoothAdapter, BluetoothLeService.this, device.getAddress());
				connector.start();
				threads.put(device.getAddress(), connector);
			}
			bleDeviceFound(device,rssi,scanRecords,0);
		}
	};*/
	/**
	 * 是否已经启动线程
	 */
	private boolean checkDevice(String address){
		return connector.checkDevice(address);
	}
	//////广播 
	/**
	 * BLE设备已找到
	 * @param device
	 * @param rssi
	 * @param scanRecord
	 * @param source
	 */
	protected void bleDeviceFound(BluetoothDevice device, int rssi,
			byte[] scanRecord, int source) {
		Log.d("blelib", "[" + new Date().toLocaleString() + "] device found "+ device.getAddress());
		Intent intent = new Intent(BLE_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		intent.putExtra(EXTRA_SCAN_RECORD, scanRecord);
		intent.putExtra(EXTRA_SOURCE, source);
		sendBroadcast(intent);
	}
	
	protected void bleCharacteristicNotification(String address, String uuid,
			boolean isEnabled, int status) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_NOTIFICATION);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_VALUE, isEnabled);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
	}

	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_INDICATION} broadcast. <br>
	 * @param address
	 * @param uuid
	 * @param status
	 */
	protected void bleCharacteristicIndication(String address, String uuid,
			int status) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_INDICATION);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
	}

	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_WRITE} broadcast. <br>
	 * @param address
	 * @param uuid
	 * @param status
	 */
	protected void bleCharacteristicWrite(String address, String uuid,
			int status) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_WRITE);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
	}

	/**
	 * Send {@link BleService#BLE_CHARACTERISTIC_CHANGED} broadcast. <br>
	 * @param address
	 * @param uuid
	 * @param value
	 */
	protected void bleCharacteristicChanged(String address, String uuid,
			byte[] value) {
		Intent intent = new Intent(BLE_CHARACTERISTIC_CHANGED);
		intent.putExtra(EXTRA_ADDR, address);
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);
	}

	/**
	 * BLE状态异常
	 * @param reason
	 */
	protected void bleStatusAbnormal(String reason) {
		Intent intent = new Intent(BLE_STATUS_ABNORMAL);
		intent.putExtra(EXTRA_VALUE, reason);
		sendBroadcast(intent);
	}
	/**
	 * 长时间未连接，已断开
	 * @param address
	 * @param type
	 * @param reason
	 */
	protected void bleRequestFailed(String address, String reason) {
		Intent intent = new Intent(BLE_REQUEST_FAILED);
		intent.putExtra(EXTRA_ADDR, address);
//		intent.putExtra(EXTRA_REQUEST, type);
		intent.putExtra(EXTRA_REASON, reason);
		sendBroadcast(intent);
	}
	/**
	 * 获取设备的rssi值
	 * @param device
	 * @param rssi
	 * @param status
	 */
	public void bleReadRssi(BluetoothDevice device, int rssi, int status) {
		Intent intent = new Intent(BLE_RSSI_READED);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}
	/**
	 * 设备gatt连接上
	 * @param device
	 */
	protected void bleGattConnected(BluetoothDevice device) {
		Intent intent = new Intent(BLE_GATT_CONNECTED);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		sendBroadcast(intent);
	}
	/**
	 * 设备断开连接
	 * @param address
	 */
	protected void bleGattDisConnected(String address) {
		Intent intent = new Intent(BLE_GATT_DISCONNECTED);
		intent.putExtra(EXTRA_ADDR, address);
		sendBroadcast(intent);
	}
	
	/**
	 * 设备报警广播
	 */
	protected void bleAlarm(BluetoothDevice device, int rssi, int status) {
		Intent intent = new Intent(BLE_RSSI_ALARM);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}

	/**
	 * 取消设备报警广播,红色点变为蓝色点
	 * @param address
	 */
	public void bleMissAlarmCancel(String address){
		Intent intent = new Intent(BLE_MISSED_ALARM);
		intent.putExtra(EXTRA_ADDR, address);
		sendBroadcast(intent);
	}
	
	/**
	 * 发消息，提示弹框，只弹一次
	 * @param address
	 */
	protected void bleMissAlarmAlert(String address){
		Intent intent = new Intent(BLE_MISSED_ALARM_ALERT);
		intent.putExtra(EXTRA_ADDR, address);
		sendBroadcast(intent);
	}
	
	public void removeMonitor(String device_addr) {
		if(device_addr==null)return;
		stopConnector(device_addr);
	}

}
