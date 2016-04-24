package com.gather.android.event;

/**
 * 活动报名后提示界面点击事件
 * Created by Administrator on 2015/8/4.
 */
public class ActEnrollTipsEvent {

    private String actId;
    private int applicant_info;

    public ActEnrollTipsEvent(String actId, int applicant_info) {
        this.actId = actId;
        this.applicant_info = applicant_info;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getApplicant_info() {
        return applicant_info;
    }

    public void setApplicant_info(int applicant_info) {
        this.applicant_info = applicant_info;
    }
}
