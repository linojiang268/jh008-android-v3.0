package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActPhotoListEntity;
import com.gather.android.entity.OrgPhotoEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoDetailData extends BaseDataSource<List<OrgPhotoEntity>> {

    private int page = 1;
    private int maxPage = 0;
    private int size = 18;
    private int type;//相册创建者类型：0主办方，1普通用户
    private String actId;

    public OrgPhotoDetailData(int type, String actId) {
        super();
        this.type = type;
        this.actId = actId;
    }

    public int getIndexPage() {
        return page;
    }

    public void setIndexPage(int page) {
        this.page = page;
    }

    public int getMaxPage() {
        return maxPage;
    }

    @Override
    public List<OrgPhotoEntity> refresh() throws Exception {
        return load(1);
    }

    @Override
    public List<OrgPhotoEntity> loadMore() throws Exception {
        if (maxPage != 0) {
            if (page + 1 > maxPage) {
                return new ArrayList<OrgPhotoEntity>();
            }
        }
        return load(page + 1);
    }

    private List<OrgPhotoEntity> load(int page) {
        BaseParams params = new BaseParams(API.ACT_ALBUM);
        params.put("activity", actId);
        params.put("creator_type", type);
        params.put("size", size);
        params.put("page", page);
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            ActPhotoListEntity entity = JSON.parseObject(status.getResult(), ActPhotoListEntity.class);
            if (entity != null) {
                if (page == 1) {
                    maxPage = entity.getPages();
                }
                this.page = page;
                return entity.getImages();
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
