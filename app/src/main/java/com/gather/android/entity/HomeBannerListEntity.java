package com.gather.android.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeBannerListEntity {

    private ArrayList<HomeBannerEntity> banners;

    public ArrayList<HomeBannerEntity> getBanners() {
        return (banners != null) ? banners : new ArrayList<HomeBannerEntity>();
    }

    public void setBanners(ArrayList<HomeBannerEntity> banners) {
        this.banners = banners;
    }
}
