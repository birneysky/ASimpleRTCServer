package com.zjjin.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zijin.dao.IbeaconDao;
import com.zjjin.entity.Tracker;

public class AlarmUtils {
	
	
	/**
     * 设置重复闹钟
     * 
     * @param context
     * @param timeInMillis
     */
	public static PendingIntent setAlarmTime(Context context, long timeInMillis, String action, String address) {
		IbeaconDao dao = IbeaconDao.getInstance(context);  
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		  Intent intent = new Intent(action);
		  intent.putExtra("address", address);
		  Tracker tracker = dao.findByBeaconMac(address);
		  PendingIntent sender = 
				  PendingIntent.getBroadcast(
						  context, tracker.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		  int interval = 5 * 60 * 1000; //闹铃间隔， 这里设为1分钟闹一次，在第2步我们将每隔1分钟收到一次广播 7 * 24 * 60 * 60 * 1000
		  am.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, interval, sender);
		  return sender;
	}
	
	/**
     * 设置一次闹钟
     * 
     * @param context
     * @param timeInMillis
     */
	public static PendingIntent setAlarmTimeOnce(Context context, long timeInMillis, String action, String address) {
		IbeaconDao dao = IbeaconDao.getInstance(context);  
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		  Intent intent = new Intent(action);
		  intent.putExtra("address", address);
		  Tracker tracker = dao.findByBeaconMac(address);
		  PendingIntent sender = 
				  PendingIntent.getBroadcast(
						  context, tracker.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		  am.set(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
		  return sender;
	}
	public void cancelAlarmTime(Context context, PendingIntent sender){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
	}
	
	/**
     * 取消闹钟
     * @param context
     * @param intent
     */
    public static void cancelAlarm(Context context, Intent intent, String address) {
    	try {
			IbeaconDao dao = IbeaconDao.getInstance(context); 
			Intent _intent = new Intent(intent.getAction()); // 必须重新new一个Intent，而不能直接用下面这行
			// Intent _intent = intent;
			Tracker tracker = dao.findByBeaconMac(address);
			PendingIntent sender = 
					PendingIntent.getBroadcast(
							context, tracker.getId(), _intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.cancel(sender);
			Log.i(context.getClass().getSimpleName(), "闹钟被取消了！");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
