package com.gather.android.entity;

import java.io.Serializable;

/**
 * 活动签到
 * Created by Administrator on 2015/8/5.
 */
public class ActCheckInEntity implements Serializable {

    private int step;
    private int status;//0：未签到， 1：已签到

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
