package com.gather.android.event;

/**
 * 微信支付是否成功
 * Created by Administrator on 2015/8/10.
 */
public class WeChatPayEvent {

    private int success;

    public WeChatPayEvent(int success) {
        this.success = success;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
