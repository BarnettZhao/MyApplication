package com.example.administrator.myapplication.set.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.administrator.myapplication.base.BaseActivity;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.dao.AddressInfo;
import com.example.administrator.myapplication.set.adapter.MapListAdapter;

import java.util.ArrayList;

/**
 * Created by Liu_Zhichao on 2016/2/19 14:24.
 * 地图定位
 */
public class LocationMapActivity extends BaseActivity implements View.OnClickListener, BDLocationListener, OnGetGeoCoderResultListener {

	private MapView locationMapView;
	private ImageView locationMapPositionBtn,back;
	private TextView locationMapCurrent,rightText, leftText;
	private ListView locationMapListview;

	private LocationClient mLocationClient;
	private BaiduMap baiduMap;
	private PoiSearch poiSearch;//搜索
	private LatLng target;//地图中心点的经纬度
	private GeoCoder mSearch;// 搜索模块，也可去掉地图模块独立使用
	private Marker locationMarker;//当前位置标记
	private Marker positionMarker;//选择位置标记
	private String address;//上一个界面传过来的地址
	private MapListAdapter adapter;
	private AddressInfo addressInfo;
	private static boolean isSelected;//是否在地址列表选择的，选择的就不再做搜索
	private LatLng result;//最终要返回的结果坐标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_map_act);
		initView();
		initListView();
		initMap();
		initLocation();
		mLocationClient.start();//开始定位当前位置
	}

	private void initView() {
		locationMapView = (MapView) findViewById(R.id.location_map_view);
		locationMapPositionBtn = (ImageView) findViewById(R.id.location_map_position_btn);
		back = (ImageView) findViewById(R.id.left_button);
		locationMapCurrent = (TextView) findViewById(R.id.location_map_current);
		locationMapListview = (ListView) findViewById(R.id.location_map_listview);
		rightText = (TextView) findViewById(R.id.right_text);
		leftText = (TextView) findViewById(R.id.left_text);
		leftText.setText("选择位置");
		rightText.setText("确定");
		rightText.setVisibility(View.VISIBLE);
		address = getIntent().getStringExtra("address");
		locationMapPositionBtn.setOnClickListener(this);
		rightText.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	private void initMap() {
		mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
		mLocationClient.registerLocationListener(this);    //注册监听函数
		//必须按照3 2 1 的顺序，否则会报错
		locationMapView.removeViewAt(3);//比例尺
		locationMapView.removeViewAt(2);//放大缩小按钮
		locationMapView.removeViewAt(1);//百度地图图标
		baiduMap = locationMapView.getMap();
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		baiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {

			float x, y;

			@Override
			public void onTouch(MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
					case MotionEvent.ACTION_DOWN:
						x = motionEvent.getX();
						y = motionEvent.getY();
						break;
					case MotionEvent.ACTION_UP:
						float ex = motionEvent.getX();
						float ey = motionEvent.getY();
						if (x != ex || y != ey) {
							locationMapPositionBtn.setImageResource(R.drawable.location_position_icon);
						}
						break;
				}
			}
		});
		baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChangeStart(MapStatus mapStatus) {
			}

			@Override
			public void onMapStatusChange(MapStatus mapStatus) {
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus mapStatus) {
				target = mapStatus.target;//中心点的经纬度
				result = target;//除了从列表选择，其他的都会走这一步
				if (!isSelected) {
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(target));//地理反编码获得该点的地址
				}
				isSelected = false;
			}
		});
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	private void initListView() {
		adapter = new MapListAdapter(this, new ArrayList<AddressInfo>());
		locationMapListview.setAdapter(adapter);
		locationMapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (addressInfo != null) {
					addressInfo.setSelected(false);
				}
				addressInfo = adapter.getItem(position);
				addressInfo.setSelected(true);
				adapter.notifyDataSetChanged();
				result = addressInfo.getLatLng();
				locationMapCurrent.setText(addressInfo.getAddress());
				isSelected = true;
				baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(addressInfo.getLatLng()));
				if (positionMarker != null) {
					positionMarker.remove();
				}
				positionMarker = (Marker) baiduMap.addOverlay(new MarkerOptions().position(addressInfo.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.position_selected_icon)));
				locationMapPositionBtn.setImageResource(R.drawable.location_position_icon);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.right_text:
				String resultAddress = locationMapCurrent.getText().toString().trim();
				if (TextUtils.isEmpty(resultAddress) || result == null) {
//					ToastUtil.shortShow(this, getString(R.string.locationmap_select_address));
					Toast.makeText(getApplicationContext(),"请选择地址",Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("address", resultAddress);
				intent.putExtra("longitude", result.longitude);
				intent.putExtra("latitude", result.latitude);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.location_map_position_btn:
				mLocationClient.requestLocation();
				break;
			case R.id.left_button:
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		LatLng point = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
		String city = bdLocation.getCity();
		String localAddress = bdLocation.getAddrStr();
		//设置地图显示的精度中心点等
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(17).build();
		//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//改变地图状态
		baiduMap.animateMapStatus(mMapStatusUpdate);
		if (locationMarker != null) {
			locationMarker.remove();
		}
		locationMarker = (Marker) baiduMap.addOverlay(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_icon)));
		locationMapPositionBtn.setImageResource(R.drawable.location_selected_icon);
//		List<Poi> list = bdLocation.getPoiList();// POI数据，暂时没有用到
		locationMapCurrent.setText(localAddress);
		if (TextUtils.isEmpty(address)) {
			//searchAllCity(bdLocation.getAddrStr(), city);
			searchAllNear(bdLocation.getAddrStr(), point);
		} else {
			//定位到搜索位置
			mSearch.geocode(new GeoCodeOption().city(city).address(address));
			//searchAllCity(address, city);
			searchAllNear(address, point);
		}
		address = "";
	}


	/**
	 * 附近搜索，用法类似城市内搜索
	 *
	 * @param address 兴趣点即具体位置，也可传入餐厅等信息
	 * @param latLng  经纬度
	 */
	public void searchAllNear(String address, LatLng latLng) {
		poiSearch = PoiSearch.newInstance();
		PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
		nearbySearchOption.location(latLng).keyword(address).radius(1000);
		poiSearch.searchNearby(nearbySearchOption);
		poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
			@Override
			public void onGetPoiResult(PoiResult poiResult) {
				if (LocationMapActivity.this == null || LocationMapActivity.this.isFinishing()) {
					return;
				}
				adapter.clear();
				if (poiResult != null && poiResult.getAllPoi() != null) {
					if (poiResult.getTotalPoiNum() > 0) {
						for (PoiInfo poiInfo : poiResult.getAllPoi()) {
							adapter.add(new AddressInfo(poiInfo.name, poiInfo.address, poiInfo.location, false));
						}
					} else {
//						ToastUtil.shortShow(LocationMapActivity.this, getString(R.string.locationmap_no_info));
						Toast.makeText(getApplicationContext(), "没有位置信息" ,Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
			}
		});
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
		if (this == null || this.isFinishing()) {
			return;
		}
		if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "没有位置信息", Toast.LENGTH_LONG).show();
			return;
		}
		locationMapCurrent.setText(geoCodeResult.getAddress());
		//找到位置后添加个图标，但是要把之前地图上的该图标清除掉
		if (positionMarker != null) {
			positionMarker.remove();
		}
		positionMarker = (Marker) baiduMap.addOverlay(new MarkerOptions().position(geoCodeResult.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.position_selected_icon)));
		baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(geoCodeResult.getLocation()));//设置地图中心为该点
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
		if (this == null || this.isFinishing()) {
			return;
		}
		if (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "没有位置信息", Toast.LENGTH_LONG).show();
			return;
		}
		locationMapCurrent.setText(reverseGeoCodeResult.getAddress());
		//加上覆盖物
		baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation()));
		//定位
		//搜索该点附近的所有地方
		searchAllNear(reverseGeoCodeResult.getAddress(), target);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		locationMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		locationMapView.onPause();
	}

	@Override
	public void onDestroy() {
		mLocationClient.stop();
		// 关闭定位图层
		baiduMap.setMyLocationEnabled(false);
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		locationMapView.onDestroy();
		locationMapView = null;
		super.onDestroy();
	}
}
