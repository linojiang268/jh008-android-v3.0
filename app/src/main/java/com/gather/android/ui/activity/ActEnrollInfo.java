package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.GatherApplication;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.ActDetailEntityy;
import com.gather.android.entity.ActEnrollOrderEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeDataUpdateEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.utils.MobileTextWatcher;
import com.gather.android.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import io.yunba.android.manager.YunBaManager;

/**
 * 活动报名信息填写
 * Created by Administrator on 2015/8/4.
 */
public class ActEnrollInfo extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.llContent)
    LinearLayout llContent;
    @InjectView(R.id.btnCommit)
    Button btnCommit;
    @InjectView(R.id.scrollView)
    ScrollView scrollView;

    private ActDetailEntityy entity;
    private List<EditText> editTexts = new ArrayList<EditText>();
    private List<HashMap<String, String>> listMap = new ArrayList<HashMap<String, String>>();
    private LoadingDialog mLoadingDialog;
    private String beforeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);

        Intent intent = getIntent();
        if (intent.hasExtra("MODEL")) {
            entity = (ActDetailEntityy) intent.getSerializableExtra("MODEL");

            mLoadingDialog = LoadingDialog.createDialog(this, true);
            titlebar.setHeaderTitle("报名信息");
            btnCommit.setText("提交报名");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            for (int i = 0; i < entity.getEnroll_attrs().size(); i++) {
                createEditText(entity.getEnroll_attrs().get(i));
            }
        } else {
            toast("报名信息错误");
            finish();
        }
    }

    @OnClick(R.id.btnCommit)
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btnCommit:
                mLoadingDialog.setMessage("提交中...");
                mLoadingDialog.show();
                for (int i = 0; i < entity.getEnroll_attrs().size(); i++) {
                    String content = editTexts.get(i).getText().toString().replaceAll(" ", "");
                    if (TextUtils.isEmpty(content)) {
                        listMap.clear();
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        toast("报名信息不能有空");
                        return;
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put("key", entity.getEnroll_attrs().get(i));
                    map.put("value", content);
                    listMap.add(map);
                }
                CommitEnrollInfo();
                break;
        }
    }

    /**
     * 提交报名信息
     */
    private void CommitEnrollInfo() {
        BaseParams params = new BaseParams(API.POST_ACT_ENROLL_INFO);
        params.put("activity_id", entity.getId());
        params.put("attrs", JSON.toJSONString(listMap));
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                try {
                    JSONObject object = new JSONObject(msg);
                    ActEnrollOrderEntity model = JSON.parseObject(object.getString("info"), ActEnrollOrderEntity.class);
                    if (model != null) {
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        switch (model.getStatus()) {
                            case 1://需要审核
                                //更新首页活动
                                EventCenter.getInstance().post(new HomeDataUpdateEvent(2));

                                Intent intent = new Intent(ActEnrollInfo.this, SignTips.class);
                                intent.putExtra("TYPE", "ACT");
                                intent.putExtra("BIG", "待审核");
                                intent.putExtra("TWO", "请到“首页>参与的活动”中查看审核结果");
                                intent.putExtra("ICON", R.drawable.icon_blue_wait);
                                if (entity.getEnroll_fee_type() == 3) {
                                    //需要付款
                                    intent.putExtra("MSG", "主办方审核通过后，将通知您支付费用");
                                } else {
                                    intent.putExtra("MSG", "主办方审核通过后，您将获得参与资格");
                                }
                                intent.putExtra("ID", entity.getId());
                                intent.putExtra("STATUS", model.getStatus());
                                startActivity(intent);
                                finish();
                                break;
                            case 2://付款
                                //更新首页活动
                                EventCenter.getInstance().post(new HomeDataUpdateEvent(0));


//                                Intent pay = new Intent(ActEnrollInfo.this, ActTips.class);
//                                pay.putExtra(ActTips.INFO, model);
//                                startActivity(pay);
                                getActPayOrder();//下订单
                                break;
                            case 3://成功
                                //更新首页活动
                                EventCenter.getInstance().post(new HomeDataUpdateEvent(2));

                                YunBaManager.subscribe(getApplicationContext(), "topic_activity_" + entity.getId(), GatherApplication.getInstance().pushListener);
                                Intent success = new Intent(ActEnrollInfo.this, SignTips.class);
                                success.putExtra("TYPE", "ACT");
                                success.putExtra("BIG", "恭喜你");
                                        success.putExtra("TWO", "请到活动详情中查看活动手册");
                                success.putExtra("ICON", R.drawable.icon_green_success);
                                success.putExtra("MSG", "成功报名本次活动");
                                success.putExtra("ID", entity.getId());
                                success.putExtra("STATUS", model.getStatus());
                                startActivity(success);
                                finish();
//                                Intent note = new Intent(ActEnrollInfo.this, ActNoteBook.class);
//                                note.putExtra("ID", entity.getId());
//                                note.putExtra("FROM_ENROLL", "");
//                                startActivity(note);
                                break;
                            case -1:
                            case 0:
                            case 4:
                            default:
                                toast("数据状态错误");
                                finish();
                                return;
                        }
                    } else {
                        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        toast("数据解析错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    toast("数据解析错误");
                }
            }

            @Override
            public void fail(int code, String error) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast(error);
            }
        });
    }

    /**
     * 活动付款下订单
     */
    private void getActPayOrder() {
        mLoadingDialog.setMessage("正在下单...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.GET_ACT_ENROLL_ORDER_INFO);
        params.put("activity_id", entity.getId());
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                try {
                    JSONObject object = new JSONObject(msg);
                    ActEnrollOrderEntity model = JSON.parseObject(object.getString("info"), ActEnrollOrderEntity.class);
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    Intent pay = new Intent(ActEnrollInfo.this, PayInfo.class);
                    pay.putExtra(PayInfo.FEE, model.getFee());
                    pay.putExtra(PayInfo.ORDER, model.getOrder_no());
                    pay.putExtra(PayInfo.ACTID, model.getActivity_id());
                    pay.putExtra(PayInfo.ACTENTITY, entity);
                    startActivity(pay);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    toast("数据解析失败");
                }
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    private void createEditText(String string) {
        LinearLayout layout = new LinearLayout(ActEnrollInfo.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhoneManager.dip2px(48));
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(ActEnrollInfo.this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhoneManager.dip2px(48));
        layout.setLayoutParams(textParams);
        textView.setGravity(Gravity.CENTER);
        textView.setText(string);
        textView.setTextSize(16);
        textView.setTextColor(0xFF999999);
        textView.setSingleLine(true);
        textView.setPadding(PhoneManager.dip2px(12), 0, PhoneManager.dip2px(12), 0);

        final EditText editText = new EditText(ActEnrollInfo.this);
        layout.addView(textView);
        layout.addView(editText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PhoneManager.dip2px(48), 1.0f);
        editText.setLayoutParams(params);
        editText.setBackgroundColor(0x00000000);
        if (string.equals("手机号")) {
            editText.setText(Checker.formatPhoneNumber(UserPref.getInstance().getUserInfo().getMobile()));
            editText.addTextChangedListener(new MobileTextWatcher(editText));
        }
        if (beforeString.equals("手机号")) {
            editText.requestFocus();
        }
        editText.setTextSize(16);
        editText.setSingleLine(true);
        editText.setTextColor(getResources().getColor(R.color.black));
        editText.setPadding(0, 0, PhoneManager.dip2px(12), 0);
        editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        View line = new View(ActEnrollInfo.this);
        line.setBackgroundResource(R.color.line_color);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(param);

        editTexts.add(editText);
        llContent.addView(layout);
        llContent.addView(line);

        beforeString = string;
    }
}
