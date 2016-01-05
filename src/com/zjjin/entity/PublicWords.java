package com.zjjin.entity;

public class PublicWords {
	private String headPicture;
	private String username;
		//findwords表
	private String wordsID;
	private String findID;
	private String wordsContent;
	private String wordsAddress;
	private String wordsDate;
	private String userID;
	private String wordsLongitude;
	private String wordsLatitude;
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
	public String getWordsID() {
		return wordsID;
	}
	public void setWordsID(String wordsID) {
		this.wordsID = wordsID;
	}
	public String getFindID() {
		return findID;
	}
	public void setFindID(String findID) {
		this.findID = findID;
	}
	public String getWordsContent() {
		return wordsContent;
	}
	public void setWordsContent(String wordsContent) {
		this.wordsContent = wordsContent;
	}
	public String getWordsAddress() {
		return wordsAddress;
	}
	public void setWordsAddress(String wordsAddress) {
		this.wordsAddress = wordsAddress;
	}
	public String getWordsDate() {
		return wordsDate;
	}
	public void setWordsDate(String wordsDate) {
		this.wordsDate = wordsDate;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getWordsLongitude() {
		return wordsLongitude;
	}
	public void setWordsLongitude(String wordsLongitude) {
		this.wordsLongitude = wordsLongitude;
	}
	public String getWordsLatitude() {
		return wordsLatitude;
	}
	public void setWordsLatitude(String wordsLatitude) {
		this.wordsLatitude = wordsLatitude;
	}
	public PublicWords(String headPicture, String username, String wordsID,
			String findID, String wordsContent, String wordsAddress,
			String wordsDate, String userID, String wordsLongitude,
			String wordsLatitude) {
		super();
		this.headPicture = headPicture;
		this.username = username;
		this.wordsID = wordsID;
		this.findID = findID;
		this.wordsContent = wordsContent;
		this.wordsAddress = wordsAddress;
		this.wordsDate = wordsDate;
		this.userID = userID;
		this.wordsLongitude = wordsLongitude;
		this.wordsLatitude = wordsLatitude;
	}
	public PublicWords() {
		super();
	}
	
	
	
}
