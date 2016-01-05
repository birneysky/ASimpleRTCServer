package com.zijin.ibeacon.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zijin.ibeacon.db.IBeaconContact.BeaconColumns;

public class SQLiteHelper extends SQLiteOpenHelper {
	/** Database version */
	private static final int DATABASE_VERSION = 2;
	/** Database file name */
	private static final String DATABASE_NAME = "beacons.db";
	
	public static final String BEACONS = "beacons";
	public static final String REGION_USER = "usertbl";
	public static final String BEACONS_SAFETY_AREA="beaconsArea";
	public static final String BEACONS_LOCATION="beaconsLocation";
			
	private static final String CREATE_BEACONS = "CREATE TABLE " + BEACONS + "(" 
												+ BeaconColumns.ID + " integer primary key autoincrement, " 	
												+ BeaconColumns.NAME+ " TEXT, " 
												+ BeaconColumns.UUID + " TEXT , " 
												+ BeaconColumns.MAC + " TEXT NOT NULL, " 
												+ BeaconColumns.MAJOR + " INTEGER , " 
												+ BeaconColumns.MINOR + " INTEGER, "
												+ BeaconColumns.IMGURL + " TEXT, " 
												+ BeaconColumns.DISTANCE + " INTEGER DEFAULT(1), " 
												+ BeaconColumns.LASTADDRESS + " TEXT, "
												+ BeaconColumns.LASTDATE + " TEXT, " 
												+ BeaconColumns.MODE + " TEXT, " 
												+ BeaconColumns.SLEEPTIME + " TEXT, " 
												+ BeaconColumns.REPEATTIME + " TEXT, " 
												+ BeaconColumns.ENABLED + " INTEGER NOT NULL DEFAULT(0)," 
												+ BeaconColumns.STATE+ " INTEGER NOT NULL DEFAULT(1),"
												+ BeaconColumns.SLEEPTIMEMODE+ " varchar,"
												+ BeaconColumns.REPEATTIMEMODE+ " varchar"
												+");";
	private static final String CREATE_USERS = "CREATE TABLE " + REGION_USER + "("+ 
												"id integer primary key autoincrement, " +
												" beaconID INTEGER not null, " +//设备编号
												" areaName varchar(50) not null, " +
												" mLat double, " +
												" mLng double, " +
												" address varchar(200))";
	private static final String CREATE_AREA = "CREATE TABLE " + BEACONS_SAFETY_AREA + "("+ 
											"id integer primary key autoincrement, " +
											" beaconID integer not null, " +
											" areaName varchar(50) not null, " +
											" address varchar(200), " +
											" southWest text, " +
											" northEast text)";
	private static final String Beacon_LOCALTION = "CREATE TABLE " + BEACONS_LOCATION + "("+ 
												"id integer primary key autoincrement, " +
												" beaconID integer not null, " +
												" address varchar(200), " +
												" date varchar(50) not null, " +
												" time varchar(50))";
			

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	/**
	 * 创建所有的表
	 * @param db 
	 */
	private void createTables(SQLiteDatabase db) {
		db.execSQL(CREATE_BEACONS);
		db.execSQL(CREATE_USERS);
		db.execSQL(CREATE_AREA);
		db.execSQL(Beacon_LOCALTION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
//			db.execSQL("select * from "+BEACONS_SAFETY_AREA+" where id = "+BEACONS_SAFETY_AREA+" and name = radius ");
			
//			if(newVersion == 2 && oldVersion == 1){
//				db.execSQL("ALTER TABLE "+BEACONS_SAFETY_AREA+" ADD COLUMN radius");
//				 Cursor cr = db.query(BEACONS_SAFETY_AREA, null, null, null, null, null, null);
//				 while(cr.moveToNext()){
//					 int id = cr.getInt(cr.getColumnIndex("id"));
//					 ContentValues values = new ContentValues();
//					 values.put("id", id);
//					 values.put("radius", 50);
//					 db.update(BEACONS_SAFETY_AREA, values, " id = ?", new String[id]);
//				 }
//			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.execSQL("DROP TABLE IF EXISTS " + BEACONS);
		db.execSQL("DROP TABLE IF EXISTS " + REGION_USER);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_AREA);
		db.execSQL("DROP TABLE IF EXISTS " + BEACONS_LOCATION);
		onCreate(db);
	}

}
