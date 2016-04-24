package com.gather.android.entity;

import java.util.ArrayList;

/**
 * 社团成员列表
 * Created by Administrator on 2015/7/23.
 */
public class OrgMemberListEntity {

    private int pages;
    private ArrayList<OrgMemberEntity> members;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public ArrayList<OrgMemberEntity> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<OrgMemberEntity> members) {
        this.members = members;
    }
}
