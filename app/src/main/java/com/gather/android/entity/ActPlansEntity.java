package com.gather.android.entity;

import java.io.Serializable;

/**
 * 活动流程列表
 * Created by Levi on 2015/9/21.
 */
public class ActPlansEntity implements Serializable{
    /**
     * plan_text : 签到
     * end_time : 2015-09-12 14:10:00
     * begin_time : 2015-09-12 14:00:00
     * id : 16
     */
    private String plan_text;
    private String end_time;
    private String begin_time;
    private int id;
    private boolean isFirst;
    private String dateKey;

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public void setPlan_text(String plan_text) {
        this.plan_text = plan_text;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlan_text() {
        return plan_text;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public int getId() {
        return id;
    }
}
