package com.summer.route;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.summer.R;
import com.summer.utils.AMapUtil;
import com.summer.utils.ShowToast;

/**
 * Created by bestotem on 2017/5/3.
 */

public class RideRoteActivity extends AppCompatActivity implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter,
        RouteSearch.OnRouteSearchListener {

    public static final String TAG = "RideRoteActivity";
    private EditText edtRouteStatr;
    private EditText edtRouteEnd;
    private RelativeLayout rltRouteSearch;
    private RelativeLayout rltRouteBottom;
    private TextView mRotueTimeDes;
    private TextView mRouteDetailDes;
    private LinearLayout ll_detail;

    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private RideRouteResult mRideRouteResult;

    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private final int ROUTE_TYPE_RIDE = 4;

    private ProgressDialog progDialog = null;// 搜索时进度条

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.map_route);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        init();
        setfromandtoMarker();
        searchRouteResult(ROUTE_TYPE_RIDE, RouteSearch.RidingDefault);
    }

    private void initView() {
        edtRouteStatr = (EditText) findViewById(R.id.edt_route_start);
        edtRouteEnd = (EditText) findViewById(R.id.edt_route_end);
        rltRouteSearch = (RelativeLayout) findViewById(R.id.rlt_route_search);
        rltRouteBottom = (RelativeLayout) findViewById(R.id.rlt_route_bottom);
        mRotueTimeDes = (TextView) findViewById(R.id.tv_firstLine);
        mRouteDetailDes = (TextView) findViewById(R.id.tv_secondLine);
        ll_detail = (LinearLayout) findViewById(R.id.ll_detail);

    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end)));
    }

    private void registerListener() {
        aMap.setOnMapClickListener(RideRoteActivity.this);
        aMap.setOnMarkerClickListener(RideRoteActivity.this);
        aMap.setOnInfoWindowClickListener(RideRoteActivity.this);
        aMap.setInfoWindowAdapter(RideRoteActivity.this);
    }


    private void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ShowToast.ColorToast(RideRoteActivity.this, "定位中，稍后再试...", 1200);
            return;
        }
        if (mEndPoint == null) {
            ShowToast.ColorToast(RideRoteActivity.this, "终点未设置", 1200);
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_RIDE) {// 骑行路径规划
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        }
    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mRideRouteResult = result;
                    final RidePath ridePath = mRideRouteResult.getPaths()
                            .get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            this,
                            aMap,
                            ridePath,
                            mRideRouteResult.getStartPos(),
                            mRideRouteResult.getTargetPos()
                    );
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                    rltRouteBottom.setVisibility(View.VISIBLE);
                    int dis = (int) ridePath.getDistance();
                    int dur = (int) ridePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    rltRouteBottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, RideRouteDetailActivity.class);
                            intent.putExtra("ride_path", ridePath);
                            intent.putExtra("ride_result", mRideRouteResult);
                            startActivity(intent);
                        }
                    });
                }
            }
        }

    }
}
