package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 15/10/1.
 */
public class SignUpListEntity {
    private int total;
    private List<SignUpEntity> applicants;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SignUpEntity> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<SignUpEntity> applicants) {
        this.applicants = applicants;
    }
}
