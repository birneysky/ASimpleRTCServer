package com.zijin.ibeacon.http;

import java.util.concurrent.CountDownLatch;

import org.json.JSONException;

import android.app.Activity;
import android.util.Log;

public abstract class AppSyncThread extends HttpThread {
    private AppRequest request;
    protected AppResponse response;
    private String TAG = "AppThread";
    protected CountDownLatch cdlatch;

    public AppSyncThread(Activity activity, AppRequest request) {
        super(activity);
        this.request = request;
    }

    public AppSyncThread(Activity activity, AppRequest request,CountDownLatch lch) {
    	super(activity);
    	this.request = request;
    	this.cdlatch = lch;
    }

    @Override
    protected void executeHttp() throws Exception, JSONException {
        String responseXml = session.post(request);
        Log.i("response", responseXml);
        response = new AppResponse(responseXml);
    }
    @Override
    protected void onFailure(Exception e) {
        LoadingIndicator.cancel();
        Log.d(TAG, e.getMessage());
        if (gotoLogin(e.getMessage())) return;
        showErrorMessage(e.getMessage());
    }

    @Override
	protected void postSuccess() {
    	getData();
	}

	protected void showInfoMessage(String message) {
        if (gotoLogin(message)) return;
        MessageBox.show(activity, message, null);
    }


    protected void showErrorMessage(String message) {
        if (gotoLogin(message)) return;
        MessageBox.show(activity, message);
    }

    private boolean gotoLogin(String message) {
        return false;
    }

	@Override
	protected void onSuccess() {
	}
	protected abstract void getData();
}
