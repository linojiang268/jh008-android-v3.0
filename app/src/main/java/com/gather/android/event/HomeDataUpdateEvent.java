package com.gather.android.event;

/**
 * 提醒首页刷新数据
 * Created by Levi on 2015/9/21.
 */
public class HomeDataUpdateEvent {

    private int type = 0;//0刷新活动; 1刷新社团; 2刷新活动社团

    public HomeDataUpdateEvent(int t){
        if (t == 0){
            type = 0;
        }
        else {
            type = 1;
        }
    }

    public int getType() {
        return type;
    }
}
