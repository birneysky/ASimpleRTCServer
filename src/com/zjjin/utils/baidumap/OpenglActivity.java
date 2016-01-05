package com.zjjin.utils.baidumap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.slidingmenu.R;
import com.example.slidingmenu.SafityAreaActivity;
import com.zijin.dao.IbeaconAreaDao;
import com.zijin.ibeacon.util.Utils;
import com.zjjin.entity.TrackerSafityArea;
import com.zjjin.utils.Consts;
import com.zjjin.utils.ScreenUtils;

/**
 * 此demo用来展示如何在地图绘制的每帧中再额外绘制一些用户自己的内容
 */
public class OpenglActivity extends Activity implements OnMapDrawFrameCallback, OnGetGeoCoderResultListener {

	private static final String LTAG = OpenglActivity.class.getSimpleName();

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	//标题栏
	private RelativeLayout  menuBackConstruct;
	private TextView tvTitle, tvBackNotice;
	private Button btnAdd;
	private static List<TrackerSafityArea> listSa;
	// 地图相关
	MapView mMapView;
	BaiduMap mBaiduMap;
	private Bitmap bitmap;
	private LatLng latlng1 = new LatLng(39.97923, 116.357428);
//	LatLng latlng2 = new LatLng(39.94923, 116.397428);
//	LatLng latlng3 = new LatLng(39.96923, 116.437428);
	LatLng latlng2 = new LatLng(39.914884, 116.403883);//39.914884,116.403883
	LatLng latlng3 = new LatLng(39.914884, 116.403883);
	private static boolean isAdd = true;//添加安全区域
	private double[] southWeastData = new double[2], northEastData = new double[2];
	private LatLngBounds bounds;
	private String address;
	private int width, height;
	private GeoCoder mSearch;
	
	private List<LatLng> latLngPolygon;
	{
		latLngPolygon = new ArrayList<LatLng>();
		latLngPolygon.add(latlng1);
		latLngPolygon.add(latlng2);
		latLngPolygon.add(latlng3);
	}

	private float[] vertexs;
	private FloatBuffer vertexBuffer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_opengl);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_sleep_mode);
		
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		initLocation();
		setupView();
		addListener();
		
		width = ScreenUtils.getScreenWidth(getApplicationContext());
		height = ScreenUtils.getScreenHeight(getApplicationContext());
		
		//设置地图触摸监听
		mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent arg0) {
				if(!isAdd)return;
				if(MotionEvent.ACTION_UP == arg0.getAction()){
					//获取当前地图屏幕中心点地理编码
//					MapStatus status = mBaiduMap.getMapStatus();
//					LatLng center = status.target;
//					Log.i("info", "center=====" +"latitude: "+center.latitude+", longitude: "+center.longitude);
					//构造当前屏幕区域
					latlng2 = mBaiduMap.getProjection().fromScreenLocation(new Point(0, height));
					latlng3 = mBaiduMap.getProjection().fromScreenLocation(new Point(width, 0));
					southWeastData[0] = latlng2.latitude;
					southWeastData[1] = latlng2.longitude;
					northEastData[0] = latlng3.latitude;
					northEastData[1] = latlng3.longitude;
					bounds = new LatLngBounds.Builder().include(latlng2).include(latlng3).build();
					// 反Geo搜索
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(bounds.getCenter()));
					// 设置地图不能旋转，即0度
					MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).rotate(0).build();
					MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
					mBaiduMap.animateMapStatus(u);
				}
			}
		});
		//设置百度地图在每一帧绘制时的回调接口，该接口在绘制线程中调用
		mBaiduMap.setOnMapDrawFrameCallback(this);
		bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ground_overlay_2);
		getData();
	}
	
	private void setupView() {
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		tvTitle.setText("");
		tvBackNotice = (TextView)findViewById(R.id.tv_back_notice);
		tvBackNotice.setText("安全区域");
		btnAdd = (Button)findViewById(R.id.btn_title_bar_right_finish);
		if(isAdd){
			btnAdd.setText("添加安全区域");
		}else{
			btnAdd.setText("");
		}
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isAdd){
					latlng2 = mBaiduMap.getProjection().fromScreenLocation(new Point(0, height));
					latlng3 = mBaiduMap.getProjection().fromScreenLocation(new Point(width, 0));
					southWeastData[0] = latlng2.latitude;
					southWeastData[1] = latlng2.longitude;
					northEastData[0] = latlng3.latitude;
					northEastData[1] = latlng3.longitude;
					bounds = new LatLngBounds.Builder().include(latlng2).include(latlng3).build();
					
					if(address != null){
						Intent intent = new Intent();
						intent.putExtra(Consts.EXTRA_KEY_SOUTH_WEAST, southWeastData);
						intent.putExtra(Consts.EXTRA_KEY_NORTH_EAST, northEastData);
						intent.putExtra("address", address);
						setResult(RESULT_OK, intent);
						finish();
					}
				}else{
					finish();
				}
			}
		});
	}

	private void initLocation() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		if(isAdd){
			// 放大到16倍
			MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(16);
			mBaiduMap.animateMapStatus(status);
		}else{
			// 放大到14倍
			MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(14);
			mBaiduMap.animateMapStatus(status);
		}
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1200);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void getData() {
		Intent intent = getIntent();
		double[] southWest = intent.getDoubleArrayExtra(Consts.EXTRA_KEY_SOUTH_WEAST);
		double[] northEast = intent.getDoubleArrayExtra(Consts.EXTRA_KEY_NORTH_EAST);
		int iBeaconID = intent.getIntExtra(Consts.IBEACON_ID, 0);
		if(iBeaconID != 0){ // 添加
			IbeaconAreaDao mIbeaconAreaDao = IbeaconAreaDao.getInstance(this);
			listSa = mIbeaconAreaDao.getAreasByBeaconID(iBeaconID);
		}
		if(southWest==null || northEast == null){ // 添加
			// 初始化搜索模块，注册事件监听
			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);
			isAdd = true;//添加安全区域
			//构造当前区域
			LatLngBounds b = new LatLngBounds.Builder().include(latlng2).include(latlng3).build();
			// 反Geo搜索
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(b.getCenter()));
			southWeastData[0] = latlng2.latitude;
			southWeastData[1] = latlng2.longitude;
			northEastData[0] = latlng3.latitude;
			northEastData[1] = latlng3.longitude;
		}else{ // 查看
			//构造两个点
			latlng2 = new LatLng(southWest[0], southWest[1]);
			latlng3 = new LatLng(northEast[0], northEast[1]);
		}
	}

	/**
	 * 查看当前安全区域
	 * @param context
	 * @param southWeast
	 * @param northEast
	 */
	public static void actionStart(Context context, double[] southWeast, double[] northEast){
		isAdd = false;
		Intent intent = new Intent(context, OpenglActivity.class);
		intent.putExtra(Consts.EXTRA_KEY_SOUTH_WEAST, southWeast);
		intent.putExtra(Consts.EXTRA_KEY_NORTH_EAST, northEast);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		// onResume 纹理失效
		textureId = -1;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		if(mSearch!=null){
			mSearch.destroy();
		}
		super.onDestroy();
	}

	/** 
     * onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) 
     *  地图每一帧绘制结束后回调接口，在此你可以绘制自己的内容 
     *  参数: 
     *  gl - 地图 opengl引用 
     *  drawingMapStatus - 地图当前正在绘制时的地图状态 
     * */ 
	public void onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) {
		/** 
         * public final Projection getProjection() 
         * 获取地图投影坐标转换器, 当地图初始化完成之前返回 null， 
         * 在 OnMapLoadedCallback.onMapLoaded() 之后才能正常 
         * 返回:地图投影坐标转换器 
         * */ 
		if (mBaiduMap.getProjection() != null) {
//			calPolylinePoint(drawingMapStatus);
//			drawPolyline(gl, Color.argb(255, 255, 0, 0), vertexBuffer, 10, 3, drawingMapStatus);
			
			if(isAdd){ // 将之前添加的安全区域全部显示
				if(listSa == null || listSa.size() == 0){
					return;
				}
				for(TrackerSafityArea sa : listSa){
					String southWest2 = sa.getSouthWest();
					String[] southWest2S = southWest2.split(",");
					String northEast2 = sa.getNorthEast();
					String[] northEast2S = northEast2.split(",");
					double[] southWest2D = new double[]{Double.parseDouble(southWest2S[0]), Double.parseDouble(southWest2S[1])};
					double[] northEast2D = new double[]{Double.parseDouble(northEast2S[0]), Double.parseDouble(northEast2S[1])};
					//构造两个点
					latlng2 = new LatLng(southWest2D[0], southWest2D[1]);
					latlng3 = new LatLng(northEast2D[0], northEast2D[1]);
					drawTexture(gl, bitmap, drawingMapStatus);
				}
			}else{ //只显示当前安全区域
				drawTexture(gl, bitmap, drawingMapStatus);
			}
		}
	}

	
	private void calPolylinePoint(MapStatus mspStatus) {
		PointF[] polyPoints = new PointF[latLngPolygon.size()];
		vertexs = new float[3 * latLngPolygon.size()];
		int i = 0;
		for (LatLng xy : latLngPolygon) {
			/** 
             * public PointF toOpenGLLocation(LatLng location,MapStatus mapStatus) 
             * 将地理坐标转换成openGL坐标，在 OnMapDrawFrameCallback 的 onMapDrawFrame 函数中使用。 
             * @param location - 地理坐标 如果传入 null 则返回null 
             *        mapStatus - 地图每一帧绘制时的地图状态  
             * @return openGL坐标 
             * */
			polyPoints[i] = mBaiduMap.getProjection().toOpenGLLocation(xy,
					mspStatus);
			vertexs[i * 3] = polyPoints[i].x;
			vertexs[i * 3 + 1] = polyPoints[i].y;
			vertexs[i * 3 + 2] = 0.0f;
			i++;
		}
		for (int j = 0; j < vertexs.length; j++) {
			Log.d(LTAG, "vertexs[" + j + "]: " + vertexs[j]);
		}
		vertexBuffer = makeFloatBuffer(vertexs);
	}

	private FloatBuffer makeFloatBuffer(float[] fs) {
		ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(fs);
		fb.position(0);
		return fb;
	}

	private void drawPolyline(GL10 gl, int color, FloatBuffer lineVertexBuffer,
			float lineWidth, int pointSize, MapStatus drawingMapStatus) {

		gl.glEnable(GL10.GL_BLEND);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		float colorA = Color.alpha(color) / 255f;
		float colorR = Color.red(color) / 255f;
		float colorG = Color.green(color) / 255f;
		float colorB = Color.blue(color) / 255f;

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
		gl.glColor4f(colorR, colorG, colorB, colorA);
		gl.glLineWidth(lineWidth);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointSize);

		gl.glDisable(GL10.GL_BLEND);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	int textureId = -1;
	/**
	 * 使用opengl坐标绘制
	 * 
	 * @param gl
	 * @param bitmap
	 * @param drawingMapStatus
	 */
	private void drawTexture(GL10 gl, Bitmap bitmap, MapStatus drawingMapStatus) {
		PointF p1 = mBaiduMap.getProjection().toOpenGLLocation(latlng2,
					drawingMapStatus);
		PointF p2 = mBaiduMap.getProjection().toOpenGLLocation(latlng3,
					drawingMapStatus);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 3 * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer vertices = byteBuffer.asFloatBuffer();
		vertices.put(new float[] { p1.x, p1.y, 0.0f, p2.x, p1.y, 0.0f, p1.x,
				p2.y, 0.0f, p2.x, p2.y, 0.0f });

		ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(6 * 2);
		indicesBuffer.order(ByteOrder.nativeOrder());
		ShortBuffer indices = indicesBuffer.asShortBuffer();
		indices.put(new short[] { 0, 1, 2, 1, 2, 3 });

		ByteBuffer textureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4);
		textureBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer texture = textureBuffer.asFloatBuffer();
		texture.put(new float[] { 0, 1f, 1f, 1f, 0f, 0f, 1f, 0f });

		indices.position(0);
		vertices.position(0);
		texture.position(0);

		// 生成纹理
		if (textureId == -1) {
			int textureIds[] = new int[1];
			gl.glGenTextures(1, textureIds, 0);
			textureId = textureIds[0];
			Log.d(LTAG, "textureId: " + textureId);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_NEAREST);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		}
	
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		// 绑定纹理ID
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture);

		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 6, GL10.GL_UNSIGNED_SHORT,
				indices);

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_BLEND);
	}

	/**
	 * Geo搜索回调
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			Toast.makeText(GeometryActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
			return;
		}
//		mBaiduMap.clear();
//		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//				.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
//		String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
//		Toast.makeText(GeometryActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}

	/**
	 * 反Geo搜索回调
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			Toast.makeText(GeometryActivity.this, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
			return;
		}
//		mBaiduMap.clear();
//		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.icon_marka)));
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
//		Toast.makeText(GeometryActivity.this, result.getAddress(), Toast.LENGTH_SHORT).show();
		address = result.getAddress();
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
			address = location.getAddrStr();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
}
