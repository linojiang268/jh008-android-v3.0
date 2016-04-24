package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.entity.ActEntity;
import com.gather.android.entity.ActListEntity;
import com.gather.android.ui.fragment.ActBaseFragment;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.List;

/**
 * Created by Levi on 2015/7/15.
 */
public class ActListData extends BaseDataSource<List<ActEntity>> {

    private int page = 1;
    private int maxPage = 0;

    private String keywords;
    private int mode;
    private String orgId;

    public ActListData(int mode) {
        this.mode = mode;
    }

    @Override
    public List<ActEntity> refresh() throws Exception {
        page = 1;
        return load(page);
    }

    @Override
    public List<ActEntity> loadMore() throws Exception {
        return load(++page);
    }

    public void setSearch(String keywords){
        this.keywords = keywords;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    private List<ActEntity> load(int page) {
        BaseParams params = null;
        if (mode == ActBaseFragment.MODE_SEARCH){
            params = new BaseParams(API.SEARCH_ACT_OF_CITY);
            params.put("keyword", keywords);
            params.put("city", CityPref.getInstance().getCityId());
        }
        else if (mode == ActBaseFragment.MODE_NORMAL) {
            params = new BaseParams(API.GET_ACT_OF_CITY);
            params.put("city", CityPref.getInstance().getCityId());
        } else {
            params = new BaseParams(API.GET_ORG_ACT_LIST);
            params.put("team", orgId);
        }
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            ActListEntity entify = JSON.parseObject(status.getResult(), ActListEntity.class);
            if (entify != null) {
                if (page == 1) {
                    maxPage = entify.getPages();
                }
                this.page = page;
                return entify.getActivities();
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean hasMore() {
        return page < maxPage;
    }
}
