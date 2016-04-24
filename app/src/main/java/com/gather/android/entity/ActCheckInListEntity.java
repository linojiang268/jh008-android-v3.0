package com.gather.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 活动签到列表
 * Created by Administrator on 2015/8/5.
 */
public class ActCheckInListEntity implements Serializable{

    private String message;
    private int step;
    private String activity_id;
    private String activity_title;
    private String ver;
    private ArrayList<ActCheckInEntity> check_list;

    public String getMessage() {
        return (message != null) ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getActivity_id() {
        return (activity_id != null) ? activity_id : "";
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getVer() {
        return (ver != null) ? ver : "";
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public ArrayList<ActCheckInEntity> getCheck_list() {
        return check_list;
    }

    public void setCheck_list(ArrayList<ActCheckInEntity> check_list) {
        this.check_list = check_list;
    }

    public String getActivity_title() {
        return (activity_title != null) ? activity_title : "";
    }

    public void setActivity_title(String activity_title) {
        this.activity_title = activity_title;
    }
}
