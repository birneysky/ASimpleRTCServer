package com.zijin.ibeacon.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.slidingmenu.R;
import com.zijin.ibeacon.model.IbeaconDevice;
import com.zijin.ibeacon.service.BluetoothLeService;
import com.zijin.ibeacon.util.Utils;

public class LeDeviceListAdapter extends BaseAdapter {

	// Adapter for holding devices found through scanning.

	private ArrayList<IbeaconDevice> mLeDevices;
	private LayoutInflater mInflator;
	private Activity mContext;
	public LeDeviceListAdapter(Activity c) {
		super();
		mContext = c;
		mLeDevices = new ArrayList<IbeaconDevice>();
		mInflator = mContext.getLayoutInflater();
	}

	public boolean addDevice(IbeaconDevice device) {
//		if (!mLeDevices.contains(device)) {
//			mLeDevices.add(device);
//		}
		boolean isExsit=false;
		for(IbeaconDevice device1 :mLeDevices){
			if(device1.getMacAddress().equals(device.getMacAddress())){
				isExsit=true;
			}
		}
		if(!isExsit){
			mLeDevices.add(device);
		}
		
		return !isExsit;
			
	}

	public IbeaconDevice getDevice(int position) {
		return mLeDevices.get(position);
	}

	public void clear() {
		mLeDevices.clear();
	}

	@Override
	public int getCount() {
		return mLeDevices.size();
	}

	@Override
	public Object getItem(int i) {
		return mLeDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}
	public void updateState(String address,String status){
		for(IbeaconDevice device :mLeDevices){
			if(device.getMacAddress().equals(address))
			if(BluetoothLeService.BLE_GATT_CONNECTED.equals(status)){
				device.setConnectStatus(mContext.getResources().getString(R.string.connected));
			}else if(BluetoothLeService.BLE_GATT_DISCONNECTED.equals(status)){
				device.setConnectStatus(mContext.getResources().getString(R.string.disconnected));
			}/*else if(BleService.BLE_REQUEST_FAILED.equals(status)){
				device.setConnectStatus(mContext.getResources().getString(R.string.connectedfail));
			}*/
		}
		this.notifyDataSetChanged();
	}
	public void updateRssi(String address,int rssi,String status){
		for(IbeaconDevice device :mLeDevices){
			if(device.getMacAddress().equals(address)){
				if(BluetoothLeService.BLE_RSSI_READED.equals(status)){
					double distance = Utils.calculateDistance(rssi);
					device.setRssi(distance);
					device.setConnectStatus(mContext.getResources().getString(R.string.connected));
				}
			}
		}
	}
	/**
	 * ɾ���豸
	 * @param address
	 */
	public void removeDevice(String address){
		for(IbeaconDevice device :mLeDevices){
			String mac = device.getMacAddress();
			if(address.equals(mac))mLeDevices.remove(device);
		}
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (view == null) {
			view = mInflator.inflate(R.layout.listitem_ibeacon_device, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
			viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
			viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
			viewHolder.deviceStatus = (TextView) view.findViewById(R.id.device_status);
			view.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		IbeaconDevice device = mLeDevices.get(i);
		final String deviceName = device.getName();
		if (deviceName != null && deviceName.length() > 0)
			viewHolder.deviceName.setText(deviceName);
		else
			viewHolder.deviceName.setText(R.string.unknown_device);
		viewHolder.deviceAddress.setText(device.getMacAddress());
		viewHolder.deviceRssi.setText("���룺"+device.getRssi()+"(��)");
		viewHolder.deviceStatus.setText(""+device.getConnectStatus());
		return view;
	}

	class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceRssi;
		TextView devicePower;
		TextView deviceStatus;
	}
}
