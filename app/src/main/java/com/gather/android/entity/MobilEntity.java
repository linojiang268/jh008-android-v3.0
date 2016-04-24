package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 15/10/12.
 */
public class MobilEntity {

    /**
     * code : 0
     * mobiles : ["15828695160"]
     */

    private int code;
    private List<String> mobiles;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
    }

    public int getCode() {
        return code;
    }

    public List<String> getMobiles() {
        return mobiles;
    }
}
