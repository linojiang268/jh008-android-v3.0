package com.gather.android.entity;

import com.baidu.mapapi.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 团长活动管理
 * Created by Levi on 2015/9/29.
 */
public class ActMgrEntity {

    private String id;
    private String title;
    private String publish_time;
    private String end_time;
    private String begin_time;
    private String team_id;
    private int sub_status;
    private String qr_code_url;
    private String cover_url;
    private String address;
    private String brief_address;
    private int enroll_fee_type;
    private String enroll_fee;
    private int essence;
    private double[] location;
    private int status;
    private boolean enrolled_team;
    private int enrolled_num;
    private int auditing;

    public void rebuildTime(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(getBegin_time());
            SimpleDateFormat timedf = new SimpleDateFormat("MM月dd日  HH:mm");
            begin_time = timedf.format(date);
        }
        catch (Exception e){

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public int getSub_status() {
        return sub_status;
    }

    public void setSub_status(int sub_status) {
        this.sub_status = sub_status;
    }

    public String getQr_code_url() {
        return qr_code_url;
    }

    public void setQr_code_url(String qr_code_url) {
        this.qr_code_url = qr_code_url;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrief_address() {
        return brief_address;
    }

    public void setBrief_address(String brief_address) {
        this.brief_address = brief_address;
    }

    public int getEnroll_fee_type() {
        return enroll_fee_type;
    }

    public void setEnroll_fee_type(int enroll_fee_type) {
        this.enroll_fee_type = enroll_fee_type;
    }

    public String getEnroll_fee() {
        return enroll_fee;
    }

    public void setEnroll_fee(String enroll_fee) {
        this.enroll_fee = enroll_fee;
    }

    public int getEssence() {
        return essence;
    }

    public void setEssence(int essence) {
        this.essence = essence;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isEnrolled_team() {
        return enrolled_team;
    }

    public void setEnrolled_team(boolean enrolled_team) {
        this.enrolled_team = enrolled_team;
    }

    public int getEnrolled_num() {
        return enrolled_num;
    }

    public void setEnrolled_num(int enrolled_num) {
        this.enrolled_num = enrolled_num;
    }

    public int getAuditing() {
        return auditing;
    }

    public void setAuditing(int auditing) {
        this.auditing = auditing;
    }

    public LatLng getLatLng(){
        return new LatLng(location[0], location[1]);
    }
}
