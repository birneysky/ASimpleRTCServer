package com.example.slidingmenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.utils.ConstsUser;

public class SuggestActivity extends Activity {

	private RelativeLayout titleBack;
	private EditText edt_suggest;
	private Button btn_suggest_send;
	private SharedPreferences usersp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_suggest);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_suggest);
		initView();
		addListener();
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SuggestActivity.this.finish();
			}
		});
	}

	private void initView() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		edt_suggest = (EditText)findViewById(R.id.edit_sugguest);
		btn_suggest_send = (Button)findViewById(R.id.btn_suggest_send);
		btn_suggest_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = edt_suggest.getText().toString();
				if(message == null || message.length() == 0){
					Utils.showMsg(getApplicationContext(), "意见不能为空");
					return;
				}
				if(message.length() > 140){
					Utils.showMsg(getApplicationContext(), "最多输入140个字，当前超出"+(message.length()-140)+"个字！");
					return;
				}
				AppRequest request =new AppRequest();
				request.setmRequestURL("/advice/IbeaconAdviceAction!finds");
				request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");//userId
				request.putPara("adviceContent", edt_suggest.getText().toString());
				request.putPara("adviceDate", Utils.getCurrentDate());
				request.putPara("clientType", "android");
				new AppThread(SuggestActivity.this,request,new AppHandler() {
					@Override
					protected void handle(AppRequest request, AppResponse response) {
						if("0".equals(response.getmCode())){
							Utils.showMsg(getApplicationContext(), "反馈成功，感谢您的意见");
							SuggestActivity.this.finish();
						}
						
					}
				}).start();
				LoadingIndicator.show(SuggestActivity.this, "正在发送意见...");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.suggest, menu);
		return true;
	}

}
