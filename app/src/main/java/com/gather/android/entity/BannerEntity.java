package com.gather.android.entity;

import java.io.Serializable;

/**
 * Created by Christain on 2015/6/18.
 */
public class BannerEntity implements Serializable , Cloneable {
    private String imgUrl;
    private String title;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
