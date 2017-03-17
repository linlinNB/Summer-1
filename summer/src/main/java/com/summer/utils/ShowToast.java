package com.summer.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.summer.R;

/**
 * Created by bestotem on 2017/3/16.
 */

public class ShowToast {
    private static final String TAG = "ShowToast";

    private static Toast toast;
    private static Context mContext;

    private ShowToast() {

    }

    public static Context getContext() {
        return mContext;
    }

    public static void init(Context context) {
        mContext = context;
        View view = Toast.makeText(mContext, "", Toast.LENGTH_SHORT).getView();
        init(mContext, view);
    }

    public static void init(Context context, View view) {
        toast = new Toast(context);
        toast.setView(view);
    }

    public static void setGravity(int gravity, int xOffset, int yOffset) {
        if (toast != null) {
            toast.setGravity(gravity, xOffset, yOffset);
        }
    }

    public static void show() {
        if (toast != null) {
            toast.show();
        }
    }

    public static void show(CharSequence text, int duration) {
        if (toast != null) {
            Log.e(TAG, "ShowToast is not initialized");
            return;
        }
        toast.setText(text);
        toast.setDuration(duration);
        toast.show();
    }

    public static void show(int resid, int duration) {
        if (toast != null) {
            Log.e(TAG, "ShowToast is not initialized");
            return;
        }
        toast.setText(resid);
        toast.setDuration(duration);
        toast.show();
    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void show(int resid) {
        show(resid, Toast.LENGTH_SHORT);
    }


    public static void ColorToast(final Activity activity,
                                  final String content,
                                  final long time) {
        activity.runOnUiThread(new Runnable() {
            View view;

            @Override
            public void run() {
                final Toast toast = Toast.makeText(activity, content, Toast.LENGTH_SHORT);
                view = LayoutInflater.from(activity).inflate(R.layout.view_color_toast, null);
                TextView textView = (TextView) view.findViewById(R.id.tv_toast);
                textView.setText(content);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, -600);
                toast.setView(view);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }

}
