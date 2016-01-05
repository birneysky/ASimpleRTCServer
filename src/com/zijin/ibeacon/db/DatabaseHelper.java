/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package com.zijin.ibeacon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zijin.ibeacon.db.IBeaconContact.BeaconColumns;
import com.zijin.ibeacon.model.IbeaconDevice;

public class DatabaseHelper {
	private static final String[] USER_PRIJECTION = new String[]{"_id", "userphonenum","userpassword","username","sex","headPicture","city","address","signature"};
	private SQLiteHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;
	private String[] mSingleArg = new String[1];
	private String[] mParamsArg = new String[3];

	public DatabaseHelper(Context context) {
		mDatabaseHelper = new SQLiteHelper(context);
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * 查询所有用户
	 * @return
	 */
	public Cursor getUser(){
		mDatabase = mDatabaseHelper.getReadableDatabase();
		return mDatabase.query(SQLiteHelper.REGION_USER, USER_PRIJECTION, null, null, null, null, null);
	}
	/**
	 * 修改用户密码
	 * @param phoneNum
	 * @param password
	 * @return
	 */
	public int updataUser(String phoneNum, String password){
		ContentValues values = new ContentValues();
		values.put("userphonenum", phoneNum);
		values.put("userpassword", password);
		return mDatabase.update(SQLiteHelper.REGION_USER, values, null, null);
	}
	/**
	 * 根据用户名和密码查找
	 * @param username
	 * @param pass
	 * @return
	 */
	public Cursor  findUser(String username,String pass){
		mDatabase = mDatabaseHelper.getReadableDatabase();
		String selection = " userphonenum = ? ";
		String[] selectionArgs = new String[]{username};
		return mDatabase.query(SQLiteHelper.REGION_USER, USER_PRIJECTION, selection, selectionArgs, null, null, null);
	}
	/**
	 * 注册新用户
	 * @param phoneNum
	 * @param password
	 * @return
	 */
	public long addUser(String phoneNum,String password){
		final ContentValues values = new ContentValues();
		values.put("userphonenum", phoneNum);
		values.put("userpassword", password);
	   return mDatabase.insert(SQLiteHelper.REGION_USER, null, values);
	}
}
