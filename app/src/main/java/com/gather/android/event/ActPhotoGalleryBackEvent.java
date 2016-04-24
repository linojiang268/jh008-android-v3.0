package com.gather.android.event;

import com.gather.android.entity.OrgPhotoEntity;

import java.util.ArrayList;

/**
 * 活动照片浏览大图返回事件
 * Created by Administrator on 2015/7/29.
 */
public class ActPhotoGalleryBackEvent {

    private int position;
    private ArrayList<OrgPhotoEntity> list;
    private int type;
    private int actId;
    private int page;
    private int maxPage;

    public ActPhotoGalleryBackEvent(int position, ArrayList<OrgPhotoEntity> list, int type, int actId, int page, int maxPage) {
        this.position = position;
        this.list = list;
        this.type = type;
        this.actId = actId;
        this.page = page;
        this.maxPage = maxPage;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<OrgPhotoEntity> getList() {
        return list;
    }

    public void setList(ArrayList<OrgPhotoEntity> list) {
        this.list = list;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getActId() {
        return actId;
    }

    public void setActId(int actId) {
        this.actId = actId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }
}
