package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 2015/8/11.
 */
public class MyAssnListEntity {

    /**
     * code : 0
     * recommended_teams : []
     * enrolled_teams : [{"member_num":4,"logo_url":"http://dev.image.jhla.com.cn/20150804/20150804172004943062.png","joined":true,"name":"测试社团1","creator_id":2,"id":1,"activity_num":7,"qr_code_url":"http://dev.image.jhla.com.cn/20150805/20150805193157962301.png","introduction":"我们是一个开放、充满基情的爱国主义社团，以中华复兴为己任。","certification":0}]
     * requested_teams : []
     */
    private List<MyAssnEntity> recommended_teams;
    private List<MyAssnEntity> enrolled_teams;
    private List<MyAssnEntity> requested_teams;
    private List<MyAssnEntity> invited_teams;

    public List<MyAssnEntity> getInvited_teams() {
        return invited_teams;
    }

    public void setInvited_teams(List<MyAssnEntity> invited_teams) {
        this.invited_teams = invited_teams;
    }

    public void setRecommended_teams(List<MyAssnEntity> recommended_teams) {
        this.recommended_teams = recommended_teams;
    }

    public void setEnrolled_teams(List<MyAssnEntity> enrolled_teams) {
        this.enrolled_teams = enrolled_teams;
    }

    public void setRequested_teams(List<MyAssnEntity> requested_teams) {
        this.requested_teams = requested_teams;
    }

    public List<MyAssnEntity> getRecommended_teams() {
        return recommended_teams;
    }

    public List<MyAssnEntity> getEnrolled_teams() {
        return enrolled_teams;
    }

    public List<MyAssnEntity> getRequested_teams() {
        return requested_teams;
    }

    public boolean isEmpty(){
        return (enrolled_teams == null || enrolled_teams.size() == 0) &&
                (requested_teams == null || requested_teams.size() == 0) &&
                (recommended_teams == null || recommended_teams.size() == 0);
    }
}
