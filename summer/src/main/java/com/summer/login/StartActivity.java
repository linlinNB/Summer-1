package com.summer.login;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.summer.MainActivity;
import com.summer.R;
import com.summer.constant.SP_Constant;
import com.summer.utils.SharedPreferencesUtil;

/**
 * Created by bestotem on 2017/3/16.
 */

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";
    Context context = StartActivity.this;
    SharedPreferencesUtil sp;
    private TextView tv_start;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        tv_start = (TextView) findViewById(R.id.tv_start);
        initView();
        startAnim();
    }

    private void initView() {
        sp = new SharedPreferencesUtil(context, SP_Constant.SP_NAME);
        tv_start = (TextView) findViewById(R.id.tv_start);
    }

    private void startAnim() {
        tv_start.postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofFloat(0,1);
                animator.setDuration(800);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float iv_alpha  = (float) animation.getAnimatedValue();

                        tv_start.setAlpha(iv_alpha);
                        Log.e(TAG,"--- show iv_alpha --- "+iv_alpha);

                    }
                });

            }
        },800);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StartApp();
            }
        },1500);

    }

    private void StartApp() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
