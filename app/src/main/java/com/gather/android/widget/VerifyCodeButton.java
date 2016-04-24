package com.gather.android.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.VerifyCodeEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.Checker;
import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取验证码的button
 * Created by Levi on 2015/7/9.
 */
public class VerifyCodeButton extends Button implements View.OnClickListener, Runnable{
    public static final int TYPE_REGISTER = 1;
    public static final int TYPE_RESET_PWD = 2;

    //每次倒数间隔（毫秒）
    private static final long COUNTDOWN_DURATION = 1000;

    private String formatStr;
    //倒计时数字
    private int countdown;
    //是否正在获取验证码流程中
    private boolean isRunning = false;
    private String phoneNumber;
    private int type;
    //倒数次数
    private int COUNTDOWN_NUMBER = 60;

    private OnGetVerifyCodeListener listener;
    private SmsObserver mObserver;

    public VerifyCodeButton(Context context) {
        super(context);
        init();
    }

    public VerifyCodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerifyCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        formatStr = getResources().getString(R.string.verify_code_countdown);
        setOnClickListener(this);
        mObserver = new SmsObserver(getHandler());
        getContext().getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, mObserver);
    }

    public void onDestroy(){
        isRunning = false;
        getHandler().removeCallbacks(this);
        getContext().getContentResolver().unregisterContentObserver(mObserver);
    }

    public void setTypeAndListener(int type, OnGetVerifyCodeListener l){
        this.type = type;
        listener = l;
    }

    /**
     *检查手机号，如无问题开始获取验证码
     */
    private void checkOrGetCode(){
        phoneNumber = listener.bindPhonenumber();
        if (Checker.isMobilePhone(phoneNumber)){
            setEnabled(false);
            setText(R.string.sending);
            requestForCode();
        }
        else {
            listener.onError(getResources().getString(R.string.input_right_mobilephone));
        }
    }

    /**
     * 发送请求获得验证码
     */
    private void requestForCode(){
        BaseParams params = new BaseParams(type == TYPE_REGISTER ? API.VERIFYCODE_REGISTER : API.VERIFYCODE_RESET_PWD);
        Logger.i(phoneNumber);
        params.put("mobile", phoneNumber);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                VerifyCodeEntity entity = JSON.parseObject(msg, VerifyCodeEntity.class);
                COUNTDOWN_NUMBER = entity.getSend_interval();
                startCountDown();
            }

            @Override
            public void fail(int code, String error) {
                setEnabled(true);
                setText(R.string.get_verify_code_again);
//                listener.onError(getResources().getString(R.string.vertifycode_send_fail));
                listener.onError(error);
            }
        });
    }

    /**
     * 开始倒数
     */
    private void startCountDown(){
        isRunning = true;
        countdown = COUNTDOWN_NUMBER;
        continueCountDown();
        listener.onCountdownStart(getResources().getString(R.string.vertifycode_send_success));
    }

    /**
     * 结束倒数
     */
    private void stopCountDown(){
        isRunning = false;
        setEnabled(true);
        setText(R.string.get_verify_code_again);
        listener.onCountdownEnd();
    }

    /**
     * 继续倒数
     */
    private void continueCountDown(){
        updateCountdownText();
        postDelayed(this, COUNTDOWN_DURATION);
    }

    /**
     * 更新倒数视图
     */
    private void updateCountdownText(){
        String text = String.format(formatStr, countdown);
        setText(text);
    }

    @Override
    public void run() {
        if (isRunning){
            if (countdown != 0){
                countdown--;
                continueCountDown();
            }
            else {
                stopCountDown();
            }
        }
    }

    @Override
    public void onClick(View view) {
        checkOrGetCode();
    }

    public interface  OnGetVerifyCodeListener {
        public String bindPhonenumber();
        public void onCountdownStart(String msg);
        public void onCountdownEnd();
        public void onError(String msg);
        public void onGetCode(String code);
    }

    private class  SmsObserver extends ContentObserver{

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // 读取收件箱中指定号码的短信
            Cursor mCursor= getContext().getContentResolver().query(Uri.parse("content://sms/inbox"),
                    new String[] { "_id", "address", "body" }, "read=?",
                    new String[] { "0" }, "date desc");
            if (mCursor != null){
                while (mCursor.moveToNext()){
                    String code = null;
                    String body = mCursor.getString(mCursor.getColumnIndex("body"));
                    if (!TextUtils.isEmpty(body) && body.matches("本次验证码\\d{4},10分钟邮箱。【集合】")){
                        Pattern pattern = Pattern.compile("\\d{4}");
                        Matcher matcher = pattern.matcher(body);
                        while (matcher.find()){
                            code = matcher.group();
                            break;
                        }
                        if (!TextUtils.isEmpty(code)){
                            listener.onGetCode(code);
                        }
                    }
                    Logger.i(body + "");
                }
            }
        }
    }
}
