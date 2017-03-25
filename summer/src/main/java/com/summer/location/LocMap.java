package com.summer.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.maps2d.MapView;
import com.summer.R;
import com.summer.publish.PubActivity;
import com.summer.roadline.AllLine;
import com.summer.roadline.ProLine;
import com.summer.utils.ShowToast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bestotem on 2017/3/20.
 */

public class LocMap extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private static final String TAG = "LocMap";
    private boolean isState = true;
    private Toolbar toolbar;
    private FloatingActionButton flabBtn;

    private MapView mapView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbs);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        initBar();
        initView();
        addListener();


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
        switch (item.getItemId()){
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
                ShowToast.ColorToast(LocMap.this,"Login Out",1200);
                break;

            default:
                break;
        }


        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            ShowToast.ColorToast(LocMap.this, "Back Map", 1200);
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
}