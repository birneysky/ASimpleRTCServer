package com.zijin.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zijin.ibeacon.db.SQLiteHelper;
import com.zjjin.entity.TrackerSafityArea;

public class IbeaconAreaDao {
	private static IbeaconAreaDao instance;
	private static SQLiteHelper mhelper;
	private static SQLiteDatabase mDatabase;
	private static final String ID_SELECTION = "id=?";
	private static final String BEACONID_SELECTION = "beaconID=?";
	private static final String ID = "id";
	private static final String BEACONID = "beaconID";
	private static final String AREANAME = "areaName";
	private static final String ADDRESS = "address";
	private static final String SOUTHWEAST = "southWest";
	private static final String NORTHEAST = "northEast";
//	private static final String BEACONUSER = "beaconUser";
	private String[] mSingleArg = new String[1];
	private String[] mParamsArg = new String[3];
	private String[] mCloumns={ID,"beaconID","areaName","address","southWest","northEast"};
	
	public IbeaconAreaDao(){
		
	}
	
	public static IbeaconAreaDao getInstance(Context context){
		if(instance ==null){
			instance = new IbeaconAreaDao();
			mhelper = new SQLiteHelper(context);
			mDatabase= mhelper.getWritableDatabase();
		}
		return instance;
	}
	
	/**
	 * 添加区域
	 * @param beacon
	 * @param phoneNum
	 * @return
	 */
	public long addArea(TrackerSafityArea area) {
		final ContentValues values = new ContentValues();
		values.put(BEACONID, area.getBeaconID());
		values.put(AREANAME, area.getAreaName());
		values.put(ADDRESS, area.getAddress());
		values.put(SOUTHWEAST, area.getSouthWest());
		values.put(NORTHEAST, area.getNorthEast());
		return mDatabase.insert(SQLiteHelper.BEACONS_SAFETY_AREA, null, values);
	}
	
	/**
	 * 按照Id删除
	 * @param id
	 */
	public void deleteArea(int id) {
		mSingleArg[0] = String.valueOf(id);
		mDatabase.delete(SQLiteHelper.BEACONS_SAFETY_AREA, ID_SELECTION, mSingleArg);
	}
	
	/**
	 * 按照beaconId删除
	 * @param id
	 */
	public int deleteAreaByBeaconId(int beaconid) {
		mSingleArg[0] = String.valueOf(beaconid);
		int result = mDatabase.delete(SQLiteHelper.BEACONS_SAFETY_AREA, BEACONID_SELECTION, mSingleArg);
		Log.i("info", "deleteAreaByBeaconId = " + result);
		return result;
	}

	/***
	 * 更新名称
	 * @param id
	 * @param name
	 * @return
	 */
	public int updateName(final long id, final String name) {
		mSingleArg[0] = String.valueOf(id);
		final ContentValues values = new ContentValues();
		values.put("areaName", name);
		return mDatabase.update(SQLiteHelper.BEACONS_SAFETY_AREA, values, ID_SELECTION, mSingleArg);
	}
	

	/**
	 * 根据id获取设备
	 * @param id
	 * @return
	 */
	public TrackerSafityArea findByBeaconId(String  id) {
		TrackerSafityArea trackerArea=null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = id;
		final ContentValues values = new ContentValues();
		values.put("id", id);
		 Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS_SAFETY_AREA, mCloumns, ID_SELECTION, mSingleArg, null, null, null);
		 if(cursor !=null){
			 trackerArea = new TrackerSafityArea();
			if(cursor.moveToNext()){ 
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setSouthWest(cursor.getString(cursor.getColumnIndex(SOUTHWEAST)));
				trackerArea.setNorthEast(cursor.getString(cursor.getColumnIndex(NORTHEAST)));
			}
		 }
		 return trackerArea;
	}
	
	/**
	 * 获取所有安全区域
	 * @return
	 */
	public ArrayList<TrackerSafityArea> getAllAreas() {
		ArrayList<TrackerSafityArea>  trackers=null;
		mDatabase = mhelper.getReadableDatabase();
		Cursor cursor =  mDatabase.query(SQLiteHelper.BEACONS_SAFETY_AREA, mCloumns, null, null, null, null, null);
		if(cursor !=null){
			trackers= new ArrayList<TrackerSafityArea>();
			while(cursor.moveToNext()){
				TrackerSafityArea trackerArea = new TrackerSafityArea();
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setSouthWest(cursor.getString(cursor.getColumnIndex(SOUTHWEAST)));
				trackerArea.setNorthEast(cursor.getString(cursor.getColumnIndex(NORTHEAST)));
				trackers.add(trackerArea);
			}
			cursor.close();
		}
		return trackers;
	}
	
	/**
	 * 获取所有安全区域根据设备ID
	 * 难道不是获取该设备已开启的安全区域么？
	 * @return
	 */
	public ArrayList<TrackerSafityArea> getAreasByBeaconID(int beaconid) {
		ArrayList<TrackerSafityArea>  trackers=null;
		mDatabase = mhelper.getReadableDatabase();
		mSingleArg[0] = beaconid+"";
//		String selection = BEACONID + " = ? and " + BEACONUSER + " = ?";
//		String[] selectionArgs = new String[]{String.valueOf(beaconid), phoneNum};
		 Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS_SAFETY_AREA, mCloumns, BEACONID_SELECTION, mSingleArg, null, null, null);
//		Cursor cursor = mDatabase.query(SQLiteHelper.BEACONS_SAFETY_AREA, mCloumns, selection, selectionArgs, null, null, null);
		if(cursor !=null){
			trackers= new ArrayList<TrackerSafityArea>();
			while(cursor.moveToNext()){
				TrackerSafityArea trackerArea = new TrackerSafityArea();
				trackerArea.setId(cursor.getInt(cursor.getColumnIndex("id")));
				trackerArea.setBeaconID(cursor.getInt(cursor.getColumnIndex("beaconID")));
				trackerArea.setAreaName(cursor.getString(cursor.getColumnIndex("areaName")));
				trackerArea.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				trackerArea.setSouthWest(cursor.getString(cursor.getColumnIndex(SOUTHWEAST)));
				trackerArea.setNorthEast(cursor.getString(cursor.getColumnIndex(NORTHEAST)));
				trackers.add(trackerArea);
			}
			cursor.close();
		}
		return trackers;
	}

}
