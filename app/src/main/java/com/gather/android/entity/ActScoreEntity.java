package com.gather.android.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/3.
 */
public class ActScoreEntity implements Serializable{

    private String id;
    private String title;
    private String publish_time;
    private String begin_time;
    private String end_time;
    private String team_id;
    private int enrolled_num;
    private boolean enrolled_team;
    private double[] location;
    private TeamEntity team;

    public String getId() {
        return (id != null) ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return (title != null) ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublish_time() {
        return (publish_time != null) ? publish_time : "";
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getBegin_time() {
        return (begin_time != null) ? begin_time : "";
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return (end_time != null) ? end_time : "";
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTeam_id() {
        return (team_id != null) ? team_id : "";
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public int getEnrolled_num() {
        return enrolled_num;
    }

    public void setEnrolled_num(int enrolled_num) {
        this.enrolled_num = enrolled_num;
    }

    public boolean isEnrolled_team() {
        return enrolled_team;
    }

    public void setEnrolled_team(boolean enrolled_team) {
        this.enrolled_team = enrolled_team;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }
}
