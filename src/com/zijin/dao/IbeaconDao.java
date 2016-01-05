package com.zijin.dao;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zijin.ibeacon.db.IBeaconContact;
import com.zijin.ibeacon.db.SQLiteHelper;
import com.zjjin.entity.Tracker;

public class IbeaconDao extends IBeaconContact{
	private static final String TAG = IbeaconDao.class.getSimpleName();
	private static IbeaconDao instance;
	private static SQLiteHelper mhelper;
	private static SQLiteDatabase mDatabase;
	private static final String ID_SELECTION = BeaconColumns.ID + "=?";
	private static final String MAC_SELECTION = BeaconColumns.MAC + "=?";
	private static final String ENABLED_SELECTION = BeaconColumns.ENABLED + "=?";
//	private static final String BEACON_USER = BeaconColumns.BEACONUSER + "=?";
	private String[] mSingleArg = new String[1];
	private IbeaconDao(){
		
	}
	
	public static IbeaconDao getInstance(Context context){
		if(instance ==null){
			instance = new IbeaconDao();
			mhelper = new SQLiteHelper(context);
			mDatabase= mhelper.getWritableDatabase();
			Log.i(IbeaconDao.class.getSimpleName(), mDatabase.getPath());
		}
		return instance;
	}
	
	/**
	 * 添加设备
	 * @param beacon
	 * @return
	 */
	public Tracker addBeacon(Tracker beacon) {
		if(beacon.getDevice_addr().length() == 0){
			return null;
		}
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.NAME, beacon.getName());
		values.put(BeaconColumns.UUID, beacon.getUuid());
		values.put(BeaconColumns.MAC, beacon.getDevice_addr());
		values.put(BeaconColumns.MAJOR, beacon.getMajor());
		values.put(BeaconColumns.MINOR, beacon.getMinor());
		values.put(BeaconColumns.IMGURL, beacon.getTrackerIconPath());
		values.put(BeaconColumns.MODE, ("0".equals(beacon.getDevice_mode())||"".equals(beacon.getDevice_mode()))?0:1);
		values.put(BeaconColumns.ENABLED,beacon.getEnabled());//default ennable
		values.put(BeaconColumns.SLEEPTIMEMODE, "0");
		values.put(BeaconColumns.STATE, beacon.getState());
		values.put(BeaconColumns.REPEATTIMEMODE, "0");//default ennable
		values.put(BeaconColumns.SLEEPTIME, beacon.getSleepTimes());//default ennable
		values.put(BeaconColumns.REPEATTIME, beacon.getRepeatTimes());//default ennable
		values.put(BeaconColumns.DISTANCE, beacon.getDistance());//default ennable
//		values.put(BeaconColumns.BEACONUSER, phoneNum);//当前用户手机号
		long result = mDatabase.insert(SQLiteHelper.BEACONS, null, values);
		if(result == -1){
			Log.e(TAG, " an error occurred ");
			return null;
		}
		Tracker tracker = this.findByBeaconMac(beacon.getDevice_addr());
		return tracker;
	}
	
	/**
	 * 按照id删除设备
	 * @param id
	 */
	@SuppressWarnings("static-access")
	public int deleteBeacon(int id) {
		mSingleArg[0] = new String().valueOf(id);
		int result = mDatabase.delete(SQLiteHelper.BEACONS, ID_SELECTION, mSingleArg);
		return result;
	}
	
	/**
	 * 更新设备地址
	 * @param id
	 * @param lastaddress
	 * @param lastdate
	 * @return
	 */
	public int updateLastAddr(String id,String lastaddress,String lastdate) {
		final ContentValues values = new ContentValues();
		mSingleArg[0] = String.valueOf(id);
		values.put(BeaconColumns.LASTADDRESS, lastaddress);
		values.put(BeaconColumns.LASTDATE, lastdate);
		return mDatabase.update(SQLiteHelper.BEACONS, values, ID_SELECTION, mSingleArg);
	}
	/**
	 * 更新设备休眠模式
	 * @param id
	 * @param lastaddress
	 * @param lastdate
	 * @return
	 */
	public int updateSleepMode(int id,String sleepMode) {
		final ContentValues values = new ContentValues();
		mSingleArg[0] = String.valueOf(id);
		values.put(BeaconColumns.SLEEPTIMEMODE, sleepMode);
		return mDatabase.update(SQLiteHelper.BEACONS, values, ID_SELECTION, mSingleArg);
	}
	
	/**
	 * 更新设备重复模式
	 * @param id
	 * @param lastaddress
	 * @param lastdate
	 * @return
	 */
	public int updateRepeatMode(int id,String repeatMode) {
		final ContentValues values = new ContentValues();
		mSingleArg[0] = String.valueOf(id);
		values.put(BeaconColumns.REPEATTIMEMODE, repeatMode);
		return mDatabase.update(SQLiteHelper.BEACONS, values, ID_SELECTION, mSingleArg);
	}
	
	/**
	 * 更新设备休眠时间
	 * @param mac
	 * @param lastaddress
	 * @param lastdate
	 * @return
	 */
	public int updateSleepTime(String mac,String sleepTime) {
		final ContentValues values = new ContentValues();
		mSingleArg[0] = mac;
		values.put(BeaconColumns.SLEEPTIME, sleepTime);
		return mDatabase.update(SQLiteHelper.BEACONS, values, MAC_SELECTION, mSingleArg);
	}
	/**
	 * 更新设备重复  周一 周二 周三 周四 周五、工作日 1,2,3,4,5 周六周日、周末 6,7
	 * @param id
	 * @param lastaddress
	 * @param lastdate
	 * @return
	 */
	public int updateRepeatTime(String mac,String repeatTime) {
		final ContentValues values = new ContentValues();
		mSingleArg[0] = mac;
		values.put(BeaconColumns.REPEATTIME, repeatTime);
		return mDatabase.update(SQLiteHelper.BEACONS, values, MAC_SELECTION, mSingleArg);
	}

	/***
	 * 更新设备名称
	 * @param id
	 * @param name
	 * @return
	 */
	public int updateName(final long id, final String name) {
		mSingleArg[0] = String.valueOf(id);
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.NAME, name);
		return mDatabase.update(SQLiteHelper.BEACONS, values, ID_SELECTION, mSingleArg);
	}
	
	/**
	 * 更改设备是否可用
	 * @param mac
	 * @param enabled
	 * @return
	 */
	public int updateEnabled(String mac,  boolean enabled) {
		mSingleArg[0] = mac;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.ENABLED, enabled ? "1" : "0");
		return mDatabase.update(SQLiteHelper.BEACONS, values, MAC_SELECTION, mSingleArg);
	}
	
	/**
	 * 更改设备众寻状态
	 * @param mac
	 * @param enabled
	 * @return
	 */
	public int updateSatateByMac(final String mac, String state) {
		mSingleArg[0] =mac;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.STATE, state);
		return mDatabase.update(SQLiteHelper.BEACONS, values, MAC_SELECTION, mSingleArg);
	}
	/**
	 * 更改设备模式
	 * @param mac
	 * @param enabled
	 * @return
	 */
	public int updateModeByMac(final String mac, String mode) {
		mSingleArg[0] =mac;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.MODE, mode);
		return mDatabase.update(SQLiteHelper.BEACONS, values, MAC_SELECTION, mSingleArg);
	}
	
	/**
	 * 更改设备距离
	 * @param mac
	 * @param distance
	 * @return
	 */
	public int updateDistanceByMac(final String mac, int distance) {
		mSingleArg[0] =mac;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.DISTANCE, distance);
		return mDatabase.update(SQLiteHelper.BEACONS, values, MAC_SELECTION, mSingleArg);
	}
	
	/**
	 * 更改设备图片
	 * @param id
	 * @param imgurl
	 * @return
	 */
	public int updateImgUrl(final String id, final String imgurl) {
		mSingleArg[0] = id;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.IMGURL, imgurl);
		return mDatabase.update(SQLiteHelper.BEACONS, values, ID_SELECTION, mSingleArg);
	}

	/**
	 * 根据id获取设备
	 * @param id
	 * @return
	 */
	public Tracker findByBeaconId(String  id) {
		Tracker tracker=null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = id;
//		final ContentValues values = new ContentValues();
//		values.put(BeaconColumns.ID, id);
		Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS, BeaconColumns.BEACON_PROJECTION, ID_SELECTION, mSingleArg, null, null, null);
		if(cursor !=null){
			tracker = new Tracker();
			if(cursor.moveToNext()){ 
				tracker.setId(cursor.getInt(cursor.getColumnIndex(BeaconColumns.ID)));
				tracker.setName(cursor.getString(cursor.getColumnIndex(BeaconColumns.NAME)));
				tracker.setMajor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAJOR)));
				tracker.setMinor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MINOR)));
				tracker.setUuid(cursor.getString(cursor.getColumnIndex(BeaconColumns.UUID)));
				tracker.setDevice_addr(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAC)));
				tracker.setDevice_mode(cursor.getString(cursor.getColumnIndex(BeaconColumns.MODE)));
				tracker.setTrackerIconPath(cursor.getString(cursor.getColumnIndex(BeaconColumns.IMGURL)));
				tracker.setSleepTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIME)));
				tracker.setRepeatTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIME)));
				tracker.setState(cursor.getInt(cursor.getColumnIndex(BeaconColumns.STATE)));
				tracker.setSleepTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIMEMODE)));
				tracker.setRepeatTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIMEMODE)));
				tracker.setDistance(cursor.getInt(cursor.getColumnIndex(BeaconColumns.DISTANCE)));
			}
		 }
		 return tracker;
	}
	
	/**
	 * 根据Mac获取设备
	 * @param id
	 * @return
	 */
	public Tracker findByBeaconMac(String  Mac) {
		Tracker tracker=null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = Mac;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.MAC, Mac);
		 Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS, BeaconColumns.BEACON_PROJECTION, MAC_SELECTION, mSingleArg, null, null, null);
		 if(cursor !=null){
			 tracker = new Tracker();
			if(cursor.moveToNext()){ 
				tracker.setId(cursor.getInt(cursor.getColumnIndex(BeaconColumns.ID)));
				tracker.setName(cursor.getString(cursor.getColumnIndex(BeaconColumns.NAME)));
				tracker.setMajor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAJOR)));
				tracker.setMinor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MINOR)));
				tracker.setUuid(cursor.getString(cursor.getColumnIndex(BeaconColumns.UUID)));
				tracker.setDevice_addr(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAC)));
				tracker.setDevice_mode(cursor.getString(cursor.getColumnIndex(BeaconColumns.MODE)));
				tracker.setTrackerIconPath(cursor.getString(cursor.getColumnIndex(BeaconColumns.IMGURL)));
				tracker.setSleepTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIME)));
				tracker.setRepeatTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIME)));
				tracker.setState(cursor.getInt(cursor.getColumnIndex(BeaconColumns.STATE)));
				tracker.setSleepTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIMEMODE)));
				tracker.setRepeatTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIMEMODE)));
				tracker.setDistance(cursor.getInt(cursor.getColumnIndex(BeaconColumns.DISTANCE)));
			}
		 }
		 return tracker;
	}
	/**
	 * 获取所有设备
	 * @return
	 */
	public ArrayList<Tracker> getAllBeacons() {
		ArrayList<Tracker>  trackers=null;
		mDatabase = mhelper.getReadableDatabase();
//		String selection = BeaconColumns.BEACONUSER+" = ?";
//		String[] selectionArgs = new String[]{phoneNum};
		Cursor cursor =  mDatabase.query(SQLiteHelper.BEACONS, BeaconColumns.BEACON_PROJECTION, null, null, null, null, null);
		if(cursor !=null){
			trackers= new ArrayList<Tracker>();
			while(cursor.moveToNext()){
				Tracker tracker = new Tracker();
				tracker.setId(cursor.getInt(cursor.getColumnIndex(BeaconColumns.ID)));
				tracker.setName(cursor.getString(cursor.getColumnIndex(BeaconColumns.NAME)));
				tracker.setMajor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAJOR)));
				tracker.setMinor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MINOR)));
				tracker.setUuid(cursor.getString(cursor.getColumnIndex(BeaconColumns.UUID)));
				tracker.setDevice_addr(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAC)));
				tracker.setDevice_mode(cursor.getString(cursor.getColumnIndex(BeaconColumns.MODE)));
				tracker.setTrackerIconPath(cursor.getString(cursor.getColumnIndex(BeaconColumns.IMGURL)));
				tracker.setSleepTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIME)));
				tracker.setRepeatTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIME)));
				tracker.setState(cursor.getInt(cursor.getColumnIndex(BeaconColumns.STATE)));
				tracker.setSleepTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIMEMODE)));
				tracker.setRepeatTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIMEMODE)));
				tracker.setDistance(cursor.getInt(cursor.getColumnIndex(BeaconColumns.DISTANCE)));
				trackers.add(tracker);
			}
			cursor.close();
		}
		return trackers;
	}

	/**
	 * 获取打开的设备 0 disable 1 ennable
	 * ennable:开关状态
	 * @return
	 */
	public ArrayList<Tracker> findBeaconsByEnable(String ennable) {
		ArrayList<Tracker>  trackers=null;
		mDatabase = mhelper.getReadableDatabase();
//		String selection = BeaconColumns.ENABLED + " = ? and " + BeaconColumns.BEACONUSER + " = ? ";
//		String[] selectionArgs = new String[]{ennable, phoneNum};
		mSingleArg[0] = ennable;
		final ContentValues values = new ContentValues();
		values.put(BeaconColumns.ENABLED, ennable);
		Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS, BeaconColumns.BEACON_PROJECTION, ENABLED_SELECTION, mSingleArg, null, null, null);
//		Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS, BeaconColumns.BEACON_PROJECTION, selection, selectionArgs, null, null, null);
		if(cursor !=null){
			trackers= new ArrayList<Tracker>();
			while(cursor.moveToNext()){
				Tracker tracker = new Tracker();
				tracker.setId(cursor.getInt(cursor.getColumnIndex(BeaconColumns.ID)));
				tracker.setName(cursor.getString(cursor.getColumnIndex(BeaconColumns.NAME)));
				tracker.setMajor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAJOR)));
				tracker.setMinor(cursor.getString(cursor.getColumnIndex(BeaconColumns.MINOR)));
				tracker.setUuid(cursor.getString(cursor.getColumnIndex(BeaconColumns.UUID)));
				tracker.setDevice_addr(cursor.getString(cursor.getColumnIndex(BeaconColumns.MAC)));
				tracker.setDevice_mode(cursor.getString(cursor.getColumnIndex(BeaconColumns.MODE)));
				tracker.setTrackerIconPath(cursor.getString(cursor.getColumnIndex(BeaconColumns.IMGURL)));
				tracker.setSleepTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIME)));
				tracker.setRepeatTimes(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIME)));
				tracker.setState(cursor.getInt(cursor.getColumnIndex(BeaconColumns.STATE)));
				tracker.setSleepTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.SLEEPTIMEMODE)));
				tracker.setRepeatTimesMode(cursor.getString(cursor.getColumnIndex(BeaconColumns.REPEATTIMEMODE)));
				trackers.add(tracker);
			}
			cursor.close();
		}
		return trackers;
	}
	
	
	
}
