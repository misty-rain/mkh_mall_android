package com.mkh.mobilemall.ui.activity.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.poi.BaiduMapPoiSearch;
import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.dao.LocationDao;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.activity.store.StoreList;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.domain.master.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiniu_wutao on 15/7/9.
 * 地图定位
 */
public class Location extends BaseActivity implements OnGetGeoCoderResultListener{

    @Bind(R.id.mapView)
    MapView mapView;
    GeoCoder mSerach;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    boolean isFirstLoc=true;
    LatLng ll=null;
    MkhTitleBar  titleBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        titleBar= (MkhTitleBar) findViewById(R.id.location_actionbar);
        titleBar.setLeftClickFinish(this);
        titleBar.setRightTextButton1("切换到门店列表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Location.this,StoreList.class);
                startActivity(intent);

            }
        });

        mBaiduMap = mapView.getMap();
// 开启定位图层
        //mBaiduMap.setMyLocationEnabled(true);

        // 实例化搜索模块
        mSerach=GeoCoder.newInstance();
        mSerach.setOnGetGeoCodeResultListener(this);


// 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListenner());
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();


    }

    /**
     *  获得附近门店
     */
    private class GetNearStoreTask extends
            MyAsyncTask<LatLng, Void, List<Store>> {


        @Override
        protected List<Store> doInBackground(LatLng... params) {

            LatLng latLng=params[0];
            return  LocationDao.getNearStore(latLng.latitude,latLng.longitude);
        }

        @Override
        protected void onPostExecute(List<Store> list) {
            if(list!=null) {
                if (list.size() > 0) {
                    onGetSearchResult(list);
                }
            }else{
                Toast.makeText(Location.this,getString(R.string.interface_timeout),Toast.LENGTH_SHORT).show();
            }




        }

    }
    public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }
    public void onGetSearchResult(List<Store> list) {
        LatLng ll;
        BitmapDescriptor bd;
        for (int i=0;i<list.size();i++) {
            if ("null".equals(list.get(i).getLatitude()) || "null".equals(list.get(i).getLongitude()) || TextUtils.isEmpty(list.get(i).getLatitude().toString()) || TextUtils.isEmpty(list.get(i).getLongitude().toString())) {
                continue;
            }
            TextView textView = new TextView(Location.this);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.ic_map_point);
            textView.setTextColor(getResources()
                    .getColor(android.R.color.white));
            textView.setTag(list.get(i).getId());
            ll = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
            bd = BitmapDescriptorFactory
                   .fromBitmap(getBitmapFromView(textView));
            Bundle bundle = new Bundle();
            bundle.putString("storeName",list.get(i).getName());
            bundle.putLong("StoreId", list.get(i).getId());
            bundle.putString("ContactPhone", list.get(i).getContactPhone());
           OverlayOptions oo = new MarkerOptions().icon(bd).position(ll).extraInfo(bundle).title(list.get(i).getAddress() + "," + list.get(i).getName() + "," + list.get(i).getContactPhone());

           mBaiduMap.addOverlay(oo);
            bd.recycle();

        }
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();//影藏气泡
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                mBaiduMap.hideInfoWindow();//影藏气泡
                return false;
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                init(marker);
                return true;

            }
        });

    }

    public  void init(final Marker marker){
        BitmapDescriptor bd;
        LatLng pt = null;
        double latitude, longitude;
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        View view = LayoutInflater.from(this).inflate(R.layout.map_item, null); //自定义气泡形状
        TextView storeName = (TextView) view.findViewById(R.id.my_postion);
        TextView storeAdd=(TextView)view.findViewById(R.id.address);
        TextView Contactphone=(TextView)view.findViewById(R.id.phone);
        pt = new LatLng(latitude + 0.0011, longitude + 0.00005);
        String line=marker.getTitle();
        String[] item= line.split(",");
        if(item.length==3) {
            storeName.setText(item[1]);
            storeAdd.setText(item[0]);
            Contactphone.setText(item[2]);
        }

        if(item.length==2){
            storeName.setText(item[1]);
            storeAdd.setText(item[0]);
        }

        if(item.length==1){
            storeAdd.setText(item[0]);
        }

        bd = BitmapDescriptorFactory
                  .fromBitmap(getBitmapFromView(view));
        // 定义用于显示该InfoWindow的坐标点
        // 创建InfoWindow的点击事件监听者
        InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                Intent intent = new Intent(Location.this,
                          MainActivity.class);
                Bundle bundle=marker.getExtraInfo();
                intent.putExtra("storeName", bundle.getString("storeName"));
                GlobalContext.getInstance().getSpUtil().saveStoreId(bundle.getLong("StoreId"));
                GlobalContext.getInstance().getSpUtil().saveStoreName(bundle.getString("storeName"));
                GlobalContext.getInstance().getSpUtil().saveStorePhone(bundle.getString("ContactPhone"));
                startActivity(intent);
                finish();

            }
        };
        // 创建InfoWindow
        InfoWindow mInfoWindow = new InfoWindow(bd, pt, 1,listener);
        mBaiduMap.showInfoWindow(mInfoWindow); //显示气泡
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();

            return;
        }
        mBaiduMap.clear();

        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
        Toast.makeText(this, result.getAddress(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Toast.makeText(this, result.getAddress(),
                Toast.LENGTH_LONG).show();

    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                      .accuracy(location.getRadius())
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                      .direction(100).latitude(location.getLatitude())
                      .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            String cityName=location.getCity();
            cityName=cityName.substring(0,cityName.indexOf("市"));
            GlobalContext.getInstance().getSpUtil().saveCityName(cityName);
            if (isFirstLoc) {
                isFirstLoc = false;
                ll = new LatLng(location.getLatitude(),
                          location.getLongitude());

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);


                mBaiduMap.animateMapStatus(u);
                //mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(20).build()));
                MapStatusUpdate uZoom = MapStatusUpdateFactory.newLatLngZoom(ll,15);
               // mBaiduMap.setMapStatus(uZoom);
                mBaiduMap.animateMapStatus(uZoom);


                new GetNearStoreTask().execute(ll);

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }

    }
}
