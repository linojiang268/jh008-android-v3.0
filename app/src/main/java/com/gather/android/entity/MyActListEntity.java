package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 2015/7/15.
 */
public class MyActListEntity {
    private List<MyActEntity> activities;
    private int pages;


    public List<MyActEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<MyActEntity> activities) {
        this.activities = activities;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
