package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 2015/8/11.
 */
public class MyAssnEntity {

    /**
     * member_num : 4
     * logo_url : http://dev.image.jhla.com.cn/20150804/20150804172004943062.png
     * joined : true
     * name : 测试社团1
     * creator_id : 2
     * id : 1
     * activity_num : 7
     * qr_code_url : http://dev.image.jhla.com.cn/20150805/20150805193157962301.png
     * introduction : 我们是一个开放、充满基情的爱国主义社团，以中华复兴为己任。
     * certification : 0
     */
    private int member_num;
    private String logo_url;
    private boolean joined;
    private String name;
    private String creator_id;
    private String id;
    private int activity_num;
    private String qr_code_url;
    private String introduction;
    private int certification;

    private boolean isFirst;
    private boolean isLast;
    private int groupType;

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActivity_num(int activity_num) {
        this.activity_num = activity_num;
    }

    public void setQr_code_url(String qr_code_url) {
        this.qr_code_url = qr_code_url;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }

    public int getMember_num() {
        return member_num;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public boolean isJoined() {
        return joined;
    }

    public String getName() {
        return name;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public String getId() {
        return id;
    }

    public int getActivity_num() {
        return activity_num;
    }

    public String getQr_code_url() {
        return qr_code_url;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getCertification() {
        return certification;
    }
}
