package com.gather.android.entity;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by Levi on 2015/8/7.
 */
public class VenueModel implements Serializable{
    private String title;
    private String id;
    private String address;
    private double[] location;
    private String city;

    public VenueModel(ActDetailEntityy entity){
        title = entity.getTitle();
        id = String.valueOf(entity.getId());
        location = entity.getLocation();
        address = entity.getAddress();
        city = entity.getCity().getName();
    }

    public String getCity() {
        return city;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }


    public double[] getLocation() {
        return location;
    }

    public LatLng getLatLng(){
        return new LatLng(location[0], location[1]);
    }

}
