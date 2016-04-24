package com.gather.android.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Levi on 2015/7/15.
 */
public class ResetPwdTokenEntity {
    private int code = -1;
    @JSONField(name = "_token")
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
