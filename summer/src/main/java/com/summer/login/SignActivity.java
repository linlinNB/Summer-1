package com.summer.login;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import com.summer.R;
import com.summer.constant.SP_Constant;
import com.summer.location.LocMap;
import com.summer.utils.SharedPreferencesUtil;
import com.summer.utils.ShowToast;
import com.summer.utils.Tools;
import com.summer.view.SignView;


/**
 * Created by bestotem on 2017/3/18.
 */

public class SignActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "SignActivity";

    private SharedPreferencesUtil sp;
    private RelativeLayout mRelat;
    private Scene mSceneLogging;

    private AutoCompleteTextView aot_phone;
    private AutoCompleteTextView aot_smsCode;
    private Button btn_smsCode;

    private SignView signViewBtn;
    private int mDuration;

    /**
     * 验证码计时器
     */
    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btn_smsCode.setText(millisUntilFinished / 1000 + " s");
        }

        @Override
        public void onFinish() {
            btn_smsCode.setText("SmsCode");
            btn_smsCode.setClickable(true);
            btn_smsCode.setBackgroundColor(getResources().getColor(R.color.colorBg));
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scen);
        sp = new SharedPreferencesUtil(SignActivity.this, SP_Constant.SP_NAME);

        initView();
        addListener();
    }

    private void initView() {
        mRelat = (RelativeLayout) findViewById(R.id.rlt_bg);
        mDuration = getResources().getInteger(R.integer.duration);

        mSceneLogging = Scene.getSceneForLayout(mRelat,R.layout.scene_logging,this);

        LoadAnim();

        aot_phone = (AutoCompleteTextView) findViewById(R.id.aot_phone);
        aot_smsCode = (AutoCompleteTextView) findViewById(R.id.aot_smsCode);
        btn_smsCode = (Button) findViewById(R.id.btn_smsCode);
        signViewBtn = (SignView) findViewById(R.id.signView_btn);
    }

    private void LoadAnim() {
        mSceneLogging.setEnterAction(new Runnable() {
            @Override
            public void run() {
                final SignView signViewBtn = (SignView)
                        mRelat.findViewById(R.id.signView_btn);

                signViewBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signViewBtn.setStatus(SignView.STATUS_LOGGING);
                    }
                }, mDuration);

                signViewBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signViewBtn.setStatus(SignView.STATUS_LOGIN_SUCCESS);
                    }
                }, 3000);


                final RelativeLayout mRelat_live = (RelativeLayout)
                        mRelat.findViewById(R.id.rlt_logging);

                mRelat_live.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ValueAnimator animator = ValueAnimator.ofFloat(0, -mRelat_live.getHeight());
                        animator.setDuration(mDuration);
                        animator.setInterpolator(new LinearInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float mHeight = (float) animation.getAnimatedValue();
                                mRelat_live.setY(mHeight);
                            }
                        });
                        animator.start();
                    }
                }, 4500);

                mRelat_live.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SignActivity.this,LocMap.class);
                        startActivity(intent);
                    }
                }, 5000);

                mRelat_live.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 5200);
            }
        });
    }


    private void addListener() {
        btn_smsCode.setOnClickListener(this);
        signViewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_smsCode:
                String uPhone = aot_phone.getText().toString().trim();

                if (!TextUtils.isEmpty(uPhone) && Tools.validatePhone(uPhone)) {
                    SendSmsCode(uPhone);
                    btn_smsCode.setText("59" + " s");
                    timer.start();
                    btn_smsCode.setClickable(false);
                    btn_smsCode.setBackgroundColor(getResources().getColor(R.color.theme_blue_two));

                    ShowToast.ColorToast(SignActivity.this, "SmsCode is Send", 1200);
                } else {
                    ShowToast.ColorToast(SignActivity.this, "phone number is error", 1200);
                    Log.e(TAG, "-- phone number is error --");
                }
                break;

            case R.id.signView_btn:
                String nPhone = aot_phone.getText().toString().trim();
                String nSmsCode = aot_smsCode.getText().toString().trim();

                if (!TextUtils.isEmpty(nPhone) && Tools.validatePhone(nPhone)) {
                    if (!TextUtils.isEmpty(nSmsCode) && Tools.isSMSCodeValid(nSmsCode)) {

                        checkSmsCode(nPhone, nSmsCode);

                    } else {
                        ShowToast.ColorToast(SignActivity.this, "smsCode is error", 1200);
                    }
                } else {
                    ShowToast.ColorToast(SignActivity.this, "phone number is error", 1200);
                    Log.e(TAG, "-- phone number is error --");
                }
                break;

            default:
                break;
        }
    }


    private void SendSmsCode(final String phone) {
        Log.e(TAG, "-- get phone is -> --" + phone);

        new AsyncTask<Void, Void, Void>() {
            boolean res;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    AVOSCloud.requestSMSCode(phone, "Spring", "注册登录", 10);
                    res = true;
                } catch (AVException e) {
                    e.printStackTrace();
                    res = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (res) {
                    Log.e(TAG, "-*- sendSucceed -*-");
                    ShowToast.ColorToast(SignActivity.this, "-*- sendSucceed -*-", 1200);
                } else {
                    Log.e(TAG, "-*- sendFailed -*-");
                    ShowToast.ColorToast(SignActivity.this, "-*- sendFailed -*-", 1200);
                }
            }
        }.execute();

    }


    private void checkSmsCode(final String phone, final String smsCode) {
        Log.e(TAG, "-- get phone is -> --" + phone);
        Log.e(TAG, "-- get smsCode is -> --" + smsCode);

        final AVUser avUser = new AVUser();

        AVOSCloud.verifySMSCodeInBackground(smsCode, phone, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.e(TAG, "-*- Verify Succeed -*- " + e);

                    sp.saveString("Verify", "Success");

                    avUser.setUsername(phone);
                    avUser.setPassword(smsCode);

                    avUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Log.e(TAG, "-*- signUp Succeed -*- " + e);
                            } else {
                                Log.e(TAG, "-*- signUp Failed -*- " + e);
                            }
                        }
                    });

                    RunAnim(signViewBtn);
                } else {
                    Log.e(TAG, "-*- Verify Failed -*- " + e);
                }
            }
        });
    }

    private void RunAnim(View view) {
        float finalRadius = (float) Math.hypot(mRelat.getWidth(), mRelat.getHeight());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        Animator anim = ViewAnimationUtils.createCircularReveal(mRelat,
                x + signViewBtn.getWidth() / 2,
                y - signViewBtn.getHeight() / 2,
                100,
                finalRadius);

        mRelat.setBackgroundColor(getResources().getColor(R.color.colorBg));
        anim.setDuration(mDuration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e(TAG, "-- onAnimationStart --");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRelat.setBackgroundColor(Color.TRANSPARENT);
                Log.e(TAG, "-- onAnimationEnd --");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e(TAG, "-- onAnimationCancel --");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e(TAG, "-- onAnimationRepeat --");
            }
        });

        anim.start();

        TransitionManager.go(mSceneLogging, new ChangeBounds()
                .setDuration(mDuration)
                .setInterpolator(new DecelerateInterpolator()));
    }


}
