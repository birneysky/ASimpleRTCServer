package com.example.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.view.sortlistview.SelectCountryActivity;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zjjin.entity.User;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

public class RegistActivity extends Activity {
	private static final String TAG=RegistActivity.class.getSimpleName();
	private RelativeLayout titleBack;
	private RelativeLayout layoutSelectCountry;
	private Button btnJoin, btnLogin;
	private EditText etPhoneNum, etPassword;
	private TextView tvPhoneNumArea, tvCountry;
	private LinearLayout layoutPrivateDeal; // 使用条款与隐私协议
	private SharedPreferences usersp;
	protected Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_regist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_regist);
		IbeaconApplication.getInstance().addActivity(this);
		setupView();
		addListener();
	}

	private void setupView() {
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		titleBack.setVisibility(View.GONE);
		layoutSelectCountry = (RelativeLayout)findViewById(R.id.layout_regest_select_country);
		btnJoin = (Button)findViewById(R.id.btn_regist_join);
		etPhoneNum = (EditText)findViewById(R.id.et_regist_phone_number);
		etPassword = (EditText)findViewById(R.id.et_regist_password);
		tvPhoneNumArea = (TextView)findViewById(R.id.tv_phone_area);
		tvCountry = (TextView)findViewById(R.id.tv_regist_country);
		btnLogin = (Button)findViewById(R.id.tv_title_bar_right_login);
		layoutPrivateDeal = (LinearLayout)findViewById(R.id.layout_deal_and_private);
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
	}

	private void addListener() {
		layoutSelectCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistActivity.this, SelectCountryActivity.class);
				startActivityForResult(intent, Consts.REQUEST_SELECT_COUNTRY);
			}
		});
		
		btnJoin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				regist();
			}
		});
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
				startActivity(intent);
				RegistActivity.this.finish();
			}
		});
		
		layoutPrivateDeal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistActivity.this, PrivateDealActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			if(requestCode == Consts.REQUEST_SELECT_COUNTRY){
				if(resultCode == Consts.RESULT_SELECT_COUNTRY){
					// tvPhoneNumArea赋值，tvCountry赋值
					String country = data.getStringExtra(Consts.COUNTRY);
					String areaNum = data.getStringExtra(Consts.NUMBER_CODE);
					for(int i = 0; i < areaNum.length(); i++){
						char c = areaNum.charAt(i);
						if('0' == c){
							continue;
						} else {
							areaNum = areaNum.substring(i);
							break;
						}
					}
					tvCountry.setText(country);
					tvPhoneNumArea.setText("+" + areaNum);
				}
			}
		}else{
			return;
		}
	}
	
	private void regist() {
		final String num = String.valueOf(etPhoneNum.getText().toString());
		final String pwd = String.valueOf(etPassword.getText().toString());
		String countryCode = tvPhoneNumArea.getText().toString();
		if("+86".equals(countryCode)){
			if(num.length() != 11){
				Toast.makeText(getApplicationContext(), "手机号码输入有误！", Toast.LENGTH_SHORT).show();
				return;
			}
		}
			if(pwd.length()<6){
				Toast.makeText(getApplicationContext(), "密码至少6位字符！", Toast.LENGTH_SHORT).show();
				return;
			}
			AppRequest request = new AppRequest();
			request.setmRequestURL("/login/MyUserAction!regs");
			request.putPara("telephone", num);
			request.putPara("username", num);
			request.putPara("password", pwd);
			request.putPara("clientType", "android");
			new AppThread(RegistActivity.this,request,new AppHandler() {
				@Override
				protected void handle(AppRequest request, AppResponse response) {
					if("0".equals(response.getmCode())){//成功返回
						//解析json   {data:{User},message:"",code:0/1}
						Log.i(TAG,"resonse:"+response.getmResult());
						Gson gson = new Gson();
						User user = gson.fromJson(response.getData(), User.class);
						// 注册成功
						edit = usersp.edit();
						edit.putInt(ConstsUser.ID, user.getId());
						edit.putString(ConstsUser.PHONENUM, num);
						edit.putString(ConstsUser.USERNAME, num);
						edit.commit();
						Intent intent = new Intent(RegistActivity.this, BeforeTrackingActivity.class);
						startActivity(intent);
						RegistActivity.this.finish();
					}else if("1".equals(response.getmCode())){
						Toast.makeText(getApplicationContext(), "注册失败,手机号已存在！", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "注册失败,请重新注册！", Toast.LENGTH_SHORT).show();
					}
				}
			}).start();
			LoadingIndicator.show(RegistActivity.this, "正在注册，请稍后...");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
