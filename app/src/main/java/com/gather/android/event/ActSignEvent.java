package com.gather.android.event;

import com.gather.android.entity.ActCheckInListEntity;

/**
 * 活动滑动确认签到
 * Created by Administrator on 2015/8/26.
 */
public class ActSignEvent {

    private ActCheckInListEntity entity;

    public ActSignEvent(ActCheckInListEntity entity) {
        this.entity = entity;
    }

    public ActCheckInListEntity getEntity() {
        return entity;
    }

    public void setEntity(ActCheckInListEntity entity) {
        this.entity = entity;
    }
}
