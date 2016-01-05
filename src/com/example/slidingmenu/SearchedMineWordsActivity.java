package com.example.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.PublicFind;
import com.zjjin.entity.PublicWords;
import com.zjjin.utils.ConstsUser;

/**
 * 从mainactivity中过来，获取对应设备的留言列表
 * @author Qinwq
 *
 */
public class SearchedMineWordsActivity extends Activity {
	
	private PublicFind data;
	private ListView lvComment;
	private EditText edtUserNewComment;
	private Button btnUserCommentSend;
	private ImageView ivUserComment;
	private CommentAdapter commentAdapter;
	private ArrayList<String> comments= new ArrayList<String>();//用户评论
	private double mBaiduLong,mBaiduLat;
	private SharedPreferences usersp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searched_help_item);
		setupView();
		addListener();
		getData();
	}

	private void addListener() {
		lvComment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i("info", "listView item clicked!");
				if(data==null)return;
				Intent intent = new Intent(SearchedMineWordsActivity.this, SearchedMineItemActivity.class);
				Bundle mBundle = new Bundle();  
		        mBundle.putSerializable("data", data);  
		        intent.putExtras(mBundle); 
				startActivity(intent);
			}
		});
	}

	private void getData() {
		data = (PublicFind) getIntent().getSerializableExtra("data");
		//评论 留言
		AppRequest request = new AppRequest();
		request.setmRequestURL("/message/FiWordsAction!message");
		request.putPara("clientType", "android");
		request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");
		request.putPara("findMac", data.getFindMac());
		new AppThread(SearchedMineWordsActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					Gson gson = new Gson();
					ArrayList<PublicWords> myfinds = gson.fromJson(response.getData(), new TypeToken<List<PublicWords>>(){}.getType());
					if(myfinds !=null && myfinds.size()>0){
						addInfoList(myfinds);
					}
				}else{
					Utils.showMsg(SearchedMineWordsActivity.this, "未获取到帮助的众寻信息");
				}
			}
		}).start();
	}
	
	protected void addInfoList(ArrayList<PublicWords> myfinds) {
		commentAdapter = new CommentAdapter(this,myfinds);
		lvComment.setAdapter(commentAdapter);
	}

	private void setupView() {
		usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
		lvComment = (ListView)findViewById(R.id.lv_search_item_help_message);
		ivUserComment = (ImageView)findViewById(R.id.iv_searched_item_icon_comment);//用户评论按钮
		edtUserNewComment = (EditText)findViewById(R.id.edt_searched_item_newcomment);//用户新评论
		btnUserCommentSend = (Button)findViewById(R.id.btn_searched_item_newcomment);//用户新评论
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searched_help_item, menu);
		return true;
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
