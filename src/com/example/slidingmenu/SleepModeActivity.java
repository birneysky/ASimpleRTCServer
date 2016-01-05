package com.example.slidingmenu;



import java.util.Calendar;
import java.util.TimeZone;

import net.simonvt.numberpicker.NumberPicker;
import net.simonvt.numberpicker.NumberPicker.OnScrollListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjjin.utils.Consts;

public class SleepModeActivity extends Activity {
	
	private RelativeLayout  menuBackConstruct;
	private Button btnFinish;
	private NumberPicker hourPicker, minutePicker;
	private LinearLayout layoutBegin, layoutEnd;
	private TextView tvBegin, tvEnd;
	private boolean begin = true, end = false;
	private String hour, minute;
	private String beginTime = "", endTime = "";
	private Calendar c = Calendar.getInstance();
	private String data; 
	private String[] oldSleepTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_sleep_mode);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
	
		getData();
		setupView();
		addListener();
	}

	private void getData() {
		data = getIntent().getStringExtra("sleepMode");
		oldSleepTime = data.split("-");
	}

	private void setupView() {
		hourPicker = (NumberPicker) findViewById(R.id.numberPickerHour);
		minutePicker = (NumberPicker) findViewById(R.id.numberPickerMinute);
		hourPicker.setMaxValue(23);
		hourPicker.setMinValue(0);
		hourPicker.setFocusable(true);
		hourPicker.setFocusableInTouchMode(true);
		minutePicker.setMaxValue(59);
		minutePicker.setMinValue(0);
		minutePicker.setFocusable(true);
		minutePicker.setFocusableInTouchMode(true);
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		layoutBegin = (LinearLayout)findViewById(R.id.layout_start_time);
		layoutEnd = (LinearLayout)findViewById(R.id.layout_end_time);
		tvBegin = (TextView)findViewById(R.id.tv_sleep_mode_show_begin_time);
		tvEnd = (TextView)findViewById(R.id.tv_sleep_mode_show_end_time);		
		
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		
		tvBegin.setText(oldSleepTime[0]);
		tvEnd.setText(oldSleepTime[1]);
		hour = oldSleepTime[0].split(":")[0];
		minute = oldSleepTime[0].split(":")[1];
		hourPicker.setValue(Integer.parseInt(formatDel0(hour)));
		minutePicker.setValue(Integer.parseInt(formatDel0(minute)));
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SleepModeActivity.this.finish();
			}
		});
		layoutBegin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour = oldSleepTime[0].split(":")[0];
				minute = oldSleepTime[0].split(":")[1];
				hourPicker.setValue(Integer.parseInt(formatDel0(hour)));
				minutePicker.setValue(Integer.parseInt(formatDel0(minute)));
				begin = true;
				end = false;
				layoutBegin.setBackground(getResources().getDrawable(R.drawable.whatsnew_btn_nor));
				layoutEnd.setBackgroundColor(getResources().getColor(R.color.bg_title_personal_info));
			}
		});
		layoutEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hour = oldSleepTime[1].split(":")[0];
				minute = oldSleepTime[1].split(":")[1];
				hourPicker.setValue(Integer.parseInt(formatDel0(hour)));
				minutePicker.setValue(Integer.parseInt(formatDel0(minute)));
				end = true;
				begin = false;
				layoutEnd.setBackground(getResources().getDrawable(R.drawable.whatsnew_btn_nor));
				layoutBegin.setBackgroundColor(getResources().getColor(R.color.bg_title_personal_info));
			}
		});
		
		hourPicker.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE){
					hour = format(view.getValue());
					if(begin){
						tvBegin.setText(hour + ":" + minute);
						beginTime = hour + ":" + minute;
						oldSleepTime[0] = beginTime;
					} 
					if(end){
						tvEnd.setText(hour + ":" + minute);
						endTime = hour + ":" + minute;
						oldSleepTime[1] = endTime;
					} 
					Log.i("info", "hour = " + hour + ", minute = " + minute +"， begin = " + begin + "， end = " + end);
				}
			}
		});
		
		minutePicker.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE){
					minute = format(view.getValue());
					if(begin){
						tvBegin.setText(hour + ":" + minute);
						beginTime = hour + ":" + minute;
						oldSleepTime[0] = beginTime;
					}
					if(end){
						tvEnd.setText(hour + ":" + minute);
						endTime = hour + ":" + minute;
						oldSleepTime[1] = endTime;
					}
					Log.i("info", "hour = " + hour + ", minute = " + minute + ", begin = " + begin + "， end = " + end);
				}
			}
		});
		
		btnFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				beginTime = tvBegin.getText().toString();
				endTime = tvEnd.getText().toString();
				intent.putExtra(Consts.EXTRA_TIME_BEGIN, beginTime);
				intent.putExtra(Consts.EXTRA_TIME_END, endTime);
				setResult(Consts.RESULT_SLEEP_MODE_CHANGED, intent);
				SleepModeActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	public String format(int value) {	
		String str = String.valueOf(value);	
		if (value < 10) {		
			str = "0" + str;	
		}	
		return str;
	}
	
	public String formatDel0(String str){
		if("0".equals(str.charAt(0))){
			str = str.substring(1);
		}
		return str;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_mode, menu);
		return true;
	}


}
