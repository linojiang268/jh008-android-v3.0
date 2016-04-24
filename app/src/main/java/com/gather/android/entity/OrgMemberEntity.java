package com.gather.android.entity;

/**
 * 社团成员
 * Created by Administrator on 2015/7/23.
 */
public class OrgMemberEntity {

    private String id;
    private String name;
    private String avatar;
    private int gender;
    private String signature;
    private Group group;

    public String getId() {
        return (id != null) ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return (name != null) ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return (avatar != null) ? avatar : "";
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getSignature() {
        return (signature != null) ? signature : "";
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public class Group {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return (name != null) ? name : "";
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
