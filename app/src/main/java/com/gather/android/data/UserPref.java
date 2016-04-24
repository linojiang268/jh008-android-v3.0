package com.gather.android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.gather.android.GatherApplication;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.utils.Checker;
import com.orhanobut.logger.Logger;


/**
 * 登录用户信息保存
 * Created by Levi on 2015/6/24.
 */
public class UserPref {
    private  static final String KEY_INFO ="info";
    private  static final String KEY_MY_ACT_COUNT ="act_count";
    private  static final String KEY_MY_ASSN_COUNT ="assn_count";
    private  static final String KEY_ALIAS ="alias";
    private  static final String KEY_ID ="id";
    private  static final String KEY_COLONEL ="colonel";


    private static  UserPref instance;

    public static  synchronized  UserPref getInstance(){
        if (instance == null){
            instance = new UserPref(GatherApplication.getContext());
        }
        return  instance;
    }

    /////////////////////////////////////////////////////////
    private static  final String PREF_NAME = "USER_INFO";
    private SharedPreferences mPref;

    private UserPref(Context ctx){
        mPref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void updateInfo(UserInfoEntity entity){
        if (entity != null){
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(KEY_INFO, JSON.toJSONString(entity));

            editor.commit();
        }
    }

    public boolean hasLogin(){
        UserInfoEntity entity = getUserInfo();
        return  entity != null;
    }

    public UserInfoEntity getUserInfo(){
        String info = mPref.getString(KEY_INFO, null);
        if (!Checker.isEmpty(info)){
            try {
                return JSON.parseObject(info, UserInfoEntity.class);
            }
            catch (Exception e){
                e.printStackTrace();
                clear();
            }
        }
        return  null;
    }

    public String getUserId(){
        return mPref.getString(KEY_ID, "");
    }

    public void setMyActCountInfo(String info){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_MY_ACT_COUNT, info);
        editor.commit();
    }

    public void setMyAssnCountInfo(String info){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_MY_ASSN_COUNT, info);
        editor.commit();
    }

    public String getMyActCountInfo(){
        return  mPref.getString(KEY_MY_ACT_COUNT, "");
    }

    public String getMyAssnCountInfo(){
        return  mPref.getString(KEY_MY_ASSN_COUNT, "");
    }

    public void setCurLoginUser(UserInfoEntity entity){
        if (entity != null){
            SharedPreferences.Editor editor = mPref.edit();
            if (!TextUtils.isEmpty(entity.getPush_alias())){
                editor.putString(KEY_ALIAS, entity.getPush_alias());
            }
            else {
                entity.setPush_alias(getAlias());
            }
            editor.putBoolean(KEY_COLONEL, entity.isColonel());
            editor.putString(KEY_INFO, JSON.toJSONString(entity));
            editor.putString(KEY_ID, entity.getUid());
            editor.commit();
        }
    }

    public String getAlias(){
        Logger.d(mPref.getString(KEY_ALIAS, ""));
        return mPref.getString(KEY_ALIAS, "");
    }

    public void setAlias(String alias){
        if (!TextUtils.isEmpty(alias)){
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString(KEY_ALIAS, alias);
            editor.commit();
        }
    }

    public boolean isColonel(){
        return mPref.getBoolean(KEY_COLONEL, false);
    }

    public void clear(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();
    }
}
