package com.example.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditUserNameActivity extends Activity {
	
	private RelativeLayout titleBar;
	private RelativeLayout titleBack;
	private TextView tvTitle;
	private ImageView ivTitleRightFinish;
	private EditText edtName;
	
	private String userName;
	private ImageView ivRightDel;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_edit_user_name);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_add_beacon);
		getData();
		setupView();
		addListener();
	}

	private void getData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		userName = data.getString("name");
		type = data.getInt("type");
	}

	private void setupView() {
		titleBar = (RelativeLayout)findViewById(R.id.title_bar);
		titleBar.setBackgroundColor(getResources().getColor(R.color.bg_title_personal_info));
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		ivTitleRightFinish = (ImageView)findViewById(R.id.iv_title_bar_right_add);
		ivTitleRightFinish.setVisibility(View.VISIBLE);
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		titleBack.setBackgroundColor(getResources().getColor(R.color.bg_title_personal_info));
		edtName = (EditText)findViewById(R.id.et_edit_name_input_name);
		ivRightDel = (ImageView)findViewById(R.id.iv_edit_name_del);
		if(userName !=null && !"".equals(userName))
			edtName.setText(userName);
		switch(type){
		case 1:{//usernmae
			tvTitle.setText("编辑用户名");	
			edtName.setHint("用户名");
		};break;
		case 2:{
			tvTitle.setText("编辑地区");	
			edtName.setHint("详细地址");
		};break;
		case 3:{
			tvTitle.setText("编辑签名");	
			edtName.setHint("展现自己个性");
		};break;
		}
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditUserNameActivity.this.finish();
			}
		});
		
		ivTitleRightFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent retIntent = new Intent();
				String name =edtName.getText().toString(); 
				retIntent.putExtra("name", name);
				setResult(0, retIntent);
				EditUserNameActivity.this.finish();
			}
		});
		
		ivRightDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edtName.setText("");
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			 	titleBack.callOnClick();
	            return true;
	        }
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_user_name, menu);
		return true;
	}

}
