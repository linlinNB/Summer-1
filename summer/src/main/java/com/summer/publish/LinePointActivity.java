package com.summer.publish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.summer.R;
import com.summer.adapter.LinePointAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bestotem on 2017/4/9.
 */

public class LinePointActivity extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TAG = "LinePointActivity";
    private Toolbar toolbar;

    private RecyclerView mRecycView;
    private LinePointAdapter adapter;
    private List<AVObject> mList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_point);
        initView();
        initBar();
        initEvent();

    }

    private void initView() {
        mRecycView = (RecyclerView) findViewById(R.id.recyc_line_point);
        mRecycView.setHasFixedSize(true);
        mRecycView.setLayoutManager(new LinearLayoutManager(LinePointActivity.this));
        adapter = new LinePointAdapter(mList,LinePointActivity.this);
        mRecycView.setAdapter(adapter);

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
        mList.clear();

        AVQuery<AVObject> avQuery = new AVQuery<>("Summer_Pub");
        avQuery.orderByDescending("createdAt");
        avQuery.include("owner");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null){
                    Log.e(TAG, "== Get Spring List Success ==" + e);
                    mList.addAll(list);
                    adapter.notifyDataSetChanged();
                }else {
                    Log.e(TAG, "== Get Spring List Failed ==" + e);
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("");
        Log.e(TAG, "-- onResume --");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "-- onDestroy --");
        this.finish();
    }
}
