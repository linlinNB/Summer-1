package com.summer.publish;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.squareup.picasso.Picasso;
import com.summer.R;


/**
 * Created by bestotem on 2017/4/11.
 */

public class PointDetail extends AppCompatActivity
        implements LocationSource, AMapLocationListener {
    private static final String TAG = "PointDetail";

    private Toolbar toolbar;
    private MapView mapPoint;
    private AMap aPoint;

    private String point_Lat;
    private String point_Long;

    private TextView tv_point_loc;
    private TextView tv_point_title;
    private TextView tv_point_content;
    private ImageView iv_point_img;

    private boolean isFirstLoc = true;
    private boolean isUpdate = true;

    // 定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detail);

        mapPoint = (MapView) findViewById(R.id.map_point);
        mapPoint.onCreate(savedInstanceState);

        initView();
        initBar();
        initEvent();
        initMap();

    }


    private void initView() {
        tv_point_loc = (TextView) findViewById(R.id.tv_point_loc);
        tv_point_title = (TextView) findViewById(R.id.tv_point_title);
        tv_point_content = (TextView) findViewById(R.id.tv_point_content);
        iv_point_img = (ImageView) findViewById(R.id.iv_point_image);
    }

    private void initBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initEvent() {
        String sumObjId = getIntent().getStringExtra("summerObjectId");
        Log.e(TAG, "== Show get Id is == " + sumObjId);
        AVObject avObject = AVObject.createWithoutData("Summer_Pub", sumObjId);
        avObject.fetchInBackground("owner", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {

                    point_Lat = avObject.getString("pub_lat");
                    point_Long = avObject.getString("pub_long");

                    tv_point_title.setText(avObject.getString("pub_title"));
                    tv_point_content.setText(avObject.getString("pub_content"));
                    tv_point_loc.setText(avObject.getString("pub_location"));

                    Picasso.with(PointDetail.this)
                            .load(avObject.getAVFile("image") == null
                                    ? "www"
                                    : avObject.getAVFile("image").getUrl())
                            .into(iv_point_img);
                } else {
                    Log.e(TAG, "== get avObject fail ==" + e);
                }
            }
        });

    }


    private void initMap() {
        aPoint = mapPoint.getMap();

        UiSettings uiSettings = aPoint.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.iv_location));
        locationStyle.strokeWidth(200);
        locationStyle.strokeColor(Color.argb(0, 255, 255, 255));
        locationStyle.radiusFillColor(Color.argb(0, 255, 255, 255));


        aPoint.setMyLocationStyle(locationStyle);
        aPoint.setLocationSource(this);
        aPoint.setMyLocationEnabled(true);

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
        mLocationOption.setInterval(1000);

        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {


                double mapLat, mapLong;
                mapLat = Double.parseDouble(point_Lat);
                mapLong = Double.parseDouble(point_Long);
                LatLng latLng = new LatLng(mapLat, mapLong);

                aPoint.moveCamera(CameraUpdateFactory.zoomTo(18));
                aPoint.moveCamera(CameraUpdateFactory.changeLatLng(
                        new LatLng(mapLat, mapLong)));

                mListener.onLocationChanged(aMapLocation);

                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                marker.draggable(false);
                marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_point)));

                aPoint.addMarker(marker);


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

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
