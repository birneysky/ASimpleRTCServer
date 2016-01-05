package com.zjjin.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class BaiduGeoCoder implements OnGetGeoCoderResultListener{
	
	private LatLng mLatLng;
	private GeoCoder mPoiSearch;
	private Callback mCallback;
	private String address;
	
	public BaiduGeoCoder(LatLng latlng){
		// 创建地理编码检索实例 
		mPoiSearch = GeoCoder.newInstance();
		mPoiSearch.setOnGetGeoCodeResultListener(this);
		mPoiSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
	
	}
	
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		mLatLng = result.getLocation();
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		address = result.getAddress();
		if(mPoiSearch != null){
			mPoiSearch.destroy();
		}
		mCallback.onResult(address);
	}
	
	public static abstract class Callback {
        /**
         * 获取当前位置信息
         * location.getLatitude();
		   mBaiduLong = location.getLongitude();
		      中文location.getAddrStr()
         * @param location
         */
        public abstract void onResult(String address);
    } 
	public void setCallback(Callback call){
		mCallback = call;
	}
	
   public void onResult(String address) {
	   if(mCallback==null)return;
	   mCallback.onResult(address);
    }
}
