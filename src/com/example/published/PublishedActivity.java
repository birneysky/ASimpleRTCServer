package com.example.published;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.example.slidingmenu.BaiduMapActivity;
import com.example.slidingmenu.R;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.Tracker;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

/**
 * 发布众寻
 * @author Qinwq
 *
 */
public class PublishedActivity extends Activity implements OnClickListener{

	private static final String TAG = PublishedActivity.class.getSimpleName();
	// title相关
	private RelativeLayout  menuBackConstruct,published_select_position;
	private TextView tvTitle,tvPosition;
	private Button btnFinish;
	// 
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private EditText etPublishedText; // 需要发布的文本
	private boolean isLocation = false;
	private double mBaiduLat  = 40.0; //= data.getDoubleExtra("lat",0.0);
	private double mBaiduLot  = 116.0;//= data.getDoubleExtra("lot",0.0);
	private String locationStr;// = data.getStringExtra("locationStr");
	private ImageButton ibMessage, ibSina, ibWeichat, ibQQ;
	private boolean shareMessage, shareSina, shareWeichat, shareQQ;
	private Button btnPublished; // 发布众寻按钮
	private Tracker mTracker;
	private SharedPreferences usersp;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // 失败
//				showAlertDialog(msg.what);
				break;
			case 1: // 成功
//				FileUtils.deleteDir();
//				showAlertDialog(msg.what);
//				showAlertDialog();
				break;
			case -1: // 取消
//				showAlertDialog(msg.what);
				break;
			}
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.activity_published);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
			Init();
			getData();
			addListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 后去需要发布众寻的信息
	 */
	private void getData() {
		mTracker = (Tracker) getIntent().getSerializableExtra("data");
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_search_by_xx:
			if(shareMessage){
				shareMessage = false;
			}else{
				shareMessage = true; shareSina = false; shareWeichat = false; shareQQ = false;
			}
			checkedDouble();
			break;
		case R.id.ib_search_by_xl:
			if(shareSina){
				shareSina = false;
			}else{
				shareSina = true;shareMessage= false; shareWeichat = false; shareQQ = false;
			}
			checkedDouble();
			break;
		case R.id.ib_search_by_wx:
			if(shareWeichat){
				shareWeichat = false;
			}else{
				shareWeichat = true;shareMessage = false; shareSina = false; shareQQ = false;
			}
			checkedDouble();
			break;
		case R.id.ib_search_by_qq:
			if(shareQQ){
				shareQQ = false;
			}else{
				shareQQ = true;shareMessage = false; shareSina = false; shareWeichat = false;
			}
			checkedDouble();
			break;
		}
	}
	
	private void checkedDouble() {
		if(shareMessage){
			ibMessage.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_01_s));
			ibSina.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_02));
			ibWeichat.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_03));
			ibQQ.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_04));
		}else if(shareSina){
			ibMessage.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_01));
			ibSina.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_02_s));
			ibWeichat.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_03));
			ibQQ.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_04));
		}else if(shareWeichat){
			ibMessage.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_01));
			ibSina.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_02));
			ibWeichat.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_03_s));
			ibQQ.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_04));
		}else if(shareQQ){
			ibMessage.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_01));
			ibSina.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_02));
			ibWeichat.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_03));
			ibQQ.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_04_s));
		}else{
			ibMessage.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_01));
			ibSina.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_02));
			ibWeichat.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_03));
			ibQQ.setImageDrawable(getResources().getDrawable(R.drawable.faqi_icon_04));
		}
	}
	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PublishedActivity.this.finish();
			}
		});
		
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(PublishedActivity.this, noScrollgridview);
				} else {
					Intent intent = new Intent(PublishedActivity.this, PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivityForResult(intent, Consts.REQUEST_PUBLISHED_SEARCH_VIEW_PHOTO);
				}
			}
		});
		
		published_select_position.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PublishedActivity.this,BaiduMapActivity.class);
        		startActivityForResult(intent, BaiduMapActivity.REQUEST_BAIDU_LOCATION);
			}
		});
		
		btnPublished.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String title = etPublishedText.getText().toString();
				if(title ==null || "".equals(title) || title.length() == 0){
					Toast.makeText(PublishedActivity.this, "描述不能为空",Toast.LENGTH_SHORT).show(); 
					return;
				}else if(title.length() > 140){
					Toast.makeText(PublishedActivity.this, "描述不能超过140个字",Toast.LENGTH_SHORT).show(); 
					return;
				}
				String findMac = mTracker.getDevice_addr();
				if(findMac.length() == 0){
					Utils.showMsg(getApplicationContext(), "发布众寻异常，请报告管理员！");
					return;
				}
				// 高清的压缩图片全部就在 list 路径里面了
				// 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
				// 完成上传服务器后 ........
				List<String> list = new ArrayList<String>();
				ArrayList<File> mUpFiles = new ArrayList<File>();
				for (int i = 0; i < Bimp.drr.size(); i++) { 
					String Str = Bimp.drr.get(i).substring(Bimp.drr.get(i).lastIndexOf("/") + 1, Bimp.drr.get(i).lastIndexOf("."));
					String imgPath = FileUtils.SDPATH + Str + ".JPEG"; 
					list.add(imgPath);
					File file = new File(imgPath);
					mUpFiles.add(file);
				}
				if(mUpFiles.size() == 0){
					Utils.showMsg(getApplicationContext(), "请上传图片再发布众寻");
					return;
				}
				if(!isLocation){
					Utils.showMsg(getApplicationContext(), "请选择位置再发布众寻");
					return;
				}
				if(shareMessage || shareSina || shareWeichat || shareQQ){ // 第三方帮助众寻
					searchByFriend(title, Bimp.drr);
				}//else{ // 客户端发起众寻
					//userID, findType,findPicture,findAddress,findLongitude,findLatitude,findDesc,findStatus);.
					AppRequest request = new AppRequest();
					request.setmRequestURL("/find/PubFindAction!finds");
					if(mUpFiles.size()>0){
						request.setmUpFiles(mUpFiles);
						request.setmRequestType(AppRequest.UPIMG);
					}
					request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");
					request.putPara("findDesc", title);
					request.putPara("clientType", "android");
					request.putPara("findLongitude", mBaiduLot+"");
					request.putPara("findLatitude", mBaiduLat+"");
					request.putPara("findAddress", locationStr);
					request.putPara("findStatus", 0+"");
					request.putPara("findType", 0+"");
					request.putPara("findDate", Utils.getCurrentDate());
					request.putPara("findMac", mTracker.getDevice_addr());
					new AppThread(PublishedActivity.this,request,new AppHandler() {
						@Override
						protected void handle(AppRequest request, AppResponse response) {
							if("0".equals(response.getmCode())){
								FileUtils.deleteDir();
								showAlertDialog();
							}else{
								Utils.showMsg(PublishedActivity.this, response.getmMessage());
							}
						}
					}).start();
					LoadingIndicator.show(PublishedActivity.this, "正在发布，请稍后...");
//				}
			}
		});
	}
	/**
	 * 发布众寻成功，调用
	 */
	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PublishedActivity.this);
        builder.setTitle("您已经成功发起众寻");
        builder.setPositiveButton(getResources().getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent retIntent = new Intent();
				retIntent.putExtra("flag", "toMine");
				setResult(RESULT_OK, retIntent);
				PublishedActivity.this.finish();
			}
		});
        builder.setNegativeButton(getResources().getString(R.string.dialog_negative), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setResult(RESULT_OK);
				PublishedActivity.this.finish();
			}
		});
        builder.show();
	}
	/**
	 * 提示发布众寻成功或失败
	 * @param tag 成功或失败
	 */
	private void showAlertDialog(int tag) {
		AlertDialog.Builder builder = new AlertDialog.Builder(PublishedActivity.this);
        if(tag == 1){
        	builder.setTitle("求助好友成功");
        	builder.setNegativeButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				PublishedActivity.this.finish();
    			}
    		});
        }else{
        	if(tag == 0){
        		builder.setTitle("求助好友失败");
        	}else{
        		builder.setTitle("求助好友取消");
        	}
        	builder.setNegativeButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
//    				PublishedActivity.this.finish();
    			}
    		});
        }
        builder.show();
	}
	
	protected void searchByFriend(String shareContent, List<String> photoPath) {
		PlatformActionListener platformActionListener = new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
//				Log.i(TAG, "onError----------"+arg2);
				Message msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
			}
			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//				Log.i(TAG, "onComplete ");
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
			@Override
			public void onCancel(Platform arg0, int arg1) {
//				Log.i(TAG, "onCancel");
				Message msg = new Message();
				msg.what = -1;
				handler.sendMessage(msg);
			}
		};
		if(shareMessage){
			shareByMessage(shareContent, photoPath, platformActionListener);
		}else if(shareSina){
			shareBySina(shareContent, photoPath, platformActionListener);
		}else if(shareWeichat){
			shareByWeichat(shareContent, photoPath, platformActionListener);
		}else if(shareQQ){
			shareByQQ(shareContent, photoPath, platformActionListener);
		}
	}
	private void shareByMessage(String shareContent, List<String> photoPath, PlatformActionListener platformActionListener) {
		ShareSDK.initSDK(this);
		ShareParams sp = new ShareParams();
		sp.setTitle("数字天网防丢");
		sp.setText(shareContent);
		sp.setImagePath(photoPath.get(0));
		
		Platform shortMessage = ShareSDK.getPlatform(ShortMessage.NAME);
		shortMessage.setPlatformActionListener(platformActionListener);
		shortMessage.share(sp);
	}
	private void shareBySina(String shareContent, List<String> photoPath, PlatformActionListener platformActionListener) {
		ShareSDK.initSDK(this);
		ShareParams sp = new ShareParams();
		sp.setText(shareContent);
		sp.setImagePath(photoPath.get(0));//"/mnt/sdcard/测试分享的图片.jpg"
		sp.setLatitude(Float.parseFloat(Double.toString(mBaiduLat)));
		sp.setLongitude(Float.parseFloat(Double.toString(mBaiduLot)));
		
		Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		weibo.setPlatformActionListener(platformActionListener); // 设置分享事件回调?
		// 授权：网页授权true；默认为客户端授权false,此时key必须通过审核才行
		weibo.SSOSetting(true);
		// 执行图文分享
		weibo.share(sp);
	}
	
	
	private void shareByWeichat(String shareContent, List<String> photoPath, PlatformActionListener platformActionListener) {
		ShareSDK.initSDK(this);
		ShareParams sp = new ShareParams();
		sp.setShareType(Platform.SHARE_IMAGE);
		sp.setTitle("数字天网防丢");
		sp.setText(shareContent);
		sp.setImagePath(photoPath.get(0));
		
		Platform weiChat = ShareSDK.getPlatform(Wechat.NAME);
		weiChat.setPlatformActionListener(platformActionListener); // 设置分享事件回调?
		// 网页授权true；默认为客户端授权false
		weiChat.SSOSetting(true);
		// 执行图文分享
		weiChat.share(sp);
	}
	private void shareByQQ(String shareContent, List<String> photoPath, PlatformActionListener platformActionListener) {
		// TODO
		ShareSDK.initSDK(this);
		ShareParams sp = new ShareParams();
		if(shareContent != null){
			if(shareContent.length() > 40){
				Toast.makeText(getApplicationContext(), "分享文字不能超过40个", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		sp.setTitle("数字天网防丢");
		sp.setTitleUrl("https://www.5iskynet.com/lost");
		sp.setText(shareContent);
		sp.setImagePath(photoPath.get(0));//"/mnt/sdcard/测试分享的图片.jpg"
		
		Platform qq = ShareSDK.getPlatform(QQ.NAME);
		qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调?
		// 网页授权true；默认为客户端授权false
		qq.SSOSetting(true);
		// 执行图文分享
		qq.share(sp);
		
	}
	public void Init() {
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		btnFinish.setVisibility(View.GONE);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText(getResources().getString(R.string.str_search_ibeacon_title));
		
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		published_select_position = (RelativeLayout)findViewById(R.id.published_select_position);
		ibMessage = (ImageButton)findViewById(R.id.ib_search_by_xx);
		ibSina = (ImageButton)findViewById(R.id.ib_search_by_xl);
		ibWeichat = (ImageButton)findViewById(R.id.ib_search_by_wx);
		ibQQ = (ImageButton)findViewById(R.id.ib_search_by_qq);
		ibMessage.setOnClickListener(this);
		ibSina.setOnClickListener(this);
		ibWeichat.setOnClickListener(this);
		ibQQ.setOnClickListener(this);
		tvPosition = (TextView)findViewById(R.id.txt_published_postion);
		etPublishedText = (EditText)findViewById(R.id.et_published_text);
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		Bimp.drr.clear();
		Bimp.bmp.clear();
		Bimp.max = 0;
		adapter = new GridAdapter(this);
		adapter.update1();
		noScrollgridview.setAdapter(adapter);
		btnPublished = (Button)findViewById(R.id.btn_published);
//		activity_selectimg_send = (TextView) findViewById(R.id.activity_selectimg_send);
		
		
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update1() {
			loading1();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.image.setVisibility(View.VISIBLE);

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}
			
			if (position == Consts.IMAGE_COUNT) {
				holder.image.setVisibility(View.GONE);
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading1() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								Bitmap bm = Bimp.revisionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update1();
		super.onRestart();
	}

	public class PopupWindows extends PopupWindow {

		@SuppressWarnings("deprecation")
		public PopupWindows(Context mContext, View parent) {
			
			super(mContext);

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() { // 拍照
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() { // 选择图片
				public void onClick(View v) {
					Intent intent = new Intent(PublishedActivity.this,
							TestPicActivity.class);
					startActivityForResult(intent, Consts.REQUEST_PUBLISHED_SEARCH_VIEW_PHOTO);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void onConfigurationChanged(Configuration config) { 
	    super.onConfigurationChanged(config); 
	} 
	
	public void photo() {
		String status=Environment.getExternalStorageState(); 
		if(status.equals(Environment.MEDIA_MOUNTED)) {
			File dir=new File(Environment.getExternalStorageDirectory() + "/myimage/"); 
			if(!dir.exists())dir.mkdirs(); 
			
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(dir, String.valueOf(System.currentTimeMillis())
					+ ".jpg");
			path = file.getPath();
			Uri imageUri = Uri.fromFile(file);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
		} else { 
			Toast.makeText(PublishedActivity.this, "没有储存卡",Toast.LENGTH_LONG).show(); 
		} 
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);  
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.drr.size() < Consts.IMAGE_COUNT && resultCode == -1) {
				Bimp.drr.add(path);
			}
			break;
		case BaiduMapActivity.REQUEST_BAIDU_LOCATION:
			if(data==null){//
				locationStr="无法获取当前位置";
				tvPosition.setText(locationStr);
				return;}
			isLocation = true;
			mBaiduLat = data.getDoubleExtra("lat",40.0);
			mBaiduLot = data.getDoubleExtra("lot",116.0);
			locationStr = data.getStringExtra("locationStr");
			tvPosition.setText(locationStr);
			break;
		case Consts.REQUEST_PUBLISHED_SEARCH_VIEW_PHOTO: // 选择图片结束，相册
			if(resultCode == RESULT_OK){
			
			} else if (resultCode == RESULT_CANCELED){ 
			
			} else { // 删除图片怎么做？
				
			}
			break;
		}
	}
	
	
}
