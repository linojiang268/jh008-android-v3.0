package com.gather.android.entity;

import java.io.File;
import java.io.Serializable;

/**
 * 用于注册或修改资料
 * Created by Levi on 2015/7/10.
 */
public class RegisterInfoEntity implements Serializable {
    //1 - male; 2 - famale
    private int gender = -1;
    private String birthday;
    private File avatarFile;
    private String nickName;
    private String[] tagIds;
    private String mobile;
    private String password;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(File avatarFile) {
        this.avatarFile = avatarFile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String[] getTagIds() {
        return tagIds;
    }

    public void setTagIds(String[] tagIds) {
        this.tagIds = tagIds;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
