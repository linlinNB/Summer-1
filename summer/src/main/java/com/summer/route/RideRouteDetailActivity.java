package com.summer.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.RidePath;
import com.summer.R;
import com.summer.adapter.RideSegmentListAdapter;
import com.summer.utils.AMapUtil;

/**
 * Created by bestotem on 2017/5/3.
 */

public class RideRouteDetailActivity extends AppCompatActivity {

    public static final String TAG = "RideRouteDetailActivity";
    private Toolbar toolbar;
    private RidePath mRidePath;
    private TextView mTitleWalkRoute;
    private ListView mRideSegmentList;
    private RideSegmentListAdapter mRideSegmentListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        initBar();
        getIntentData();
        init();
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

    private void init() {
        mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
        String dur = AMapUtil.getFriendlyTime((int) mRidePath.getDuration());
        String dis = AMapUtil
                .getFriendlyLength((int) mRidePath.getDistance());
        mTitleWalkRoute.setText(dur + "(" + dis + ")");
        mRideSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mRideSegmentListAdapter = new RideSegmentListAdapter(
                this.getApplicationContext(), mRidePath.getSteps());
        mRideSegmentList.setAdapter(mRideSegmentListAdapter);
    }

    public void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mRidePath = intent.getParcelableExtra("ride_path");
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
