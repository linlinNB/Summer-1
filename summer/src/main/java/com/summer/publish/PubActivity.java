package com.summer.publish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.summer.R;

/**
 * Created by bestotem on 2017/3/25.
 */

public class PubActivity extends AppCompatActivity{
    private static final String TAG = "PubActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);
    }
}
