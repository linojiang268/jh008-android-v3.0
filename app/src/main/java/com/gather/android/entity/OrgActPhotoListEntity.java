package com.gather.android.entity;

import java.util.ArrayList;

/**
 * 社团相册列表
 * Created by Administrator on 2015/7/24.
 */
public class OrgActPhotoListEntity {

    private int total_num;
    private ArrayList<OrgPhotoListEntity> activities;


    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public ArrayList<OrgPhotoListEntity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<OrgPhotoListEntity> activities) {
        this.activities = activities;
    }
}
