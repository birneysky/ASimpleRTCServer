package com.zjjin.entity;

import java.io.Serializable;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String phoneNum;
	private String password;
	
	private String userName;
	private String userGender;
	private String userPhoto;
	private String userRigon;
	private String userAddress;
	private String userSign;
	
	public User() {
		super();
	}
	
	public User(int id, String phoneNum, String password, String userName,
			String userGender, String userPhoto, String userRigon,
			String userAddress, String userSign) {
		super();
		this.id = id;
		this.phoneNum = phoneNum;
		this.password = password;
		this.userName = userName;
		this.userGender = userGender;
		this.userPhoto = userPhoto;
		this.userRigon = userRigon;
		this.userAddress = userAddress;
		this.userSign = userSign;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserGender() {
		return userGender;
	}
	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}
	public String getUserPhoto() {
		return userPhoto;
	}
	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}
	public String getUserRigon() {
		return userRigon;
	}
	public void setUserRigon(String userRigon) {
		this.userRigon = userRigon;
	}
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public String getUserSign() {
		return userSign;
	}
	public void setUserSign(String userSign) {
		this.userSign = userSign;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
