package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.ui.activity.OrgMember;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.OrgMemberEntity;
import com.gather.android.entity.OrgMemberListEntity;
import com.gather.android.http.OkHttpUtil;
import com.shizhefei.HttpResponseStatus;

import java.util.List;

/**
 * Created by Administrator on 2015/7/9.
 */
public class OrgMemberData extends BaseDataSource<List<OrgMemberEntity>> {

    private int page = 1;
    private int maxPage = 0;
    private String orgId;
    private String actId;
    private int type;//活动成员或社团成员

    public OrgMemberData(int type) {
        this.type = type;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    @Override
    public List<OrgMemberEntity> refresh() throws Exception {
        return load(1);
    }

    @Override
    public List<OrgMemberEntity> loadMore() throws Exception {
        return load(page + 1);
    }

    private List<OrgMemberEntity> load(int page) {
        BaseParams params = null;
        if (type == OrgMember.ORG) {
            params = new BaseParams(API.ORG_MEMBER_LIST);
            params.put("team", orgId);
            params.put("page", page);
        } else {
            params = new BaseParams(API.GET_ACT_MEMBER_LIST);
            params.put("activity_id", actId);
            params.put("page", page);
        }
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            OrgMemberListEntity entity = JSON.parseObject(status.getResult(), OrgMemberListEntity.class);
            if (entity != null) {
                if (page == 1) {
                    maxPage = entity.getPages();
                }
                this.page = page;
                return entity.getMembers();
            }
        }
        return null;
    }

    @Override
    public boolean hasMore() {
        return page < maxPage;
    }
}

