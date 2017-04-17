package com.summer.location;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.summer.adapter.SearchAdapter;

import java.util.ArrayList;

/**
 * Created by bestotem on 2017/4/17.
 */

public class InputTask implements PoiSearch.OnPoiSearchListener {
    private static final String TAG = "InputTask";

    private static InputTask mInstance;
    private SearchAdapter mAdapter;
    private PoiSearch mSearch;
    private Context mContext;

    private InputTask(Context context, SearchAdapter adapter) {
        this.mContext = context;
        this.mAdapter = adapter;
    }

    public static InputTask getInstance(Context context, SearchAdapter adapter) {
        if (mInstance == null) {
            synchronized (InputTask.class) {
                mInstance = new InputTask(context, adapter);
            }
        }
        return mInstance;
    }

    public void setmAdapter(SearchAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void onSearch(String key, String city) {
        PoiSearch.Query query = new PoiSearch.Query(key, "", city);
        mSearch = new PoiSearch(mContext, query);
        mSearch.setOnPoiSearchListener(this);
        mSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode==0&&poiResult!=null){
            ArrayList<AddressBean> data = new ArrayList<AddressBean>();
            ArrayList<PoiItem> items = poiResult.getPois();
            for (PoiItem item:items){
                LatLonPoint lonPoint = item.getLatLonPoint();
                double mapLong = lonPoint.getLongitude();
                double mapLat = lonPoint.getLatitude();

                String title = item.getTitle();
                String content = item.getSnippet();

                data.add(new AddressBean(mapLong,mapLat,title,content));

            }

            mAdapter.setData(data);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
