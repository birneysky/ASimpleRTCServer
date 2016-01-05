package com.example.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zijin.dao.IbeaconAreaDao;
import com.zijin.dao.IbeaconLocationDao;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.adapter.LocationListAdapter;
import com.zjjin.adapter.SafityAreaListAdapter;
import com.zjjin.entity.TrackerLocation;
import com.zjjin.entity.TrackerLocation;

public class LocationNoteActivity extends Activity {
	
	private RelativeLayout  menuBackConstruct;
	private TextView tvTitle;
	private Button btnFinish;
	private ListView lvTrackerLocation;
	private LocationListAdapter adapter;
	private List<TrackerLocation> list;
	private int iBeaconId=0;
	private IbeaconLocationDao mIbeaconLocationDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_location_note);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
		setupView();
		addListener();
		initData();
	}
	
	private void initData() {
		Intent intent = getIntent();
		iBeaconId = intent.getIntExtra("iBeaconId", 0);
		/**
		 * 初始安全区域
		 */
		mIbeaconLocationDao =IbeaconLocationDao.getInstance(LocationNoteActivity.this);
		list = mIbeaconLocationDao.getAreasByBeaconID(iBeaconId);
		if(list ==null){
			list = new ArrayList<TrackerLocation>();
			Utils.showMsg(LocationNoteActivity.this, "没有查到历史记录");
		}
		adapter = new LocationListAdapter(getApplicationContext(), list);
		lvTrackerLocation.setAdapter(adapter);
	}

	private void setupView() {
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText("位置记录");
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		btnFinish.setVisibility(View.GONE);
		lvTrackerLocation = (ListView)findViewById(R.id.lv_location_list);
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationNoteActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.location_note, menu);
		return true;
	}

}
