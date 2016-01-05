package com.zjjin.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PublicFind implements Serializable{
	private String findID;
	private String userID;
	private String findType;
	private String findPicture;
	private String findAddress;
	private String findLongitude;
	private String findLatitude;
	private String findDesc;
	private String findStatus;
	private String findDate;
	private String longitude;
	private String latitude;
	private String findMac;
	private String headPicture;
	private String username;

	public PublicFind() {
		super();
	}

	public String getFindID() {
		return findID;
	}

	public void setFindID(String findID) {
		this.findID = findID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getFindType() {
		return findType;
	}

	public void setFindType(String findType) {
		this.findType = findType;
	}

	public String getFindPicture() {
		return findPicture;
	}

	public void setFindPicture(String findPicture) {
		this.findPicture = findPicture;
	}

	public String getFindAddress() {
		return findAddress;
	}

	public void setFindAddress(String findAddress) {
		this.findAddress = findAddress;
	}

	public String getFindLongitude() {
		return findLongitude;
	}

	public void setFindLongitude(String findLongitude) {
		this.findLongitude = findLongitude;
	}

	public String getFindLatitude() {
		return findLatitude;
	}

	public void setFindLatitude(String findLatitude) {
		this.findLatitude = findLatitude;
	}

	public String getFindDesc() {
		return findDesc;
	}

	public void setFindDesc(String findDesc) {
		this.findDesc = findDesc;
	}

	public String getFindStatus() {
		return findStatus;
	}

	public void setFindStatus(String findStatus) {
		this.findStatus = findStatus;
	}

	public String getFindDate() {
		return findDate;
	}

	public void setFindDate(String findDate) {
		this.findDate = findDate;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getHeadPicture() {
		return headPicture;
	}

	public void setHeadPicture(String headPicture) {
		this.headPicture = headPicture;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	

	public String getFindMac() {
		return findMac;
	}

	public void setFindMac(String findMac) {
		this.findMac = findMac;
	}

	public PublicFind(String findID, String userID, String findType,
			String findPicture, String findAddress, String findLongitude,
			String findLatitude, String findDesc, String findStatus,
			String findDate,String findMac) {
		super();
		this.findID = findID;
		this.userID = userID;
		this.findType = findType;
		this.findPicture = findPicture;
		this.findAddress = findAddress;
		this.findLongitude = findLongitude;
		this.findLatitude = findLatitude;
		this.findDesc = findDesc;
		this.findStatus = findStatus;
		this.findDate = findDate;
		this.findMac = findMac;

	}

}
