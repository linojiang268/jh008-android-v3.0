package com.gather.android.entity;

/**
 * Created by Levi on 2015/8/25.
 */
public class VerifyCodeEntity {

    /**
     * send_interval : 60
     * code : 0
     * message : 发送成功
     */
    private int send_interval = 60;
    private int code;
    private String message;

    public void setSend_interval(int send_interval) {
        this.send_interval = send_interval;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSend_interval() {
        if (send_interval <= 0){
            return 60;
        }
        else {
            return send_interval;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
