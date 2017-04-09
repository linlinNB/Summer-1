package com.summer.publish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.summer.R;

/**
 * Created by bestotem on 2017/4/9.
 */

public class LinePointActivity extends AppCompatActivity
        implements View.OnClickListener{

    private static final String TAG = "LinePointActivity";
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_point);
    }

    @Override
    public void onClick(View v) {

    }
}
