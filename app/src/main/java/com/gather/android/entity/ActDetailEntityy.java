package com.gather.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Levi on 2015/9/21.
 */
public class ActDetailEntityy implements Serializable{
    /**
     * city : {"name":"成都","id":1}
     * essence : 0
     * enroll_fee : 79.00
     * roadmap : null
     * auditing : 0
     * team_id : 4
     * title : 黑暗之旅
     * enroll_attrs : ["手机号","姓名","性别","年龄"]
     * activity_check_in_list : [{"step":1,"status":0}]
     * update_step : 5
     * activity_album_count : 8
     * publish_time : 2015-09-09 10:21:17
     * contact : Tracy
     * enroll_end_time : 2015-09-12 00:00:00
     * brief_address : 牛王庙地铁口
     * activity_members_count : 1
     * id : 15
     * timeout_seconds : 0
     * cover_url : http://file.jh008.com/20150909/20150909101336916830.jpg
     * address : 四川省成都市锦江区宏济路65锦江区宏济巷71号
     * sub_status : 5
     * end_time : 2015-09-12 15:30:00
     * enrolled_team : false
     * begin_time : 2015-09-12 14:00:00
     * telephone : 18683626551
     * team : {"logo_url":"http://file.jh008.com/20150909/20150909100502294810.jpg","name":"转角遇到爱","id":4,"introduction":"成都同城单身活动"}
     * applicant_status : 0
     * will_payment_timeout : true
     * qr_code_url : http://image.jh008.com/20150909/20150909102117915738.png
     * enrolled_num : 1
     * enroll_begin_time : 2015-09-09 10:00:00
     * enroll_type : 1
     * organizers : ["转角遇到爱"]
     * images_url : ["http://file.jh008.com/20150909/20150909101336096283.jpg","http://file.jh008.com/20150909/20150909101336952648.jpg","http://file.jh008.com/20150909/20150909101336217486.jpg","http://file.jh008.com/20150909/20150909101336716354.jpg"]
     * enroll_limit : 12
     * location : ["30.647698","104.103201"]
     * activity_plans : [{"plan_text":"签到","end_time":"2015-09-12 14:10:00","begin_time":"2015-09-12 14:00:00","id":16},{"plan_text":"活动过程","end_time":"2015-09-12 17:00:00","begin_time":"2015-09-12 14:10:00","id":17},{"plan_text":"自由交流","end_time":"2015-09-12 17:30:00","begin_time":"2015-09-12 17:00:00","id":18},{"plan_text":"活动结束","end_time":"2015-09-30 17:00:00","begin_time":"2015-09-12 17:30:00","id":19}]
     * detail : <p>成都同城单身活动</p><p>摆脱视觉带来的偏见，到黑暗中感受自己、感受他人发来的讯息和日常的不同</p><p>结识新朋友，交流认识不同人背后的不同生活</p><p>品尝进口矿泉水冲出的美味咖啡</p><p>邂逅你的那个对的人</p>
     * activity_file_count : 0
     * enroll_fee_type : 3
     * status : 1
     */
    private CityEntity city;
    private int essence;
    private String enroll_fee;
    private List<double[]> roadmap;
    private int auditing;//0不审核
    private String team_id;
    private String title;
    private List<String> enroll_attrs;
    private List<ActCheckInEntity> activity_check_in_list;
    private int update_step;
    private int activity_album_count;
    private String publish_time;
    private String contact;
    private String enroll_end_time;
    private String brief_address;
    private int activity_members_count;
    private String id;
    private int timeout_seconds;
    private String cover_url;
    private String address;
    private int sub_status;
    private String end_time;
    private boolean enrolled_team;
    private String begin_time;
    private String telephone;
    private TeamEntity team;
    private int applicant_status;
    private boolean will_payment_timeout;
    private String qr_code_url;
    private int enrolled_num;
    private String enroll_begin_time;
    private int enroll_type;
    private List<String> organizers;
    private List<String> images_url;
    private int enroll_limit;
    private double[] location;
    private List<ActPlansEntity> activity_plans;
    private String detail;
    private int activity_file_count;
    private int enroll_fee_type;
    private int status;

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public void setEssence(int essence) {
        this.essence = essence;
    }

    public void setEnroll_fee(String enroll_fee) {
        this.enroll_fee = enroll_fee;
    }

    public void setRoadmap(List<double[]> roadmap) {
        this.roadmap = roadmap;
    }

    public void setAuditing(int auditing) {
        this.auditing = auditing;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEnroll_attrs(List<String> enroll_attrs) {
        this.enroll_attrs = enroll_attrs;
    }

    public void setActivity_check_in_list(List<ActCheckInEntity> activity_check_in_list) {
        this.activity_check_in_list = activity_check_in_list;
    }

    public void setUpdate_step(int update_step) {
        this.update_step = update_step;
    }

    public void setActivity_album_count(int activity_album_count) {
        this.activity_album_count = activity_album_count;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEnroll_end_time(String enroll_end_time) {
        this.enroll_end_time = enroll_end_time;
    }

    public void setBrief_address(String brief_address) {
        this.brief_address = brief_address;
    }

    public void setActivity_members_count(int activity_members_count) {
        this.activity_members_count = activity_members_count;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeout_seconds(int timeout_seconds) {
        this.timeout_seconds = timeout_seconds;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSub_status(int sub_status) {
        this.sub_status = sub_status;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setEnrolled_team(boolean enrolled_team) {
        this.enrolled_team = enrolled_team;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public void setApplicant_status(int applicant_status) {
        this.applicant_status = applicant_status;
    }

    public void setWill_payment_timeout(boolean will_payment_timeout) {
        this.will_payment_timeout = will_payment_timeout;
    }

    public void setQr_code_url(String qr_code_url) {
        this.qr_code_url = qr_code_url;
    }

    public void setEnrolled_num(int enrolled_num) {
        this.enrolled_num = enrolled_num;
    }

    public void setEnroll_begin_time(String enroll_begin_time) {
        this.enroll_begin_time = enroll_begin_time;
    }

    public void setEnroll_type(int enroll_type) {
        this.enroll_type = enroll_type;
    }

    public void setOrganizers(List<String> organizers) {
        this.organizers = organizers;
    }

    public void setImages_url(List<String> images_url) {
        this.images_url = images_url;
    }

    public void setEnroll_limit(int enroll_limit) {
        this.enroll_limit = enroll_limit;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public void setActivity_plans(List<ActPlansEntity> activity_plans) {
        this.activity_plans = activity_plans;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setActivity_file_count(int activity_file_count) {
        this.activity_file_count = activity_file_count;
    }

    public void setEnroll_fee_type(int enroll_fee_type) {
        this.enroll_fee_type = enroll_fee_type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public CityEntity getCity() {
        return city;
    }

    public int getEssence() {
        return essence;
    }

    public String getEnroll_fee() {
        return enroll_fee;
    }

    public List<double[]> getRoadmap() {
        return roadmap;
    }

    public int getAuditing() {
        return auditing;
    }

    public String getTeam_id() {
        return team_id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getEnroll_attrs() {
        return enroll_attrs;
    }

    public List<ActCheckInEntity> getActivity_check_in_list() {
        return activity_check_in_list;
    }

    public int getUpdate_step() {
        return update_step;
    }

    public int getActivity_album_count() {
        return activity_album_count;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public String getContact() {
        return contact;
    }

    public String getEnroll_end_time() {
        return enroll_end_time;
    }

    public String getBrief_address() {
        return brief_address;
    }

    public int getActivity_members_count() {
        return activity_members_count;
    }

    public String getId() {
        return id;
    }

    public int getTimeout_seconds() {
        return timeout_seconds;
    }

    public String getCover_url() {
        return cover_url;
    }

    public String getAddress() {
        return address;
    }

    public int getSub_status() {
        return sub_status;
    }

    public String getEnd_time() {
        return end_time;
    }

    public boolean isEnrolled_team() {
        return enrolled_team;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public String getTelephone() {
        return telephone;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public int getApplicant_status() {
        return applicant_status;
    }

    public boolean isWill_payment_timeout() {
        return will_payment_timeout;
    }

    public String getQr_code_url() {
        return qr_code_url;
    }

    public int getEnrolled_num() {
        return enrolled_num;
    }

    public String getEnroll_begin_time() {
        return enroll_begin_time;
    }

    public int getEnroll_type() {
        return enroll_type;
    }

    public List<String> getOrganizers() {
        return organizers;
    }

    public List<String> getImages_url() {
        return images_url;
    }

    public int getEnroll_limit() {
        return enroll_limit;
    }

    public double[] getLocation() {
        return location;
    }

    public List<ActPlansEntity> getActivity_plans() {
        return activity_plans;
    }

    public String getDetail() {
        return detail;
    }

    public int getActivity_file_count() {
        return activity_file_count;
    }

    public int getEnroll_fee_type() {
        return enroll_fee_type;
    }

    public int getStatus() {
        return status;
    }

    public ActFlowEntity getFlowEntity(){
        ActFlowEntity entity = new ActFlowEntity();
        entity.setActivity_plans(activity_plans);
        entity.setId(id);
        entity.setOrganizers(organizers);
        entity.setTeam(team);
        return entity;
    }
}
