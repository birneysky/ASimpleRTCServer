package com.zijin.ibeacon.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.zijin.ibeacon.util.Utils;

public class AppRequest {

	/**上传图片标示 **/
	public  static final int UPIMG=1;
	/**普通 **/
	public static final int NORMAL=0;
	/** 请求方式 NOMAL正常,UPIMG 图片**/
	private int mRequestType =NORMAL;
	private ArrayList<File> mUpFiles;
	private String mRequestURL;
	private Map<String,String> mParas;
	
	
	public AppRequest(){
		if(this.mParas==null)this.mParas = new HashMap<String,String>();
		mParas.put("clientType","android");
	}
	
	public String getmRequestURL() {
		return mRequestURL;
	}
	public void setmRequestURL(String mRequestURL) {
		this.mRequestURL = Utils.mServerURl+mRequestURL;
	}
	public Map<String, String> getmParas() {
		return mParas;
	}
	public void setmParas(Map<String, String> mParas) {
		this.mParas = mParas;
	}
	
	public int getmRequestType() {
		return mRequestType;
	}
	public void setmRequestType(int mRequestType) {
		this.mRequestType = mRequestType;
	}
	
	public void putPara(String key,String value){
		if(this.mParas==null)this.mParas = new HashMap<String,String>();
		this.mParas.put(key, value);
	}
	
	public String getPara(String key){
		return this.mParas.get(key);
	}
	
	public ArrayList<File> getmUpFiles() {
		return mUpFiles;
	}
	public void setmUpFiles(ArrayList<File> mUpFiles) {
		this.mUpFiles = mUpFiles;
	}
	public String toJson(){
		String ret="";
		if(mParas ==null)return "";
		Gson gson = new Gson();
		ret = gson.toJson(mParas);
		return ret;
	}
}
