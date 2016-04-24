package com.gather.android.entity;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Levi on 2015/8/7.
 */
public class MemberEntity {

    /**
     * role : 0
     * lng : 104.121201
     * avatar_url : http://dev.image.jhla.com.cn/default/activity1.png
     * user_id : 1
     * name : zhangsan
     * lat : 30.123201
     */
    private int role;
    private double lng;
    private String avatar_url;
    private String user_id;
    private String name;
    private double lat;

    public LatLng getLatLng(){
        return  new LatLng(lat, lng);
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getRole() {
        return role;
    }

    public double getLng() {
        return lng;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }
}
