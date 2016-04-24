package com.gather.android.event;

import com.gather.android.entity.HomeBannerEntity;

/**
 * 首页Banner点击事件
 * Created by Administrator on 2015/9/18.
 */
public class HomeBannerClickEvent {

    private HomeBannerEntity model;
    private int position;

    public HomeBannerClickEvent(HomeBannerEntity model, int position) {
        this.model = model;
        this.position = position;
    }

    public HomeBannerEntity getModel() {
        return model;
    }

    public void setModel(HomeBannerEntity model) {
        this.model = model;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
