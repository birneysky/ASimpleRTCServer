package com.view.sortlistview;

public class GroupMemberBean {

	private String numberCode; // 显示的电话区号
	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getNumberCode() {
		return numberCode;
	}
	public void setNumberCode(String numberCode) {
		this.numberCode = numberCode;
	}
}
