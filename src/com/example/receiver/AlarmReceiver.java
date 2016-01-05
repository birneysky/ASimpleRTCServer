package com.example.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.zijin.dao.IbeaconDao;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.Tracker;
import com.zjjin.utils.Consts;

public class AlarmReceiver extends BroadcastReceiver {
	
	private final String TAG = AlarmReceiver.class.getSimpleName();
	private IbeaconDao ibeaconDao;
	
	public AlarmReceiver(Context context){
		ibeaconDao = IbeaconDao.getInstance(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String address = intent.getStringExtra("address");
			Log.i(TAG, "收到  action = " + action + ", address = " + address);
			
			// 获取Tracker，判断模式，判断action是start还是end
			// 休眠时间之内不报警，否则不操作，即自动报警 状态
			Tracker tracker 	= ibeaconDao.findByBeaconMac(address);
			int state = tracker.getState();	
			String 	sleepMode 	= tracker.getSleepTimesMode();
			String 	repeatMode 	= tracker.getRepeatTimesMode();
			String 	repeatTimes = tracker.getRepeatTimes();
			String 	currentWeek = Utils.getWeekOfDate();
			Log.i(TAG, "sleepMode="+sleepMode+", repeatMode="+repeatMode+", repeatTimes="+repeatTimes+", currentWeek="+currentWeek);
			// Tracker的状态是开还是关或者是其它
			if(state == Consts.TRACKER_STATE_TRACKING){
				if("1".equals(repeatMode)){ // 重复，24小时进行提醒
					if(repeatTimes != null){
						String[] repeatTime = repeatTimes.split(",");
						for(int i=0; i<repeatTime.length; i++){
							if(currentWeek.equals(repeatTime[i])){
								Log.i(TAG, "repeatTime 中包含今天 ");
								if("1".equals(sleepMode)){ // 开，进行休眠，
									Log.i(TAG, "休眠开关今天开启。");
									if(Consts.ACTION_SET_START_ALARM.equals(action)){
										// 当前Tracker开始睡眠，不工作了
										Log.i(TAG, "休眠开关开启，开始休眠时间到了，让设备睡觉。");
										ibeaconDao.updateEnabled(address, false);
									}else if(Consts.ACTION_SET_END_ALARM.equals(action)){
										// 当前Tracker开始正常工作
										Log.i(TAG, "休眠开关开启，开始休眠时间到了，让设备工作。");
										ibeaconDao.updateEnabled(address, true);
									}
								}else{ // 关，不进行休眠
									// 如果是关闭状态
									// 当前Tracker开始睡眠，不工作了
									Log.i(TAG, "休眠开关今天关闭，时间到了，让设备工作。");
									ibeaconDao.updateEnabled(address, true);
								}
								break;
							}
						}
						Log.i(TAG, "重复开关打开， 重复周期中不包含今天，今天闹钟不响应。");
					}	
					
				}else{
					Log.i(TAG, "重复开关关闭。");
					if("1".equals(sleepMode)){ // 开，进行休眠，
						Log.i(TAG, "重复开关关闭。休眠开关开启。");
						if(Consts.ACTION_SET_START_ALARM.equals(action)){
							// 当前Tracker开始睡眠，不工作了
							Log.i(TAG, "重复开关关闭。休眠开关开启。开始休眠。只进行一次响应，然后取消闹钟");
							ibeaconDao.updateEnabled(address, false);
						}else if(Consts.ACTION_SET_END_ALARM.equals(action)){
							// 当前Tracker开始正常工作
							Log.i(TAG, "重复开关关闭。休眠开关开启。结束休眠。只进行一次响应，然后取消闹钟");
							ibeaconDao.updateEnabled(address, true);
						}
					}else{ // 关，不进行休眠
						// 如果是关闭状态
						// 当前Tracker开始睡眠，不工作了
						Log.i(TAG, "重复开关关闭。休眠开关关闭。");
						ibeaconDao.updateEnabled(address, false);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
