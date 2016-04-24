package com.gather.android.entity;

/**
 * 首页轮播图数据
 * Created by Administrator on 2015/9/18.
 */
public class HomeBannerEntity {

    private String id;
    private String image_url;
    private String type;
    private String attributes;

    public String getId() {
        return (id != null) ? id : "";
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

    public String getType() {
        return (type != null) ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttributes() {
        return (attributes != null) ? attributes : "";
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}
