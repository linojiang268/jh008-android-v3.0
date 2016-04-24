package com.gather.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 活动流程
 * Created by Levi on 2015/9/21.
 */
public class ActFlowEntity implements Serializable{
    private String id;
    private List<String> organizers;
    private List<ActPlansEntity> activity_plans;
    private TeamEntity team;
    private List<ActCheckInEntity> activity_check_in_list;

    public List<ActCheckInEntity> getActivity_check_in_list() {
        return activity_check_in_list;
    }

    public void setActivity_check_in_list(List<ActCheckInEntity> activity_check_in_list) {
        this.activity_check_in_list = activity_check_in_list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(List<String> organizers) {
        this.organizers = organizers;
    }

    public List<ActPlansEntity> getActivity_plans() {
        return activity_plans;
    }

    public void setActivity_plans(List<ActPlansEntity> activity_plans) {
        this.activity_plans = activity_plans;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }
}
