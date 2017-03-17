package com.summer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.summer.constant.SP_Constant;

/**
 * Created by bestotem on 2017/3/16.
 */

public class SharedPreferencesUtil {
    private Editor editor;
    private SharedPreferences sp;

    public SharedPreferencesUtil(Context context, String sharedName) {
        sp = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 保存一个字符串
     */
    public boolean saveString(String key, String value) {
        try {
            editor.putString(key, value);
            return editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得一个字符串
     */
    public String getStringByKey(String key) {
        try {
            return sp.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 全部清除
     */
    public void clear() {
        sp.edit().clear().commit();
    }


    /**
     * 清除单个
     */
    public void clearInfo(String name) {
        try {
            sp.edit().remove(name).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否允许推送通知
     *
     * @return
     */
    public boolean isAllowPushNotify() {
        return sp.getBoolean(SP_Constant.SHARED_KEY_NOTIFY, true);
    }

    public void setPushNotifyEnable(boolean isChecked) {
        editor.putBoolean(SP_Constant.SHARED_KEY_NOTIFY, isChecked);
        editor.commit();
    }

    /**
     * 允许声音
     *
     * @return
     */
    public boolean isAllowVoice() {
        return sp.getBoolean(SP_Constant.SHARED_KEY_VOICE, true);
    }

    public void setAllowVoiceEnable(boolean isChecked) {
        editor.putBoolean(SP_Constant.SHARED_KEY_VOICE, isChecked);
        editor.commit();
    }

    /**
     * 允许震动
     *
     * @return
     */
    public boolean isAllowVibrate() {
        return sp.getBoolean(SP_Constant.SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrateEnable(boolean isChecked) {
        editor.putBoolean(SP_Constant.SHARED_KEY_VIBRATE, isChecked);
        editor.commit();
    }

}
