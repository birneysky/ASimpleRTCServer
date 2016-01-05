package com.zjjin.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.slidingmenu.R;
import com.example.slidingmenu.SafityAreaActivity;
import com.zjjin.entity.TrackerSafityArea;

public class SafityAreaListAdapter extends BaseAdapter {
	private SafityAreaActivity context;
	private List<TrackerSafityArea> list;
	private LayoutInflater inflater;

	public SafityAreaListAdapter(SafityAreaActivity context, List<TrackerSafityArea> list){
		this.context = context;
		if(list != null){
			this.list = list;
		}else{
			this.list = new ArrayList<TrackerSafityArea>();
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
	
	public void addData(TrackerSafityArea area){
		list.add(area);
		this.notifyDataSetChanged();
	}
	
	public void removeArea(TrackerSafityArea area){
		list.remove(area);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.safityarea_item, null);
			holder = new ViewHolder();
			holder.tvAddress = (TextView)convertView.findViewById(R.id.tv_safity_address);
			holder.tvSafityName = (TextView)convertView.findViewById(R.id.tv_safity_name);
			holder.ivSafityIcon = (ImageView)convertView.findViewById(R.id.iv_safity_icon);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		TrackerSafityArea tsa = (TrackerSafityArea) getItem(position);
		holder.tvAddress.setText(tsa.getAddress());
		holder.tvSafityName.setText(tsa.getAreaName());
		holder.ivSafityIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_loc));
		return convertView;
	}
	
	
	class ViewHolder{
		private TextView tvSafityName, tvAddress;
		private ImageView ivSafityIcon;
	}

}
