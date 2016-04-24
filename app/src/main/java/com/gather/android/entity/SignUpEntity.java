package com.gather.android.entity;

import java.util.List;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by Levi on 2015/9/29.
 */
public class SignUpEntity {
    private String id;
    private String activity_id;
    private String name;
    private String mobile;
    private String remark;
    private String applicant_time;
    private int enroll_fee;//单位分
    private int status;
    private List<AttrsEntity> attrs;

    public String getEnrollFeeStr(){
        if (enroll_fee > 0) {
            double yuan = (double) enroll_fee / 100d;
            return yuan + "元";
        }
        else {
            return "";
        }
    }

    public int getEnroll_fee() {
        return enroll_fee;
    }

    public void setEnroll_fee(int enroll_fee) {
        this.enroll_fee = enroll_fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApplicant_time() {
        return applicant_time;
    }

    public void setApplicant_time(String applicant_time) {
        this.applicant_time = applicant_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<AttrsEntity> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AttrsEntity> attrs) {
        this.attrs = attrs;
    }

}
