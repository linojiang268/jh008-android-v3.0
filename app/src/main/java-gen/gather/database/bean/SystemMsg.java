package gather.database.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "SYSTEM_MSG".
 */
public class SystemMsg {

    private Long id;
    private String userId;
    private String content;
    private String type;
    private String attribute;
    private String createdAt;
    private Boolean readed;

    public SystemMsg() {
    }

    public SystemMsg(Long id) {
        this.id = id;
    }

    public SystemMsg(Long id, String userId, String content, String type, String attribute, String createdAt, Boolean readed) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.type = type;
        this.attribute = attribute;
        this.createdAt = createdAt;
        this.readed = readed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getReaded() {
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }

}