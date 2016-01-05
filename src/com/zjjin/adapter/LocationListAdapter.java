package com.zjjin.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.slidingmenu.R;
import com.zjjin.entity.TrackerLocation;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocationListAdapter extends BaseAdapter {
	private List<TrackerLocation> list;
	private LayoutInflater inflater;
	
	public LocationListAdapter(Context context, List<TrackerLocation> list){
		if(list != null){
			this.list = list;
		}else{
			this.list = new ArrayList<TrackerLocation>();
		}
		this.inflater = LayoutInflater.from(context);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.location_item, null);
			holder = new ViewHolder();
			holder.tvTime = (TextView)convertView.findViewById(R.id.tv_location_time);
			holder.tvAddress = (TextView)convertView.findViewById(R.id.tv_location_address);
			holder.tvDate = (TextView)convertView.findViewById(R.id.tv_location_date);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		TrackerLocation tl = (TrackerLocation) getItem(position);
		holder.tvTime.setText(tl.getTime());
		holder.tvAddress.setText(tl.getAddress());
		holder.tvDate.setText(tl.getDate());
		return convertView;
	}
	
	class ViewHolder{
		private TextView tvTime, tvDate, tvAddress;
	}

}
