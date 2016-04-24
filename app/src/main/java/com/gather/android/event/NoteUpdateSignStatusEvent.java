package com.gather.android.event;

import com.gather.android.entity.ActCheckInListEntity;

/**
 * 活动手册更新扫码信息
 * Created by Administrator on 2015/8/26.
 */
public class NoteUpdateSignStatusEvent {

    private ActCheckInListEntity entity;

    public NoteUpdateSignStatusEvent(ActCheckInListEntity entity) {
        this.entity = entity;
    }

    public ActCheckInListEntity getEntity() {
        return entity;
    }

    public void setEntity(ActCheckInListEntity entity) {
        this.entity = entity;
    }
}
