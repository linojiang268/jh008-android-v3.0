package com.gather.android.event;

/**
 * Created by Administrator on 2015/8/6.
 */
public class CheckMsgReadedEvent {

    private boolean hasNoRead;

    public CheckMsgReadedEvent(boolean hasNoRead) {
        this.hasNoRead = hasNoRead;
    }

    public boolean isHasNoRead() {
        return hasNoRead;
    }

    public void setHasNoRead(boolean hasNoRead) {
        this.hasNoRead = hasNoRead;
    }
}
