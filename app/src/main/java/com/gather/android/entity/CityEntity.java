package com.gather.android.entity;


import java.io.Serializable;

/**
 * 城市列表
 * Created by Administrator on 2015/7/11.
 */
public class CityEntity implements Serializable {
    /**
     * name : 成都
     * id : 1
     */
    private String name;
    private int id;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return (name != null) ? name : "";
    }

    public int getId() {
        return id;
    }

}
