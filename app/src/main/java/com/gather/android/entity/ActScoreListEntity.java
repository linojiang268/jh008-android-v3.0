package com.gather.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/3.
 */
public class ActScoreListEntity implements Serializable{

    private ArrayList<ActScoreEntity> activities;

    public ArrayList<ActScoreEntity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<ActScoreEntity> activities) {
        this.activities = activities;
    }
}
