package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.OrgNewsEntity;
import com.gather.android.entity.OrgNewsListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.List;

/**
 * 社团往期回顾（资讯）列表数据
 * Created by Administrator on 2015/7/29.
 */
public class OrgNewsListData extends BaseDataSource<List<OrgNewsEntity>> {

    private int page = 1;
    private int maxPage = 0;
    private String orgId;

    public OrgNewsListData(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public List<OrgNewsEntity> refresh() throws Exception {
        return load(1);
    }

    @Override
    public List<OrgNewsEntity> loadMore() throws Exception {
        return load(page + 1);
    }

    private List<OrgNewsEntity> load(int page) {
        BaseParams params = new BaseParams(API.ORG_NEWS_LIST);
        params.put("team_id", orgId);
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            OrgNewsListEntity entify = JSON.parseObject(status.getResult(), OrgNewsListEntity.class);
            if (entify != null && entify.getNews() != null) {
                if (page == 1) {
                    maxPage = entify.getPages();
                }
                this.page = page;
                return entify.getNews();
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
