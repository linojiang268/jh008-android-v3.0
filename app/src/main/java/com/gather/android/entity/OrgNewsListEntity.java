package com.gather.android.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/29.
 */
public class OrgNewsListEntity {

    private int pages;
    private ArrayList<OrgNewsEntity> news;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public ArrayList<OrgNewsEntity> getNews() {
        return news;
    }

    public void setNews(ArrayList<OrgNewsEntity> news) {
        this.news = news;
    }
}
