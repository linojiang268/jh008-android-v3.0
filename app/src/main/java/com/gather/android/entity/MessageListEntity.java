package com.gather.android.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/28.
 */
public class MessageListEntity {

    private int total_num;
    private String last_requested_time;
    private ArrayList<MessageEntity> messages;

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public String getLast_requested_time() {
        return (last_requested_time != null) ? last_requested_time : "";
    }

    public void setLast_requested_time(String last_requested_time) {
        this.last_requested_time = last_requested_time;
    }

    public ArrayList<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageEntity> messages) {
        this.messages = messages;
    }
}
