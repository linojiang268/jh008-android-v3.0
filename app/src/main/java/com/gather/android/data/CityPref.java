package com.gather.android.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.gather.android.GatherApplication;
import com.gather.android.entity.CityEntity;
import com.gather.android.utils.Checker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Levi on 2015/7/24.
 */
public class CityPref {
    //选择的城市的id
    private  static final String KEY_CITY_ID ="city_id";
    //选择的城市的名字
    private  static final String KEY_CITY_NAME ="city_name";
    //是否是自动定位（手选城市则不会因为位置变化而自动改变城市）
    private static final String KEY_AUTO_LOCATION = "auto_loc";
    //可选城市列表
    private static final String KEY_CITY_LIST = "city_list";
    //城市列表更新时间
    private static final String KEY_CITYLIST_UPDATE_TIME = "update_time";


    private static  CityPref instance;

    public static  synchronized  CityPref getInstance(){
        if (instance == null){
            instance = new CityPref(GatherApplication.getContext());
        }
        return  instance;
    }

    /////////////////////////////////////////////////////////
    private static  final String PREF_NAME = "CITY_SELECTED";
    private SharedPreferences mPref;

    private CityPref(Context ctx){
        mPref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public void setAutoLocationEnable(boolean enable){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(KEY_AUTO_LOCATION, enable);
        editor.commit();
    }

    public boolean isAutoLocation(){
        return mPref.getBoolean(KEY_AUTO_LOCATION, false);
    }

    public void setSelectedCity(String name, int id){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(KEY_CITY_ID, id);
        editor.putString(KEY_CITY_NAME, name);
        editor.commit();
    }

    public boolean hasSelectedCity(){
        return getCityId() != -1;
    }

    public String getCityName(){
        return mPref.getString(KEY_CITY_NAME, null);
    }

    public int getCityId(){
        return mPref.getInt(KEY_CITY_ID, -1);
    }

    public void clear(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();
    }

    public boolean needUpdateCityList(){
        String lastUpdateDate = mPref.getString(KEY_CITYLIST_UPDATE_TIME, "");
        String curDate = getCurTimeDateKey();
        return !lastUpdateDate.equals(curDate);
    }

    public void updateCityList(String citylistJsonStr){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_CITY_LIST, citylistJsonStr);
        editor.putString(KEY_CITYLIST_UPDATE_TIME, getCurTimeDateKey());
        editor.commit();
    }

    private String getCurTimeDateKey(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }

    public List<CityEntity> getCityList(){
        List<CityEntity> list = null;
        String citysStr = mPref.getString(KEY_CITY_LIST, null);
        if (!Checker.isEmpty(citysStr)){
            try {
                list = JSON.parseArray(citysStr, CityEntity.class);
            }
            catch (Exception e){
                SharedPreferences.Editor editor = mPref.edit();
                editor.remove(KEY_CITY_LIST);
                editor.remove(KEY_CITYLIST_UPDATE_TIME);
                editor.commit();
                list = null;
            }
        }
        return list;
    }

    public HashMap<String, Integer> getCityMap(){
        List<CityEntity> list = getCityList();
        if (list != null){
            HashMap<String, Integer> map = new HashMap<>();
            for (CityEntity entity : list){
                map.put(entity.getName(), entity.getId());
            }
            return map;
        }
        return null;
    }
}
