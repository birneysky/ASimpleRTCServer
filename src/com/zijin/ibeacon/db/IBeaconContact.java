package com.zijin.ibeacon.db;


public class IBeaconContact {
	protected interface BeaconColumns {
		/** use mac as ID*/
		public final static String ID="id";
		/** The user defined sensor name */
		public final static String NAME = "name";
		/** The beacon service uuid */
		public final static String UUID = "uuid";
		public final static String MAC="mac";
		/** The beacon major number */
		public final static String MAJOR = "major";
		/** The beacon minor */
		public final static String MINOR = "minor";
		/** The last signal strength in percentage */
		public final static String IMGURL = "imgurl";
		/** beacon set distance of alarm*/
		public final static String DISTANCE="distance";
		/** 1 if beacon notifications are enabled, 0 if disabled */
		public final static String ENABLED = "enabled";
		/** last position*/
		public final static String LASTADDRESS="lastAddress";
		/**last postion date time */
		public final static String LASTDATE="lastDate";
		
//		public final static String BEACONUSER = "beaconUser";//beacon所属的用户,传入手机号
		
		public final static String MODE="deviceMode";//设备模式全自动  手动
		public final static String SLEEPTIME="sleepTime";//休眠时间
		public final static String SLEEPTIMEMODE="sleepMode";//休眠模式 1 开 0 关
		public final static String REPEATTIME="repeatTime";//重复时间
		public final static String REPEATTIMEMODE="repeatMode";//休眠模式 1 开 0 关
		/**
		 * 众寻状态
		 * public static final int TRACKER_STATE_UNSELECTED = 0; // 未连接
			public static final int TRACKER_STATE_LOST =1;		// 已丢失
			public static final int TRACKER_STATE_TRACKING = 2;	// 正在防丢
		 */
		public final static String STATE="state";
		
		public static  String[] BEACON_PROJECTION = new String[] { BeaconColumns.ID, 
															 BeaconColumns.NAME,
															 BeaconColumns.UUID,
															 BeaconColumns.MAJOR,
															 BeaconColumns.MAC, 
															 BeaconColumns.MINOR, 
															 BeaconColumns.IMGURL, 
															 BeaconColumns.DISTANCE,
															 BeaconColumns.LASTADDRESS, 
															 BeaconColumns.LASTDATE, 
															 BeaconColumns.MODE,
															 BeaconColumns.SLEEPTIME,
															 BeaconColumns.REPEATTIME,
															 BeaconColumns.ENABLED,
															 BeaconColumns.STATE,
															 BeaconColumns.SLEEPTIMEMODE,
															 BeaconColumns.REPEATTIMEMODE
															 };
		
		
		
		
	}
}
