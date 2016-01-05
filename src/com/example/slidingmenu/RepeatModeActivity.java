package com.example.slidingmenu;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zijin.ibeacon.util.Utils;
import com.zjjin.utils.Consts;

public class RepeatModeActivity extends Activity {
	
	private RelativeLayout  menuBackConstruct;
	private TextView tvTitle;
	private Button btnFinish;
	private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
	private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
	
	private OnCheckedChangeListener myCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_monday:
				monday = isChecked;
				break;
			case R.id.cb_tuesday:
				tuesday = isChecked;
				break;
			case R.id.cb_wednesday:
				wednesday = isChecked;
				break;
			case R.id.cb_thursday:
				thursday = isChecked;
				break;
			case R.id.cb_friday:
				friday = isChecked;
				break;
			case R.id.cb_saturday:
				saturday = isChecked;
				break;
			case R.id.cb_sunday:
				sunday = isChecked;
				break;
			}
		}
	};
	private String repeatTime;
	private String[] repeatTimes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.auto_repeed_weekday_selected);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
	
		getData();
		setupView();
		addListener();
	}

	private void getData() {
		repeatTime = getIntent().getStringExtra("repeatMode");
		repeatTimes = repeatTime.split(",");
	}

	private void setupView() {
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText("自动重复");
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		
		cbMonday 	= (CheckBox)findViewById(R.id.cb_monday);
		cbTuesday 	= (CheckBox)findViewById(R.id.cb_tuesday);
		cbWednesday = (CheckBox)findViewById(R.id.cb_wednesday);
		cbThursday 	= (CheckBox)findViewById(R.id.cb_thursday);
		cbFriday 	= (CheckBox)findViewById(R.id.cb_friday);
		cbSaturday 	= (CheckBox)findViewById(R.id.cb_saturday);
		cbSunday 	= (CheckBox)findViewById(R.id.cb_sunday);
		
		for(int i=0; i<repeatTimes.length; i++){
			if("1".equals(repeatTimes[i])){
				cbMonday.setChecked(true);
				monday = true;
			}
			else if("2".equals(repeatTimes[i])){
				cbTuesday.setChecked(true);
				tuesday = true;
			}
			else if("3".equals(repeatTimes[i])){
				cbWednesday.setChecked(true);
				wednesday = true;
			}
			else if("4".equals(repeatTimes[i])){
				cbThursday.setChecked(true);
				thursday = true;
			}
			else if("5".equals(repeatTimes[i])){
				cbFriday.setChecked(true);
				friday = true;
			}
			else if("6".equals(repeatTimes[i])){
				cbSaturday.setChecked(true);
				saturday = true;
			}
			else if("7".equals(repeatTimes[i])){
				cbSunday.setChecked(true);
				sunday = true;
			}
		}
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RepeatModeActivity.this.finish();
			}
		});
		cbMonday .setOnCheckedChangeListener(myCheckedChangeListener);	
		cbTuesday 	.setOnCheckedChangeListener(myCheckedChangeListener);	
		cbWednesday .setOnCheckedChangeListener(myCheckedChangeListener);	
		cbThursday 	.setOnCheckedChangeListener(myCheckedChangeListener);	
		cbFriday 	.setOnCheckedChangeListener(myCheckedChangeListener);	
		cbSaturday 	.setOnCheckedChangeListener(myCheckedChangeListener);	
		cbSunday 	.setOnCheckedChangeListener(myCheckedChangeListener);	
		
		btnFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer returnText = new StringBuffer();
				StringBuffer returnIntText = new StringBuffer();
				Intent intent = new Intent();
				if(monday == true && tuesday == true && wednesday == true && thursday == true && friday == true && saturday == false && sunday == false){
					returnText.append("工作日");
					returnIntText.append("1,2,3,4,5");
				}else if(monday == true && tuesday == true && wednesday == true && thursday == true && friday == true && saturday == true && sunday == true){
					returnText.append("全天");
					returnIntText.append("1,2,3,4,5,6,7");
				}else if(monday == false && tuesday == false && wednesday == false && thursday == false && friday == false && saturday == false && sunday == false){
					returnText.append(Utils.getWeekOfDateStr()); // 当天
					returnIntText.append(Utils.getWeekOfDate());
				}else{
					if(monday){
						returnText.append("周一  ");
						returnIntText.append("1,");
					}
					if(tuesday){
						returnText.append("周二  ");
						returnIntText.append("2,");
					}
					if(wednesday){
						returnText.append("周三  ");
						returnIntText.append("3,");
					}
					if(thursday){
						returnText.append("周四  ");
						returnIntText.append("4,");
					}
					if(friday){
						returnText.append("周五  ");
						returnIntText.append("5,");
					}
					if(saturday){
						returnText.append("周六  ");
						returnIntText.append("6,");
					}
					if(sunday){
						returnText.append("周日");
						returnIntText.append("7");
					}
				}
				intent.putExtra(Consts.EXTRA_KEY_REPEAT_MODE, returnText.toString());
				intent.putExtra("returnIntText", returnIntText.toString());
				setResult(Consts.RESULT_REPEAT_MODE_CHANGED, intent);
				RepeatModeActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_mode, menu);
		return true;
	}


}
