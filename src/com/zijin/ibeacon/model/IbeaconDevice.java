package com.zijin.ibeacon.model;

public class IbeaconDevice {
	
	private String uuid;
	private String name;
	private String macAddress;
	private int major;
	private int minor;
	private double rssi;
	private int power;
	private String connectStatus;
	
	public IbeaconDevice() {
		super();
	}
	public IbeaconDevice(String uuid, String name, String macAddress,
			int major, int minor, double rssi, int power,String connectStatus) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.macAddress = macAddress;
		this.major = major;
		this.minor = minor;
		this.rssi = rssi;
		this.power = power;
		this.connectStatus = connectStatus;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public double getRssi() {
		return rssi;
	}
	public void setRssi(double rssi) {
		this.rssi = rssi;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public String getConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
	}
	
	

}
