package com.example.slidingmenu;

import java.io.File;
import java.io.FileNotFoundException;

import javax.net.ssl.ManagerFactoryParameters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.published.PopupWindows;
import com.view.circleimageview.CircleImageView;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.utils.BitmapUtils;
import com.zjjin.utils.Consts;

public class AddNewTrackerActivity extends Activity {
	
	private RelativeLayout titleBack;
	private FrameLayout layout_add_image;
	private EditText edt_ibeacon_name;
	private Button btn_finish_pair;
	private ToggleButton tbtnSwitch;
	private CircleImageView img_pic;
	private View layout;
	private String mPicName="";
	private static final int PHOTO_RESOULT=0x000001;
	private static final String IMAGE_UNSPECIFIED = "image/*";
	private String mResultPicPath=Consts.IMG_PATH;//Environment.getExternalStorageDirectory()+ PopupWindows.PIC_PATH;
	Uri uritempFile ;
	private int mSleepMode;
	private static String TAG = AddNewTrackerActivity.class.getSimpleName();
//	private String device_mac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_add_new_tracker);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_add_tracker);
		initView();
		addListener();
		IbeaconApplication.getInstance().addActivity(this);
		getData();
	}

	private void getData() {
		// TODO Auto-generated method stub
//		device_mac = getIntent().getExtras().getString(Consts.DEVICE_MAC);
	}

	private void addListener() {
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddNewTrackerActivity.this.finish();
			}
		});
		
		layout_add_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//让键盘隐藏
				InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
				boolean isOpen = imm.isActive();
				if(isOpen){
					imm.hideSoftInputFromWindow(AddNewTrackerActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				mPicName = System.currentTimeMillis()+".jpg";
				new PopupWindows(AddNewTrackerActivity.this,layout,mPicName);
			}
		});
		btn_finish_pair.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name ="";
				name = edt_ibeacon_name.getText().toString();
				if("".equals(name) || name.length() == 0){
					Toast.makeText(AddNewTrackerActivity.this, "物品名称不能为空", Toast.LENGTH_SHORT).show();
				}else{
					Intent data = new Intent();
					data.putExtra("name", name);
					if(!"".equals(mPicName) && mPicName.length() != 0){
						data.putExtra("picPath", mPicName);
					}
					data.putExtra("sleepMode", mSleepMode);//data.putExtra(Consts.DEVICE_MAC, device_mac);
					setResult(AddBeaconActivity.REQEUSET_ADD_IBEACON_NAME, data);
					finish();
				}
			}
		});
		
		tbtnSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){ // 开
					mSleepMode= 0;
				}else{ // 关
					mSleepMode =1;
				}
			}
		});
	}

	private void initView() {
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		layout = findViewById(R.id.add_new_tracker_layout);
		layout_add_image =(FrameLayout)findViewById(R.id.laout_add_ibeacon_image);
		img_pic = (CircleImageView) layout_add_image.findViewById(R.id.add_new_tracker_pic);
		edt_ibeacon_name=(EditText)findViewById(R.id.tv_add_tracker_name_notice);
		btn_finish_pair=(Button)findViewById(R.id.btn_add_tracker_moddle_notice);
		tbtnSwitch = (ToggleButton)findViewById(R.id.tbtn_switch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_new_tracker, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "requestCode = " + requestCode+", resultCode = " + resultCode);//9,4128
		if(resultCode == RESULT_CANCELED){
			return;
		}
		switch (requestCode) {
		case PopupWindows.TAKE_PICTURE://拍照；
			 // 设置文件保存路径
            File picture = new File(mResultPicPath+mPicName);
            startPhotoZoom(Uri.fromFile(picture),mResultPicPath+mPicName);
			break;
			// 处理结果
		case PopupWindows.OTHER_PICTURE:{ // 选择相册照片；选择iBeacon图片
			String imagePath = data.getStringExtra("imagePath"); // /storage/emulated/0/UCDownloads/雨.jpg
			if(imagePath==null){
				Utils.showMsg(AddNewTrackerActivity.this, "加载图片失败");
				return;
			}
			Bitmap bitmap = BitmapUtils.loadBitmap(imagePath, img_pic.getWidth(), img_pic.getHeight());
			
			if(bitmap !=null){
				BitmapUtils.photo2Sd(bitmap, mResultPicPath, mPicName);
			}else{
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
			}
			img_pic.setImageBitmap(bitmap); //把图片显示在ImageView控件上
			};break;
		case PHOTO_RESOULT:
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
				if(bitmap !=null){
					 BitmapUtils.photo2Sd(bitmap, mResultPicPath, mPicName);
		             img_pic.setImageBitmap(bitmap); //把图片显示在ImageView控件上
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
	        break;
		}
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
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 500);
//        intent.putExtra("return-data", true);
        uritempFile = Uri.parse("file://" + "/" + path);  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);  
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());  
        startActivityForResult(intent, PHOTO_RESOULT);
    }

}
