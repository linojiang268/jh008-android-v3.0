package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 15/10/1.
 */
public class SignInListEntity {
    private int total;
    private List<SignInEntity> checkins;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SignInEntity> getCheckins() {
        return checkins;
    }

    public void setCheckins(List<SignInEntity> checkins) {
        this.checkins = checkins;
    }
}
