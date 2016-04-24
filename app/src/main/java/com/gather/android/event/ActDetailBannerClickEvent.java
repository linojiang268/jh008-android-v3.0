package com.gather.android.event;

import com.gather.android.entity.BannerEntity;

/**
 * 活动详情轮播图点击事件
 * Created by Administrator on 2015/8/3.
 */
public class ActDetailBannerClickEvent {

    private BannerEntity model;
    private int position;

    public ActDetailBannerClickEvent(BannerEntity model, int position) {
        this.model = model;
        this.position = position;
    }

    public BannerEntity getModel() {
        return model;
    }

    public void setModel(BannerEntity model) {
        this.model = model;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
