package com.summer.constant;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by bestotem on 2017/3/16.
 */

public class MyLeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"v0Jnwpt0bahbUFAKumtgDXIH-gzGzoHsz",
                "eq0Gv7pkLeaUbrJ42g3bk28M");

        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);

    }
}
