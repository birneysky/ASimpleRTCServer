package com.zijin.ibeacon.http;

import java.io.IOException;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

public class AppThread extends HttpThread {
    private AppRequest request;
    private AppResponse response;
    private AppHandler handler;
    private String TAG = "AppThread";
    

    public AppThread(Activity activity, AppRequest request) {
        super(activity);
        this.request = request;
    }

    public AppThread(Activity activity, AppRequest request, AppHandler handler) {
        super(activity);
        this.request = request;
        this.handler = handler;
    }

    @Override
    protected void executeHttp() throws IOException, org.json.JSONException{
        String responseXml = session.post(request);
        Log.i("response", responseXml);
        response = new AppResponse(responseXml);
    }



    @Override
    protected void onSuccess() {
        LoadingIndicator.cancel();
        if("0".equals(response.getmCode())){
        	handler.handle(request, response);
        }else{
        	 showErrorMessage(response.getmMessage());
        }
    }

    @Override
    protected void onFailure(Exception e) {
    	LoadingIndicator.cancel();
    	Log.d(TAG, e.getMessage());
    	if(e instanceof ConnectTimeoutException){
    		showErrorMessage("服务器连接超时");//网络不给力
    	}
    	else if(e instanceof java.net.SocketTimeoutException){
    		showErrorMessage("服务器连接超时");
    	}
    	else if(e instanceof org.json.JSONException){
//    		showErrorMessage("服务器返回错误，请联系管理员");
    	}
    	else{
	        showErrorMessage(e.getMessage());
    	}
    }

    protected void showInfoMessage(String message) {
    	if(activity ==null)return;
        MessageBox.show(activity, message, infoListener());
    }

    private OnClickListener infoListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.handle(request, response);
            }
        };
    }

    protected void showErrorMessage(String message) {
    	if(activity ==null)return;
        MessageBox.show(activity, message);
    }

}
