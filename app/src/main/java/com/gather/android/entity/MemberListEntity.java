package com.gather.android.entity;


import java.util.List;

/**
 * Created by Levi on 2015/8/7.
 */
public class MemberListEntity {


    /**
     * code : 0
     * members : [{"role":0,"lng":104.121201,"avatar_url":"http://dev.image.jhla.com.cn/default/activity1.png","user_id":1,"name":"zhangsan","lat":30.123201}]
     */
    private int code;
    private List<MemberEntity> members;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMembers(List<MemberEntity> members) {
        this.members = members;
    }

    public int getCode() {
        return code;
    }

    public List<MemberEntity> getMembers() {
        return members;
    }
}
