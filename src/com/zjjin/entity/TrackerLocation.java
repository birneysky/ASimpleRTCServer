package com.zjjin.entity;

public class TrackerLocation {
	
	private int id;
	private int beaconID;
	private String time;
	private String address;
	private String date;
//	private String phoneNum;
	public static final String[] mCloumns={"id","beaconID","address","date","time"};
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBeaconID() {
		return beaconID;
	}
	public void setBeaconID(int beaconID) {
		this.beaconID = beaconID;
	}
	public TrackerLocation() {
		super();
	}
	
	public TrackerLocation(int id, int beaconID, String time, String address,
			String date) {
		super();
		this.id = id;
		this.beaconID = beaconID;
		this.time = time;
		this.address = address;
		this.date = date;
	}
		
}
