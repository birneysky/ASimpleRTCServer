package com.example.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class BeforeTrackingActivity extends Activity {
	private Button btnTracking;
	private LinearLayout layoutToUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_before_tracking);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_before_tracking);
		setupView();
		addListener();
		IbeaconApplication.getInstance().addActivity(this);
	}

	private void setupView() {
		btnTracking = (Button)findViewById(R.id.btn_before_tracking_start);
		layoutToUserInfo = (LinearLayout)findViewById(R.id.layout_before_tracking_detailies);
		
	}

	private void addListener() {
		btnTracking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BeforeTrackingActivity.this, MainActivity.class);
				startActivity(intent);
				BeforeTrackingActivity.this.finish();
			}
		});
		
		layoutToUserInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentInfo = new Intent(BeforeTrackingActivity.this, EditUserInfoActivity.class);
				startActivity(intentInfo);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.before_tracking, menu);
		return true;
	}

}
