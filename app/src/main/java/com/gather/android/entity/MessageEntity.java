package com.gather.android.entity;

/**
 * Created by Administrator on 2015/7/28.
 */
public class MessageEntity {

    //     "id": 1,
//     “team_id”: 1,
//     “team_name”: “社团名称”,
//     “activity_id”: 1,
//     “content”: “消息内容”,
//     “type”: text,
//     “attribute”: “http://domain/webview.html” ,
//     “created_at”: “1980-12-09 00:00:00”

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_URL = "url";
    public static final String TYPE_TEAM = "team";
    public static final String TYPE_ACTIVITY = "activity";


    private long id;
    private String team_id;
    private String team_name;
    private String activity_id;
    private String content;
    private String type;
    private String attributes;
    private String created_at;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTeam_id() {
        return (team_id != null) ? team_id : "";
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getTeam_name() {
        return (team_name != null) ? team_name : "";
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getActivity_id() {
        return (activity_id != null) ? activity_id : "";
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getContent() {
        return (content != null) ? content : "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return (type != null) ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getCreated_at() {
        return (created_at != null) ? created_at : "";
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
