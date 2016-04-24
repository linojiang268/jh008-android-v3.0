package com.gather.android.colonel.data;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.SignUpEntity;
import com.gather.android.entity.SignUpListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.List;

/**
 * 活动管理报名数据源
 * Created by Levi on 2015/9/29.
 */
public class SignUpListData extends BaseDataSource<SignUpListEntity> {

    private int curCount = 0;
    private int total = 1;
    private String lastId = null;

    private int type = 3;//1 – 待审核；2 – 待支付；3 – 成功报名
    private String  actId;
    public SignUpListData(String id, int type){
        this.type = type;
        this.actId = id;
    }

    @Override
    public SignUpListEntity refresh() throws Exception {
        lastId = null;
        curCount = 0;
        return loadData();
    }

    @Override
    public SignUpListEntity loadMore() throws Exception {
        return loadData();
    }

    private SignUpListEntity loadData(){
        BaseParams params = new BaseParams(API.SIGNUP_MGR_LIST);
        params.put("activity", actId);
        params.put("status", type);
        if (TextUtils.isEmpty(lastId)){
            params.put("id", "");
        }
        params.put("size", 10);
        params.put("sort", 0);
        params.put("is_pre", 0);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            SignUpListEntity entity = JSON.parseObject(status.getResult(), SignUpListEntity.class);
            total = entity.getTotal();
            List<SignUpEntity> list = entity.getApplicants();
            if (list != null && list.size() > 0){
                curCount += list.size();
                lastId = list.get(list.size() - 1).getId();
            }
            return entity;
        }

        return null;
    }

    @Override
    public boolean hasMore() {
        return curCount < total;
    }
}
