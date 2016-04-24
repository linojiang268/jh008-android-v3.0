package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.OrgActPhotoListEntity;
import com.gather.android.entity.OrgPhotoListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.List;

/**
 * 社团相册列表
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoListData extends BaseDataSource<List<OrgPhotoListEntity>> {

    private int page = 1;
    private int maxPage = 0;
    private String orgId;

    public OrgPhotoListData(String orgId) {
        super();
        this.orgId = orgId;
    }

    @Override
    public List<OrgPhotoListEntity> refresh() throws Exception {
        return load(1);
    }

    @Override
    public List<OrgPhotoListEntity> loadMore() throws Exception {
        return load(page + 1);
    }

    private List<OrgPhotoListEntity> load(int page) {
        BaseParams params = new BaseParams(API.ORG_ACT_ALBUM_LIST);
        params.put("team", orgId);
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            OrgActPhotoListEntity entity = JSON.parseObject(status.getResult(), OrgActPhotoListEntity.class);
            if (entity != null) {
                if (page == 1) {
                    maxPage = entity.getTotal_num();
                }
                this.page = page;
                return entity.getActivities();
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
