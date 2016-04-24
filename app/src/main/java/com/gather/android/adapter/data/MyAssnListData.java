package com.gather.android.adapter.data;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.entity.MyAssnEntity;
import com.gather.android.entity.MyAssnListEntity;
import com.gather.android.http.OkHttpUtil;
import com.orhanobut.logger.Logger;
import com.shizhefei.HttpResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Levi on 2015/8/11.
 */
public class MyAssnListData extends BaseDataSource<List<MyAssnEntity>> {

    @Override
    public List<MyAssnEntity> refresh() throws Exception {
        return load();
    }


    @Override
    public List<MyAssnEntity> loadMore() throws Exception {
        return load();
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    private List<MyAssnEntity> load() {
        BaseParams params = new BaseParams(API.GET_MY_ASSN);
        params.put("city", CityPref.getInstance().getCityId());
        HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
        if (status.isSuccess()) {
            Logger.i(status.getResult());
            MyAssnListEntity entity = JSON.parseObject(status.getResult(), MyAssnListEntity.class);
            List<MyAssnEntity> result = new ArrayList<MyAssnEntity>();
            List<MyAssnEntity> list = entity.getEnrolled_teams();
            int n = 0;
            if (list != null && list.size() > 0){
                for (MyAssnEntity en : list){
                    en.setIsFirst(n == 0);
                    en.setIsLast(n == list.size() - 1);
                    en.setGroupType(R.string.my_assn);
                    result.add(en);
                    n++;
                }
            }
            list = entity.getRequested_teams();
            n = 0;
            if (list != null && list.size() > 0){
                for (MyAssnEntity en : list){
                    en.setIsFirst(n == 0);
                    en.setIsLast(n == list.size() - 1);
                    en.setGroupType(R.string.apply_assn);
                    result.add(en);
                    n++;
                }
            }
            list = entity.getRecommended_teams();
            n = 0;
            if (list != null && list.size() > 0){
                for (MyAssnEntity en : list){
                    en.setIsFirst(n == 0);
                    en.setIsLast(n == list.size() - 1);
                    en.setGroupType(R.string.recommend_assn);
                    result.add(en);
                    n++;
                }
            }
            list = entity.getInvited_teams();
            n = 0;
            if (list != null && list.size() > 0){
                for (MyAssnEntity en : list){
                    en.setIsFirst(n == 0);
                    en.setIsLast(n == list.size() - 1);
                    en.setGroupType(R.string.invited_assn);
                    result.add(en);
                    n++;
                }
            }
            return  result;
        }
        return null;
    }

}
