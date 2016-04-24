package com.gather.android;


import com.gather.android.manager.PhoneManager;

public final class Constant {

	public static final boolean SHOW_LOG = BuildConfig.DEBUG;// 显示log信息
    public static final String DEFOULT_TEST_REQUEST_URL = "http://staging.jhla.com.cn/";// 测试数据访问地址
	public static final String DEFOULT_REQUEST_URL = "http://app.jh008.com/";// 外网数据访问地址

	/**
	 * Bugtags SDK
	 */
	public static final String BUGTAGS_APPKEY = "ace0795e6bbdb7273465b9a5c79a1ffb";

	/**
	 * 集合啦
	 */
	public static final String WE_CHAT_APPID = "wxdaf869f9bd24b02f"; //微信ID
	public static final String TENCENT_APPID = "1103292660"; //QQ登录ID
	public static final String SINA_APPID = "2247106580";	//新浪登录ID

    /**
     * 微信支付
     */
    //  API密钥，在商户平台设置
    public static final  String WX_PAY_API_KEY="9bfe7c6249f79ab1012e726fd84d872b";

	public static final String TEMP_FILE_DIR_PATH = PhoneManager.getSdCardRootPath() + "/Gather/tempFile/";// 零时文件地址
	public static final String IMAGE_CACHE_DIR_PATH = PhoneManager.getSdCardRootPath() + "/Gather/cache/";// 图片缓存地址
	public static final String UPLOAD_FILES_DIR_PATH = PhoneManager.getSdCardRootPath() + "/Gather/upload/";
    public static final String DOWNLOAD_IMAGE_DIR_PATH = PhoneManager.getSdCardRootPath() + "/Gather/download/";
    public static final String EXCEPTION_LOG_DIR_PATH = PhoneManager.getSdCardRootPath() + "/Gather/log/";// 报告文件存放地址
	public static final String DOWNLOAD_DIR_PATH = PhoneManager.getSdCardRootPath() + "/Gather/download/";// 下载文件存放地址

	public static final int HEADPORTRAIT_SIZE  = 400; //头像大小

}