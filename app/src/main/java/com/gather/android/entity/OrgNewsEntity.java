package com.gather.android.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/29.
 */
public class OrgNewsEntity implements Serializable {

    private int id;
    private String title;
    private String cover_url;
    private String publish_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return (title != null) ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover_url() {
        return (cover_url != null) ? cover_url : "";
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getPublish_time() {
        return (publish_time != null) ? publish_time : "";
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }
}
