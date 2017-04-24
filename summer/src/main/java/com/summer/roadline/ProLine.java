package com.summer.roadline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import com.summer.R;

/**
 * Created by bestotem on 2017/3/25.
 */

public class ProLine extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TAG = "ProLine";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_line);

        init();
    }

    private void init() {

    }

    @Override
    public void onClick(View v) {

    }
}
