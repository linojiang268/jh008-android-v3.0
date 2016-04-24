package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActDetailEntityy;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import org.json.JSONObject;

/**
 * 活动详情数据
 * Created by Administrator on 2015/8/3.
 */
public class ActDetailData extends BaseDataSource<ActDetailEntityy> {

    private String actId;

    public ActDetailData(String actId) {
        this.actId = actId;
    }

    @Override
    public ActDetailEntityy refresh() throws Exception {
        BaseParams params = new BaseParams(API.GET_ACT_DETAIL);
        params.put("activity", actId);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            JSONObject object = new JSONObject(status.getResult());
            ActDetailEntityy entity = JSON.parseObject(object.getString("activity"), ActDetailEntityy.class);
            if (entity != null) {
                return entity;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ActDetailEntityy loadMore() throws Exception {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

}
