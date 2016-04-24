package com.shizhefei;

/**
 * Created by Administrator on 2015/7/13.
 */
public class HttpResponseStatus {

    private boolean success;
    private String result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return (result != null) ? result : "";
    }

    public void setResult(String result) {
        this.result = result;
    }
}
