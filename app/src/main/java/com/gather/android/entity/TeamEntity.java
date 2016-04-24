package com.gather.android.entity;

import java.io.Serializable;

/**
 * Created by Levi on 2015/8/6.
 */
public class TeamEntity implements Serializable {
    /**
     * logo_url : http://dev.image.jhla.com.cn/20150804/20150804172004943062.png
     * name : 测试社团1
     * id : 1
     * introduction : 我们是一个开放、充满基情的爱国主义社团，已中华复兴为己任。
     */
    private String logo_url;
    private String name;
    private String id;
    private String introduction;

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public String getName() {
        return (name != null) ? name : "";
    }

    public String getId() {
        return (id != null) ? id : "";
    }

    public String getIntroduction() {
        return introduction;
    }
}
