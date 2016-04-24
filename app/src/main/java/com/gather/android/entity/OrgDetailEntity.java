package com.gather.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/13.
 */
public class OrgDetailEntity implements Serializable {

    private String logo_url;
    private String name;
    private String id;
    private int activity_num;//该社团的活动数
    private String introduction;//社团简介
    private int member_num;//社团成员数
    private boolean joined;//是否已加入
    private String qr_code_url;//社团二维码的url
    private int certification; //社团认证：0未认证，1认证中，2已认证
    private boolean requested;//是否已提交加入申请
    private boolean in_whitelist;//是否在社团白名单
    private int visibility;//隐私设置：0所有人可见，1仅社团成员可见
    private int join_type;//社团加入条件设置：0任何人，1需审核
    private long activities_updated_at;//社团活动最后更新时间
    private long members_updated_at;//社团成员最后更新时间
    private long news_updated_at;//社团资讯最后更新时间
    private long albums_updated_at;//社团相册最后更新时间
    private long notices_updated_at;//社团通知最后更新时间
    private ArrayList<JoinRequirements> join_requirements;//社团加入条件数组
    private boolean in_blacklist;//是否在黑名单

    public OrgDetailEntity() {
        super();
    }


    /**
     * 如果joined为true，表示用户已加入社团，requested、in_whitelist不会出现，对应visibility必填；如果joined为false，表示用户未加入该社团，visibility不会出现，requested为必填：
     * - 如果requested为true，表示用户已提交社团加入申请，in_whitelist不会出现
     * - 如果requested为false，表示用户未提交社团加入申请，in_whitelist为必填。
     */

    public int getJoin_type() {
        return join_type;
    }

    public void setJoin_type(int join_type) {
        this.join_type = join_type;
    }

    public long getActivities_updated_at() {
        return activities_updated_at;
    }

    public void setActivities_updated_at(long activities_updated_at) {
        this.activities_updated_at = activities_updated_at;
    }

    public long getMembers_updated_at() {
        return members_updated_at;
    }

    public void setMembers_updated_at(long members_updated_at) {
        this.members_updated_at = members_updated_at;
    }

    public long getNews_updated_at() {
        return news_updated_at;
    }

    public void setNews_updated_at(long news_updated_at) {
        this.news_updated_at = news_updated_at;
    }

    public long getAlbums_updated_at() {
        return albums_updated_at;
    }

    public void setAlbums_updated_at(long albums_updated_at) {
        this.albums_updated_at = albums_updated_at;
    }

    public long getNotices_updated_at() {
        return notices_updated_at;
    }

    public void setNotices_updated_at(long notices_updated_at) {
        this.notices_updated_at = notices_updated_at;
    }

    public ArrayList<JoinRequirements> getJoin_requirements() {
        return (join_requirements != null ? join_requirements : new ArrayList<JoinRequirements>());
    }

    public void setJoin_requirements(ArrayList<JoinRequirements> join_requirements) {
        this.join_requirements = join_requirements;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActivity_num(int activity_num) {
        this.activity_num = activity_num;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLogo_url() {
        return (logo_url != null) ? logo_url : "";
    }

    public String getName() {
        return (name != null) ? name : "";
    }

    public String getId() {
        return id;
    }

    public int getActivity_num() {
        return activity_num;
    }

    public String getIntroduction() {
        return (introduction != null) ? introduction : "";
    }

    public int getMember_num() {
        return member_num;
    }

    public void setMember_num(int member_num) {
        this.member_num = member_num;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public int getCertification() {
        return certification;
    }

    public void setCertification(int certification) {
        this.certification = certification;
    }

    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public boolean isIn_whitelist() {
        return in_whitelist;
    }

    public void setIn_whitelist(boolean in_whitelist) {
        this.in_whitelist = in_whitelist;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getQr_code_url() {
        return (qr_code_url != null) ? qr_code_url : "";
    }

    public void setQr_code_url(String qr_code_url) {
        this.qr_code_url = qr_code_url;
    }

    public class JoinRequirements implements Serializable {

        private int id;//社团加入条件字段id
        private String requirement;//社团加入条件字段名称

        public JoinRequirements() {
            super();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRequirement() {
            return (requirement != null ? requirement : "");
        }

        public void setRequirement(String requirement) {
            this.requirement = requirement;
        }
    }

    public boolean isIn_blacklist() {
        return in_blacklist;
    }

    public void setIn_blacklist(boolean in_blacklist) {
        this.in_blacklist = in_blacklist;
    }
}