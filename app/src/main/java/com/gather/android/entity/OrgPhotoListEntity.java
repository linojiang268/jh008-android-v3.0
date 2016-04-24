package com.gather.android.entity;

/**
 * 社团相册
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoListEntity {

    private String id;
    private String title;
    private String publish_time;
    private String begin_time;
    private String end_time;
    private boolean added_activity;

    public String getId() {
        return (id != null) ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return (title != null) ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublish_time() {
        return (publish_time != null) ? publish_time : "";
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getBegin_time() {
        return (begin_time != null) ? begin_time : "";
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return (end_time != null) ? end_time : "";
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public boolean isAdded_activity() {
        return added_activity;
    }

    public void setAdded_activity(boolean added_activity) {
        this.added_activity = added_activity;
    }
}
