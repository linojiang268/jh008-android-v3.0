package com.gather.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/7/13.
 */
public class OrgListEntity implements Serializable {

    private List<OrgDetailEntity> teams;
    private int pages;

    public void setTeams(List<OrgDetailEntity> teams) {
        this.teams = teams;
    }

    public List<OrgDetailEntity> getTeams() {
        return teams;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

}
