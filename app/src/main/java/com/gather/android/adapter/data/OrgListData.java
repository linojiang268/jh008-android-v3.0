package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.entity.OrgListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.List;

/**
 * Created by Administrator on 2015/7/7.
 */
public class OrgListData extends BaseDataSource<List<OrgDetailEntity>> {

    private int page = 1;
    private int maxPage = 0;
    private String name;

    public OrgListData() {
        super();
    }

    public OrgListData(String name) {
        super();
        this.name = name;
    }

    public void setSearch(String name) {
        this.name = name;
    }

    @Override
    public List<OrgDetailEntity> refresh() throws Exception {
        return load(1);
    }

    @Override
    public List<OrgDetailEntity> loadMore() throws Exception {
        return load(page + 1);
    }

    private List<OrgDetailEntity> load(int page) {
        BaseParams params = new BaseParams(API.GET_TAB_HOST_ORG_LIST);
        params.put("city", CityPref.getInstance().getCityId());
        params.put("name", name);
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            OrgListEntity entify = JSON.parseObject(status.getResult(), OrgListEntity.class);
            if (entify != null) {
                if (page == 1) {
                    maxPage = entify.getPages();
                }
                this.page = page;
                return entify.getTeams();
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
