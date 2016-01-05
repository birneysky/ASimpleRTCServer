package com.example.slidingmenu;

import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResetPasswordActivity extends Activity {
	
	private TextView tvTitle;
	private RelativeLayout titleBack;
	private EditText etPwdNew, etPwdRepeat;
	private Button btnFinish;
	private String phoneNum;
	private SharedPreferences usersp;
	protected Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_reset_password);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_question);
		getData();
		setupView();
		addListener();
	}

	private void getData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		phoneNum = (String) data.get(Consts.PHONE_NUMBER);
	}

	private void setupView() {
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText("重置密码");
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		btnFinish = (Button)findViewById(R.id.btn_reset_pwd_finish);
		etPwdNew = (EditText)findViewById(R.id.et_reset_pwd_new_pwd);
		etPwdRepeat = (EditText)findViewById(R.id.et_reset_pwd_repeat_pwd);
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ResetPasswordActivity.this.finish();
			}
		});
		btnFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 判断重置密码是否成功，与服务器数据库交互，成功进行页面跳转，失败提示
				String pwdNew = etPwdNew.getText().toString();
				String pwdRepeat =etPwdRepeat.getText().toString();
				if(pwdNew.length() != 0 && pwdRepeat.length() != 0){
					if(pwdNew.length() < 6 || pwdRepeat.length() < 6){
						Toast.makeText(getApplicationContext(), "新密码或验证密码至少6位", Toast.LENGTH_SHORT).show();
						return;
					}
					if(pwdNew.equals(pwdRepeat)){
						resetPwd(phoneNum, pwdNew);
					} else {
						Toast.makeText(getApplicationContext(), "新密码与验证密码不一致", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "新密码或验证密码不能为空", Toast.LENGTH_SHORT).show();
				}
			}

		});
	}
	
	/**
	 * 重置密码
	 * @param phoneNum
	 * @param pwdNew
	 */
	private void resetPwd(final String phoneNum, final String pwdNew) {
		AppRequest request = new AppRequest();
		request.setmRequestURL("/login/MyUserAction!password");
		request.putPara("telephone",phoneNum);
		request.putPara("password",pwdNew);
		request.putPara("clientType", "android");
		new AppThread(ResetPasswordActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){//成功返回
					edit = usersp.edit();
					edit.putString(ConstsUser.PASSWORD, pwdNew);
					edit.commit();
					LoginActivity.actionStart(getApplicationContext(), phoneNum);
					ResetPasswordActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "重置失败", Toast.LENGTH_SHORT).show();
				}
			}
		}).start();
		LoadingIndicator.show(ResetPasswordActivity.this, "正在重置，请稍后...");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reset_password, menu);
		return true;
	}

}
