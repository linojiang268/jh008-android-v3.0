package com.gather.android.baseclass;


import com.gather.android.Constant;
import com.gather.android.http.Pair;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by Christain on 2015/5/15.
 */
public class BaseParams {

    private String url;
    private boolean hasFile = false;
    private TreeMap<String, Object> params;

    public BaseParams(String url) {
        this.params = new TreeMap<String, Object>();
        setUrl(url);
    }

    private void setUrl(String url) {
        if (url != null) {
            this.url = url;
        } else {
            this.url =  "";
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

    public void put(String key, boolean value) {
        this.params.put(key, value ? "1" : "0");
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

    public void put(String key, Pair value) {
        if (key != null && !key.equals("") && value != null) {
            if (((File) value.getSecond()).exists()) {
                this.params.put(key, value);
                this.hasFile = true;
            }
        }
    }

    public boolean hasParameters() {
        return !params.isEmpty();
    }

    public String getUrl() {
        if (url.startsWith("http")) {
            return url;
        } else {
            if (Constant.SHOW_LOG) {
                return Constant.DEFOULT_TEST_REQUEST_URL + url;
            } else {
                return Constant.DEFOULT_REQUEST_URL + url;
            }
        }
    }

    public boolean hasFile() {
        return hasFile;
    }

    public TreeMap<String, Object> getParameters() {
        return params;
    }
}
