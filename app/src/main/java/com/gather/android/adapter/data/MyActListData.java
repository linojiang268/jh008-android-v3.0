package com.gather.android.adapter.data;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.MyActEntity;
import com.gather.android.entity.MyActListEntity;
import com.gather.android.http.OkHttpUtil;
import com.orhanobut.logger.Logger;
import com.shizhefei.HttpResponseStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * Created by Levi on 2015/7/15.
 */
public class MyActListData extends BaseDataSource<List<MyActEntity>> {
    private String[] WEEKS;
    private Stack<String> dateStack = new Stack<>();
    private int page = 1;
    private int maxPage = 1;
    private String mType = "All";

    public MyActListData(Context ctx){
        WEEKS = ctx.getResources().getStringArray(R.array.weeks);
    }

    @Override
    public List<MyActEntity> refresh() throws Exception {
        page = 1;
        maxPage = 1;
        dateStack.clear();
        return load(page);
    }

    public void setType(String type){
        mType = type;
    }

    @Override
    public List<MyActEntity> loadMore() throws Exception {
        return load(page + 1);
    }

    private String getFirstDateKey(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd号");
        String dateStr = sdf.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > 7){
            return dateStr;
        }
        else {

            return  dateStr + " " + WEEKS[dayIndex-1];
        }
    }


    private List<MyActEntity> load(int page) {
        BaseParams params = new BaseParams(API.GET_MY_ACT_LIST);
        params.put("type", mType);
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            Logger.i(status.getResult());
            MyActListEntity entity = JSON.parseObject(status.getResult(), MyActListEntity.class);
            maxPage = entity.getPages();
            List<MyActEntity> list = entity.getActivities();
            List<MyActEntity> result = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat keySdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            String todayKey = keySdf.format(new Date());
            for (MyActEntity en : list){
                try {
                    Date date = sdf.parse(en.getBegin_time());
                    String dateKey = keySdf.format(date);
                    String time = timeSdf.format(date);
                    en.setStartTime(time);

                    if (dateStack.contains(dateKey)){
                        en.setIsFirst(false);
                        en.setDateKey(dateKey);
                    }
                    else {
                        en.setIsFirst(true);
                        String key = getFirstDateKey(date);
                        en.setDateKey(key);
                        dateStack.add(dateKey);
                    }
                    en.setIsToday(todayKey.equals(dateKey));
                    result.add(en);
                } catch (ParseException e) {
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public boolean hasMore() {
        return page < maxPage;
    }
}
