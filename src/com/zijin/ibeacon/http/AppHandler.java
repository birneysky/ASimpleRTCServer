package com.zijin.ibeacon.http;

import android.os.Looper;


public abstract class AppHandler {
    protected AppRequest request;
    protected AppResponse response;

	protected abstract void handle(AppRequest request, AppResponse response);
}
