package com.zijin.ibeacon.http;

import java.io.IOException;

import com.zjjin.utils.NetworkUtil;

import android.app.Activity;
import android.os.Handler;

public abstract class HttpThread extends Thread {
    protected static final HttpSession session = new HttpSession();

    protected Activity activity;
    private Handler handler = new Handler();

    public HttpThread(Activity activity) {
        this.activity = activity;
    }

    protected abstract void executeHttp() throws Exception;

    protected abstract void onSuccess();

    protected abstract void onFailure(Exception e);

    @Override
    public void run() {
    	if(!NetworkUtil.checkNetwork(activity.getApplicationContext())){
    		postFailure(new Exception("无网络连接"));
    	}else{
	        try {
	            executeHttp();
	            postSuccess();
	        } catch (Exception e) {
	        	e.printStackTrace();
	            postFailure(e);
	        }
    	}
    }

    protected void postSuccess() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess();
            }
        });
    }

    public void postFailure(final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(e);
            }
        });
    }
}
