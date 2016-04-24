package com.gather.android.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.gather.android.GatherApplication;

/**
 * 时间
 * Created by Administrator on 2015/8/5.
 */
public class TimePref {

    /**
     * 社团活动消息最后请求时间
     */
    private static final String KEY_ORG_ACT_MSG_TIME = "KEY_ORG_ACT_MSG_TIME";

    /**
     * 系统消息最后请求时间
     */
    private static final String KEY_SYSTEM_MSG_TIME = "KEY_SYSTEM_MSG_TIME";

    /**
     * 消息红点提示
     */
    private static final String KEY_MESSAGE_RED_TIPS = "KEY_MESSAGE_RED_TIPS";


    private static  TimePref instance;

    public static  synchronized  TimePref getInstance(){
        if (instance == null){
            instance = new TimePref(GatherApplication.getContext());
        }
        return  instance;
    }

    /////////////////////////////////////////////////////////
    private static  final String PREF_NAME = "TIME_SHARE";
    private SharedPreferences mPref;

    private TimePref(Context ctx){
        mPref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取社团活动消息最后请求时间
     */
    public String getOrgActMsgTime() {
        return mPref.getString(KEY_ORG_ACT_MSG_TIME + "_" + UserPref.getInstance().getUserInfo().getUid(), "");
    }

    public void setOrgActMsgTime(String time) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_ORG_ACT_MSG_TIME + "_" + UserPref.getInstance().getUserInfo().getUid(), time);
        editor.commit();
    }

    /**
     * 获取系统消息最后请求时间
     */
    public String getSystemMsgTime() {
        return mPref.getString(KEY_SYSTEM_MSG_TIME, "");
    }

    public void setSystemMsgTime(String time) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_SYSTEM_MSG_TIME, time);
        editor.commit();
    }

    /**
     *  社团红点提示最后更新时间
     */
    public void setOrgTipLastTime(String key, long time) {
        key = "TIME_" + key;
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(key, time);
        editor.commit();
    }

    public long getOrgLastTime(String key) {
        key = "TIME_" + key;
        return mPref.getLong(key, 0);
    }


    /**
     * 消息是否显示红点
     */
    public boolean hasRedTips() {
        return mPref.getBoolean(KEY_MESSAGE_RED_TIPS, false);
    }

    public void setRedTips(boolean tips) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(KEY_MESSAGE_RED_TIPS, tips);
        editor.commit();
    }
}
