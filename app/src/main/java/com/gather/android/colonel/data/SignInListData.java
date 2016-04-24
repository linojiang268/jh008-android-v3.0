package com.gather.android.colonel.data;


import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.SignInListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;


/**
 * 活动管理报名数据源
 * Created by Levi on 2015/9/29.
 */
public class SignInListData extends BaseDataSource<SignInListEntity> {
    private static final int PAGE_SIZE = 10;
    private int page = 1;
    private int maxPage = 1;

    private int type = 0;//0 – 获取未签到列表；1－获取已签到列表
    private String  actId;
    public SignInListData(String id, int type){
        this.type = type;
        this.actId = id;
    }

    @Override
    public SignInListEntity refresh() throws Exception {
        page = 1;
        return loadData();
    }

    @Override
    public SignInListEntity loadMore() throws Exception {
        page++;
        return loadData();
    }

    private SignInListEntity loadData(){
        BaseParams params = new BaseParams(API.SIGNIN_MGR_LIST);
        params.put("activity", actId);
        params.put("type", type);
        params.put("size", PAGE_SIZE);
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            SignInListEntity entity = JSON.parseObject(status.getResult(), SignInListEntity.class);
            int total = entity.getTotal();
            maxPage = total / PAGE_SIZE;
            return entity;
        }

        return null;
    }

    @Override
    public boolean hasMore() {
        return page < maxPage;
    }
}
