package com.gather.android.adapter.data;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActFileEntity;
import com.gather.android.entity.ActFileListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;


/**
 * 活动详情数据
 * Created by Administrator on 2015/8/3.
 */
public class ActFileListData extends BaseDataSource<List<ActFileEntity>>{
    private final String[] WEEKS;

    private int page = 1;
    private int maxPage = 1;
    private String actId;
    private Stack<String> dateStack = new Stack<>();
    public ActFileListData(Context context, String actId) {
        this.WEEKS = context.getResources().getStringArray(R.array.weeks);
        this.actId = actId;
    }

    @Override
    public List<ActFileEntity> refresh() throws Exception {
        page = 1;
        dateStack.clear();
        return load(page);
    }

    @Override
    public List<ActFileEntity> loadMore() throws Exception {
        page++;
        return load(page);
    }

    private List<ActFileEntity> load(int p){
        BaseParams params = new BaseParams(API.GET_ACT_FILE_LIST);
        params.put("activity", actId);
        params.put("page", p);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            ActFileListEntity entity = JSON.parseObject(status.getResult(), ActFileListEntity.class);
            maxPage = entity.getPages();

            List<ActFileEntity> list = entity.getFiles();
            List<ActFileEntity> result = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat keySdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            String todayKey = keySdf.format(new Date());
            for (ActFileEntity en : list){
                try {
                    Date date = sdf.parse(en.getCreated_at());
                    String dateKey = keySdf.format(date);
                    String time = timeSdf.format(date);
                    en.setCreated_at(time);

                    if (dateStack.contains(dateKey)){
                        en.setIsFirst(false);
                        en.setDateKey(dateKey);
                    }
                    else {
                        en.setIsFirst(true);
                        String key = getFormatTime(date);
                        en.setDateKey(key);
                        dateStack.add(dateKey);
                    }
                    result.add(en);
                } catch (ParseException e) {
                }
            }

//            List<ActFileEntity> list = entity.getFiles();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            for (int i = 0; i < list.size(); i++){
//                String time = list.get(i).getCreated_at();
//                try {
//                    Date date = sdf.parse(time);
//                    list.get(i).setCreated_at(getFormatTime(date));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
            return result;
        } else {
            return null;
        }
    }

    private String getFormatTime(Date date){
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

    @Override
    public boolean hasMore() {
        return page < maxPage;
    }

}
