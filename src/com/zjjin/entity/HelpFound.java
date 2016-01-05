package com.zjjin.entity;

public class HelpFound {
private String helpID;
private String userID;
private String helpUserID;
private int helpCount;
private String helpTime;
private String helpLong;
private String helpLat;
private String findID;

private String findMac;
public String getHelpID() {
	return helpID;
}
public void setHelpID(String helpID) {
	this.helpID = helpID;
}
public String getUserID() {
	return userID;
}
public void setUserID(String userID) {
	this.userID = userID;
}
public String getHelpUserID() {
	return helpUserID;
}
public void setHelpUserID(String helpUserID) {
	this.helpUserID = helpUserID;
}

public String getHelpTime() {
	return helpTime;
}
public void setHelpTime(String helpTime) {
	this.helpTime = helpTime;
}
public String getHelpLong() {
	return helpLong;
}
public void setHelpLong(String helpLong) {
	this.helpLong = helpLong;
}
public String getHelpLat() {
	return helpLat;
}
public void setHelpLat(String helpLat) {
	this.helpLat = helpLat;
}
public HelpFound() {
	super();
	// TODO Auto-generated constructor stub
}

public int getHelpCount() {
	return helpCount;
}
public void setHelpCount(int helpCount) {
	this.helpCount = helpCount;
}
public String getFindID() {
	return findID;
}
public void setFinID(String finID) {
	this.findID = finID;
}
public HelpFound(String helpID, String userID, String helpUserID,
		int helpCount, String helpTime, String helpLong, String helpLat,
		String finID,String findMac) {
	super();
	this.helpID = helpID;
	this.userID = userID;
	this.helpUserID = helpUserID;
	this.helpCount = helpCount;
	this.helpTime = helpTime;
	this.helpLong = helpLong;
	this.helpLat = helpLat;
	this.findID = finID;
	this.findMac=findMac;
}
public String getFindMac() {
	return findMac;
}
public void setFindMac(String findMac) {
	this.findMac = findMac;
}
public void setFindID(String findID) {
	this.findID = findID;
}





}
