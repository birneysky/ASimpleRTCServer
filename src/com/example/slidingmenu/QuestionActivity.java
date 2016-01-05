package com.example.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zijin.ibeacon.model.IbeaconQuestion;
import com.zjjin.utils.ConstsUser;
import com.zjjin.utils.ScreenUtils;

public class QuestionActivity extends Activity {
	private final String TAG = QuestionActivity.class.getSimpleName();
	private int width;
	private ExpandableListView elvQuestion;
	private  List<String> groupArray; //组列表
	private  List<String> childArray; //子列表
	private ExpandableListViewaAdapter mAdapter;
	private RelativeLayout titleBack;
	private SharedPreferences usersp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_question);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_question);
		getData();
		setupView();
		addListener();
	}

	private void getData() {
		width = ScreenUtils.getScreenWidth(getApplicationContext());
		groupArray = new ArrayList<String>();
        childArray = new ArrayList<String>();
        usersp = getSharedPreferences(ConstsUser.USERSPNAME, MODE_PRIVATE);
	}

	private void setupView() {
		titleBack = (RelativeLayout)findViewById(R.id.menu_back_construct);
		elvQuestion = (ExpandableListView)findViewById(R.id.elv_activity_question);
		elvQuestion.setGroupIndicator(null);
		initdata();
		mAdapter = new ExpandableListViewaAdapter(QuestionActivity.this);
		elvQuestion.setAdapter(mAdapter);
	}


	private void initdata() {
		AppRequest request= new AppRequest();
		request.setmRequestURL("/find/PubFindAction!question");//
		request.putPara("telephone", usersp.getString(ConstsUser.PHONENUM, null));
		request.putPara("password", usersp.getString(ConstsUser.PASSWORD, null));
		request.putPara("clientType", "android");
		new AppThread(QuestionActivity.this,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					Toast.makeText(QuestionActivity.this, response.getmMessage(), Toast.LENGTH_SHORT).show();
					if(!"".equals(response.getData())){
						Gson gson = new Gson();
						List<IbeaconQuestion> questions = gson.fromJson(response.getData(), new TypeToken<List<IbeaconQuestion>>(){}.getType());
						if(questions !=null){
							addInfoList(questions);
						}
					}
				}
				
				
			}
		}).start();
		LoadingIndicator.show(QuestionActivity.this, "正在加载数据...");
//		addInfo(getResources().getStringArray(R.array.menu_group), 
//				getResources().getStringArray(R.array.menu_child));
	}
	private void addInfoList(List<IbeaconQuestion> questions){
		for(IbeaconQuestion question:questions){
			if(question.getTitle()!=null && !"".equals(question.getTitle())){
				groupArray.add(question.getTitle());
				childArray.add(question.getContent());
			}
		}
		mAdapter.notifyDataSetChanged();
	}
	private void addInfo(String[] group, String[] child) {
		for(int i = 0; i < group.length; i++){
			groupArray.add(group[i]);
			childArray.add(child[i]);
		}
    }

	private void addListener() {
		elvQuestion.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
			}
		});
		
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionActivity.this.finish();
			}
		});
	}


	class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
		private LayoutInflater inflater;
		private Activity activity;

		public ExpandableListViewaAdapter(Context context, List<String> groupArray, List<Integer> childArray){
			this.inflater = LayoutInflater.from(context);
			
		}
		
		public ExpandableListViewaAdapter(QuestionActivity questionActivity) {
			// TODO Auto-generated constructor stub
			this.activity = questionActivity;
			this.inflater = LayoutInflater.from(questionActivity);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childArray.get(groupPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String string = childArray.get(groupPosition);
			View childLayout = inflater.inflate(R.layout.menu_question_child_item, null);
			TextView childTextView = (TextView) childLayout.findViewById(R.id.tv_question_item_answer);
			childTextView.setText(string);
//			return getGenericView(string);
			return childLayout;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupArray.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groupArray.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "parent view");
//			RelativeLayout parentLayout = (RelativeLayout) View.inflate(context, R.layout.menu_question_parent_item, null);
			View parentLayout = inflater.inflate(R.layout.menu_question_parent_item, null);
			TextView parentTextView = (TextView) parentLayout.findViewById(R.id.tv_question_item_quest);
			parentTextView.setText(groupArray.get(groupPosition));
			ImageView parentImageViw = (ImageView) parentLayout.findViewById(R.id.iv_question_item_arrow);
			// 判断isExpanded就可以控制是按下还是关闭，同时更换图片
			if (isExpanded) {
				parentImageViw.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
			} else {
				parentImageViw.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
			}
			return parentLayout;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
		
		private TextView getGenericView(String string ) {
              AbsListView.LayoutParams  layoutParams = new AbsListView.LayoutParams(
            		  ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
              
              TextView  textView = new TextView(activity);
              textView.setLayoutParams(layoutParams);
               
              textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
              textView.setPadding(40, 0, 0, 0);
              textView.setBackgroundColor(getResources().getColor(R.color.bg_menu_question_item_child));
              textView.setText(string);
              textView.setTextSize(13.0f);
              textView.setTextColor(getResources().getColor(R.color.black));
              return textView;
         }
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.question, menu);
		return true;
	}

}
