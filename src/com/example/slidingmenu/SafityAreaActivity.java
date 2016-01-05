package com.example.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zijin.dao.IbeaconAreaDao;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.adapter.SafityAreaListAdapter;
import com.zjjin.entity.TrackerSafityArea;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;
import com.zjjin.utils.baidumap.OpenglActivity;

public class SafityAreaActivity extends Activity {
	
	private RelativeLayout  menuBackConstruct;
	private TextView tvTitle;
	private Button btnFinish;
	
	private ListView lvSafityArea;
	private SafityAreaListAdapter adapter;
	private List<TrackerSafityArea> list;
	private Button btnAddSafityArea;
	private int iBeaconId=0;
	private IbeaconAreaDao mIbeaconAreaDao;
	private int mSelectedIndex=0;
	private String retIntentStr ="";
	private SharedPreferences usersp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_safity_area);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
		setupView();
		addListener();
		initData();
	}

	private void initData() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		Intent intent = getIntent();
		iBeaconId = intent.getIntExtra("iBeaconId", 0);
		/**
		 * 初始安全区域
		 */
		mIbeaconAreaDao =IbeaconAreaDao.getInstance(SafityAreaActivity.this);
		list = mIbeaconAreaDao.getAreasByBeaconID(iBeaconId);
		if(list ==null){
			list = new ArrayList<TrackerSafityArea>();
			Utils.showMsg(SafityAreaActivity.this, "暂没有安全区域");
		}
		/*TrackerSafityArea safityArea = new TrackerSafityArea("家", "厂洼小区23号楼");
		list.add(safityArea);*/
		adapter = new SafityAreaListAdapter(SafityAreaActivity.this, list);
		lvSafityArea.setAdapter(adapter);
	}

	private void setupView() {
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText("安全区域");
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		btnFinish.setVisibility(View.GONE);
		btnAddSafityArea = (Button)findViewById(R.id.btn_safity_area_add);
		lvSafityArea = (ListView)findViewById(R.id.lv_safity_area_list);
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SafityAreaActivity.this.finish();
			}
		});
		
		btnFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Consts.EXTRA_KEY_SAFITY_AREA,retIntentStr);
				setResult(Consts.RESULT_SAFITY_AREA_CHANGED, intent);
				SafityAreaActivity.this.finish();
			}
		});
		
		btnAddSafityArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 记录整屏地图信息，不再输入名称或其他信息
				Intent intent = new Intent(SafityAreaActivity.this, OpenglActivity.class);
				intent.putExtra(Consts.IBEACON_ID, iBeaconId);
				startActivityForResult(intent, Consts.REQUEST_ADD_SAFITY_AREA);
			}
		});
		lvSafityArea.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIndex = position;
                return false;
            }
        });
		lvSafityArea.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            	   MenuItem item = menu.add(0, 0, 0, "删除");
            	   item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                        	delSafetyArea();
							return false;
                        }
            	   });
            }
	  });
		
		lvSafityArea.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TrackerSafityArea areaItem= (TrackerSafityArea) adapter.getItem(position);
				String southWest = areaItem.getSouthWest();
				String[] southWestD = southWest.split(",");
				String northEast = areaItem.getNorthEast();
				String[] northEastD = northEast.split(",");
				String addr = areaItem.getAddress();
//				if(addr!=null && addr.length()>0){
//					Intent intent = new Intent(SafityAreaActivity.this,BaiduMapActivity.class);
//					intent.putExtra("mBaiduLat", areaItem.getmLat());
//					intent.putExtra("mBaiduLot", areaItem.getmLot());
//					intent.putExtra("addr", areaItem.getAddress());
//					intent.putExtra(Consts.EXTRA_BAIDUMAP_TYPE_KEY, Consts.EXTRA_BAIDUMAP_TYPE_VIEW_POSITION);
//					startActivity(intent);
					OpenglActivity.actionStart(
							getApplicationContext(), 
							new double[]{Double.parseDouble(southWestD[0]), Double.parseDouble(southWestD[1])}, 
							new double[]{Double.parseDouble(northEastD[0]), Double.parseDouble(northEastD[1])}
							);
//				}
			}
		});
		
	}
	
	private void delSafetyArea() {
		TrackerSafityArea area = list.get(mSelectedIndex);
		if(area !=null){
			mIbeaconAreaDao.deleteArea(area.getId());
			adapter.removeArea(area);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null) return;
		if (requestCode == Consts.REQUEST_ADD_SAFITY_AREA) {
			// locationlocation 116.401969,39.915599
			if (resultCode == RESULT_OK) {
				final double[] southWest = data.getDoubleArrayExtra(Consts.EXTRA_KEY_SOUTH_WEAST);
				final double[] northEast = data.getDoubleArrayExtra(Consts.EXTRA_KEY_NORTH_EAST);
				final String locationStr = data.getStringExtra("address");
				
				LayoutInflater factory = LayoutInflater.from(SafityAreaActivity.this);
				final View view = factory.inflate(R.layout.alert_dialog_safety_tiltle, null);
				AlertDialog dlg = new AlertDialog.Builder(SafityAreaActivity.this)
						.setTitle("请帮新安全区域起个名字")
						.setView(view)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										EditText title = (EditText) view.findViewById(R.id.edt_dialog_safety_title);
										String titleStr = title.getText().toString();
										if (titleStr == null || "".equals(titleStr)) {
											Toast.makeText(SafityAreaActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
										}
										TrackerSafityArea safityArea = new TrackerSafityArea(
												iBeaconId, titleStr, locationStr, 
												southWest[0]+","+southWest[1], 
												northEast[0]+","+northEast[1]);
										// 将新添加的安全区域添加到数据库中
										int rowNum = (int) mIbeaconAreaDao.addArea(safityArea);
										safityArea.setId(rowNum);
										retIntentStr = titleStr;
										adapter.addData(safityArea);
									}
								})
						.setNegativeButton("取消", 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int whichButton) {

									}
								})
						.create();
				dlg.show();

			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.safity_area, menu);
		return true;
	}
}
