package com.zjjin.entity;

import java.io.Serializable;

public class SearchMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phoneNum;
	private String userName;
	private String helpMessage;
	private String distance;
	private String photoAddr;
	
	public SearchMessage() {
		super();
	}

	public SearchMessage(String phoneNum, String userName, String helpMessage,
			String distance, String photoAddr) {
		super();
		this.phoneNum = phoneNum;
		this.userName = userName;
		this.helpMessage = helpMessage;
		this.distance = distance;
		this.photoAddr = photoAddr;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getPhotoAdd() {
		return photoAddr;
	}

	public void setPhotoAdd(String photoAddr) {
		this.photoAddr = photoAddr;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	
	

}
