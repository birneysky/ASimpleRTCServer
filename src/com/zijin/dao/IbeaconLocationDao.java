package com.zijin.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zijin.ibeacon.db.SQLiteHelper;
import com.zjjin.entity.TrackerLocation;

/**
 *位置记录操作DAO
 * @author Qinwq
 *
 */
public class IbeaconLocationDao {
	private static IbeaconLocationDao instance;
	private static SQLiteHelper mhelper;
	private static SQLiteDatabase mDatabase;
	private static final String ID_SELECTION = "id=?";
	private static final String BEACONID_SELECTION = "beaconID=?";
	private String[] mSingleArg = new String[1];
	private String[] mParamsArg = new String[3];
	private String mOrderBY ="date desc,time desc";
	public IbeaconLocationDao(){
		
	}
	
	public static IbeaconLocationDao getInstance(Context context){
		if(instance ==null){
			instance = new IbeaconLocationDao();
			mhelper = new SQLiteHelper(context);
			mDatabase= mhelper.getWritableDatabase();
		}
		return instance;
	}
	
	/**
	 * 添加位置
	 * @param beacon
	 * @return
	 */
	public long addLocation(TrackerLocation area) {
		final ContentValues values = new ContentValues();
		values.put("beaconID", area.getBeaconID());
		values.put("address", area.getAddress());
		values.put("date", area.getDate());
		values.put("time", area.getTime());
		return mDatabase.insert(SQLiteHelper.BEACONS_LOCATION, null, values);
	}
	
	/**
	 * 按照Id删除
	 * @param id
	 */
	public void deleteLocation(int id) {
		mSingleArg[0] = String.valueOf(id);
		int result = mDatabase.delete(SQLiteHelper.BEACONS_LOCATION, ID_SELECTION, mSingleArg);
	}
	
	/**
	 * 按照beaconId删除
	 * @param id
	 */
	public int deleteLocationByBeaconId(int beaconid) {
		mSingleArg[0] = String.valueOf(beaconid);
		int result = mDatabase.delete(SQLiteHelper.BEACONS_LOCATION, BEACONID_SELECTION, mSingleArg);
		Log.i("info", "deleteLocationByBeaconId = " + result);
		return result;
	}


	/**
	 * 根据id获取设备
	 * @param id
	 * @return
	 */
	public TrackerLocation findByBeaconId(String  id) {
		TrackerLocation trackerArea=null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = id;
		final ContentValues values = new ContentValues();
		values.put("id", id);
		 Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS_LOCATION, TrackerLocation.mCloumns, ID_SELECTION, mSingleArg, null, null, null);
		 if(cursor !=null){
			 trackerArea = new TrackerLocation();
			if(cursor.moveToNext()){ 
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setDate(cursor.getString(cursor.getColumnIndex("date")));
				trackerArea.setTime(cursor.getString(cursor.getColumnIndex("time")));
			}
		 }
		 return trackerArea;
	}
	/**
	 * 根据beaconID获取最近一条位置记录
	 * @param id
	 * @return
	 */
	public TrackerLocation findByBeaconIdDesc(String  id) {
		TrackerLocation trackerArea=null;
		List<TrackerLocation> listLocation = null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = id;
		final ContentValues values = new ContentValues();
		values.put("id", id);
		Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS_LOCATION, TrackerLocation.mCloumns, BEACONID_SELECTION, mSingleArg, null, null, mOrderBY);
		if(cursor !=null){
			listLocation = new ArrayList<TrackerLocation>();
			if(cursor.moveToNext()){ 
				trackerArea = new TrackerLocation();
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setDate(cursor.getString(cursor.getColumnIndex("date")));
				trackerArea.setTime(cursor.getString(cursor.getColumnIndex("time")));
				listLocation.add(trackerArea);
			}
		}
		if(listLocation.size() != 0){
			return listLocation.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 获取位置记录信息
	 * @return
	 */
	public ArrayList<TrackerLocation> getAllAreas() {
		ArrayList<TrackerLocation>  trackers=null;
		mDatabase = mhelper.getReadableDatabase();
		Cursor cursor =  mDatabase.query(SQLiteHelper.BEACONS_LOCATION, TrackerLocation.mCloumns, null, null, null, null, mOrderBY);
		if(cursor !=null){
			trackers= new ArrayList<TrackerLocation>();
			while(cursor.moveToNext()){
				TrackerLocation trackerArea = new TrackerLocation();
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setDate(cursor.getString(cursor.getColumnIndex("date")));
				trackerArea.setTime(cursor.getString(cursor.getColumnIndex("time")));
				trackers.add(trackerArea);
			}
			cursor.close();
		}
		return trackers;
	}
	
	/**
	 * 获取位置记录信息根据设备ID
	 * @return
	 */
	public ArrayList<TrackerLocation> getAreasByBeaconID(int beaconid) {
		ArrayList<TrackerLocation>  trackers=null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = beaconid+"";
		 Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS_LOCATION, TrackerLocation.mCloumns, BEACONID_SELECTION, mSingleArg, null, null, mOrderBY);
		if(cursor !=null){
			trackers= new ArrayList<TrackerLocation>();
			while(cursor.moveToNext()){
				TrackerLocation trackerArea = new TrackerLocation();
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setDate(cursor.getString(cursor.getColumnIndex("date")));
				trackerArea.setTime(cursor.getString(cursor.getColumnIndex("time")));
				trackers.add(trackerArea);
			}
			cursor.close();
		}
		return trackers;
	}

}
