package com.gather.android.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.ActDetailEntityy;
import com.gather.android.event.ActEnrollTipsEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeDataUpdateEvent;
import com.gather.android.event.WeChatPayEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.Pair;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.MD5;
import com.gather.android.utils.PayResult;
import com.gather.android.widget.TitleBar;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.Subscribe;

/**
 * 支付信息
 * Created by Administrator on 2015/7/20.
 */
public class PayInfo extends BaseActivity {

    public static final String FEE = "FEE";
    public static final String ORDER = "ORDER";
    public static final String ACTID = "ACTID";
    public static final String ACTENTITY = "ACTENTITY";

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.tvPrice)
    TextView tvPrice;
    @InjectView(R.id.llTips)
    LinearLayout llTips;
    @InjectView(R.id.tvTips)
    TextView tvTips;

    /**
     * wechatPay
     */
    private PayReq req;
    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

    /**
     * AliPay
     */
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;


    private double fee;//付款金额
    private String order;//订单号
    private String actId;
    private ActDetailEntityy entity;
    private boolean isRequest = false;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_info);
        Intent intent = getIntent();
        if (intent.hasExtra(FEE) && intent.hasExtra(ORDER) && intent.hasExtra(ACTID) && intent.hasExtra(ACTENTITY)) {
            fee = intent.getExtras().getDouble(FEE);
            order = intent.getExtras().getString(ORDER);
            actId = intent.getExtras().getString(ACTID);
            entity = (ActDetailEntityy) intent.getSerializableExtra(ACTENTITY);

            if (entity.isWill_payment_timeout()) {
                llTips.setVisibility(View.GONE);
                getPayTimeOut();
            } else {
                llTips.setVisibility(View.VISIBLE);
                tvTips.setText("请在活动开始前完成支付，获得参与资格\n请到“首页>参与的活动”中查看支付结果");
            }

            mLoadingDialog = LoadingDialog.createDialog(PayInfo.this, true);
            EventCenter.getInstance().register(this);
            titlebar.setHeaderTitle("支付方式");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            req = new PayReq();

            tvPrice.setText(String.valueOf(fee));
        } else {
            toast("支付信息错误");
            finish();
        }
    }

    /**
     * 获取支付过期时间
     */
    private void getPayTimeOut() {
        BaseParams params = new BaseParams(API.PAY_TIME_OUT);
        params.put("activity", actId);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                try {
                    JSONObject object = new JSONObject(msg);
                    llTips.setVisibility(View.VISIBLE);
                    String tips = "您还剩" + (object.getInt("timeout_seconds") / 60) + "分钟完成支付，获得参与资格\n请到“首页>参与的活动”中查看支付结果";
                    tvTips.setText(TipsProgress(tips));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(int code, String error) {
                llTips.setVisibility(View.VISIBLE);
                tvTips.setText("请在活动开始前完成支付，获得参与资格");
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventCenter.getInstance().post(new ActEnrollTipsEvent(actId, 2));
        finish();
    }

    @OnClick({R.id.llWeChatPay, R.id.llALiPay})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.llWeChatPay:
                if (!isRequest) {
                    isRequest = true;
                    WeChatPay();
                }
                break;
            case R.id.llALiPay:
                if (!isRequest) {
                    isRequest = true;
                    toast("正在检测支付环境...");
                    checkAliPayClient();
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    // String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (Constant.SHOW_LOG) {
                            toast("支付成功");
                        }
                        //更新首页活动
                        EventCenter.getInstance().post(new HomeDataUpdateEvent(0));

                        Intent success = new Intent(PayInfo.this, SignTips.class);
                        success.putExtra("TYPE", "PAY");
                        success.putExtra("BIG", "恭喜你");
                        success.putExtra("TWO", "请到活动详情中查看活动手册");
                        success.putExtra("ICON", R.drawable.icon_green_success);
                        success.putExtra("MSG", "成功报名本次活动");
                        startActivity(success);
                        finish();
                    } else {
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            toast("请支付渠道原因或者系统原因\n还在等待支付结果确认");
                            isRequest = false;
                        } else {
                            isRequest = false;
                            Intent success = new Intent(PayInfo.this, SignTips.class);
                            success.putExtra("TYPE", "PAY");
                            success.putExtra("BIG", "对不起");
                            success.putExtra("TWO", "请到活动详情中查看结果");
                            success.putExtra("ICON", R.drawable.icon_red_fail);
                            success.putExtra("MSG", "您没有支付成功，请重试");
                            startActivity(success);
                        }
                    }
                    break;
                case SDK_CHECK_FLAG:
                    if (Constant.SHOW_LOG) {
                        toast("检查结果为：" + msg.obj);
                    }
                    if ((boolean) msg.obj == true) {
                        AliPay();
                    } else {
                        toast("请先安装支付宝客户端");
                        isRequest = false;
                    }
                    break;
            }
        }
    };

    @Subscribe
    public void onEvent(WeChatPayEvent event) {
        //微信支付结果
        if (event.getSuccess() == 1) {
            //更新首页活动
            EventCenter.getInstance().post(new HomeDataUpdateEvent(0));

            Intent success = new Intent(PayInfo.this, SignTips.class);
            success.putExtra("TYPE", "PAY");
            success.putExtra("BIG", "恭喜你");
            success.putExtra("TWO", "请到活动详情中查看活动手册");
            success.putExtra("ICON", R.drawable.icon_green_success);
            success.putExtra("MSG", "成功报名本次活动");
            startActivity(success);
            finish();
        } else {
            Intent success = new Intent(PayInfo.this, SignTips.class);
            success.putExtra("TYPE", "PAY");
            success.putExtra("BIG", "对不起");
            success.putExtra("TWO", "请到活动详情中查看结果");
            success.putExtra("ICON", R.drawable.icon_red_fail);
            success.putExtra("MSG", "您没有支付成功，请重试");
            startActivity(success);
        }
        isRequest = false;
    }

    /**
     * 微信支付
     */
    private void WeChatPay() {
        mLoadingDialog.setMessage("请等待...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.WECHAT_PAY);
        params.put("order_no", order);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(msg);
                    String appId = object.getString("appid");
                    String mchid = object.getString("mchid");
                    String nonce_str = object.getString("nonce_str");
                    String prepay_id = object.getString("prepay_id");
                    msgApi.registerApp(appId);
                    if (msgApi.isWXAppInstalled() && msgApi.isWXAppSupportAPI()) {
                        getWeChatPayReq(appId, mchid, nonce_str, prepay_id);
                    } else {
                        toast("请先安装微信客户端");
                        isRequest = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isRequest = false;
                }
            }

            @Override
            public void fail(int code, String error) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                toast(error);
            }
        });
    }

    private void getWeChatPayReq(String appId, String mchid, String nonce_str, String prepay_id) {
        req.appId = appId;
        req.partnerId = mchid;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonce_str;
        req.timeStamp = String.valueOf(genTimeStamp());

//        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//        signParams.add(new BasicNameValuePair("appid", req.appId));
//        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//        signParams.add(new BasicNameValuePair("package", req.packageValue));
//        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        List<Pair> params = new LinkedList<Pair>();
        params.add(new Pair("appid", req.appId));
        params.add(new Pair("noncestr", req.nonceStr));
        params.add(new Pair("package", req.packageValue));
        params.add(new Pair("partnerid", req.partnerId));
        params.add(new Pair("prepayid", req.prepayId));
        params.add(new Pair("timestamp", req.timeStamp));

        req.sign = genAppSign(params);

//        req.sign = genAppSign(signParams);
        sendPayReq();
    }

//    private String genAppSign(List<NameValuePair> params) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
//        sb.append("key=");
//        sb.append(Constant.WX_PAY_API_KEY);
//        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
//        return appSign;
//    }

    private String genAppSign(List<Pair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getFirst());
            sb.append('=');
            sb.append(params.get(i).getSecond());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constant.WX_PAY_API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private void sendPayReq() {
        msgApi.sendReq(req);
    }

    /**
     * 支付宝支付
     */
    private void AliPay() {
        mLoadingDialog.setMessage("请等待...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.ALIPAY);
        params.put("order_no", order);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                try {
                    JSONObject object = new JSONObject(msg);
                    String payment_data = object.getString("payment_data");
                    AliPayTask(payment_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    toast("支付信息错误");
                    isRequest = false;
                }
            }

            @Override
            public void fail(int code, String error) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                toast(error);
            }
        });
    }

    /**
     * 调用AliPay SDK支付
     */
    public void AliPayTask(final String payment_data) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PayInfo.this);
                String result = alipay.pay(payment_data);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 查询终端设备是否存在支付宝认证账户
     */
    public void checkAliPayClient() {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask(PayInfo.this);
                boolean isExist = payTask.checkAccountIfExist();
                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };
        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }

    /**
     * 关键字颜色区别
     */
    private Spannable TipsProgress(String tips) {
        int start = tips.indexOf("剩", 1) + 1;
        int end = tips.indexOf("钟", 1) + 1;
        Spannable word = new SpannableString(tips);
        word.setSpan(new ForegroundColorSpan(0xffe6581f), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return word;
    }
}
