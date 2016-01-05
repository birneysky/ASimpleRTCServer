package com.zjjin.entity;


public class TrackerSafityArea {

	private int id;
	private int beaconID;
	private String areaName;
	private String address;
	private String southWest;
	private String northEast;
	
	public TrackerSafityArea() {
		super();
	}
	
	public TrackerSafityArea(int beaconID, String name, String address,
			String southWest, String northEast) {
		this.beaconID = beaconID;
		this.areaName = name;
		this.address = address;
		this.southWest = southWest;
		this.northEast = northEast;
	}

	public TrackerSafityArea(String name, String address) {
		super();
		this.areaName = name;
		this.address = address;
	}


	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getSouthWest() {
		return southWest;
	}

	public void setSouthWest(String southWest) {
		this.southWest = southWest;
	}

	public String getNorthEast() {
		return northEast;
	}

	public void setNorthEast(String northEast) {
		this.northEast = northEast;
	}

	
}
