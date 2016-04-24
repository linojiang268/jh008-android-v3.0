package com.gather.android.colonel.data;


import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActMgrEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import org.json.JSONObject;

import java.util.List;

/**
 * 活动管理列表数据源
 * Created by Levi on 2015/9/29.
 */
public class ActMgrListData extends BaseDataSource<List<ActMgrEntity>> {
    private static final int PAGE_SIZE = 10;

    private int curPage = 1;
    private int maxPages = 1;

    @Override
    public List<ActMgrEntity> refresh() throws Exception {
        curPage = 1;
        return loadData(curPage);
    }

    @Override
    public List<ActMgrEntity> loadMore() throws Exception {
        curPage++;
        return loadData(curPage);
    }

    private List<ActMgrEntity> loadData(int page){
        BaseParams params = new BaseParams(API.ACT_MGR_LIST);
        params.put("page", page);
        params.put("size", PAGE_SIZE);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            try {
                JSONObject object = new JSONObject(status.getResult());
                int totalNum = object.getInt("total_num");
                maxPages = totalNum / PAGE_SIZE;
                List<ActMgrEntity> list = JSON.parseArray(object.getString("activities"), ActMgrEntity.class);
                if (list != null && list.size() > 0){
                    for (int i = 0; i < list.size(); i++){
                        list.get(i).rebuildTime();
                    }
                }
                return list;
            }
            catch (Exception e){

            }
        }
        return null;
    }

    @Override
    public boolean hasMore() {
        return curPage < maxPages;
    }
}
