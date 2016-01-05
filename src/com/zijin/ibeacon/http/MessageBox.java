package com.zijin.ibeacon.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;

public abstract class MessageBox {
    public static void show(Activity activity, String message) {
        show(activity, message, null);
    }

    public static void show(Activity activity, String message, OnClickListener listener) {
        new AlertDialog.Builder(activity).setMessage(message).setPositiveButton("确定", listener).show();
    }
}
