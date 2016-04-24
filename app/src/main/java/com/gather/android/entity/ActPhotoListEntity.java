package com.gather.android.entity;

import java.util.ArrayList;

/**
 * 活动相册列表
 * Created by Administrator on 2015/7/24.
 */
public class ActPhotoListEntity {

    private int pages;
    private ArrayList<OrgPhotoEntity> images;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public ArrayList<OrgPhotoEntity> getImages() {
        return images;
    }

    public void setImages(ArrayList<OrgPhotoEntity> images) {
        this.images = images;
    }
}
