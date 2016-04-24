package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.gather.android.API;
import com.gather.android.GatherApplication;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.data.UserPref;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.yunba.android.manager.YunBaManager;

/**
 * 启动页
 * Created by Christain on 15/4/19.
 */
public class StartActivity extends BaseActivity implements Runnable {

    private static final long START_DURATION = 2000;
    private static final int START_PROGRESS_OVER = 12;
    private boolean needLogin = false, needGuide;
    private static final String LOGIN_TIMES = "LOGIN_TIMES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(this).start();
        updateCityList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    //更新城市列表
    private void updateCityList() {
        if (CityPref.getInstance().needUpdateCityList()) {
            OkHttpUtil.get(new BaseParams(API.GET_CITY_LIST), new ResponseHandler() {

                @Override
                public void success(String msg) {
                    try {
                        JSONObject object = new JSONObject(msg);
                        String citysStr = object.getString("cities");
                        CityPref.getInstance().updateCityList(citysStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void fail(int code, String error) {

                }
            });
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        if (PhoneManager.isSdCardExit()) {
            PhoneManager.checkPath();
        } else {
            handler.sendEmptyMessage(0);
        }

        needLogin = !UserPref.getInstance().hasLogin();

        if (needLogin) {
            YunBaManager.subscribe(getApplicationContext(), new String[]{"topic_static_all", "topic_static_android", "topic_static_no_login"}, GatherApplication.getInstance().pushListener);
        }

        SharedPreferences timePreferences = StartActivity.this.getSharedPreferences(LOGIN_TIMES, Context.MODE_PRIVATE);
        int times = timePreferences.getInt("TIMES", 0);
        if (times == 0) {
            needGuide = false;
            SharedPreferences.Editor editor = timePreferences.edit();
            editor.putInt("TIMES", 1);
            editor.apply();
        } else {
            needGuide = false;
        }

        while (System.currentTimeMillis() - startTime < START_DURATION){
            Thread.yield();
        }
        handler.sendEmptyMessage(START_PROGRESS_OVER);
    }

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Toast.makeText(StartActivity.this, "没有存储卡", Toast.LENGTH_SHORT).show();
                    break;
                case START_PROGRESS_OVER:
                    Intent intent = null;
                    if (needGuide) {
                        intent = new Intent(StartActivity.this, GuideActivity.class);
                        startActivity(intent);
                    } else {
                        if (needLogin){
                            intent = new Intent(StartActivity.this, Login.class);
                            startActivity(intent);
                        }
                        else {
                            IndexHome.checkAndStartIndexActivity(StartActivity.this);
                        }
                    }
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
            }
            return false;
        }
    };

    private final Handler handler = new Handler(callback);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
