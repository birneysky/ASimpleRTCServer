package com.example.slidingmenu;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

@SuppressLint("ShowToast")
public class ForgetPasswordActivity extends Activity {
	
	private RelativeLayout titleBack;
	private Button btnSendCheckCode, btnNextStep;
	private EditText etCheckCode;
	private TextView tvTimeCal, tvPhoneNum;
	private Handler mHandler;
	private TimerTask timerTask;
	private int countTime=60;
	private SharedPreferences spuser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_forget_password);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_forget_passowrd);
		IbeaconApplication.getInstance().addActivity(this);
		setupView();
		addListener();
		initData();
		mHandler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch(msg.what){
				case 1:{
					if(countTime ==0){
						countTime=60;
						numTime(false);
						tvTimeCal.setText(countTime+"秒");
					}else{
						tvTimeCal.setText(countTime+"秒");  
					}
					
				}break;
				}
				return false;
			}
		});
	}

	private void initData() {
		Intent intent  = getIntent();
		String phoneNum = intent.getStringExtra("phoneNum");
		if(phoneNum!=null && !"".equals(phoneNum)){
			tvPhoneNum.setText(phoneNum);
		}else{
			tvPhoneNum.setText(spuser.getString(ConstsUser.PHONENUM, null));
		}
		
	}

	private void setupView() {
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		btnSendCheckCode = (Button)findViewById(R.id.btn_send_check_code_forget_pwd);
		btnNextStep = (Button)findViewById(R.id.btn_next_step_forget_pwd);
		etCheckCode = (EditText)findViewById(R.id.et_input_check_code_forget_pwd);
		tvPhoneNum = (TextView)findViewById(R.id.tv_input_phnoe_number_froget_pwd);
		tvTimeCal = (TextView)findViewById(R.id.tv_show_time_forget_pwd);
		spuser = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IbeaconApplication.getInstance().removeAcitvity(ForgetPasswordActivity.this);
				ForgetPasswordActivity.this.finish();
			}
		});
		
		btnSendCheckCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String phoneNum = tvPhoneNum.getText().toString();
				if(phoneNum.length() != 0){
					AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);  
//			        builder.setIcon(R.drawable.icon);  
			        builder.setTitle("确认手机号码");  
			        builder.setMessage("我们将发送验证码到这个号码：\n"+phoneNum);  
			        builder.setPositiveButton("确定",  
			                new DialogInterface.OnClickListener() {  
			                    public void onClick(DialogInterface dialog, int whichButton) {  //title_bar_login_toregist
			                    	sendCode(phoneNum);
			                    }  
			                });  
			        builder.setNegativeButton("取消",  
			                new DialogInterface.OnClickListener() {  
			                    public void onClick(DialogInterface dialog, int whichButton) {  
			                        	
			                    }  
			                });  
			        builder.show();
				} else {
					Toast.makeText(getApplicationContext(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		btnNextStep.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String phoneNum = tvPhoneNum.getText().toString();
				String checkCode = etCheckCode.getText().toString();
				if(checkCode.length() != 0 && phoneNum.length() != 0){
					// TODO 向服务器发送数据，验证手机号码和验证码的正确性，如果正确，进入“重置密码”；如果错误，进行提示
					// 1.手机号码是否注册 2.手机号码和验证码的匹配性
					verifyCode(checkCode,phoneNum);
				} else {
					Toast.makeText(getApplicationContext(), "手机号码或验证码不能为空", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.forget_password, menu);
		return true;
	}
	
	public void numTime(final boolean flag){
		Timer timer =new Timer();
		if(flag){
			timerTask = new TimerTask() {
				@Override
				public void run() {
						Message msg = Message.obtain();
						msg.what=1;
						msg.obj =countTime;
						countTime--;
						mHandler.sendMessage(msg);
				}
			};
			timer.schedule(timerTask, 0, 1000);
		}else{
			if(timerTask==null)return;
			timerTask.cancel();
		}
		
	}
	

	/**
	 * 验证验证码
	 * @param code
	 * @param phoneNum
	 */
	  private void verifyCode(String code,final String phoneNum) {
	    AVOSCloud.verifySMSCodeInBackground(code,phoneNum,new AVMobilePhoneVerifyCallback() {
	      @Override
	      public void done(AVException e) {
	    	  if(e ==null){
					Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
					intent.putExtra(Consts.PHONE_NUMBER, phoneNum);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "手机号码或验证码错误", Toast.LENGTH_SHORT).show();
				}
	      }
	    });
	  }
	  /**
	   * 发送验证码
	   * @param phone
	   */
	  public void sendCode(final String phone) {
		  numTime(true);
		    new AsyncTask<Void, Void, Void>() {
		      boolean res;
		      @Override
		      protected Void doInBackground(Void... params) {
		        try {
		          AVOSCloud.requestSMSCode(phone,getResources().getString(R.string.app_name),getResources().getString(R.string.app_reset_password), 5);
		          res=true;
		        } catch (AVException e) {
		          e.printStackTrace();
		          res=false;
		        }
		        return null;
		      }

		      @Override
		      protected void onPostExecute(Void aVoid) {
		        super.onPostExecute(aVoid);
		        if(res){
		        	btnSendCheckCode.setEnabled(false);
		          Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.sms_send_success), Toast.LENGTH_LONG).show();
		        }else{
		          btnSendCheckCode.setEnabled(false);
		          Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.sms_send_fail), Toast.LENGTH_SHORT).show();
		        }
		      }
		    }.execute();
		  }
}
