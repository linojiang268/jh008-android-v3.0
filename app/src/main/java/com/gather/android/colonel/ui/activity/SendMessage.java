package com.gather.android.colonel.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.ToastDialog;
import com.gather.android.entity.MobilEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.jihe.dialog.widget.NormalDialog;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;



import butterknife.InjectView;

/**
 * Created by Levi on 15/10/12.
 */
public class SendMessage extends BaseActivity {
    private final static int MAX_COUNT = 60;

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.tvLeftMessage)
    TextView tvLeftMessage;
    @InjectView(R.id.tvMsgRecivers)
    TextView tvMsgRecivers;
    @InjectView(R.id.etContent)
    EditText etContent;
    @InjectView(R.id.tvWordsCount)
    TextView tvWordsCount;

    private int curTextCount;
    private int leftMessage = 0;
    private String actId;

    private LoadingDialog loadingDialog;
    private NormalDialog sendMsgSelfDialog;

    private MobilEntity mobiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colonel_send_message);
        actId = getIntent().getStringExtra("ACTID");
        Logger.i(actId);
        initView();
    }

    private void initView(){
        titlebar.setHeaderTitle("通知");
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etContent.requestFocus();

        loadingDialog = LoadingDialog.createDialog(this, false);

        TextView btnSend = new TextView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        btnSend.setLayoutParams(params);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        btnSend.setPadding(padding, 0, padding, 0);
        btnSend.setGravity(Gravity.CENTER);
        btnSend.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        btnSend.setTextColor(Color.WHITE);
        btnSend.setText("发送");
        titlebar.setCustomizedRightView(btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        curTextCount = 0;
        updateTextCountView();
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.length();
                if (count <= MAX_COUNT && curTextCount > MAX_COUNT) {
                    tvWordsCount.setTextColor(getResources().getColor(R.color.gray_dark));
                } else if (count > MAX_COUNT && curTextCount <= MAX_COUNT) {
                    tvWordsCount.setTextColor(Color.RED);
                }
                curTextCount = count;
                updateTextCountView();
            }
        });

        loadRestMessageCount();
    }

    private void updateTextCountView(){
        tvWordsCount.setText(curTextCount + "/" + MAX_COUNT + "");
    }

    private void updateRestMessageView(){
        SpannableStringBuilder builder = new SpannableStringBuilder("短信发送，还剩" + leftMessage + "次");
        builder.setSpan(new ForegroundColorSpan(Color.RED), 7, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLeftMessage.setText(builder);
    }

    private void sendMessage(){
        if (curTextCount == 0){
            ToastDialog.showToast(this, "请输入通知内容");
        }
        else if (curTextCount > MAX_COUNT){
            ToastDialog.showToast(this, "通知内容已超过字数限制，请重新编辑");
        }
        else if (leftMessage <= 0){
            if (sendMsgSelfDialog == null){
                sendMsgSelfDialog = new NormalDialog(this);
                sendMsgSelfDialog.content("（需自行承担短信费用）")//
                        .title("是否通过本机发送通知？")
                        .style(NormalDialog.STYLE_TWO)
                        .titleTextSize(16)
                        .titleTextColor(Color.parseColor("#000000"))
                        .contentTextColor(Color.parseColor("#fd7037"))
                        .contentTextSize(12)
                        .setCancelable(true);
                sendMsgSelfDialog.setCanceledOnTouchOutside(true);
                sendMsgSelfDialog.setOnBtnLeftClickL(new OnBtnLeftClickL() {
                    @Override
                    public void onBtnLeftClick() {
                        sendMsgSelfDialog.dismiss();
                        sendMessageSilf();
                    }
                });
            }
            sendMsgSelfDialog.show();
        }
        else {
            loadingDialog.setMessage("正在发送通知...");
            loadingDialog.show();
            String content = etContent.getText().toString().trim();
            BaseParams params = new BaseParams(API.SEND_MESSAGE);
            params.put("activity", actId);
            params.put("content", content);
            OkHttpUtil.post(params, new ResponseHandler() {
                @Override
                public void success(String data) {
                    loadingDialog.dismiss();
                    leftMessage--;
                    updateRestMessageView();
                    etContent.setText("");
                    ToastDialog.showToast(SendMessage.this, "发送成功", R.drawable.ic_toast_success, false);
                }

                @Override
                public void fail(int errorCode, String msg) {
                    loadingDialog.dismiss();
                    ToastDialog.showToast(SendMessage.this, msg, R.drawable.ic_toast_fail, false);
                }
            });

        }
    }

    private void loadRestMessageCount(){
        loadingDialog.setMessage("正在加载剩余通知次数...");
        loadingDialog.show();
        BaseParams params = new BaseParams(API.REST_MESSAGE_TIMES);
        params.put("activity", actId);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String data) {
                loadingDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(data);
                    leftMessage = object.getInt("rest_times");
                    updateRestMessageView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(int errorCode, String msg) {
                loadingDialog.dismiss();
                if (errorCode != 1){
                    toast(msg);
                    finish();
                }
                else {
                    leftMessage = 0;
                    updateRestMessageView();
                    ToastDialog.showToast(SendMessage.this, msg);
                }

            }
        });
    }

    private void sendMessageSilf(){
        if (mobiles == null){
            loadSuccessMobiles();
        }
        else {
            intentToSms();
        }
    }

    private void intentToSms(){
        StringBuilder sb = new StringBuilder();
        for (String mobile : mobiles.getMobiles()){
            if (sb.length() != 0){
                sb.append(";");
            }
            sb.append(mobile);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", sb.toString());
        intent.putExtra("sms_body", etContent.getText().toString().trim());
        intent.setType("vnd.android-dir/mms-sms");
        try {
            startActivity(intent);
        }
        catch (Exception e){

        }

    }

    private void loadSuccessMobiles(){
        loadingDialog.setMessage("正在获取手机号码列表...");
        loadingDialog.show();
        BaseParams params = new BaseParams(API.GET_SUCCESS_MOBILES);
        params.put("activity", actId);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String data) {
                loadingDialog.dismiss();
                mobiles = JSON.parseObject(data, MobilEntity.class);
                intentToSms();
            }

            @Override
            public void fail(int errorCode, String msg) {
                loadingDialog.dismiss();
                ToastDialog.showToast(SendMessage.this, msg);
            }
        });
    }





}
