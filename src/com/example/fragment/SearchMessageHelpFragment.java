package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.slidingmenu.R;
import com.example.slidingmenu.SearchedHelpItemActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.http.LoadingIndicator;
import com.zjjin.adapter.SearchMessageListAdapter;
import com.zjjin.entity.PublicFind;
import com.zjjin.utils.ConstsUser;

public class SearchMessageHelpFragment extends Fragment {
	
	private ListView lvMessageHelp;
	private ArrayList<PublicFind> list;
	private SearchMessageListAdapter adapter;
	private View view;
	private Activity mActivity; 
	private SharedPreferences usersp;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		usersp = getActivity().getSharedPreferences(ConstsUser.USERSPNAME, getActivity().getApplicationContext().MODE_PRIVATE);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.activity_location_note, container, false);
		}
		setupView(view);
		addListener();
		initData();
		return view;
	}
	
	/**
	 * 从后台获取数据,需要帮助的众寻
	 */
	private void initData() {
		AppRequest request =new AppRequest();
		request.setmRequestURL("/help/SearchHelpAction!helpsearch");
		request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");//userId
		new AppThread(mActivity,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					Gson gson = new Gson();
					ArrayList<PublicFind> myfinds = gson.fromJson(response.getData(), new TypeToken<List<PublicFind>>(){}.getType());
					if(myfinds !=null){
						addInfoList(myfinds);
					}
				}else{
					Toast.makeText(mActivity, "未获取到帮助的众寻信息", Toast.LENGTH_SHORT).show();
				}
			}
		}).start();
		LoadingIndicator.show(mActivity, "正在获取列表信息...");
	}
	private void addInfoList(ArrayList<PublicFind> questions){
		list = questions;
		adapter.putData(list);
		adapter.notifyDataSetChanged();
	}
	public void getItem(){
		Log.i("info", "listView111 item clicked!");
	}

	private void setupView(View view) {
		if(lvMessageHelp == null){
			lvMessageHelp = (ListView)view.findViewById(R.id.lv_location_list);
		}
		list = new ArrayList<PublicFind>();
		adapter = new SearchMessageListAdapter(getActivity(), getActivity().getApplicationContext(), list);
		lvMessageHelp.setAdapter(adapter);
	}

	public SearchMessageListAdapter getAdapter(){
		return adapter;
	}
	
	private void addListener() {
		lvMessageHelp.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(), SearchedHelpItemActivity.class);
				PublicFind data = (PublicFind) adapter.getItem(arg2);
				Bundle mBundle = new Bundle();  
		        mBundle.putSerializable("data", data);  
		        intent.putExtras(mBundle); 
				startActivity(intent);
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		System.out.println("AAAAAAAAAA____onActivityCreated");
	}

	public View getView(Context context) {
		/*if(inflater == null){
			inflater = LayoutInflater.from(context);
		}
		if(view == null){
			view = inflater.inflate(R.layout.activity_location_note, container, false);
		}
		if(lvMessageHelp == null){
			lvMessageHelp = (ListView)view.findViewById(R.id.lv_location_list);
		}*/
		return lvMessageHelp;
	}
	
	@Override
	public void onStart() {
		super.onStart();
//		System.out.println("AAAAAAAAAA____onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
//		System.out.println("AAAAAAAAAA____onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
//		System.out.println("AAAAAAAAAA____onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
//		System.out.println("AAAAAAAAAA____onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		System.out.println("AAAAAAAAAA____onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		System.out.println("AAAAAAAAAA____onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
//		System.out.println("AAAAAAAAAA____onDetach");
	}

}
