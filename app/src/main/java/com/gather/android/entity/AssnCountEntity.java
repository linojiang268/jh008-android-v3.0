package com.gather.android.entity;

/**
 * Created by Levi on 2015/8/10.
 */
public class AssnCountEntity {

    /**
     * recommended_teams : 0
     * enrolled_teams : 1
     * requested_teams : 0
     */
    private int recommended_teams;
    private int enrolled_teams;
    private int requested_teams;
    private int invited_teams;

    public void setRecommended_teams(int recommended_teams) {
        this.recommended_teams = recommended_teams;
    }

    public void setEnrolled_teams(int enrolled_teams) {
        this.enrolled_teams = enrolled_teams;
    }

    public void setRequested_teams(int requested_teams) {
        this.requested_teams = requested_teams;
    }

    public int getRecommended_teams() {
        return recommended_teams;
    }

    public int getEnrolled_teams() {
        return enrolled_teams;
    }

    public int getRequested_teams() {
        return requested_teams;
    }

    public int getInvited_teams() {
        return invited_teams;
    }

    public void setInvited_teams(int invited_teams) {
        this.invited_teams = invited_teams;
    }
}
