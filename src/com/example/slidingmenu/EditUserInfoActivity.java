package com.example.slidingmenu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.published.PopupWindows;
import com.view.circleimageview.CircleImageView;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zijin.ibeacon.util.ImageLoaderUtil;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.User;
import com.zjjin.utils.BitmapUtils;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

public class EditUserInfoActivity extends Activity {
	
	private RelativeLayout titleBar, titleBack;
	private TextView tvTitle;
	private ImageView ivTitleRight;
	private CircleImageView ivUserIcon;
	private RelativeLayout changeUserIcon, changeUserName, changeUserGender, changeUserRegin, changeUserAddress, changeUserSign;
	private TextView tvUserName,tv_userinfo_address,tv_userinfo_resign,tv_userinfo_gender,tv_userinfo_regin;
	private View layout;
	private String mPicName="";
	private File userimgPath;
	private Bitmap userIcon = null;
	
	public final int REQUEST_CODE_CHANGE_NAME = 1;
	public final int REQUEST_CODE_CHANGE_GENDER = 2;
	public final int REQUEST_CODE_CHANGE_REGIN = 3;
	public final int REQUEST_CODE_CHANGE_ADDRESS = 4;
	public final int REQUEST_CODE_CHANGE_SIGN = 5;
	private User mUserinfo;//本地数据
	private SharedPreferences usersp;
	private static final int PHOTO_RESOULT=0x000010;
	public  static final int GET_PROVINCE_RESOULT=0x000011;
	private static final String IMAGE_UNSPECIFIED = "image/*";
	private String mResultPicPath=Consts.IMG_PATH;//Environment.getExternalStorageDirectory()+ PopupWindows.PIC_PATH;
	Uri uritempFile ;
	private Handler handler = null;
	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_edit_user_info);
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_add_beacon);
		setupView();
		getData();
		addListener();
	}

	private void getData() {
		
		mUserinfo = new User();
		mUserinfo.setId(usersp.getInt(ConstsUser.ID, 0));
		mUserinfo.setUserName(usersp.getString(ConstsUser.USERNAME, null));
		mUserinfo.setPhoneNum(usersp.getString(ConstsUser.PHONENUM, null));
		mUserinfo.setPassword(usersp.getString(ConstsUser.PASSWORD, null));
		mUserinfo.setUserAddress(usersp.getString(ConstsUser.USERADDRESS, null));
		mUserinfo.setUserGender(usersp.getString(ConstsUser.USERGENDER, null));
		mUserinfo.setUserPhoto(usersp.getString(ConstsUser.USERPHOTOADDRESS, null));
		mUserinfo.setUserRigon(usersp.getString(ConstsUser.USERRIGON, null));
		mUserinfo.setUserSign(usersp.getString(ConstsUser.USERSIGN, null));
		
		this.handler = IbeaconApplication.getInstance().getHandler();
//		String userJson = usersp.getString("userinfo", "");
//		if("".equals(userJson))return;
//		Gson gson =new Gson();
//		mUserinfo = gson.fromJson(userJson, User.class);
		tvUserName.setText(mUserinfo.getUserName());
		tv_userinfo_address.setText(mUserinfo.getUserAddress());
		tv_userinfo_resign.setText(mUserinfo.getUserSign());
		tv_userinfo_gender.setText(mUserinfo.getUserGender());
		tv_userinfo_regin.setText(mUserinfo.getUserRigon());
		if(mUserinfo.getUserPhoto()!=null){
			Bitmap bitmap = BitmapFactory.decodeFile(Consts.IMG_PATH+mUserinfo.getUserPhoto());
			if(bitmap !=null){
				ivUserIcon.setImageBitmap(bitmap);
			}else{//后台服务器获取
				String imagePath = Utils.mServerImgPath+mUserinfo.getUserPhoto();
				ImageLoaderUtil.getInstance(EditUserInfoActivity.this).getImage(ivUserIcon, imagePath);
			}
		}
	}

	private void setupView() {
		layout = findViewById(R.id.activity_user_info);
		titleBar = (RelativeLayout)findViewById(R.id.title_bar);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText("个人信息");
		ivTitleRight = (ImageView)findViewById(R.id.iv_title_bar_right_add);
		ivTitleRight.setImageDrawable(getResources().getDrawable(R.drawable.weekday_selected));
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		titleBack.setBackgroundColor(getResources().getColor(R.color.bg_title_personal_info));
		titleBar.setBackgroundColor(getResources().getColor(R.color.bg_title_personal_info));
		changeUserIcon = (RelativeLayout)findViewById(R.id.layout_userinfo_change_usericon);
		changeUserName = (RelativeLayout)findViewById(R.id.layout_userinfo_change_username);
		changeUserGender = (RelativeLayout)findViewById(R.id.layout_userinfo_change_usergender);
		changeUserRegin = (RelativeLayout)findViewById(R.id.layout_userinfo_change_userregin);
		changeUserAddress = (RelativeLayout)findViewById(R.id.layout_userinfo_change_useraddress);
		changeUserSign = (RelativeLayout)findViewById(R.id.layout_userinfo_change_usersign);
		tvUserName = (TextView)findViewById(R.id.tv_userinfo_show_name);
		tv_userinfo_gender = (TextView)findViewById(R.id.tv_userinfo_gender);
		tv_userinfo_address = (TextView)findViewById(R.id.tv_userinfo_address);
		tv_userinfo_resign = (TextView)findViewById(R.id.tv_userinfo_resign);
		tv_userinfo_regin = (TextView)findViewById(R.id.tv_userinfo_regin);
		ivUserIcon = (CircleImageView)findViewById(R.id.img_userinfo_icon);
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
	}

	private void addListener() {
		changeUserIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPicName = System.currentTimeMillis()+".jpg";
				new PopupWindows(EditUserInfoActivity.this,layout,mPicName);
			}
		});
		
		changeUserName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditUserInfoActivity.this, EditUserNameActivity.class);
				intent.putExtra("name", mUserinfo.getUserName());
				intent.putExtra("type", 1);
				startActivityForResult(intent, REQUEST_CODE_CHANGE_NAME);
			}
		});
		
		changeUserRegin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditUserInfoActivity.this, GetAddressInfoActivity.class);
				startActivityForResult(intent, GET_PROVINCE_RESOULT);
			}
		});
		
		changeUserAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditUserInfoActivity.this, EditUserNameActivity.class);
				intent.putExtra("name", mUserinfo.getUserAddress());
				intent.putExtra("type", 2);
				startActivityForResult(intent, REQUEST_CODE_CHANGE_ADDRESS);
			}
		});
		
		changeUserSign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditUserInfoActivity.this, EditUserNameActivity.class);
				intent.putExtra("name",mUserinfo.getUserSign());
				intent.putExtra("type", 3);
				startActivityForResult(intent, REQUEST_CODE_CHANGE_SIGN);
			}
		});
		
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		ivTitleRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveUser();
			}
		});
		
		changeUserGender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfoActivity.this);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("选择一个性别");
                //    指定下拉列表的显示数据
                final String[] sexs = {"男", "女"};
                //    设置一个下拉的列表选择项
                builder.setItems(sexs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	tv_userinfo_gender.setText(sexs[which]);
                    	mUserinfo.setUserGender(sexs[which]);
                    }
                });
                builder.show();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(PopupWindows.TAKE_PICTURE == requestCode){ //照相：
			if(resultCode == RESULT_CANCELED){
				return;
			}
			 // 设置文件保存路径
            File picture = new File(mResultPicPath+mPicName);
            startPhotoZoom(Uri.fromFile(picture),mResultPicPath+mPicName);//没保存么？
			// 处理结果
        } else if (PopupWindows.OTHER_PICTURE == requestCode && data != null){ // 图库：上传头像
			String imagePath = data.getStringExtra("imagePath");// /storage/emulated/0/MIUI/wallpaper/宝马_&_457f9e19-e66c-4ec7-9c64-a26fc3f03612.jpg
			if(imagePath==null){
				Utils.showMsg(EditUserInfoActivity.this, "加载图片失败");
				return;
			}
			
			userIcon = BitmapUtils.loadBitmap(imagePath, ivUserIcon.getWidth(), ivUserIcon.getHeight());
			if(userIcon !=null){
				BitmapUtils.photo2Sd(userIcon, mResultPicPath, mPicName);
			}else{
				userIcon = BitmapFactory.decodeResource(getResources(), R.drawable.home_photo);
			}
			if(userIcon !=null){
				mUserinfo.setUserPhoto(mPicName);
			}
			ivUserIcon.setImageBitmap(userIcon); //把图片显示在ImageView控件上
		} else if (PHOTO_RESOULT == requestCode){ //保存拍照的图片
			 try {
				 userIcon = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
				if(userIcon !=null){
					mUserinfo.setUserPhoto(mPicName);
					BitmapUtils.photo2Sd(userIcon, mResultPicPath, mPicName);
				}
				ivUserIcon.setImageBitmap(userIcon); //把图片显示在ImageView控件上
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		} else if( GET_PROVINCE_RESOULT == requestCode && data != null){ // 到底该显示省还是市？
			String province = data.getStringExtra("province");
			String code = data.getStringExtra("code");
			String city = data.getStringExtra("city");
			if(city != null){
				mUserinfo.setUserRigon(city);
				tv_userinfo_regin.setText(city);
			}else{
				if(province != null){
					mUserinfo.setUserRigon(province);
					tv_userinfo_regin.setText(province);
				}else{
					return;
				}
			}
		} 
		if(data!=null){
			String devive_name = data.getStringExtra("name");
			if(devive_name != null){
				if(REQUEST_CODE_CHANGE_NAME == requestCode){
					mUserinfo.setUserName(devive_name);
					tvUserName.setText(devive_name);
				} else if(REQUEST_CODE_CHANGE_ADDRESS == requestCode){
					mUserinfo.setUserAddress(devive_name);
					tv_userinfo_address.setText(devive_name);
				} else if(REQUEST_CODE_CHANGE_SIGN == requestCode){
					mUserinfo.setUserSign(devive_name);
					tv_userinfo_resign.setText(devive_name);
				} 
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_user_info, menu);
		return true;
	}
	/**
     * 收缩图片
     * 
     * @param uri
     */
    public void startPhotoZoom(Uri uri,String path) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        uritempFile = Uri.parse("file://" + "/" + path);  //Uri.parse("file://"+path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);  
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());  
        startActivityForResult(intent, PHOTO_RESOULT);
    }
    @Override
    protected void onPause() {
    	super.onPause();
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
    protected void onDestroy() {
    	super.onDestroy();
    }
    /**
     * 保存用户信息
     */
	private void saveUser() {
		//qinwq 更改为向服务器发起
		/*Gson gson = new Gson();
		String userJson = gson.toJson(mUserinfo);
		Editor editor = usersp.edit();
		editor.putString("userinfo", userJson);
		editor.commit();*///		if(mNewUserinfo ==mUserinfo)return;
		AppRequest request = new AppRequest();
		if(mPicName !=null && !"".equals(mPicName)){
			request.setmRequestType(AppRequest.UPIMG);
			userimgPath = new File(mResultPicPath+mPicName);// /storage/emulated/0/ARTICLE_IMG/1435048479151.jpg
			ArrayList<File> files = new ArrayList<File>();
			files.add(userimgPath);// /storage/emulated/0/ARTICLE_IMG/1435048479151.jpg
			request.setmUpFiles(files);	
		}
		request.setmRequestURL("/login/MyUserAction!information");
		request.putPara("userID", mUserinfo.getId()+"");
		request.putPara("username", mUserinfo.getUserName());
		request.putPara("headPicture", mUserinfo.getUserPhoto());
		request.putPara("sex", mUserinfo.getUserGender());
		request.putPara("city", mUserinfo.getUserRigon());
		request.putPara("address", mUserinfo.getUserAddress());
		request.putPara("signature", mUserinfo.getUserSign());
		request.putPara("clientType", "android");
		LoadingIndicator.show(EditUserInfoActivity.this, "正在更新信息....");
		new AppThread(EditUserInfoActivity.this, request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				// {"message":"完善成功","data":"","code":"0"}
				if("0".equals(response.getmCode())){
					Utils.showMsg(getApplicationContext(), ""+response.getmMessage());
					//此处应该保存信息到sp
					saveUsrinfo2sp();
					if(request.getmRequestType() == AppRequest.UPIMG){
						if(userIcon != null && handler != null){
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							userIcon.compress(Bitmap.CompressFormat.PNG, 100, baos);
							byte[] bitmapArray = baos.toByteArray();
							Message msg = new Message();
							Bundle data = new Bundle();
							data.putByteArray("data", bitmapArray);
							msg.setData(data);
							msg.what = Consts.MESSAGE_CHANGE_USER_ICON;
							handler.sendMessage(msg);
							edit.putString(ConstsUser.USERPHOTOADDRESS, mUserinfo.getUserPhoto());
						}
					}
					edit.commit();
					finish();
				}else{ //此处应该释放照片缓存
					ivUserIcon.setImageDrawable(getResources().getDrawable(R.drawable.home_photo));
					Utils.showMsg(getApplicationContext(), response.getmMessage());
				}
			}
		}).start();
	}

	protected void saveUsrinfo2sp() {
		edit = usersp.edit();
		edit.putString(ConstsUser.USERNAME, mUserinfo.getUserName());
		edit.putString(ConstsUser.USERGENDER, mUserinfo.getUserGender());
		edit.putString(ConstsUser.USERRIGON, mUserinfo.getUserRigon());
		edit.putString(ConstsUser.USERADDRESS, mUserinfo.getUserAddress());
		edit.putString(ConstsUser.USERSIGN, mUserinfo.getUserSign());
	}
    
    

}
