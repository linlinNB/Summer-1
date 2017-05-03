package com.summer.location;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.summer.R;
import com.summer.constant.SP_Constant;
import com.summer.publish.LinePointActivity;
import com.summer.publish.PointDetail;
import com.summer.publish.PubActivity;
import com.summer.roadline.AllLine;
import com.summer.roadline.ProLine;
import com.summer.route.RideRoteActivity;
import com.summer.utils.SharedPreferencesUtil;
import com.summer.utils.ShowToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestotem on 2017/3/20.
 */

public class LocMap extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener, View.OnClickListener,
        LocationSource, AMapLocationListener, AMap.OnMapClickListener,
        AMap.InfoWindowAdapter, AMap.OnMarkerClickListener,
        PoiSearch.OnPoiSearchListener {

    private static final String TAG = "LocMap";
    private boolean isState = true;
    private Toolbar toolbar;
    private String pointDate;
    SharedPreferencesUtil sp;

    // 定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器
    private LatLonPoint latLonPoint;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类

    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据

    private String keyWord = "";
    private String city = "呼和浩特市";

    private EditText mSearchText;
    private TextView searchButton;
    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;

    private PoiItem poiItemDisplayContent;

    // 地图控件
    private MapView mapView;
    private AMap aMap;

    private boolean locShow = true;
    private boolean isFirstLoc = true;

    private double mLong;
    private double mLat;

    private int[] markers = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbs);

        sp = new SharedPreferencesUtil(LocMap.this, SP_Constant.SP_NAME);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        initBar();
        initView();
        setUpMap();
        addListener();

    }

    private void initBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initView() {
        mSearchText = (EditText) findViewById(R.id.edt_search);
        searchButton = (TextView) findViewById(R.id.btn_search);
        mPoiDetail = (RelativeLayout) findViewById(R.id.rlt_poi_detail);
        mPoiName = (TextView) findViewById(R.id.tv_poi_name);
        mPoiAddress = (TextView) findViewById(R.id.tv_poi_address);

    }

    private void addListener() {
        toolbar.setOnMenuItemClickListener(this);
        searchButton.setOnClickListener(this);
        mPoiDetail.setOnClickListener(this);

    }

    /**
     * 设置定位样式
     */
    private void setUpMap() {
        aMap = mapView.getMap();

        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);

        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.iv_location));
        locationStyle.strokeWidth(200);
        locationStyle.strokeColor(Color.argb(0, 255, 255, 255));
        locationStyle.radiusFillColor(Color.argb(0, 255, 255, 255));


        aMap.setMyLocationStyle(locationStyle);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        initLoc();

    }

    /**
     * 设置定位监听 开始定位
     */
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


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {

                SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);
                pointDate = df.format(date);

                double mapLong = aMapLocation.getLongitude();
                double mapLat = aMapLocation.getLatitude();

                latLonPoint = new LatLonPoint(mapLong,mapLat);

                if (isFirstLoc) {
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(
                            new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));

                    mListener.onLocationChanged(aMapLocation);
                    isFirstLoc = false;

                }
                StringBuffer mapLocation = new StringBuffer();
                mapLocation.append(aMapLocation.getCity()
                        + " " + aMapLocation.getDistrict()
                        + " " + aMapLocation.getStreet()
                        + " " + aMapLocation.getStreetNum()
                );

                final String changeLoc = aMapLocation.getStreet();

                if (changeLoc != aMapLocation.getStreet() || locShow) {
                    ShowToast.ColorToast(LocMap.this, mapLocation.toString(), 2500);

                    String location = mapLocation.toString();
                    String maplong = mapLong + "";
                    String maplat = mapLat + "";

                    sp.saveString("MapLong", maplong);
                    sp.saveString("MapLat", maplat);
                    sp.saveString("MapLoc", location);

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
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();
                    // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new myPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.mipmap.iv_location)))
                                .position(new LatLng(latLonPoint.getLatitude(),
                                        latLonPoint.getLongitude())));

                        aMap.addCircle(new CircleOptions()
                                .center(new LatLng(latLonPoint.getLatitude(),
                                        latLonPoint.getLongitude())).radius(500)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 60, 60, 60))
                                .strokeWidth(2));
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {

                        ShowToast.ColorToast(LocMap.this, "对不起，没有搜索到相关数据！", 1500);
                    }
                }
            } else {
                ShowToast.ColorToast(LocMap.this, "对不起，没有搜索到相关数据！", 1500);
            }
        } else {
            ShowToast.ColorToast(LocMap.this, rcode + "", 1500);
        }
    }

    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName()
                    + "城市区号:" + cities.get(i).getCityCode()
                    + "城市编码:" + cities.get(i).getAdCode()
                    + "\n";
        }
        ShowToast.ColorToast(LocMap.this, infomation, 1200);
    }

    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;
    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);

        }
    }

    public void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                keyWord = mSearchText.getText().toString().trim();
                if ("".equals(keyWord)) {
                    ShowToast.ColorToast(LocMap.this, "请输入要搜索的关键字", 1500);
                    return;
                } else {
                    doSearchQuery();
                }
                break;

            default:
                break;
        }
    }

    private void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", city);
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (latLonPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 1500, true));//
            // 设置搜索区域为以lp点为圆心，其周围1500米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_aLine:
//                Intent intentALine = new Intent(LocMap.this, AllLine.class);
                Intent intentALine = new Intent(LocMap.this, PointDetail.class);
                startActivity(intentALine);
                break;

            case R.id.action_MyLine:
                Intent intentMyLine = new Intent(LocMap.this, ProLine.class);
//                Intent intentMyLine = new Intent(LocMap.this, LinePointActivity.class);
                startActivity(intentMyLine);
                break;

            case R.id.action_Point:
                addLinePoint();
                break;

            case R.id.action_SetRoute:
                Intent intentRoute = new Intent(LocMap.this, RideRoteActivity.class);
                startActivity(intentRoute);
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

    private void addLinePoint() {
        Intent intent = new Intent(LocMap.this, PubActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("LocDate", pointDate);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
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
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.poi_marker_pressed)));
                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }
        return true;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public myPoiOverlay(AMap amap, List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         *
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         *
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         *
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            } else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.marker_other_highlight));
                return icon;
            }
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        whetherToShowDetailInfo(false);
    }

    @Override
    public void deactivate() {
        mListener = null;
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

}