package com.zijin.ibeacon.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 目前返回的json内容为{data:{对象列表或者对象},message:"成功失败信息 ",type:"类型 ,主要用来区别data数据类型 list/single",code:'0\1\2'}
 * @author Qinwq
 *
 */
public class AppResponse {
      private String mResult;//服务器返回的json串   
      private String mMessage;
      private String mCode;
      private String type;
      private String data;    
      
      
	@Override
	public String toString() {
		return "AppResponse [mResult=" + mResult + ", mMessage=" + mMessage
				+ ", mCode=" + mCode + ", type=" + type + ", data=" + data
				+ "]";
	}
	public String getmCode() {
		return mCode;
	}
	public void setmCode(String mCode) {
		this.mCode = mCode;
	}
	public String getmResult() {
		return mResult;
	}
	public void setmResult(String mResult) {
		this.mResult = mResult;
	}
	public String getmMessage() {
		return mMessage;
	}
	public void setmMessage(String mMessage) {
		this.mMessage = mMessage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public AppResponse(String mResult) throws JSONException {
		this.mResult = mResult;
		if(mResult.length()==0)return;
		JSONObject  obj = new JSONObject(mResult);
		if(obj==null)return;
		this.mMessage  = obj.getString("message");
		this.mCode  = obj.getString("code");
		this.data = obj.getString("data");
	}
}
