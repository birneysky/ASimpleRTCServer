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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
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

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.published.Bimp;
import com.example.published.FileUtils;
import com.example.published.PhotoActivity;
import com.example.slidingmenu.SearchedHelpItemActivity.GridAdapter;
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

public class SearchedMineItemActivity extends Activity {
	
	private static final String TAG = SearchedMineItemActivity.class.getSimpleName();
	private PublicFind data;
	private GridView gvPhoto;
	private RelativeLayout layoutNewComment;
	private InnerListView lvComment;
	private ScrollView scrollView;
	private Button btnHelpSearch;
	private TextView tvHelpUserName,tvHelpContent,tvDistance;
	private EditText edtUserNewComment;
	private Button btnUserCommentSend;
	private CircleImageView civUserIcon; 	// 用户头像
	private LinearLayout layoutComment; 	// 评论按钮
	private GridAdapter adapter;
	private CommentAdapter commentAdapter;
	private ArrayList<String> comments= new ArrayList<String>();//用户评论
	private ArrayList<String> imagesPath =new ArrayList<String>();
	// title
	private RelativeLayout  menuBackConstruct;
	private Button btnFinish;
	private TextView tvTitleName;
	private SharedPreferences usersp;
	
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
		data = (PublicFind) getIntent().getSerializableExtra("data");
		if(data==null) return;
		tvHelpUserName.setText(data.getUsername());
		tvHelpContent.setText(data.getFindDesc());
		String path = data.getHeadPicture();
		setUserIcon(path);
		Bimp.max = 0;
		Bimp.drr.clear();
		Bimp.bmp.clear();
		/**
		 * 添加图片测试
		 */
		ArrayList<String> lists = new ArrayList<String>(); //图片路径集合
		if(data.getFindPicture()!=null){
			String[] images = data.getFindPicture().split(",");
			if(images!=null && images.length>0){
				for(String name :images){
					lists.add(name);
					Bimp.drr.add(name);
				}
			}
			
		}
		adapter = new GridAdapter(this, lists);
		adapter.update1();//?为什么调用bm预览
		gvPhoto.setAdapter(adapter);
		//获取之间距离
		if(data.getFindLatitude()!=null && data.getFindLongitude()!=null){
			getDistanceFromDevice(data.getFindLatitude(),data.getFindLongitude());
		}
//		tvDistance.setText(data.getFindAddress());
		//评论 留言
		//需要发送到后台
		AppRequest request = new AppRequest();
		request.setmRequestURL("/message/FiWordsAction!message");
		request.putPara("clientType", "android");
		request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");
		request.putPara("findID", data.getFindID()+"");
		new AppThread(SearchedMineItemActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					Gson gson = new Gson();
					ArrayList<PublicWords> myfinds = gson.fromJson(response.getData(), new TypeToken<List<PublicWords>>(){}.getType());
					if(myfinds !=null && myfinds.size()>0){
						addInfoList(myfinds);
					}
				}else{
//					Toast.makeText(SearchedMineItemActivity.this, "未获取到帮助的众寻信息", Toast.LENGTH_SHORT).show();
				}
			}
		}).start();
	}
	/**
	 * 设置用户头像
	 * @param path：头像路径
	 */
	private void setUserIcon(final String path) {
		try {
			//设置头像
			if(civUserIcon!=null && path!=null && path.length() != 0){
				ImageLoaderUtil imageLoader = ImageLoaderUtil.getInstance(SearchedMineItemActivity.this);
				imageLoader.getImage(civUserIcon, Utils.mServerImgPath+path);
				imageLoader.setCallBack(new ImageLoaderCallBack() {
					@Override
					public void refreshAdapter() {
						Bitmap iconBmp = BitmapFactory.decodeFile(Consts.IMG_PATH+path);
						if(iconBmp!=null){
							civUserIcon.setImageBitmap(iconBmp);
						}
					}
				});	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取距离
	 * @param findLatitude
	 * @param findLongitude
	 */
	private void getDistanceFromDevice(String findLatitude, String findLongitude) {
		final BaiduUtils mBaidu = new BaiduUtils(SearchedMineItemActivity.this); 
		double mLat = new Double(findLatitude);
		double mLot = new Double(findLongitude);
		final LatLng lat = new LatLng(mLat, mLot); 
		mBaidu.setCallback(new Callback() {
			@Override
			public void onResult(BDLocation location) {
				LatLng mCurrentLat = new LatLng(location.getLatitude(),location.getLongitude());
				double distance = mBaidu.getInstance(lat);
				if(distance <=0){
					tvDistance.setText("无法获取");//通过百度地图计算距离
				}else{
					tvDistance.setText(distance+"米");//通过百度地图计算距离
				}
				mBaidu.stop();
			}
		});
		mBaidu.start();
	}

	/**
	 * 从服务器获取图片
	 * @param data2
	 */
	private void getImgFromServer(PublicFind data2) {
		String[] imgsArr =data.getFindPicture().split(",");
		if(imgsArr!=null && imgsArr.length>0){
			for(String name:imgsArr){
				imagesPath.add(name);
				Bimp.drr.add(name);
			}
		}
		if(imagesPath!=null && imagesPath.size()>0){
			adapter = new GridAdapter(this,imagesPath);
			adapter.update1();
			gvPhoto.setAdapter(adapter);
		}
		
	}

	protected void addInfoList(ArrayList<PublicWords> myfinds) {
		commentAdapter = new CommentAdapter(this,myfinds);
		lvComment.setAdapter(commentAdapter);
	}

	private void setupView() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		civUserIcon = (CircleImageView)findViewById(R.id.civ_searched_item_user_icon);
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		btnFinish.setVisibility(View.GONE);
		tvTitleName = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitleName.setText(getResources().getString(R.string.str_searched_message_my));
		gvPhoto = (GridView)findViewById(R.id.gv_searched_item_help_photo);
		lvComment = (InnerListView)findViewById(R.id.lv_search_item_help_message);
		scrollView = (ScrollView)findViewById(R.id.sv_parent);
		btnHelpSearch = (Button)findViewById(R.id.btn_searched_item_help_search);
		tvHelpUserName = (TextView)findViewById(R.id.tv_searched_item_user_name);
		tvHelpContent = (TextView)findViewById(R.id.tv_searched_item_help_text);
		layoutComment = (LinearLayout)findViewById(R.id.layout_comment); 	// 评论按钮
		tvDistance = (TextView)findViewById(R.id.tv_searched_item_distance);//距离
		edtUserNewComment = (EditText)findViewById(R.id.edt_searched_item_newcomment);//用户新评论
		btnUserCommentSend = (Button)findViewById(R.id.btn_searched_item_newcomment);//用户新评论
		layoutNewComment = (RelativeLayout)findViewById(R.id.layout_new_comment);//用户新评论
		lvComment.setParentScrollView(scrollView);
		lvComment.setMaxHeight(400);
		hideView();
	}
	private void hideView(){
		layoutNewComment.setVisibility(View.GONE);
		btnHelpSearch.setVisibility(View.GONE);
		layoutComment.setVisibility(View.GONE);
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchedMineItemActivity.this.finish();
			}
		});
		gvPhoto.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(SearchedMineItemActivity.this, PhotoActivity.class);
				intent.putExtra("ID", arg2);
				intent.putExtra("type","look");
				startActivity(intent);
			}
		});
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
		private ArrayList<String> datas;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context,ArrayList<String> imagePath) {
			inflater = LayoutInflater.from(context);
			datas = imagePath;
		}

		public void update1() {
			loading1();
		}

		public int getCount(){
			return datas.size();
		}

		public String getItem(int arg0) {

			return datas.get(arg0);
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
		public View getView(final int position, View convertView, ViewGroup parent) {
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
			File f = new File(Consts.IMG_PATH + datas.get(position));
			if(f.isDirectory()){
				return convertView;
			}
			ImageLoaderUtil imageLoader = ImageLoaderUtil.getInstance(SearchedMineItemActivity.this);
			imageLoader.getImage(holder.image, Utils.mServerImgPath+datas.get(position));
			imageLoader.setCallBack(new ImageLoaderCallBack() {
				@Override
				public void refreshAdapter() {
				}
			});	
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
								String name = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								if(name == null || name.length() == 0){
									return;
								}
								Bitmap bm = Bimp.revisionImageSize(Consts.IMG_PATH+File.separator+path);
								if(bm == null)return;
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

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
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
		super.onRestart();
	}

}
