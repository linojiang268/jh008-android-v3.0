package com.gather.android.entity;

import java.io.Serializable;

/** 活动报名成功后返回的信息
 * Created by Administrator on 2015/8/4.
 */
public class ActEnrollOrderEntity implements Serializable{

//    "name": "张三", 		//报名填写的姓名
//            "mobile": "13500135000",	//报名填写的手机号
//            "attrs": "[{\"key\":\"\\u8eab\\u9ad8\",\"value\":172},{\"key\":\"\\u4f53\\u91cd\",\"value\":70}]", 					//报名填写的其他信息
//            "activity_id": 1,		//活动ID
//            "user_id": 1,			//用户ID
//            "status": 1,			//报名状态
//            "order_no": "JH2015080414223555C05A2BB0FBD811", 	//订单号
//            "updated_at": "2015-08-04 14:22:35",				//更新时间
//            "created_at": "2015-08-04 14:22:35",				//创建时间
//            "id": 4								//报名ID
//            "fee": "10.00",		//金额
//            "activity_title": "第一次活动"	//活动标题

    private String name;
    private String mobile;
    private String activity_id;
    private String user_id;
    private int status;
    private String order_no;
    private int id;
    private double fee;
    private String activity_title;


    public String getName() {
        return (name != null) ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return (mobile != null) ? mobile : "";
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getActivity_id() {
        return (activity_id != null) ? activity_id : "";
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getUser_id() {
        return (user_id != null) ? user_id : "";
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrder_no() {
        return (order_no != null) ? order_no : "";
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getActivity_title() {
        return (activity_title != null) ? activity_title : "";
    }

    public void setActivity_title(String activity_title) {
        this.activity_title = activity_title;
    }
}
