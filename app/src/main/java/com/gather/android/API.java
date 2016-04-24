package com.gather.android;

/**
 * Created by Levi on 2015/7/9.
 */
public class API {
    /**
     * 注册发送手机验证码
     */
    public static final String VERIFYCODE_REGISTER = "/api/register/verifycode";
    /**
     * 重置密码发送手机验证码
     */
    public static final String VERIFYCODE_RESET_PWD = "/api/password/reset/verifycode";

    /**
     * 登录
     */
    public static final String LOGIN = "api/login";

    /**
     * 注销
     */
    public static final String LOGIN_OUT = "api/logout";

    /**
     * 注册
     */
    public static final String REGISTER = "api/register";

    /**
     * 完善资料
     */
    public static final String FIX_PROFILE = "api/profile/complete";

    /**
     * 修改资料
     */
    public static final String MODIFY_PROFILE = "api/user/profile";

    /**
     * 修改密码
     */
    public static final String MODIFY_PASSWORD = "/api/password/change";

    /**
     * 获取资料
     */
    public static final String GET_PROFILE = "api/user/profile";

    /**
     * 获取城市列表
     */
    public static final String GET_CITY_LIST = "api/city/list";

    /**
     * 切换卡社团列表
     */
    public static final String GET_TAB_HOST_ORG_LIST = "api/team/list";

    /**
     * 切换卡社团列表
     */
    public static final String GET_MY_ACT_LIST = "/api/activity/my";

    /**
     * 社团详情
     */
    public static final String GET_ORG_DETAIL = "api/team/info";

    /**
     * 获取重设密码需要的token
     */
    public static final String GET_RESET_PWD_TOKEN = "api/password/reset";

    /**
     * 重设密码
     */
    public static final String RESET_PWD = "api/password/reset";

    /**
     * 完善资料
     */
    public static final String PERFECT_PROFILE = "api/user/profile/complete";

    /**
     * 获取城市内的活动
     */
    public static final String GET_ACT_OF_CITY = "api/activity/city/list";

    /**
     * 我的相册列表
     */
    public static final String MY_SHARE_PHOTO = "api/activity/album/image/self/list";

    /**
     * 删除用户自己上传相册图片
     */
    public static final String DEL_MY_SHARE_PHOTO = "api/activity/album/image/self/remove";

    /**
     * 获取城市内的活动
     */
    public static final String SEARCH_ACT_OF_CITY = "api/activity/search/name";

    /**
     * 获取中心点周围的活动
     */
    public static final String GET_NEARBY_ACT = "api/activity/search/point";

    /**
     * 退出社团成员
     */
    public static final String EXIT_ORG = "api/team/member/quit";

    /**
     * 修改社团成员隐私设置
     */
    public static final String CHANGE_ORG_SECRET_SET = "api/team/member/update";

    /**
     * 申请加入社团
     */
    public static final String APPLY_JOIN_ORG = "api/team/member/enroll";

    /**
     * 社团成员列表
     */
    public static final String ORG_MEMBER_LIST = "api/team/member/list";

    /**
     * 社团活动相册列表
     */
    public static final String ORG_ACT_ALBUM_LIST = "api/activity/hasalbum/list";

    /**
     * 活动相册列表
     */
    public static final String ACT_ALBUM = "api/activity/album/image/list";

    /**
     * 社团资讯（往期回顾）
     */
    public static final String ORG_NEWS_LIST = "api/news";

    /**
     * 地图活动分组成员
     */
    public static final String ACT_MAP_USER = "api/activity/member/location";

    /**
     * 活动评分
     */
    public static final String ACT_SCORE = "api/activity/member/score";

    /**
     * 获取活动评分（检查是否需要评分）
     */
    public static final String GET_ACT_SCORE = "api/activity/no/score";

    /**
     * 活动详情
     */
    public static final String GET_ACT_DETAIL = "api/activity/detail";

    /**
     * 活动文件
     */
    public static final String GET_ACT_FILE_LIST = "/api/activity/file/list";

    /**
     * 添加上传相册图片
     */
    public static final String UPLOAD_ACT_PHOTO = "api/activity/album/image/add";

    /**
     * 提交活动报名信息
     */
    public static final String POST_ACT_ENROLL_INFO = "api/activity/applicant/applicant";

    /**
     * 活动支付费用下订单
     */
    public static final String GET_ACT_ENROLL_ORDER_INFO = "api/activity/applicant/payment/info";

    /**
     * 社团近期活动列表
     */
    public static final String GET_ORG_ACT_LIST = "api/activity/team/list";

    /**
     * 获取消息列表
     */
    public static final String GET_MESSAGE_LIST = "api/message/list";

    /**
     * 活动二维码签到
     */
    public static final String ACT_QRCODE_CHECKIN = "api/activity/checkIn/checkIn";

    /**
     * 获取签到列表
     */
    public static final String GET_ACT_QRCODE_LIST = "api/activity/checkIn/list";

    /**
     * 活动成员列表
     */
    public static final String GET_ACT_MEMBER_LIST = "api/activity/member/list";

    /**
     * 获取周围活动成员列表
     */
    public static final String GET_NEAR_ACT_MEMBERS = "api/activity/member/location";

    /**
     * 微信支付
     */
    public static final String WECHAT_PAY = "api/payment/wxpay/app/prepay";

    /**
     * 支付宝支付
     */
    public static final String ALIPAY = "api/payment/alipay/app/prepay";

    /**
     * 获取周围活动统计
     */
    public static final String GET_ACT_COUNT = "/api/activity/my/count";

    /**
     * 获取我的社团
     */
    public static final String GET_MY_ASSN = "/api/team/relate/list";

    /**
     * Push订阅的频道
     */
    public static final String SUBSCRIBE_TOPIC = "api/activity/my/topics";

    /**
     * 活动手册
     */
    public static final String ACT_NOTE_BOOK = "api/activity/checkin/info";

    /**
     * 绑定推送别名成功后，通知服务器
     */
    public static final String NOTE_SERVICE_ALIAS_SUCCESS = "api/alias/bound";

    /**
     * 支付倒计时
     */
    public static final String PAY_TIME_OUT = "api/activity/pay/timeout/seconds";

    /**
     * 首页Banner
     */
    public static final String HOME_BANNER = "api/banner/list";

    /**
     * 首页我的活动
     */
    public static final String HOME_MY_ACT = "api/activity/home/my";

    /**
     * 首页我的社团列表
     */
    public static final String HOME_MY_ORG_LIST = "api/team/self/list";

    //************管理********************************************************************
    /**
     * 首页我的社团列表
     */
    public static final String SIGNUP_MGR_LIST = "api/manage/act/applicant/list";
    /**
     * 社团活动列表
     */
    public static final String ACT_MGR_LIST = "api/manage/act/list";

    /**
     * 活动审核通过
     */
    public static final String ACT_PENDING_PASS = "api/manage/act/applicant/approve";

    /**
     *活动审核拒绝
     */
    public static final String ACT_PENDING_REFUSE = "api/manage/act/applicant/refuse";

    /**
     * 报名成功添加备注
     */
    public static final String ADD_REMARKS = "api/manage/act/applicant/remark";

    /**
     * 签到列表
     */
    public static final String SIGNIN_MGR_LIST = "api/manage/act/checkin/list";

    /**
     * 剩余通知次数
     */
    public static final String REST_MESSAGE_TIMES = "api/manage/act/notice/send/times";

    /**
     * 发送通知
     */
    public static final String SEND_MESSAGE = "api/manage/act/notice/send";

    /**
     * 获取报名成功的人的手机号
     */
    public static final String GET_SUCCESS_MOBILES = "/api/manage/act/member/mobile";
}
