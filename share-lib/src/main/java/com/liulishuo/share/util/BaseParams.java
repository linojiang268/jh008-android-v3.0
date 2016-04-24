package com.liulishuo.share.util;


import java.util.HashMap;

/**
 * Created by Christain on 2015/5/15.
 */
public class BaseParams {

    private String url;
    private HashMap<String, Object> params;

    public BaseParams(String url) {
        this.params = new HashMap<String, Object>();
        setUrl(url);
    }

    private void setUrl(String url) {
        if (url != null) {
            this.url = url;
        } else {
            this.url = "";
        }
    }

    protected void initParameters() {
        this.params.clear();
    }

    public void put(String key, String value) {
        if (key != null && !key.equals("") && value != null) {
            this.params.put(key, value);
        }
    }

    public void put(String key, int value) {
        if (key != null && !key.equals("")) {
            this.params.put(key, String.valueOf(value));
        }
    }

    public void put(String key, long value) {
        if (key != null && !key.equals("")) {
            this.params.put(key, String.valueOf(value));
        }
    }

    public void put(String key, float value) {
        if (key != null && !key.equals("")) {
            this.params.put(key, String.valueOf(value));
        }
    }

    public void put(String key, double value) {
        if (key != null && !key.equals("")) {
            this.params.put(key, String.valueOf(value));
        }
    }


    public boolean hasParameters() {
        return !params.isEmpty();
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, Object> getParameters() {
        return params;
    }
}
