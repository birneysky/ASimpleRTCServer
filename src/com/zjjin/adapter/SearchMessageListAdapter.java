package com.zjjin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.published.FileUtils;
import com.example.slidingmenu.R;
import com.view.circleimageview.CircleImageView;
import com.zijin.ibeacon.util.BaiduUtils;
import com.zijin.ibeacon.util.BaiduUtils.Callback;
import com.zijin.ibeacon.util.ImageLoaderUtil;
import com.zijin.ibeacon.util.ImageLoaderUtil.ImageLoaderCallBack;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.PublicFind;
import com.zjjin.utils.Consts;

public class SearchMessageListAdapter extends BaseAdapter implements OnScrollListener{
	private List<PublicFind> list;
	private LayoutInflater inflater;
	private Activity activity;
	private String tempAddress;
	private boolean isScrolling = false;
	
	public SearchMessageListAdapter(Activity activity, Context context, List<PublicFind> list){
		if(list != null){
			this.list = list;
		}else{
			this.list = new ArrayList<PublicFind>();
		}
		this.inflater = LayoutInflater.from(context);
		this.activity = activity;
	}
	
	public void putData(List<PublicFind> data){
		list =data;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.search_message_item, null);
			holder = new ViewHolder();
			holder.tvUserName = (TextView)convertView.findViewById(R.id.tv_search_message_user_name);
			holder.tvSearchMessage = (TextView)convertView.findViewById(R.id.tv_search_message_message);
			holder.tvSearchDistance = (TextView)convertView.findViewById(R.id.tv_search_message_dis);
			holder.civSearchIcon = (CircleImageView)convertView.findViewById(R.id.civ_search_message_item);
			holder.ivSearchLoc = (ImageView)convertView.findViewById(R.id.iv_search_message_loc);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		PublicFind myFind = (PublicFind) getItem(position);
		holder.tvUserName.setText(myFind.getUsername());
		holder.tvSearchMessage.setText(myFind.getFindDesc());
		/*String address = myFind.getFindAddress();
		if(address != null && address.length() != 0){
			holder.tvSearchDistance.setText(address);
		}else{
			String lat = myFind.getFindLatitude();
			String lng = myFind.getFindLongitude();
			if((lat != null && lat.length() != 0) && (lng != null && lng.length() != 0)){
				LatLng latLng = new LatLng(Double.parseDouble(myFind.getFindLatitude()), Double.parseDouble(myFind.getFindLongitude()));
				BaiduGeoCoder baiduGeoCoder = new BaiduGeoCoder(latLng);
				baiduGeoCoder.setCallback(new Callback() {
					@Override
					public void onResult(String address) {
						tempAddress = address;
						list.get(position).setFindAddress(tempAddress);
						notifyDataSetChanged();
					}
				});
				holder.tvSearchDistance.setText(tempAddress);
			}else{
				holder.tvSearchDistance.setText("该用户未上传地理位置");
			}
		}*/
		holder.tvSearchDistance.setText("正在获取...");
		if(!"".equals(myFind.getFindLatitude()) && myFind.getFindLatitude()!=null){
			double mLat = Double.valueOf(myFind.getFindLatitude());
			double mLot = Double.valueOf(myFind.getFindLongitude());
			final TextView txtDistance = holder.tvSearchDistance;
			final LatLng latLng =new LatLng(mLat, mLot);
			final BaiduUtils utils = new BaiduUtils(activity);
			utils.setCallback(new Callback() {
				@Override
				public void onResult(BDLocation location) {
//						LatLng mCurrentLat = new LatLng(location.getLatitude(),location.getLongitude());
					double distance = utils.getInstance(latLng);
					if(distance <=0){
						txtDistance.setText("无法获取");//通过百度地图计算距离
					}else{
						if(distance>1000){
							txtDistance.setText(distance/1000+"千米");//通过百度地图计算距离
						}else{
							txtDistance.setText(distance+"米");//通过百度地图计算距离
						}
					}
//					utils.stop();
				}
			});
			utils.start();
		}else{
			holder.tvSearchDistance.setText("正在获取...");
		}
			
		holder.civSearchIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.app_logo));
		try {
			//设置头像 ：1.我的众寻我的头像; 2.帮助众寻别人头像
			String iconPath = myFind.getHeadPicture();
			if(holder.civSearchIcon != null && iconPath != null && iconPath.length() != 0){
				if(FileUtils.fileIsExists(Consts.IMG_PATH+iconPath)){
					Bitmap iconBmp = BitmapFactory.decodeFile(Consts.IMG_PATH+iconPath);
					if(iconBmp != null){
						holder.civSearchIcon.setImageBitmap(iconBmp);
					}
				}else{
					ImageLoaderUtil imageLoader = ImageLoaderUtil.getInstance(activity);
					imageLoader.getImage(holder.civSearchIcon, Utils.mServerImgPath+iconPath);
					imageLoader.setCallBack(new ImageLoaderCallBack() {
						@Override
						public void refreshAdapter() {
//							notifyDataSetChanged();
							
						}
					});	
				}
			}else{
				holder.civSearchIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.app_logo));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		holder.ivSearchLoc.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_distance_loc));
		return convertView;
	}
	
	class ViewHolder{
		private TextView tvUserName, tvSearchMessage, tvSearchDistance;
		private CircleImageView civSearchIcon;
		private ImageView ivSearchLoc;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		/** * firstVisibleItem 表示在当前屏幕显示的第一个listItem在整个listView里面的位置（下标从0开始） 
	        * visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数 
	        * totalItemCount表示ListView的ListItem总数  
	        * listView.getLastVisiblePosition()表示在现时屏幕最后一个ListItem 
	        * (最后ListItem要完全显示出来才算)在整个ListView的位置（下标从0开始）  
	        */  
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		/** *scrollState有三种状态，分别是SCROLL_STATE_IDLE、SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING
		    *SCROLL_STATE_IDLE是当屏幕停止滚动时
		    *SCROLL_STATE_TOUCH_SCROLL是当用户在以触屏方式滚动屏幕并且手指仍然还在屏幕上时（The user is scrolling using touch, and their finger is still on the screen）
		    *SCROLL_STATE_FLING是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时（The user had previously been scrolling using touch and had performed a fling）
		    */  
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			isScrolling = false;
		}else if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
			isScrolling = true;
		}else if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
			isScrolling = true;
		}
	}

}
