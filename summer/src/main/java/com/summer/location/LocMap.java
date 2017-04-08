package com.summer.location;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.summer.R;
import com.summer.publish.PubActivity;
import com.summer.roadline.AllLine;
import com.summer.roadline.ProLine;
import com.summer.utils.ShowToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestotem on 2017/3/20.
 */

public class LocMap extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener, View.OnClickListener,
        LocationSource, AMapLocationListener {

    private static final String TAG = "LocMap";
    private boolean isState = true;
    private Toolbar toolbar;
    private FloatingActionButton flabBtn;

    // 地图控件
    private MapView mapView;
    private AMap aMap;

    private boolean locShow = true;


    // 定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbs);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        initBar();
        initView();
        initMap();
        addListener();

    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
    }

    private void setUpMap() {
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.strokeWidth(200);
        locationStyle.strokeColor(Color.argb(0,255,255,255));
        locationStyle.radiusFillColor(Color.argb(0,255,255,255));


        aMap.setMyLocationStyle(locationStyle);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        initLoc();

    }

    private void initLoc() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(15000);

        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();

    }

    private void initBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        flabBtn = (FloatingActionButton) findViewById(R.id.float_btn);

    }

    private void addListener() {
        toolbar.setOnMenuItemClickListener(this);
        flabBtn.setOnClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_aLine:
                Intent intentALine = new Intent(LocMap.this, AllLine.class);
                startActivity(intentALine);
                break;

            case R.id.action_MyLine:
                Intent intentMyLine = new Intent(LocMap.this, ProLine.class);
                startActivity(intentMyLine);
                break;

            case R.id.action_out:
                /**
                 * TODO
                 * 退出登录
                 */
                ShowToast.ColorToast(LocMap.this, "Login Out", 1200);
                break;

            default:
                break;
        }


        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.float_btn:
                Intent intent = new Intent(LocMap.this, PubActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (isState) {
            isState = false;
            ShowToast.ColorToast(LocMap.this, "退出骑行", 1200);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isState = true;
                }
            }, 1200);
        } else {
            this.finish();
        }

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLocationType();
                aMapLocation.getLatitude();
                aMapLocation.getLongitude();
                aMapLocation.getAccuracy();

                Log.e(TAG, "-- Latitude【纬度】 --" + aMapLocation.getLatitude());
                Log.e(TAG, "-- Longitude【经度】 --" + aMapLocation.getLongitude());
                Log.e(TAG, "-- Accuracy【精度】 --" + aMapLocation.getAccuracy());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);

                aMapLocation.getAddress();
                aMapLocation.getCountry();
                aMapLocation.getProvince();
                aMapLocation.getCity();
                aMapLocation.getDistrict();
                aMapLocation.getStreet();
                aMapLocation.getStreetNum();
                aMapLocation.getCityCode();
                aMapLocation.getAdCode();

                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));

                aMap.moveCamera(CameraUpdateFactory.changeLatLng(
                        new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                mListener.onLocationChanged(aMapLocation);

                StringBuffer buffer = new StringBuffer();
                buffer.append(aMapLocation.getCity()
                        + "" + aMapLocation.getDistrict()
                        + "" + aMapLocation.getStreet()
                        + "");

                final String changeLoc = aMapLocation.getStreet();

                if (changeLoc != aMapLocation.getStreet() || locShow) {
                    ShowToast.ColorToast(LocMap.this, buffer.toString(), 2500);
                    locShow = false;
                }


            } else {
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }
}