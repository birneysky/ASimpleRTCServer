package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slidingmenu.IbeaconApplication;
import com.example.slidingmenu.R;
import com.example.slidingmenu.SearchedMineItemActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zijin.ibeacon.http.AppHandler;
import com.zijin.ibeacon.http.AppRequest;
import com.zijin.ibeacon.http.AppResponse;
import com.zijin.ibeacon.http.AppThread;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.adapter.SearchMessageListAdapter;
import com.zjjin.entity.PublicFind;
import com.zjjin.utils.ConstsUser;
/**
 * 我的众寻
 * @author birney
 *
 */
public class SearchMessageMineFragment extends Fragment {
	
	private ListView lvMessageHelp;
	private ArrayList<PublicFind> list;
	private SearchMessageListAdapter adapter;
	private View view;
	private Activity mActivity;
	private SharedPreferences usersp;
	private Handler handler;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getApplicationContext();
		usersp = getActivity().getSharedPreferences(ConstsUser.USERSPNAME, Context.MODE_PRIVATE);
		this.handler = IbeaconApplication.getInstance().getHandler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.activity_location_note, container,false);
		}
		setupView(view);
		addListener();
		initData();
		return view;
	}
	
	/**
	 * 从后台获取数据,需要我发布的
	 */
	private void initData() {
		AppRequest request =new AppRequest();
		request.setmRequestURL("/find/PubFindAction!myfinds");
		request.putPara("userID", usersp.getInt(ConstsUser.ID, 0)+"");//userId
		request.putPara("clientType", "android");
		new AppThread(mActivity,request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				if("0".equals(response.getmCode())){
					Gson gson = new Gson();
					ArrayList<PublicFind> myfinds = gson.fromJson(response.getData(), new TypeToken<List<PublicFind>>(){}.getType());
					if(myfinds !=null && myfinds.size()>0){
						addInfoList(myfinds);
					}else{
						Toast.makeText(mActivity, "未获取到帮助的众寻信息", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(mActivity, "获取帮助众寻信息错误", Toast.LENGTH_SHORT).show();
				}
			}

		}).start();
//		LoadingIndicator.show(mActivity, "正在获取列表信息...");
	}
	private void addInfoList(ArrayList<PublicFind> questions){
		list = questions;
		adapter.putData(list);
		adapter.notifyDataSetChanged();
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
				Intent intent = new Intent(getActivity(), SearchedMineItemActivity.class);
				PublicFind data = (PublicFind) adapter.getItem(arg2);
				Bundle mBundle = new Bundle();  
		        mBundle.putSerializable("data", data);  
		        intent.putExtras(mBundle); 
				startActivity(intent);
			}
		});
		
		lvMessageHelp.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("操作");
				menu.add(1, 3, 4, "删除");
			}
		});
	}

	/**
	 * 选中按钮事件处理
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
		case 3:
			cancelFind(list.get(info.position), info.position);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	
	private void cancelFind(PublicFind myfind, final int position) {
		String findMac = myfind.getFindMac();
		if(findMac ==null){
			Utils.showMsg(getActivity().getApplicationContext(), "删除我的众寻失败！请联系管理员后台删除！");
			return;
		}
		//需要发送到后台
		AppRequest request = new AppRequest();
		request.setmRequestURL("/find/PubFindAction!cancelFind");
		request.putPara("clientType", "android");
		request.putPara("findMac", findMac);
		new AppThread(getActivity(),request,new AppHandler() {
			@Override
			protected void handle(AppRequest request, AppResponse response) {
				// 报文格式是什么样的？
				if("0".equals(response.getmCode())){
					list.remove(position);
					adapter.notifyDataSetChanged();
					Utils.showMsg(getActivity(), "删除成功");
					//返回主界面时，主界面要更新主界面
					Message msg = new Message();
					msg.what = 1211;
					handler.sendMessage(msg);
				}else{
					Utils.showMsg(getActivity(), "删除失败");
				}
			}
		}).start();
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
