package com.gather.android.entity;


import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by Levi on 2015/7/15.
 */
public class ActEntity implements Serializable{

    /**
     * cover_url : http://dev.image.jhla.com.cn/20150805/20150805194212160349.jpg
     * address : 四川省成都市武侯区高升桥南街3号附4号;甘肃省张掖市
     * sub_status : 4
     * city : {"name":"成都","id":1}
     * essence : 0
     * end_time : 2015-08-28 23:00:00
     * enroll_fee : 0.00
     * enrolled_team : false
     * begin_time : 2015-08-04 16:42:00
     * team_id : 1
     * team : {"logo_url":"http://dev.image.jhla.com.cn/20150804/20150804172004943062.png","name":"测试社团1","id":1,"introduction":"我们是一个开放、充满基情的爱国主义社团，已中华复兴为己任。"}
     * title : 测试活动一
     * enrolled_num : 0
     * publish_time : null
     * brief_address : 香年广场
     * location : ["30.640929","104.047645"]
     * id : 3
     * enroll_fee_type : 1
     */
    private String cover_url;
    private String address;
    private int sub_status;
    private CityEntity city;
    private int essence;
    private String end_time;
    private String enroll_fee;
    private boolean enrolled_team;
    private String begin_time;
    private String team_id;
    private TeamEntity team;
    private String title;
    private int enrolled_num;
    private String publish_time;
    private String brief_address;
    private double[] location;
    private String id;
    private int enroll_fee_type;
    private int applicant_status;

    public int getApplicant_status() {
        return applicant_status;
    }

    public void setApplicant_status(int applicant_status) {
        this.applicant_status = applicant_status;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSub_status(int sub_status) {
        this.sub_status = sub_status;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public void setEssence(int essence) {
        this.essence = essence;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setEnroll_fee(String enroll_fee) {
        this.enroll_fee = enroll_fee;
    }

    public void setEnrolled_team(boolean enrolled_team) {
        this.enrolled_team = enrolled_team;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEnrolled_num(int enrolled_num) {
        this.enrolled_num = enrolled_num;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public void setBrief_address(String brief_address) {
        this.brief_address = brief_address;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEnroll_fee_type(int enroll_fee_type) {
        this.enroll_fee_type = enroll_fee_type;
    }

    public String getCover_url() {
        return cover_url;
    }

    public String getAddress() {
        return address;
    }

    public int getSub_status() {
        return sub_status;
    }

    public CityEntity getCity() {
        return city;
    }

    public int getEssence() {
        return essence;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getEnroll_fee() {
        return enroll_fee;
    }

    public boolean isEnrolled_team() {
        return enrolled_team;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public String getTeam_id() {
        return (team_id != null) ? team_id : "";
    }

    public TeamEntity getTeam() {
        return team;
    }

    public String getTitle() {
        return title;
    }

    public int getEnrolled_num() {
        return enrolled_num;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public String getBrief_address() {
        return brief_address;
    }

    public double[] getLocation() {
        return location;
    }

    public String getId() {
        return (id != null) ? id : "";
    }

    public int getEnroll_fee_type() {
        return enroll_fee_type;
    }

    public LatLng getLatLng(){
        return new LatLng(location[0], location[1]);
    }

}
