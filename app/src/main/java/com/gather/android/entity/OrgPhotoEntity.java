package com.gather.android.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoEntity implements Serializable{

    private String id;
    private String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return (image_url != null) ? image_url : "";
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
