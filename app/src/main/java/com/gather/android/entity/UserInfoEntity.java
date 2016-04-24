package com.gather.android.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.gather.android.dialog.DatePickDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户资料
 * Created by Levi on 2015/7/10.
 */
public class UserInfoEntity implements Serializable{

    private String birthday;
    @JSONField(name = "tag_ids")
    private List<Integer> intrestIds;
    //1 - male; 2 - famale
    private int gender;
    @JSONField(name = "avatar_url")
    private String avatarUrl;
    @JSONField(name = "user_id")
    private String uid;
    @JSONField(name = "nick_name")
    private String nickName;
    private String mobile;
    @JSONField(name = "is_team_owner")
    private boolean isColonel;
    @JSONField(name = "push_alias")
    private String pushAlias;//push唯一标识

    public String getBirthday() {
        return (birthday != null) ? birthday : "";
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public List<Integer> getIntrestIds() {
        return intrestIds;
    }

    public void setIntrestIds(List<Integer> intrestIds) {
        this.intrestIds = intrestIds;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return (avatarUrl != null) ? avatarUrl : "";
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isColonel() {
        return isColonel;
    }

    public void setIsColonel(boolean isColonel) {
        this.isColonel = isColonel;
    }

    public int getAge(){
        int[] dateInt = DatePickDialog.getDate(birthday);
        if (dateInt != null){
            return DatePickDialog.getAgeByBirthday(dateInt[0], dateInt[1], dateInt[2]);
        }
        return 0;
    }

    public ArrayList<String> getTagIdsForArray(){
        ArrayList<String> list = new ArrayList<>();
        if (intrestIds != null && intrestIds.size() > 0){
            for (int i=0;i<intrestIds.size();i++){
                list.add(String.valueOf(intrestIds.get(i).intValue()));
            }
        }
        return list;
    }

    public String getPush_alias() {
        return (pushAlias != null) ? pushAlias : "";
    }

    public void setPush_alias(String push_alias) {
        this.pushAlias = push_alias;
    }
}
