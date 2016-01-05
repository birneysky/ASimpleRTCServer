package com.example.slidingmenu;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.m;

import com.baidu.location.BDLocation;
import com.example.fragment.SearchedMessageActivity;
import com.example.published.FileUtils;
import com.example.published.PublishedActivity;
import com.example.receiver.AlarmReceiver;
import com.google.gson.Gson;
import com.view.circleimageview.CircleImageView;
import com.zijin.dao.IbeaconAreaDao;
import com.zijin.dao.IbeaconDao;
import com.zijin.dao.IbeaconLocationDao;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zijin.ibeacon.service.BluetoothConnector;
import com.zijin.ibeacon.service.BluetoothLeService;
import com.zijin.ibeacon.service.BluetoothSences;
import com.zijin.ibeacon.service.BluetoothSences.CallBack;
import com.zijin.ibeacon.util.BaiduUtils;
import com.zijin.ibeacon.util.BaiduUtils.Callback;
import com.zijin.ibeacon.util.ImageLoaderUtil;
import com.zijin.ibeacon.util.ImageLoaderUtil.ImageLoaderCallBack;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.adapter.GridViewAdapter;
import com.zjjin.entity.HelpFound;
import com.zjjin.entity.Tracker;
import com.zjjin.entity.TrackerLocation;
import com.zjjin.entity.TrackerSafityArea;
import com.zjjin.utils.AlarmUtils;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;
import com.zjjin.utils.DateFormatUtils;
import com.zjjin.utils.LogService;
import com.zjjin.utils.ScreenUtils;


@SuppressLint({ "NewApi", "HandlerLeak" })
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressWarnings("unused")
public class MainActivity extends Activity{
	private static final String TAG =MainActivity.class.getSimpleName();
	private static final int SLEEP_MODE = 11;
	private SlidingMenu mMenu; // 整个布局
	private ImageView ivMenuLeft;
	private ImageView ivSearchedMessage;
	private CircleImageView ivUserIcon;
	private int screenWidth, screenHeight;
	private GridView gridViewTrackers;
	private GridViewAdapter gvAdapter;
	private ArrayList<Tracker> lists; // gridView 的 填充数据
	private PopupWindow pop;
	private LayoutInflater inflater;
	private HorizontalScrollView layoutBtns;
	private LinearLayout layoutSeekBar;
	private RelativeLayout layoutLocation, layoutMode, layoutSleepMode, layoutRepeatMode, layoutSafityArea, layoutSearchedMessage, layoutSearchedwords, layoutSearchArea;
	private ToggleButton tbtnSleepMode, tbtnRepeatMode; // 开关
	private OnClickListener sleepModeListener, repeatModeListener; // 单击事件监听器
	private boolean sleepModeClickable, repeatModeClickable; // 开关控制是否可以进入新页面
	private Button btnOpen, btnDelete, btnViewPosition, btnStartSearch, btnSearchedCancle; //popupWindow上面
	private View view; // 防丢器属性view
	private  int mSlectedItem = -1; // 当前防丢器在adapter中的位置（size-1）
	private Tracker tracker; // 当前防丢器
	private String changeAddress;
	private SeekBar mSeekBar;
	private TextView txtSeekBarShow,txtIbeaconMode, tvSleepTime, tvRepeatMode, tvSafityArea,tvLocationTime,tvLocationAddr,tvSearchingUser,tvSearchingTimes,tvUsername;
	private SharedPreferences usersp;
	
	/**侧边菜单*/
	private RelativeLayout menuQuestion, menuSuggest, menuAbout, menuShare, menuExit;
	/**线程管理*/
//	private HashMap<String,Thread> threads =new HashMap<String,Thread>();
	private BluetoothLeService mService;
	private static HashMap<String,Integer> mAlarms = new HashMap<String,Integer>(); // mac地址，距离
	private static Map<String,Long> mTimestmp=Collections.synchronizedMap(new HashMap<String,Long>());//定时场景阀值
	/**报警器*/
	private static MediaPlayer mMediaPlayer=null;
	//场景线程
	private BluetoothSences myThread;
	private BluetoothAdapter mBluetoothAdapter;
	private IbeaconApplication application;
	/** 数据库操作*/
	private IbeaconDao  beaconDAO;
	private IbeaconLocationDao mLocationDAO;
	private IbeaconAreaDao areaDao;
	/**
	 * popupWindow按钮监听
	 */
	OnClickListener myClickListener = new OnClickListener() {  
        @Override  
        public void onClick(View v) {
        	switch(v.getId()){
        	case R.id.btn_tracker_state_start_search: // 发布众寻
        		Tracker tracker = lists.get(mSlectedItem);// TODO ?
        		int stateL = tracker.getState();
        		String device_addrL = tracker.getDevice_addr();
        		if(device_addrL.length() != 0){
        			Intent intentSearchL = new Intent(MainActivity.this, PublishedActivity.class);
        			intentSearchL.putExtra("data", tracker);
        			startActivityForResult(intentSearchL, Consts.REQUEST_PUBLISHED_SEARCH);
        		}else{
        			Utils.showMsg(getApplicationContext(), "发布众寻失败，iBeacon无法获取");
        		}
        		break;
        	case R.id.btn_tracker_state_open:{ //open ibeacon
        		tracker = lists.get(mSlectedItem);
        		String str_open = getResources().getString(R.string.str_open_ibeacon);
        		String str_close = getResources().getString(R.string.str_close_ibeacon);
        		int state = tracker.getState();
        		String device_addr = tracker.getDevice_addr();
				if (tracker != null && device_addr != null && device_addr.length() != 0) {
					switch (state) {
					case Consts.TRACKER_STATE_LOST: // 丢失，发起众寻
//						Intent intentSearch = new Intent(MainActivity.this, PublishedActivity.class);
//						intentSearch.putExtra("data", tracker);
//						startActivityForResult(intentSearch, Consts.REQUEST_PUBLISHED_SEARCH);
						break;
					case Consts.TRACKER_STATE_ICON_CHANGE_RED:
						mService.removeMonitor(device_addr);
						updateConnectionState(device_addr, BluetoothLeService.BLE_GATT_DISCONNECTED);
						break;
					case Consts.TRACKER_STATE_TRACKING: // 正在工作，关闭贴片
						mService.removeMonitor(device_addr);
						updateConnectionState(device_addr, BluetoothLeService.BLE_GATT_DISCONNECTED);
						break;
					case Consts.TRACKER_STATE_UNSELECTED: // 未连接，打开贴片
						mService.addMonitor(device_addr);
						break;
					case Consts.TRACKER_STATE_SEARCHING: // 正在众寻，取消众寻
//						cancelFind();
						break;
					case Consts.TRACKER_STATE_ICON_CHANGE_BACK:
						mService.removeMonitor(device_addr);
						updateConnectionState(device_addr, BluetoothLeService.BLE_GATT_DISCONNECTED);
						break;
						
					}
				}
        	};break;
        	case R.id.btn_tracker_state_view_position:{//圆按钮，查看位置view postion
        		Intent intent = new Intent(MainActivity.this,BaiduMapActivity.class);
        		startActivityForResult(intent, BaiduMapActivity.REQUEST_BAIDU_LOCATION);
        	};break;
        	case R.id.layout_tracker_mode:{//设备防丢模式
        		  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	              builder.setIcon(R.drawable.ic_launcher);
	              builder.setTitle("请选择模式");
	              //    指定下拉列表的显示数据
	              final String[] modes = {"全自动", "手动"};//0 自动 1在手动
	              //    设置一个下拉的列表选择项
	              builder.setItems(modes, new DialogInterface.OnClickListener(){
	                  @Override
	                  public void onClick(DialogInterface dialog, int which){
	                  	txtIbeaconMode.setText(modes[which]);
	                  	Tracker tracker = lists.get(mSlectedItem);// TODO ?
	                   	if(tracker !=null){
	                   		tracker.setDevice_mode(which+"");
	                  		txtIbeaconMode.setText(modes[which]);
	                  		beaconDAO.updateModeByMac(tracker.getDevice_addr(),which+"");
	                   	}
	                   	if(which==1 && mSeekBar!=null){//手动模式
	                   		mSeekBar.setEnabled(true);
	                   	}else{
	                   		mSeekBar.setEnabled(false);
	                   	}
	                  }
	              });
	              builder.show();
        	};break;
			case R.id.btn_tracker_state_delete: {// del ibeacon
				Tracker trackerdel = lists.get(mSlectedItem);
				if (trackerdel != null) {
					String mac = trackerdel.getDevice_addr();
					if(mac != null){
						int result = beaconDAO.deleteBeacon(trackerdel.getId());// 删除数据库
						if(result == 0){
							Utils.showMsg(getApplicationContext(), "删除失败，请重新删除！");
							return;
						}
						deleteDataAboutTracker(trackerdel);
						mService.removeMonitor(mac);
						btnOpen.setEnabled(true);
						Utils.ibeaconArr.remove(mac);
						mAlarms.remove(mac);
						lists.remove(trackerdel);
						gvAdapter = new GridViewAdapter(getApplicationContext(), lists);
						gridViewTrackers.setAdapter(gvAdapter);
						if (pop != null && pop.isShowing()) {
							// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
							pop.dismiss();
						}
					}
				}
			};break;
        	case R.id.layout_linear_location_record: // 位置记录
        		Intent intent = new Intent(MainActivity.this, LocationNoteActivity.class);
        		tracker = lists.get(mSlectedItem);
        		if(tracker != null){
        			int trackerId = tracker.getId();
        			intent.putExtra("iBeaconId",trackerId);
        		}
        		startActivity(intent);
        		break;
        	case R.id.layout_tracker_safity_area: // 安全区域
        		try {
					Intent intentSafity = new Intent(MainActivity.this, SafityAreaActivity.class);
					tracker = lists.get(mSlectedItem);
					if(tracker!=null){
						int trackerId = tracker.getId();
						intentSafity.putExtra("iBeaconId",trackerId);
					}
					startActivityForResult(intentSafity, Consts.REQUEST_CHANGE_SAFITY_AREA);
				} catch (Exception e) {
					e.printStackTrace();
				}
        		break;
    		default:
    			break;
        	}
        }  
    };
	private PendingIntent sleepSender;
	private AlarmReceiver alarmReceiver;
	private PendingIntent wakeupSender;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.i(TAG, "收到行动指令——————"+msg.what);
			switch (msg.what) {
			case Consts.MESSAGE_CHANGE_USER_ICON:
				updateIcon(msg.getData());
				break;
			case 1211://在我的众寻中删除众寻后，这里也要取消众寻
				tracker.setState(Consts.TRACKER_STATE_UNSELECTED);
				gvAdapter.updateState(tracker.getDevice_addr(), Consts.TRACKER_STATE_UNSELECTED+"");
				beaconDAO.updateSatateByMac(tracker.getDevice_addr(), Consts.TRACKER_STATE_UNSELECTED+"");
				break;
			case Consts.MESSAGE_ALARM_ALERT:
				final String address = (String) msg.obj;
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);  
					builder.setTitle("提示");  
					builder.setMessage("信号消失，设备丢失");  
					builder.setPositiveButton("确定",  
					        new DialogInterface.OnClickListener() {  
					            public void onClick(DialogInterface dialog, int whichButton) {  
				            	// 发消息，取消响铃,是否需要更换红色为蓝色？
				            	mService.bleMissAlarmCancel(address);
					            }  
					        });  
				builder.show();
				alarmStart(address);
				break;
			case Consts.MESSAGE_SHOW_ALERT_DIALOG:
				final String alarmAddress = (String) msg.obj;
				AlertDialog.Builder builderA = new AlertDialog.Builder(MainActivity.this);  
				builderA.setTitle("提示");  
				builderA.setMessage("设备距离达到警报阈值");  
				builderA.setPositiveButton("确定",  
				        new DialogInterface.OnClickListener() {  
				            public void onClick(DialogInterface dialog, int whichButton) {  
			            	// 取消响铃,主界面不用标识哪个设备么？
			            	alarmStop(alarmAddress);
				            }  
				        });  
				builderA.show();
				break;
			case Consts.MESSAGE_CHECE_SENCE_ON_NET:
				String areaMac = (String)msg.obj;
				final int rssi = msg.arg1;
				AppRequest request = new AppRequest();
				request.setmRequestURL("/msg/CloudMsgAction!uuid");
				request.putPara("Mac", areaMac);
				new AppThread(MainActivity.this, request, new AppHandler() {
					@Override
					protected void handle(AppRequest request, AppResponse response) {
						// {"message":"非场景","data":"-1","code":"0"}
						if ("0".equals(response.getmCode())) {//成功返回
							double disVal = Double.parseDouble(response.getData());
							double distanceFact = Utils.calculateDistance(rssi);
							if(disVal == Double.parseDouble("-1")){//非场景
								//1，将当前打开的所有设备设置手动模式
								for(int i = 0; i < lists.size(); i++){
									Tracker track = lists.get(i);
									if("1".equals(track.getEnabled())){
										track.setDevice_mode("1");
										lists.set(i, track);
									}
								}
								//2，获取设备防丢距离，判断该距离与当前设备实时距离之间的关系
							}else{
								//1，将当前打开的所有设备设置为自动模式，并且更新防丢距离
								for(int i = 0; i < lists.size(); i++){
									Tracker track = lists.get(i);
									if("1".equals(track.getEnabled())){
										track.setDevice_mode("0");
										track.setDistance((int)disVal);
										mAlarms.put(track.getDevice_addr(), (int)disVal);
										lists.set(i, track);
									}
								}
								//2,判断设备是否在场景有效距离范围内：若在，没有警报；若不在，警报。
							}
							if(pop != null){
								if(pop.isShowing()) {  
					                tracker = lists.get(mSlectedItem);
					                setDevice(tracker);
					            }
							}
						}
					}
				}).start();
				break;
			default:
				break;
			}
		}
	};
	private TextView tvShowDistanceSeekbar;
	private TrackerLocation location;
	//记录日志服务
	private LogService logService;
	private ServiceConnection conn = new ServiceConnection() {
		/** 无法获取到服务对象时的操作 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			logService = null;
		}
		/** 获取服务对象时的操作 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			logService = ((LogService.ServiceBinder)service).getService();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = (IbeaconApplication) getApplication();
		setupView();
		getData();
		setData();
		addListener();
		setupMediaPlayer();
		bindService();
		initialize();
		IbeaconApplication.getInstance().addActivity(this);
		registReceivers();
		//日志记录服务
		Intent stateService = new Intent (this, LogService.class);
	    bindService(stateService, conn , Context.BIND_AUTO_CREATE);
	}
	
	private void updateIcon(Bundle data) {
		byte[] bmdata = data.getByteArray("data");
		Bitmap bm = BitmapFactory.decodeByteArray(bmdata, 0, bmdata.length);
		ivUserIcon.setImageBitmap(bm);
	}
	/** 
	 * 动态注册接收器
	 */
	private void registReceivers() {
		alarmReceiver = new AlarmReceiver(MainActivity.this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Consts.ACTION_SET_START_ALARM );
		filter.addAction(Consts.ACTION_SET_END_ALARM );
		registerReceiver(alarmReceiver, filter);
		
		registerReceiver(mBleReceiver, BluetoothLeService.getIntentFilter());
	}
	/**
	 * 删除相关的关联数据
	 * @param tracker
	 */
	protected void deleteDataAboutTracker(Tracker tracker) {
		int result = mLocationDAO.deleteLocationByBeaconId(tracker.getId());
		int res = areaDao.deleteAreaByBeaconId(tracker.getId());
		//qinwq 是否需要删除发布的众寻?
	}
	private void bindService() {
		Intent bindIntent = new Intent(this, BluetoothLeService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void getData() {
		lists = new ArrayList<Tracker>();
		beaconDAO = IbeaconDao.getInstance(MainActivity.this);
		mLocationDAO= IbeaconLocationDao.getInstance(MainActivity.this);
		areaDao = IbeaconAreaDao.getInstance(MainActivity.this);
		ArrayList<Tracker> retList = beaconDAO.getAllBeacons();
		if(retList != null && retList.size()>0){
			lists = retList;
			for(Tracker tracker:retList){
				Utils.ibeaconArr.add(tracker.getDevice_addr());
				mAlarms.put(tracker.getDevice_addr(), tracker.getDistance());
			}
		}
		gvAdapter = new GridViewAdapter(getApplicationContext(), lists);
		gridViewTrackers.setAdapter(gvAdapter);
	}
	

	private void setupView() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		inflater = LayoutInflater.from(MainActivity.this);
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
		ivMenuLeft = (ImageView) mMenu.findViewById(R.id.iv_title_bar_left);
		ivSearchedMessage = (ImageView) mMenu.findViewById(R.id.iv_title_bar_right_add);
		ivUserIcon = (CircleImageView)findViewById(R.id.iv_user_photo);
		tvUsername = (TextView)findViewById(R.id.tv_user_name);
		gridViewTrackers = (GridView)mMenu.findViewById(R.id.gridView_trackers);
		menuQuestion = (RelativeLayout)mMenu.findViewById(R.id.layout_menu_question);
		menuSuggest  = (RelativeLayout)mMenu.findViewById(R.id.layout_menu_suggest);
		menuAbout    = (RelativeLayout)mMenu.findViewById(R.id.layout_menu_about);
		menuShare 	 = (RelativeLayout)mMenu.findViewById(R.id.layout_menu_share);
		menuExit 	 = (RelativeLayout)mMenu.findViewById(R.id.layout_menu_exit);
	}
	/**
	 * 设置头像
	 */
	private void setData() {
		screenWidth = ScreenUtils.getScreenWidth(this);
		screenHeight = ScreenUtils.getScreenHeight(this);
		try {
			//设置头像
			final String iconPath = usersp.getString(ConstsUser.USERPHOTOADDRESS, null);
			String username = usersp.getString(ConstsUser.USERNAME, null);
			if(ivUserIcon!=null && iconPath!=null){
				if(FileUtils.fileIsExists(Consts.IMG_PATH+iconPath)){
					Bitmap iconBmp = BitmapFactory.decodeFile(Consts.IMG_PATH+iconPath);
					if(iconBmp!=null){
						ivUserIcon.setImageBitmap(iconBmp);
					}
				}else{
					ImageLoaderUtil imageLoader = ImageLoaderUtil.getInstance(MainActivity.this);
					imageLoader.getImage(ivUserIcon, Utils.mServerImgPath+iconPath);
					imageLoader.setCallBack(new ImageLoaderCallBack() {
						@Override
						public void refreshAdapter() {
//							Utils.showMsg(MainActivity.this, "");
							Bitmap iconBmp = BitmapFactory.decodeFile(Consts.IMG_PATH+iconPath);
							if(iconBmp!=null){
								ivUserIcon.setImageBitmap(iconBmp);
							}
						}
					});	
				}
				
			}
			if(username!=null){
				tvUsername.setText(username);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final int REQUEST_ENABLE_BT = 1;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
			Utils.showMsg(getApplicationContext(), "请打开蓝牙再使用此应用");
            return;
        }
		if(resultCode == BaiduMapActivity.REQUEST_BAIDU_LOCATION && data != null){//查看位置记录，返回地图坐标，地址，时间
			double mBaiduLat = data.getDoubleExtra("lat",0.0);
			double mBaiduLot = data.getDoubleExtra("lot",0.0);
			String locationStr = data.getStringExtra("locationStr");
			if(locationStr !=null && locationStr.length() != 0){
				tracker = lists.get(mSlectedItem);
				if(tracker !=null){
					TrackerLocation location = new TrackerLocation();
					location.setBeaconID(tracker.getId());
					location.setAddress(locationStr);
					location.setDate(Utils.getCurrentDate());
					location.setTime(Utils.getCurrentTime());
					// 添加进位置记录数据库
					mLocationDAO.addLocation(location);
				}
			}
//			updateLocation();
			//TODO 更新popupwindow显示
			initLocation(tracker);
		}else if(resultCode == AddBeaconActivity.REQUEST_ADD_IBEACON && data != null){//add ibeacon return 
			Tracker tracker = new Tracker();
			String device_name = data.getStringExtra("tracker_name");
			String device_mac = data.getStringExtra("tracker_mac");
			String major = data.getStringExtra("major");
			String minor = data.getStringExtra("minor");
			String pic_path = data.getStringExtra("picPath");
			String sleepMode = data.getStringExtra("sleepMode")==null?"0":data.getStringExtra("sleepMode");
			tracker.setName(device_name);
			tracker.setState(Consts.TRACKER_STATE_UNSELECTED);
			tracker.setDevice_addr(device_mac);
			tracker.setMajor(major);
			tracker.setMinor(minor);
			tracker.setSleepTimesMode(sleepMode);//默认关闭
			tracker.setSleepTimes(getResources().getString(R.string.str_ibeacon_default_repeattimes));
			tracker.setRepeatTimes(getResources().getString(R.string.str_ibeacon_default_repeatdate));
			tracker.setRepeatTimesMode("0");
			tracker.setDevice_mode("1");//0全自动/1手动
			tracker.setDistance(5);
			tracker.setEnabled(0);
			if(pic_path !=null){
				tracker.setTrackerIconPath(pic_path);
			}
			//插入数据库：必须保证mac地址不为空，mac地址唯一，成功添加后再加入记录数组
			Tracker dbTracker = beaconDAO.addBeacon(tracker);
			if(dbTracker!=null){
				lists.add(dbTracker);
				gvAdapter = new GridViewAdapter(getApplicationContext(), lists);
				gridViewTrackers.setAdapter(gvAdapter);
				gvAdapter.notifyDataSetChanged();
				Utils.ibeaconArr.add(dbTracker.getDevice_addr());
			}else{
				Utils.showMsg(MainActivity.this,"添加设备失败");
			}
		} else if (requestCode == Consts.REQUEST_CHANGE_SLEEP_MODE && data != null){ // 修改休眠模式
			if(resultCode == Consts.RESULT_SLEEP_MODE_CHANGED){
				try {
					// 改变休眠时间之前先取消所有闹钟
					String startTime = data.getStringExtra(Consts.EXTRA_TIME_BEGIN);
					String endTime 	 = data.getStringExtra(Consts.EXTRA_TIME_END);
					tracker = lists.get(mSlectedItem);
					String newSleepTime = startTime + "-" + endTime;
					tvSleepTime.setText(newSleepTime);
					String address = tracker.getDevice_addr();
					tracker.setSleepTimes(newSleepTime);
					beaconDAO.updateSleepTime(address, newSleepTime);
					sendPendingIntent(address);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (requestCode == Consts.REQUEST_CHANGE_REPEAT_MODE && data != null){ // 修改重复模式
			if(resultCode == Consts.RESULT_REPEAT_MODE_CHANGED){
				String text = data.getStringExtra(Consts.EXTRA_KEY_REPEAT_MODE); // 周一  周二  周三
				String intText = data.getStringExtra("returnIntText"); // 1,2,3,4,5,6,7
				tracker = lists.get(mSlectedItem);
				tvRepeatMode.setText(text);
				//重复模式是指每周哪几天进行工作，工作时间唤醒tracker
				tracker.setRepeatTimes(intText);
				beaconDAO.updateRepeatTime(tracker.getDevice_addr(), intText);
			}
		} else if (requestCode == Consts.REQUEST_CHANGE_SAFITY_AREA && data != null){ // 添加安全区域
			if(resultCode == Consts.RESULT_SAFITY_AREA_CHANGED){
				String area = data.getStringExtra(Consts.EXTRA_KEY_SAFITY_AREA);
				tvSafityArea.setText(area);
			} else if (resultCode == RESULT_OK) { // TODO 
				
			}
		} else if (requestCode == Consts.REQUEST_PUBLISHED_SEARCH){ // 发起众寻结束
			//?防丢器图片没了
			if(resultCode == RESULT_OK){
				if(tracker != null){//更新本地设备状态为众寻
                	IbeaconDao mIbeaconDAO = IbeaconDao.getInstance(MainActivity.this);
                	tracker.setState(Consts.TRACKER_STATE_SEARCHING);
                	lists.set(mSlectedItem, tracker);
                	gvAdapter.updateState(tracker.getDevice_addr(), Consts.TRACKER_STATE_SEARCHING+"");
                	mIbeaconDAO.updateSatateByMac(tracker.getDevice_addr(), Consts.TRACKER_STATE_SEARCHING+"");
                }
				gvAdapter.notifyDataSetChanged();
				setViewVisible(Consts.TRACKER_STATE_SEARCHING);
				if(data != null){ // 查看刚发布的众寻
					Intent intent  = new Intent(MainActivity.this, SearchedMessageActivity.class);
					IbeaconApplication.getInstance().setHandler(handler);
					startActivity(intent);
				}
			}
		}
	}
	
	
	
	private void addListener() {
		sleepModeListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sleepModeClickable){
					Intent intent = new Intent(MainActivity.this, SleepModeActivity.class);
					intent.putExtra("sleepMode", tvSleepTime.getText().toString());
					startActivityForResult(intent, Consts.REQUEST_CHANGE_SLEEP_MODE);
				}
			}
		};
		
		repeatModeListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(repeatModeClickable){
					Intent intent = new Intent(MainActivity.this, RepeatModeActivity.class);
					tracker = lists.get(mSlectedItem);
					intent.putExtra("repeatMode", tracker.getRepeatTimes());
					startActivityForResult(intent, Consts.REQUEST_CHANGE_REPEAT_MODE);
				}
			}
		};
		
		ivMenuLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMenu.toggle();
			}
		});
		ivSearchedMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//帮助众寻，我的众寻
				Intent intent = new Intent(MainActivity.this, SearchedMessageActivity.class);
				IbeaconApplication.getInstance().setHandler(handler);
				startActivity(intent);
			}
		});
		
		ivUserIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IbeaconApplication.getInstance().setHandler(handler);
				Intent intent = new Intent(MainActivity.this, EditUserInfoActivity.class);
				startActivity(intent);
				mMenu.toggle();
			}
		});
        
		gridViewTrackers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
//				int[] locationS = new int[2];
//				arg1.getLocationOnScreen(locationS);
//				Log.i("info", "locationS:"+locationS[0]+", "+locationS[1]);
//				Log.i("info", "arg1W:"+arg1.getLeft()+", "+arg1.getTop()+", "+arg1.getRight()+", "+arg1.getBottom());
				mMenu.closeMenu();
				if(position == (gvAdapter.getCount()-1)){//单击添加按钮
					//暂时注释  qinwq 
					if(!application.isSupport){
						Toast.makeText(MainActivity.this, "手机不支持蓝牙,无法添加设备", Toast.LENGTH_LONG).show();
						return;
					}
					Intent intent = new Intent(MainActivity.this,AddBeaconActivity.class);
					startActivityForResult(intent,AddBeaconActivity.REQUEST_ADD_IBEACON);
					return;
				}
				//arg1是当前item的view，通过它可以获得该项中的各个组件。 arg2是当前item的ID。这个id根据你在适配器中的写法可以自己定义。arg3是当前的item在listView中的相对位置！
				// 选中标识箭头显示,现在被挤压的看不见了
				final com.zjjin.adapter.GridViewAdapter.ViewHolder holder = (com.zjjin.adapter.GridViewAdapter.ViewHolder) arg1.getTag();
				if(holder.ivSelectedBg.getVisibility() == View.GONE){
					holder.ivSelectedBg.setVisibility(View.VISIBLE);
				}
				
				// 如果不是第一行，向上移动再显示弹出项，没做完呢
//				if(arg3/3 != 0){
//					android.view.ViewGroup.LayoutParams params = gridViewTrackers.getLayoutParams();
//					params.height = screenHeight + 
//				}
				// 引入窗口配置文件  
				if(view == null){
					view = inflater.inflate(R.layout.tracker_unlinked, null); 
					setupPopView();
					addPopListener();
				}
		        mSlectedItem = position;
		        tracker = lists.get(position);
	        	initLocation(tracker);
	        	setDevice(tracker);
	        	if(pop == null){
	        		setupPop(parent, arg1, arg3, holder); 
	        	}
	        	if(pop.isShowing()) {  
	                // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏  
	                pop.dismiss();  
	            } else {  
	                // 显示窗口  
//	                pop.showAsDropDown(gvAdapter.getView((int)arg3, arg1, parent) , 0, 0);  
	                pop.showAsDropDown(gvAdapter.getView((int)arg3, arg1, parent));
	                int[] location = new int[2];  
	                arg1.getLocationOnScreen(location);  
//	                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
	            }
			}

			
		});
		
		menuQuestion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
				startActivity(intent);
			}
		});
		menuSuggest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SuggestActivity.class);
				startActivity(intent);
			}
		});
		menuAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AboutusActivity.class);
				startActivity(intent);
			}
		});
		menuShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMenu.toggle();
				showShare();
			}
		});
		menuExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginActivity.actionStart(getApplicationContext(), usersp.getString(ConstsUser.PHONENUM, null));
				application.exit();
				MainActivity.this.finish();
			}
		});
	}
	private void setupPop(AdapterView<?> parent, View arg1, long arg3,
			final com.zjjin.adapter.GridViewAdapter.ViewHolder holder) {
		// 创建PopupWindow对象  
    	pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
    	// 需要设置一下此参数，点击外边可消失  
    	pop.setBackgroundDrawable(new BitmapDrawable());  
    	//设置点击窗口外边窗口消失  
    	pop.setOutsideTouchable(false);  
    	// 设置此参数获得焦点，否则无法点击  
    	pop.setFocusable(true);
    	pop.setTouchable(true);
    	//设置PopupWindow消失的时候触发的事件
        pop.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
            	holder.ivSelectedBg.setVisibility(View.INVISIBLE);
            }
        });
        
	}
	/**
	 * 发送不同类型闹钟
	 * @param tracker
	 */
	private void sendPendingIntent(String address){
		// 先取消所有闹钟
		Intent intentStart 	= new Intent(Consts.ACTION_SET_START_ALARM);
		Intent intentEnd	= new Intent(Consts.ACTION_SET_END_ALARM);
		AlarmUtils.cancelAlarm(getApplicationContext(), intentStart, address);
		AlarmUtils.cancelAlarm(getApplicationContext(), intentEnd, address);
		
		// 再判断，根据4种不同情况发送不同闹钟
		IbeaconDao ibeaconDao = IbeaconDao.getInstance(getApplicationContext());
		Tracker tracker = ibeaconDao.findByBeaconMac(address);
		
		String sleepMode = tracker.getSleepTimesMode();
		String sleepTimes = tracker.getSleepTimes();
		String[] sleepTime = sleepTimes.split("-");
		String sleepStart = sleepTime[0];
		String sleepEnd = sleepTime[1];
		long sleepStartTime = DateFormatUtils.getLongTime(sleepStart);
		long sleepEndTime = DateFormatUtils.getLongTime(sleepEnd);
		long currentTime = System.currentTimeMillis();
		String repeatMode = tracker.getRepeatTimesMode();
		if("1".equals(repeatMode)){
			if("1".equals(sleepMode)){
				// 查看周期内的休眠时间
				// 检验当前周期与休眠时间的关系，发送重复闹钟
				AlarmUtils.setAlarmTime(getApplicationContext(), sleepStartTime, Consts.ACTION_SET_START_ALARM, address);
				AlarmUtils.setAlarmTime(getApplicationContext(), sleepEndTime, Consts.ACTION_SET_END_ALARM, address);
			}else{
				// 周期开，休眠时间关，
				AlarmUtils.setAlarmTime(getApplicationContext(), sleepEndTime, Consts.ACTION_SET_END_ALARM, address);
			}
		}else{
			if("1".equals(sleepMode)){
				// 周期关，休眠时间开，发送一次性休眠闹钟
				AlarmUtils.setAlarmTimeOnce(getApplicationContext(), sleepStartTime, Consts.ACTION_SET_START_ALARM, address);
				AlarmUtils.setAlarmTimeOnce(getApplicationContext(), sleepEndTime, Consts.ACTION_SET_END_ALARM, address);
			}else{
				// 周期关，休眠时间关，设置开启 enable = true
				AlarmUtils.setAlarmTimeOnce(getApplicationContext(), sleepEndTime, Consts.ACTION_SET_END_ALARM, address);
			}
		}
		
	}
	
	
	/**
	 * 每次打开Popwindows，获取设备最新状态
	 */
	protected void setDevice(Tracker tracker) {
		if(tracker == null)return;
		int distance = tracker.getDistance();
		String device_addr = tracker.getDevice_addr();
		Log.i(TAG, "distance = " + distance);
		if(distance == 0){//全部设置1米
			mAlarms.put(device_addr, 1);
			this.tracker.setDistance(1);
			tracker.setDistance(1);
			this.lists.set(mSlectedItem, tracker);
			beaconDAO.updateDistanceByMac(device_addr, 1);
		}
		mSeekBar.setProgress(tracker.getDistance());
		if(device_addr != null){
			int defaultVal = mAlarms.get(device_addr)==null?1:mAlarms.get(device_addr);
			txtSeekBarShow.setText(defaultVal+"米");
			tvShowDistanceSeekbar.setText(defaultVal+"米");
			mSeekBar.setProgress(defaultVal);
			Log.i(TAG, "text = " + mAlarms.get(device_addr)+", defaultVal = "+defaultVal);
		}
		// UI显示控制
		switch (tracker.getState()) {
		case Consts.TRACKER_STATE_SEARCHING: {// 正在众寻
			setViewVisible(Consts.TRACKER_STATE_SEARCHING);
			getSearchNumData();
		}
			break;
		case Consts.TRACKER_STATE_LOST: // 丢失
			btnOpen.setText(getResources().getString(R.string.str_search_ibeacon_title));
			setViewVisible(Consts.TRACKER_STATE_LOST);
			break;
		case Consts.TRACKER_STATE_TRACKING: // 正在工作
			btnOpen.setText(getResources().getString(R.string.str_close_ibeacon));
			setViewVisible(Consts.TRACKER_STATE_TRACKING);
			break;
		case Consts.TRACKER_STATE_UNSELECTED: // 未连接
			btnOpen.setText(getResources().getString(R.string.str_open_ibeacon));
			setViewVisible(Consts.TRACKER_STATE_UNSELECTED);
			break;
		case Consts.TRACKER_STATE_ICON_CHANGE_RED:
			btnOpen.setText(getResources().getString(R.string.str_close_ibeacon));
			setViewVisible(Consts.TRACKER_STATE_TRACKING);
			break;
		case Consts.TRACKER_STATE_ICON_CHANGE_BACK:
			btnOpen.setText(getResources().getString(R.string.str_close_ibeacon));
			setViewVisible(Consts.TRACKER_STATE_TRACKING);
			break;
		}
		//设置模式
		if("1".equals(tracker.getDevice_mode())){
			txtIbeaconMode.setText("手动");
		}else{
			txtIbeaconMode.setText("全自动");
		}
		//休眠模式
		if("1".equals(tracker.getSleepTimesMode())){
			tbtnSleepMode.setChecked(true);
			sleepModeClickable = true;
		}else{
			tbtnSleepMode.setChecked(false);
			sleepModeClickable = false;
		}
		
		//重复模式
		if("1".equals(tracker.getRepeatTimesMode())){
			tbtnRepeatMode.setChecked(true);
			repeatModeClickable = true;
		}else{
			tbtnRepeatMode.setChecked(false);
			repeatModeClickable = false;
		}
		//休眠时间
		tvSleepTime.setText(tracker.getSleepTimes());
		String mRepeatTimes = tracker.getRepeatTimes()==null?"1,2,3,4,5":tracker.getRepeatTimes();
		if("1,2,3,4,5".equals(mRepeatTimes)){
			tvRepeatMode.setText(getResources().getString(R.string.str_ibeacon_workdate));
		}else if("6,7".equals(mRepeatTimes)){
			tvRepeatMode.setText(getResources().getString(R.string.str_ibeacon_weekend));
		}else{
			mRepeatTimes.replace("1", "星期一");
			mRepeatTimes.replace("2", "星期二");
			mRepeatTimes.replace("3", "星期三");
			mRepeatTimes.replace("4", "星期四");
			mRepeatTimes.replace("5", "星期五");
			mRepeatTimes.replace("6", "星期六");
			mRepeatTimes.replace("7", "星期日");
			tvRepeatMode.setText(mRepeatTimes);
		}
		//获取安全区域
		List<TrackerSafityArea> areas = areaDao.getAreasByBeaconID(tracker.getId());
		if(areas!=null && areas.size()>0){
			tvSafityArea.setText(areas.get(0).getAreaName());
		}else{
			tvSafityArea.setText(getResources().getString(R.string.str_ibeacon_nosafetyarea));
		}
		
	}
	
	/**
	 * 获取上传记录 
	 */
	private void initLocation(Tracker tracker) {
		if(tracker==null){ 
			Utils.showMsg(MainActivity.this, "未找到选中的设备");
			return;
		}
		location = mLocationDAO.findByBeaconIdDesc(tracker.getId()+"");
		if(location != null){
			if(location.getAddress() !=null){
				tvLocationTime.setText(location.getDate()+" "+location.getTime());
				tvLocationAddr.setText(location.getAddress());
			}
		}
		else{
			 tvLocationTime.setText(getResources().getString(R.string.str_location_notime));
		     tvLocationAddr.setText(getResources().getString(R.string.str_location_address));
		}
	}
	/**
	 * 获取服务器查询次数等
	 */
	protected void getSearchNumData() {
		tracker = lists.get(mSlectedItem);
		if(tracker==null)return;
		//需要发送到后台
		AppRequest request = new AppRequest();
		request.setmRequestURL("/help/SearchHelpAction!helpedNum");
		request.putPara("clientType", "android");
		request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");
		request.putPara("findMac", tracker.getDevice_addr());
		new AppThread(MainActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					Gson gson = new Gson();
					HelpFound found = gson.fromJson(response.getData(), HelpFound.class);
					if(found!=null){
						if(found.getUserID()!=null){
							tvSearchingUser.setText("共有"+found.getUserID()+"用户");
							tvSearchingTimes.setText("共"+found.getHelpCount()+"次扫描");
						}
					}
				}
			}
		}).start();
	}
	/**
	 * 是否已经启动线程
	 */
	/*private boolean checkDevice(String address){
		return threads.containsKey(address);
	}*/
	private void setupPopView() {
		btnOpen = (Button)view.findViewById(R.id.btn_tracker_state_open);
		btnDelete = (Button)view.findViewById(R.id.btn_tracker_state_delete);
		btnViewPosition = (Button)view.findViewById(R.id.btn_tracker_state_view_position);
		btnStartSearch = (Button)view.findViewById(R.id.btn_tracker_state_start_search);
        layoutBtns = (HorizontalScrollView)view.findViewById(R.id.layout_btns);
        layoutSearchArea = (RelativeLayout)view.findViewById(R.id.layout_search_area);
        layoutSeekBar = (LinearLayout)view.findViewById(R.id.layout_seekbar);
        layoutLocation = (RelativeLayout)view.findViewById(R.id.layout_linear_location_record);
        tvLocationTime = (TextView)view.findViewById(R.id.txt_last_location_time); // 位置记录时间
        tvLocationAddr = (TextView)view.findViewById(R.id.txt_last_location); // 位置记录地址
        layoutMode = (RelativeLayout)view.findViewById(R.id.layout_tracker_mode); // 防丢模式
        layoutSleepMode = (RelativeLayout)view.findViewById(R.id.layout_auto_sleep);
        tvSleepTime = (TextView) view.findViewById(R.id.tv_tracker_sleep_mode);
        tbtnSleepMode = (ToggleButton)view.findViewById(R.id.tbtn_tracker_sleep_mode);
        layoutRepeatMode = (RelativeLayout)view.findViewById(R.id.layout_repeat_mode);
        tbtnRepeatMode = (ToggleButton)view.findViewById(R.id.tbtn_tracker_repeat);
        tvRepeatMode = (TextView)view.findViewById(R.id.tv_tracker_repeat_mode);
	    layoutSafityArea = (RelativeLayout)view.findViewById(R.id.layout_tracker_safity_area);    
        tvSafityArea = (TextView)view.findViewById(R.id.tv_tracker_safity_area);
        txtIbeaconMode = (TextView)view.findViewById(R.id.tv_tracker_mode_mode);
        tvSearchingUser = (TextView)view.findViewById(R.id.tv_searching_user);//帮助户数
        tvSearchingTimes = (TextView)view.findViewById(R.id.tv_searching_times);//帮助次数
        
        layoutSearchedMessage 	= (RelativeLayout)view.findViewById(R.id.layout_searchied_message);
        layoutSearchedwords 	= (RelativeLayout)view.findViewById(R.id.layout_searched_words);
        btnSearchedCancle 		= (Button)view.findViewById(R.id.btn_searched_cancle);
        
        mSeekBar = (SeekBar)view.findViewById(R.id.pop_seekbar);
        txtSeekBarShow = (TextView)view.findViewById(R.id.txt_seekbar_show);
        tvShowDistanceSeekbar = (TextView)view.findViewById(R.id.tv_show_distance_seekbar);
	}
	
	private void addPopListener() {
		/**
		 * 众寻留言
		 */
		layoutSearchedwords.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent wordsIntent = new Intent(MainActivity.this,SearchedMessageActivity.class);
				startActivity(wordsIntent);
			}
		});
		/**
		 * 取消众寻,更改本地状态为非众寻,删除云平台
		 */
		btnSearchedCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelFind();
			}
		});
		// 休眠模式开关改变
		tbtnSleepMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sleepModeClickable = isChecked;
				tracker = lists.get(mSlectedItem);
				beaconDAO.updateSleepMode(tracker.getId(), isChecked==true?"1":"0");//可用
				tracker.setSleepTimesMode(isChecked==true?"1":"0");
				sendPendingIntent(tracker.getDevice_addr());
			}
		});
		// 重复模式开关改变
        tbtnRepeatMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				repeatModeClickable = isChecked;
				tracker = lists.get(mSlectedItem);
				tracker.setRepeatTimesMode(isChecked==true?"1":"0");
				beaconDAO.updateRepeatMode(tracker.getId(), isChecked==true?"1":"0");//可用
				sendPendingIntent(tracker.getDevice_addr());
			}
		});
		btnOpen.setOnClickListener(myClickListener); // 打开贴片，关闭贴片
        btnDelete.setOnClickListener(myClickListener); // 删除贴片
        btnViewPosition.setOnClickListener(myClickListener); // 查看位置记录
        btnStartSearch.setOnClickListener(myClickListener); // 发起众寻
        layoutMode.setOnClickListener(myClickListener);
        layoutLocation.setOnClickListener(myClickListener);
        layoutSafityArea.setOnClickListener(myClickListener);
        layoutSleepMode.setOnClickListener(sleepModeListener);
        layoutRepeatMode.setOnClickListener(repeatModeListener);
        mSeekBar.setOnSeekBarChangeListener(seekBarListener);
	}
	private SeekBar.OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mSeekBar.setEnabled(true);
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			tracker = lists.get(mSlectedItem);
			if(tracker == null)	return;
			if("0".equals(tracker.getDevice_mode())){
				Utils.showMsg(MainActivity.this, getResources().getString(R.string.str_seekbar_disableseek));
				mSeekBar.setEnabled(false);
				return;
			}else{
				mSeekBar.setEnabled(true);
			}
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			//TODO 如果显示的是改变的设备，则改变显示；否则不显示，只是更新数据库
			Tracker tracker = lists.get(mSlectedItem);
			if(tracker == null)	return;
			if(txtSeekBarShow == null || tvShowDistanceSeekbar == null)return;
    		if(tracker.getDevice_addr() == null)	return;
    		/*if(fromUser){
    			if("0".equals(tracker.getDevice_mode())){
    				return;
    			}else{ //手动
    				txtSeekBarShow.setText(progress+"米");
    				tvShowDistanceSeekbar.setText(progress+"米");
    				String device_addr = tracker.getDevice_addr();
    				mAlarms.put(device_addr, progress);
        			beaconDAO.updateDistanceByMac(device_addr, progress);
        			tracker.setDistance(progress);
        			lists.set(mSlectedItem, tracker);
    			}
    		}else{
    			if("0".equals(tracker.getDevice_mode())){
    				
    			}else{
    				return;
    			}
    		}*/
    		
			if("0".equals(tracker.getDevice_mode())){
				mSeekBar.setEnabled(false);
			}else if("1".equals(tracker.getDevice_mode())){
				mSeekBar.setEnabled(true);
				txtSeekBarShow.setText(progress+"米");
				tvShowDistanceSeekbar.setText(progress+"米");
				String device_addr =tracker.getDevice_addr();
    			mAlarms.put(device_addr, progress);
    			beaconDAO.updateDistanceByMac(device_addr, progress);
    			tracker.setDistance(progress);
    			lists.set(mSlectedItem, tracker);
			}
		}
	};
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			String action = intent.getAction();
			Log.i(TAG, "action = " + action);
			if (BluetoothLeService.BLE_GATT_CONNECTED.equals(action)) {
				final BluetoothDevice device = extras.getParcelable(BluetoothLeService.EXTRA_DEVICE);
				Log.d(TAG,"设备连接");
				updateConnectionState(device.getAddress(),BluetoothLeService.BLE_GATT_CONNECTED);
//				invalidateOptionsMenu();
				startScanScene();//开启场景检测
			} else if (BluetoothLeService.BLE_GATT_DISCONNECTED.equals(action)) {
				Log.d(TAG,"设备断开连接");
				String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDR);
				updateConnectionState(address, BluetoothLeService.BLE_GATT_DISCONNECTED);
			} else if (BluetoothLeService.BLE_SERVICE_DISCOVERED.equals(action)) {
				Log.d(TAG,"设备被发现");
			} else if (BluetoothLeService.BLE_CHARACTERISTIC_CHANGED.equals(action)) {
				Log.d(TAG,"BLE_CHARACTERISTIC_CHANGED");
			} else if (BluetoothLeService.BLE_CHARACTERISTIC_NOTIFICATION
					.equals(action)) {
				Log.d(TAG,"BLE_CHARACTERISTIC_NOTIFICATION");
			} else if (BluetoothLeService.EXTRA_RSSI.equals(action)) {
				Log.d(TAG,"EXTRA_RSSI");
			} else if (BluetoothLeService.BLE_REQUEST_FAILED.equals(action)) {// 设备断开
				Log.d(TAG, "设备已经断开,清除设备");
				//设备断开连接，警报,此时只需要改变后面小圆圈指示
				String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDR);
				updateConnectionState(address, BluetoothLeService.BLE_REQUEST_FAILED);
				// TODO 警报,什么时候停止?
				alarmStart(address);
			} 
			else if (BluetoothLeService.BLE_MISSED_ALARM.equals(action)){
				// 此时停止警报,只需改变后面小圆圈指示红色变蓝色，监控信号那边要停止发送警报广播(什么时候置false？)
//				BluetoothConnector.stopAlarm = true;
				String address = intent.getStringExtra(BluetoothLeService.EXTRA_ADDR);
				alarmStop(address);
				updateConnectionState(address, BluetoothLeService.BLE_MISSED_ALARM);
			}
			else if (BluetoothLeService.BLE_MISSED_ALARM_ALERT.equals(action)){
				final String address = extras.getString(BluetoothLeService.EXTRA_ADDR);
				updateConnectionState(address, BluetoothLeService.BLE_MISSED_ALARM_ALERT);
				//向主线程发消息，告诉主弹框
				Message msg = new Message();
				msg.what = Consts.MESSAGE_ALARM_ALERT;
				msg.obj = address;
				handler.sendMessage(msg);
			}
			else if (BluetoothLeService.BLE_RSSI_READED.equals(action)) {
				int rssi = intent.getIntExtra(BluetoothLeService.EXTRA_RSSI, 0);
				Log.d(TAG,"获取到设备RSSI-----" + rssi);
				final BluetoothDevice device = extras.getParcelable(BluetoothLeService.EXTRA_DEVICE);
				/*if (device != null) {
					updateConnectionState(device.getAddress(), rssi,BluetoothLeService.BLE_RSSI_READED);
				}*/
				checkAlarm(device,rssi);
			}else if (BluetoothLeService.BLE_NO_BT_ADAPTER.equals(action)) {
				Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_LONG).show();
			}else if(BluetoothLeService.BLE_RSSI_ALARM.equals(action)){//设备警报
				final BluetoothDevice device = extras.getParcelable(BluetoothLeService.EXTRA_DEVICE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "设备发出警报："+ device.getAddress());
//						Toast.makeText(MainActivity.this, device.getAddress()+"发出警报", Toast.LENGTH_SHORT).show();
					}
				});
			}else if(BluetoothLeService.ACTION_PAIRING_REQUEST.equals(action)){
				Log.d(TAG, "BLE发出配对请求");
				final BluetoothDevice device = extras.getParcelable(BluetoothLeService.EXTRA_DEVICE);
				if(device != null){
					try {
						Utils.pair(device.getAddress());
						Utils.cancelPairingUserInput(device.getClass(), device); 
					} catch (Exception e) {  
						e.printStackTrace();  
					}
				}
				abortBroadcast();//取消传递
			}
		}
	};

	private HashMap<String, Integer> alarmTemp = new HashMap<String, Integer>();
	/** mAlarms：key:mac地址，value:距离
	 * 检测设备是否应该报警
	 * 条件：设备打开状态 重复模式为1
	 * @param device
	 * @param rssi
	 */
	private void checkAlarm(BluetoothDevice device, int rssi) {
		if(device ==null)return;
		String address = device.getAddress();
		int distance = mAlarms.get(address)==null?1:mAlarms.get(address);//场景距离5，默认距离1。??
		int currentDis = (int) Utils.calculateDistance(rssi)==0?1:(int) Utils.calculateDistance(rssi);
		Log.i(TAG, "rssi = " + rssi+", distance = " + distance + ", currentDis = "+currentDis);
		
		if(distance<currentDis){//警报阈值 并且非场景
			if(alarmTemp.containsKey(address)){
				int count = alarmTemp.get(address).intValue();
				if(count != 3){
					alarmTemp.put(address, Integer.valueOf(count + 1));
					return;
				}else{
					alarmTemp.remove(address);
				}
			}else{
				alarmTemp.put(address, Integer.valueOf(1));
				return;
			}
			
			Log.e(TAG, "警报阈值!");
			Message msg = new Message();
			msg.what = Consts.MESSAGE_SHOW_ALERT_DIALOG;
			msg.obj = address;
			handler.sendMessage(msg);
			alarmStart(address);
		}else{
			if(alarmTemp.containsKey(address)){
				alarmTemp.remove(address);
			}
			Log.e(TAG, "非警报阈值");
			alarmStop(address);
		}
	}
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
			mService = ((BluetoothLeService.LocalBinder) rawBinder).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mService = null;
		}
	};
	private Vibrator vibrator;
	
	/**
	 * 取消众寻
	 */
	private void cancelFind() {
		if(tracker.getDevice_addr() ==null){
			return;
		}
		//需要发送到后台
		AppRequest request = new AppRequest();
		request.setmRequestURL("/find/PubFindAction!cancelFind");
		request.putPara("clientType", "android");
		request.putPara("findMac", tracker.getDevice_addr());
		new AppThread(MainActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					tracker.setState(Consts.TRACKER_STATE_UNSELECTED);
					gvAdapter.updateState(tracker.getDevice_addr(), Consts.TRACKER_STATE_UNSELECTED+"");
					beaconDAO.updateSatateByMac(tracker.getDevice_addr(), Consts.TRACKER_STATE_UNSELECTED+"");
					
					Utils.showMsg(MainActivity.this, response.getmMessage());
				}else{
					Utils.showMsg(MainActivity.this, response.getmMessage());
				}
				if(pop!=null && pop.isShowing()){
					pop.dismiss();
				}
			}
		}).start();
	}
	
	/**
	 * 更改连接状态,更改按钮名称
	 * 当信号丢失，只需要更改小圆点并弹框提示，其他不用更改
	 * 点击弹框，提示消失，按钮还是显示“关闭贴片”，后台依然在扫描
	 * @param address
	 * @param status
	 */
	private void updateConnectionState(final String address, final String status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Tracker changedTracker = null;
				for(Tracker tracker : lists){
					if(address.equalsIgnoreCase(tracker.getDevice_addr())){
						changedTracker = tracker;
						break;
					}
				}
				Log.i(TAG, "status: "+status);
				if(BluetoothLeService.BLE_GATT_CONNECTED.equals(status)){//连接
					btnOpen.setText(getResources().getString(R.string.str_close_ibeacon));
					beaconDAO.updateEnabled(address, true);
//					Tracker trakcer =lists.get(mSlectedItem); 
					if(changedTracker==null)return;
					changedTracker.setEnabled(1);
					changedTracker.setState(Consts.TRACKER_STATE_TRACKING);
					gvAdapter.notifyDataSetChanged();
				}else if(BluetoothLeService.BLE_GATT_DISCONNECTED.equals(status)){//断开
					btnOpen.setText(getResources().getString(R.string.str_open_ibeacon));
					beaconDAO.updateEnabled(address, false);
//					Tracker trakcer =lists.get(mSlectedItem); 
					if(changedTracker==null)return;
					changedTracker.setEnabled(0);
					changedTracker.setState(Consts.TRACKER_STATE_UNSELECTED);//临时更改
					gvAdapter.notifyDataSetChanged();
				}else if(BluetoothLeService.BLE_REQUEST_FAILED.equals(status)){//丢失信号
//					beaconDAO.updateEnabled(address, false);
//					Tracker trakcer =lists.get(mSlectedItem); 
					if(changedTracker==null)return;
//					changedTracker.setEnabled(0);
//					changedTracker.setState(Consts.TRACKER_STATE_LOST);
					gvAdapter.updateIconState(address, status);
				}
				else if(BluetoothLeService.BLE_MISSED_ALARM.equals(status)){//红色点点变回蓝色点点
					if(changedTracker==null)return;
					gvAdapter.updateIconState(address, status);
				}
				else if (BluetoothLeService.BLE_MISSED_ALARM_ALERT.equals(status)){
					if(changedTracker==null)return;
					gvAdapter.updateIconState(address, status);
				}
				else if(BluetoothLeService.BLE_DEVICE_LOSTED.equals(status)){//已发布众寻 
					beaconDAO.updateEnabled(address, false);
//					Tracker trakcer =lists.get(mSlectedItem); 
					if(changedTracker==null)return;
					changedTracker.setEnabled(0);
					changedTracker.setState(Consts.TRACKER_STATE_SEARCHING);
					gvAdapter.notifyDataSetChanged();
				}
//				gvAdapter.updateState(address, status);
//				gvAdapter.notifyDataSetChanged();
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * 更新位置到数据库
	 */
	private void updateLocation() {
		final IbeaconLocationDao  locationDao = IbeaconLocationDao.getInstance(MainActivity.this);
		final BaiduUtils mBaidu = new BaiduUtils(MainActivity.this);
		mBaidu.setCallback(new Callback() {
			@Override
			public void onResult(BDLocation location) {
				if(location ==null)return;
				String date = Utils.getCurrentDate();
				String time = Utils.getCurrentTime();
				for(Tracker tracker:lists){
					TrackerLocation  loc =new TrackerLocation();
					loc.setBeaconID(tracker.getId());
					loc.setAddress(location.getAddrStr());
					loc.setTime(time);
					loc.setDate(date);
					locationDao.addLocation(loc);
				}
				mBaidu.stop();
			}
		});
		mBaidu.start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mService != null) {
			mService.stopConnectors();
		}
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		unbindService(mServiceConnection);
		unregisterReceiver(alarmReceiver);
		unregisterReceiver(mBleReceiver);
		//记录日志服务
		unbindService(conn);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		getData();
//		setData();
	}
	private HashMap<String, Boolean> recordMap;
	
	private void setupMediaPlayer() {
		mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.notice);
		mMediaPlayer.setLooping(true);
		vibrator = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		recordMap = new HashMap<String, Boolean>();
	}
	private synchronized void alarmStart(String mac) {
		if(recordMap.containsKey(mac)){
			if(recordMap.get(mac).booleanValue()){
				if(mMediaPlayer.isPlaying()){
					return;
				}
			}
		}
		recordMap.put(mac, Boolean.valueOf(true));
		mMediaPlayer.start();
		vibrator.vibrate(new long[]{500,10,100,1000}, 0);
	}
	private synchronized void alarmStop(String mac) {
		if(recordMap.containsKey(mac)){
			if(recordMap.get(mac).booleanValue()){
				recordMap.put(mac, Boolean.valueOf(false));
			}
		}else{
			return;
		}
		
		for(Boolean b : recordMap.values()){
			if(b.booleanValue()){
				return;
			}
		}
		vibrator.cancel();
		mMediaPlayer.stop(); 
		try{  
			mMediaPlayer.prepare();  
		}catch(IllegalStateException e){  
			e.printStackTrace();  
		}catch(IOException e){  
			e.printStackTrace();  
		}  
		mMediaPlayer.seekTo(0);
	}
	
   @Override  
    public void onBackPressed() {  
        if(mMediaPlayer.isPlaying()){  
            mMediaPlayer.stop();  
            mMediaPlayer.release();  
        }  
        super.onBackPressed();  
    }
   
   /**
    * 演示内容
    */
   /**
	 * Initializes a reference to the local Bluetooth adapter.
	 */
	private void initialize() {
		if(application.isSupport){
			final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
			mBluetoothAdapter = bluetoothManager.getAdapter();
			// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
			/*if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}*/
		}
	}
   
	private void startScanScene(){
		if(!application.isSupport){
			Utils.showMsg(MainActivity.this, getResources().getString(R.string.ble_not_supported));
			return;
		}
		if(myThread == null){
			myThread= new BluetoothSences(mBluetoothAdapter,MainActivity.this, handler);//多线程扫描：场景？
			myThread.setCallback(new CallBack() {
				@Override
				public void onResult(final HashMap<String, String> result) {
					if(result == null)	return;
					final String deviceAddr = result.get(Consts.DEVICE_MAC);
					String type = result.get("type");
					if(deviceAddr == null || deviceAddr.length() == 0)	return;
					if("1".equals(result.get("result"))){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if("area".equals(result.get("type"))){//在安全区域
									Utils.showMsg(MainActivity.this, "进入安全区域");
								}
							}
						});
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if("area".equals(result.get("type"))){//不再安全区域
//									Utils.showMsg(MainActivity.this, "不在安全区域");
								}
							}
						});
					}
				}
			});
			myThread.start();
		}
		
	}
	long exitTime=0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	    		IbeaconApplication.getInstance().exit();
	        }
	        return true;   
	    }
		return true;
	}
	
	private void setViewVisible(int state){
		if(state == Consts.TRACKER_STATE_SEARCHING){
			layoutBtns.setVisibility(View.GONE);
			layoutSearchArea.setVisibility(View.GONE);
			layoutSeekBar.setVisibility(View.GONE);
			layoutMode.setVisibility(View.GONE);
			layoutSleepMode.setVisibility(View.GONE);
			layoutRepeatMode.setVisibility(View.GONE);
			layoutSafityArea.setVisibility(View.GONE);
			layoutSearchedMessage.setVisibility(View.VISIBLE); 	
			layoutSearchedwords.setVisibility(View.VISIBLE); 	
			btnSearchedCancle.setVisibility(View.VISIBLE);
		}else{
			layoutBtns.setVisibility(View.VISIBLE);
			layoutSearchArea.setVisibility(View.VISIBLE);
			layoutSeekBar.setVisibility(View.VISIBLE);
			layoutMode.setVisibility(View.VISIBLE);
			layoutSleepMode.setVisibility(View.VISIBLE);
			layoutRepeatMode.setVisibility(View.VISIBLE);
			layoutSafityArea.setVisibility(View.VISIBLE);
			layoutSearchedMessage.setVisibility(View.GONE); 	
			layoutSearchedwords.setVisibility(View.GONE); 	
			btnSearchedCancle.setVisibility(View.GONE); 
		}
	}
	
	private void showShare() { // 微信好友，微信朋友圈，QQ好友，信息，邮件，新浪博客
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
//		 oks.disableSSOWhenAuthorize(); 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(getString(R.string.share));
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText(getResources().getString(R.string.app_name)+","+getResources().getString(R.string.guide_notice_1));
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数"/sdcard/test.jpg"
		 oks.setImagePath(WelcomeActivity.TEST_IMAGE);//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用https://www.5iskynet.com/lost
		 oks.setUrl("https://www.5iskynet.com/lost");//http://www.5iskynet.com/skynet/app
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
//		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		 oks.setSiteUrl("http://sharesdk.cn");
		// 启动分享GUI
		 oks.show(this);
		 }
}