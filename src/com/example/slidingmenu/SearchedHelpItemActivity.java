package com.example.slidingmenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.published.Bimp;
import com.example.published.FileUtils;
import com.example.published.PhotoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.view.circleimageview.CircleImageView;
import com.view.listviewforscrollview.InnerListView;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.util.BaiduUtils;
import com.zijin.ibeacon.util.ImageLoaderUtil;
import com.zijin.ibeacon.util.BaiduUtils.Callback;
import com.zijin.ibeacon.util.ImageLoaderUtil.ImageLoaderCallBack;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.PublicFind;
import com.zjjin.entity.PublicWords;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ConstsUser;

public class SearchedHelpItemActivity extends Activity {
	
	private static final String TAG=SearchedHelpItemActivity.class.getSimpleName();
	private PublicFind data;
	private GridView gvPhoto;
	private GridAdapter adapter; // 图片gridview
	private RelativeLayout layoutNewComment;
	private InnerListView lvComment; // 评论列表
	private ArrayList<PublicWords> commentList;
	private CommentAdapter commentAdapter; // 评论列表数据
	private ScrollView svParent;
	private LinearLayout layoutComment; // 弹出评论框
	private Button btnHelpSearch; // 帮助众寻按钮
	private TextView tvHelpUserName,tvHelpContent,tvDistance;
	private EditText edtUserNewComment; // 发表评论编辑器
	private Button btnUserCommentSend; // 发表评论按钮
	private CircleImageView civUserIcon; // 用户头像
	private double mBaiduLong,mBaiduLat;
	private String mBaiduAddress;
	// 标题栏
	private RelativeLayout  menuBackConstruct;
	private Button btnFinish;
	private TextView tvTitleName;
	private SharedPreferences usersp;
	private boolean isFound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_searched_help_item);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
		setupView();
		addListener();
		getData();
	}

	private void getData() {
		commentList = new ArrayList<PublicWords>();
		commentAdapter = new CommentAdapter(this, commentList);
		lvComment.setAdapter(commentAdapter);
		data = (PublicFind) getIntent().getSerializableExtra("data");
		if(data==null) return;
		tvHelpUserName.setText(data.getUsername());
		tvHelpContent.setText(data.getFindDesc());
		String path = data.getHeadPicture(); // 用户头像
		setUserIcon(path);
		Bimp.max = 0;
		Bimp.drr.clear();
		Bimp.bmp.clear();
		/**
		 * 添加图片测试
		 */
		List<String> lists = new ArrayList<String>(); //图片路径集合
		if(data.getFindPicture()!=null){
			String[] images = data.getFindPicture().split(",");
			if(images!=null && images.length>0){
				for(String name :images){
					lists.add(name);
					Bimp.drr.add(name);
				}
			}
			
		}
		//获取之间距离
		if(data.getFindLatitude()!=null && data.getFindLongitude()!=null){
			getDistanceFromDevice(data.getFindLatitude(),data.getFindLongitude());
		}
		// 显示地理位置
//		tvDistance.setText(data.getFindAddress());
		adapter = new GridAdapter(this,lists);
		adapter.update1(lists);//?为什么调用bm预览
		gvPhoto.setAdapter(adapter);
		//评论 留言
		AppRequest request = new AppRequest();
		request.setmRequestURL("/message/FiWordsAction!message");
		request.putPara("clientType", "android");
		request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");
		request.putPara("findID", data.getFindID()+"");
		new AppThread(SearchedHelpItemActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				// 数据格式是什么样的？
				if("0".equals(response.getmCode())){
					Gson gson = new Gson();
					ArrayList<PublicWords> myfinds = gson.fromJson(response.getData(), new TypeToken<List<PublicWords>>(){}.getType());
					if(myfinds !=null && myfinds.size()>0){
						addInfoList(myfinds);
					}
				}else{
					Utils.showMsg(getApplicationContext(), "发布众寻失败！"+response.getData()+", "+response.getmCode()+", "+response.getmMessage());
				}
			}
		}).start();
	}
	/**
	 * 设置头像
	 * @param path
	 */
	private void setUserIcon(final String path) {
		try {
			if(civUserIcon!=null && path!=null && path.length() != 0){
				if(FileUtils.fileIsExists(Consts.IMG_PATH+path)){
					Bitmap iconBmp = BitmapFactory.decodeFile(Consts.IMG_PATH+path);
					if(iconBmp!=null){
						civUserIcon.setImageBitmap(iconBmp);
					}
				}else{
					ImageLoaderUtil imageLoader = ImageLoaderUtil.getInstance(SearchedHelpItemActivity.this);
					imageLoader.getImage(civUserIcon, Utils.mServerImgPath+path);
					imageLoader.setCallBack(new ImageLoaderCallBack() {
						@Override
						public void refreshAdapter() {
//							Utils.showMsg(MainActivity.this, "");?
							Bitmap iconBmp = BitmapFactory.decodeFile(Consts.IMG_PATH+path);
							if(iconBmp!=null){
								civUserIcon.setImageBitmap(iconBmp);
							}
						}
					});	
				}
			}else{ 
				Log.i(TAG, "用户无头像！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addInfoList(ArrayList<PublicWords> myfinds) {
		commentList.addAll(myfinds);
		commentAdapter = new CommentAdapter(this, myfinds);
		lvComment.setAdapter(commentAdapter);
//		commentAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 获取距离
	 * @param findLatitude 	纬度
	 * @param findLongitude 经度
	 */
	private void getDistanceFromDevice(String findLatitude, String findLongitude) {
		final BaiduUtils mBaidu = new BaiduUtils(SearchedHelpItemActivity.this); 
		double mLat = Double.parseDouble(findLatitude);
		double mLot = Double.parseDouble(findLongitude);
		final LatLng lat = new LatLng(mLat, mLot); 
		mBaidu.setCallback(new Callback() {
			@Override
			public void onResult(BDLocation location) {
//				new LatLng(location.getLatitude(),location.getLongitude());
				double distance = mBaidu.getInstance(lat);
				if(distance <=0){
					tvDistance.setText("无法获取");//通过百度地图计算距离
				}else{
					if(distance>1000){
						String s = String.valueOf(distance/1000);
						String str = s.substring(0, s.indexOf(".")+4);
						tvDistance.setText(str+"千米");//通过百度地图计算距离
					}else{
						String s = String.valueOf(distance);
						tvDistance.setText(s.substring(0, s.indexOf(".")+4)+"米");//通过百度地图计算距离
					}
				}
				mBaidu.stop();
			}
		});
		mBaidu.start();
	}

	private void setupView() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		svParent = (ScrollView)findViewById(R.id.sv_parent);
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		btnFinish.setVisibility(View.GONE);
		tvTitleName = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitleName.setText(getResources().getString(R.string.str_searched_message_help));
		gvPhoto = (GridView)findViewById(R.id.gv_searched_item_help_photo);
		lvComment = (InnerListView)findViewById(R.id.lv_search_item_help_message);
		btnHelpSearch = (Button)findViewById(R.id.btn_searched_item_help_search);
		tvHelpUserName = (TextView)findViewById(R.id.tv_searched_item_user_name);
		tvHelpContent = (TextView)findViewById(R.id.tv_searched_item_help_text);
		layoutComment = (LinearLayout)findViewById(R.id.layout_comment);//用户评论按钮
		tvDistance = (TextView)findViewById(R.id.tv_searched_item_distance);//距离
		edtUserNewComment = (EditText)findViewById(R.id.edt_searched_item_newcomment);//用户新评论
		btnUserCommentSend = (Button)findViewById(R.id.btn_searched_item_newcomment);//用户新评论
		layoutNewComment = (RelativeLayout)findViewById(R.id.layout_new_comment);//用户新评论
		civUserIcon = (CircleImageView)findViewById(R.id.civ_searched_item_user_icon);
		lvComment.setParentScrollView(svParent);
		lvComment.setMaxHeight(400);
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchedHelpItemActivity.this.finish();
			}
		});
		gvPhoto.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(SearchedHelpItemActivity.this, PhotoActivity.class);
				intent.putExtra("ID", arg2);
				intent.putExtra("type","look");
				startActivity(intent);
			}
		});
		/**
		 * 帮助众寻
		 */
		btnHelpSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//暂时注释  qinwq 
				IbeaconApplication application = (IbeaconApplication) getApplication();
				if(!application.isSupport){
					Toast.makeText(SearchedHelpItemActivity.this, "手机不支持蓝牙,无法扫描设备", Toast.LENGTH_LONG).show();
					return;
				}
				if(data.getFindMac().length() == 0){
					Utils.showMsg(getApplicationContext(), "iPhone发布众寻，暂时无法帮助查找");
					return;
				}
				Intent intent = new Intent(SearchedHelpItemActivity.this, AddBeaconActivity.class);
				intent.putExtra(Consts.EXTRA_KEY_SCAN_TYPE, Consts.EXTRA_SCAN_TYPE_HELP);
				intent.putExtra("data", data);
				startActivityForResult(intent, Consts.REQUEST_HELP_SEARCH);
			}
		});
		//弹出评论框：帮助众寻并且找到了才能弹出评论框，否则不弹出
		layoutComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFound){
					if(layoutNewComment.getVisibility() == View.VISIBLE){
						layoutNewComment.setVisibility(View.GONE);
					}else{
						layoutNewComment.setVisibility(View.VISIBLE);
					}
				}else{
					Utils.showMsg(getApplicationContext(), "未找到不可以评论哦");
				}
			}
		});
		
		/**
		 * 发送评论
		 */
		btnUserCommentSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isFound){
					Utils.showMsg(getApplicationContext(), "未查找到不可以评论哦");
					return;
				}
				String newComment = edtUserNewComment.getText().toString();
				if(newComment ==null || "".equals(newComment)){
					Utils.showMsg(getApplicationContext(), "评论内容不能为空");
					return;
				}
				if(mBaiduLat == 0 && mBaiduLong == 0){
					Utils.showMsg(getApplicationContext(), "未查找到不能评论");
					return;
				}
				PublicWords word = new PublicWords();
				word.setUsername(usersp.getString(ConstsUser.USERNAME, null));
				word.setWordsContent(newComment);
				final PublicWords temp = word;
//				commentAdapter.addData(word);
				//需要发送到后台
				Log.i(TAG,""+newComment);
				AppRequest request = new AppRequest();
				request.setmRequestURL("/message/FiWordsAction!finds");
				request.putPara("clientType", "android");
				request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");
				request.putPara("findID", data.getFindID()+"");
				request.putPara("wordsContent", newComment);
				request.putPara("wordsDate", Utils.getCurrentDate());
				request.putPara("wordsLatitude",mBaiduLat+"");
				request.putPara("wordsLongitude",mBaiduLong+"");
				request.putPara("wordsAddress", mBaiduAddress);
				new AppThread(SearchedHelpItemActivity.this,request,new AppHandler() {
					@Override
					protected void handle(AppRequest request, AppResponse response) {
						// 数据格式是什么样的？
						if("0".equals(response.getmCode())){
							//添加成功,将当前评论添加到列表
							Toast.makeText(SearchedHelpItemActivity.this, "评论发送成功", Toast.LENGTH_SHORT).show();
							commentList.add(temp);
							commentAdapter = new CommentAdapter(SearchedHelpItemActivity.this, commentList);
							lvComment.setAdapter(commentAdapter);
//							commentAdapter.notifyDataSetChanged();
						}
					}
				}).start();
				layoutNewComment.setVisibility(View.GONE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null){
			return;
		}
		if(requestCode == Consts.REQUEST_HELP_SEARCH){
			if(resultCode == Consts.RESULT_HELP_SEARCH_SATATE){//
				isFound = data.getBooleanExtra("isFound",false);
				Log.i(TAG, "isFound = " + isFound);
				if(isFound){
					/*Intent retIntent = new Intent();
					retIntent.putExtra("isFound", isFound);
					setResult(Consts.RESULT_HELP_SEARCH_SATATE, retIntent);
					SearchedHelpItemActivity.this.finish();*/
					//找到
					final BaiduUtils mBaidu = new BaiduUtils(SearchedHelpItemActivity.this);
					mBaidu.setCallback(new Callback() {

						@Override
						public void onResult(BDLocation location) {
							//当前坐标
							if(location==null) return;
							mBaiduLat = location.getLatitude();
							mBaiduLong = location.getLongitude();
							mBaiduAddress = location.getAddrStr();
							mBaidu.stop();
						}
					});
					mBaidu.start();
					layoutNewComment.setVisibility(View.VISIBLE);
//						Toast.makeText(SearchedHelpItemActivity.this, "找到了", 1).show();
				}
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searched_help_item, menu);
		return true;
	}
	
	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;
		private List<String> lists;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context,List<String> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		public void update1(List<String> lists) {
			loading1();
		}

		public int getCount() {
			return lists.size();
		}

		public Object getItem(int arg0) {

			return lists.get(arg0);
		}

		public long getItemId(int arg0) {

			return arg0;
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
		public View getView(final int position, View convertView, ViewGroup parent) {
//			Log.i(TAG, "getView ....");
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
			File f = new File(Consts.IMG_PATH+lists.get(position));
			if(f.isDirectory()){
//				Utils.showMsg(getApplicationContext(), "没有发布图片！");
				return convertView;
			}
//			Log.i(TAG, "1, Bimp.drr.size = "+Bimp.drr.size() + ", Bimp.drr.add( "+f.getAbsolutePath()+" )");
//			if(f.exists()){ // BitmapUtils.loadBitmap(path, width, height)
//				holder.image.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath())); //OOM!
//			}else{
				ImageLoaderUtil imageLoader = ImageLoaderUtil.getInstance(SearchedHelpItemActivity.this);
				imageLoader.getImage(holder.image, Utils.mServerImgPath+lists.get(position));
				imageLoader.setCallBack(new ImageLoaderCallBack() {
					@Override
					public void refreshAdapter() {
//						if(adapter!=null){
//							adapter.notifyDataSetChanged();
//							if(!Bimp.drr.contains(Consts.IMG_PATH+lists.get(position))){
//								Bimp.drr.add(Consts.IMG_PATH+lists.get(position));
//							}
//							Log.i(TAG, "2, Bimp.drr.size = "+Bimp.drr.size() + ", Bimp.drr.add( "+Consts.IMG_PATH+lists.get(position)+" )");
//						}
					}
				});	
//			}
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
//						Log.i(TAG, "loading1 run true");
						if (Bimp.max == Bimp.drr.size()) {
//							Log.i(TAG, "if: bimp.max = " + Bimp.max + ", bimp.drr.size = " + Bimp.drr.size());
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
//							Log.i(TAG, "else: bimp.max = " + Bimp.max + ", bimp.drr.size = " + Bimp.drr.size());
							try {
								String path = Bimp.drr.get(Bimp.max);// /storage/emulated/0/ARTICLE_IMG/2398734975.jpg 
								String name = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
//								Bimp.bmp.contains(path)
								if(name == null || name.length() == 0){
									return;
								}
								Bitmap bm = Bimp.revisionImageSize(Consts.IMG_PATH+File.separator+path);
								if(bm==null)return;
								Bimp.bmp.add(bm);
								FileUtils.saveBitmap(bm, "" + name);
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
	
	/**
	 * 评论适配器
	 */
	@SuppressLint("HandlerLeak")
	public class CommentAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private ArrayList<PublicWords> data = new ArrayList<PublicWords>();
		public CommentAdapter(Context context, ArrayList<PublicWords> words) {
			inflater = LayoutInflater.from(context);
			data = words;
		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int arg0) {

			return data.get(arg0);
		}

		public long getItemId(int arg0) {

			return arg0;
		}
		
		public void addData(PublicWords words){
			data.add(words);
			this.notifyDataSetChanged();
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.comment,
						parent, false);
				holder = new ViewHolder();
				holder.tvUsername = (TextView) convertView.findViewById(R.id.tv_comment_username);
				holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvUsername.setText(data.get(position).getUsername()+":");
			holder.tvComment.setText(data.get(position).getWordsContent());
			return convertView;
		}

		public class ViewHolder {
			public TextView tvUsername;
			public TextView tvComment;
		}
	}
	
	protected void onRestart() {
//		adapter.update1();
		super.onRestart();
		Log.i(TAG, "onRestart()");
	}

}
