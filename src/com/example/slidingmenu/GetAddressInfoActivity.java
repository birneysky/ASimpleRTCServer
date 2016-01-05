package com.example.slidingmenu;

import java.util.List;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.zjjin.utils.GetAddressUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.location.BDLocation;

public class GetAddressInfoActivity extends Activity {
	
	private final String TAG = GetAddressInfoActivity.class.getSimpleName();
	private RelativeLayout  menuBackConstruct;
	private TextView tvTitle;
	private Button btnFinish;
	
	private ListView listView;
	private ProvinceAdapter adapter;
	
	private GetAddressUtil location;
	private List<String> addressList = null;
	private boolean isCityChoose = false;
	private String province = null;
	private String city = null;
	// 百度地图定位
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private String currentLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_address_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
		// 百度地图定位
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );
	    mLocationClient.start();
		setupView();
		addListener();
	}
	
	private void setupView() {
		// TODO Auto-generated method stub
		location = new GetAddressUtil(this);
		addressList = location.getProvinceList();
		btnFinish = (Button)findViewById(R.id.btn_title_bar_right_finish);
		btnFinish.setVisibility(View.GONE);
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText(getResources().getString(R.string.title_bar_region));
		listView = (ListView)findViewById(R.id.listview);
		adapter = new ProvinceAdapter();
		listView.setAdapter(adapter);
	}

	private void addListener() {
		// TODO Auto-generated method stub
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GetAddressInfoActivity.this.finish();
			}
		});
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (!isCityChoose) {
					province = addressList.get(arg2);
					addressList = location.getCityList(addressList.get(arg2));
					if (addressList.size() == 1) {
						Intent intent = new Intent();
						intent.putExtra("province", province);
						if (!addressList.get(0).equals(province)) {
							intent.putExtra("city", addressList.get(0));
							intent.putExtra("code", location.getCode(addressList.get(0)));
						}else{
							intent.putExtra("code", location.getCode(province));
						}
						setResult(EditUserInfoActivity.GET_PROVINCE_RESOULT, intent);
						finish();
						return;
					}
					adapter.notifyDataSetChanged();
					isCityChoose = true;
				}else{
					city = addressList.get(arg2);
					Intent intent = new Intent();
					intent.putExtra("province", province);
					intent.putExtra("city", city);
					intent.putExtra("code", location.getCode(city));
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				
			}
		});
	}

	class ProvinceAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return addressList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = GetAddressInfoActivity.this.getLayoutInflater().inflate(R.layout.item_addressinfo, null);
				convertView = view;
				convertView.setTag(view);
			}else{
				view = (View)convertView.getTag();
			}
			TextView text = (TextView)view.findViewById(R.id.item_address_city);
			text.setText(addressList.get(position));
			return convertView;
		}
		
	}
	
	class MyLocationListener implements BDLocationListener {
		 
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
 
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            /*sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                List<Poi> list = location.getPoiList();// POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }
            Log.i("BaiduLocationApiDem", sb.toString());*/
//            LogUtil.i(TAG, location.getCity());
            currentLocation = location.getCity();
            if(currentLocation != null){
            	mLocationClient.stop();
            }
      	}
        
     }
}
