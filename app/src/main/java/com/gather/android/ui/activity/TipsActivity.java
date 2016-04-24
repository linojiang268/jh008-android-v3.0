package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeDataUpdateEvent;
import com.gather.android.event.OrgHomeRefreshEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.widget.TitleBar;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 活动或社团信息提示页面
 * Created by Administrator on 2015/7/22.
 */
public class TipsActivity extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.ivTips)
    ImageView ivTips;
    @InjectView(R.id.tvTips)
    TextView tvTips;
    @InjectView(R.id.btClick)
    TextView btClick;

    private OrgDetailEntity model;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_activity);
        Intent intent = getIntent();

        if (intent.hasExtra("MODEL")) {
            model = (OrgDetailEntity) intent.getSerializableExtra("MODEL");
            mLoadingDialog = LoadingDialog.createDialog(this, true);
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (model.getJoin_requirements().size() != 0) {
                //需要填写信息验证
                if (model.isIn_whitelist()) {
                    //在白名单
                    titlebar.setHeaderTitle("身份验证");
                    ivTips.setImageResource(R.drawable.icon_green_success);
                    btClick.setTextColor(getResources().getColorStateList(R.color.green_to_white_text_color));
                    btClick.setBackgroundResource(R.drawable.green_corner_stroke_click_style);
                    tvTips.setText("你是社团邀请的成员，请完善信息即刻加入");
                    btClick.setText("完善信息");
                } else {
                    //不在白名单
                    titlebar.setHeaderTitle("身份验证");
                    ivTips.setImageResource(R.drawable.icon_blue_wait);
                    btClick.setTextColor(getResources().getColorStateList(R.color.blue_to_white_text_color));
                    btClick.setBackgroundResource(R.drawable.blue_corner_stroke_click_style);
                    tvTips.setText("本社团需要确认身份方可加入");
                    btClick.setText("立即确认");
                }
            } else {
                //不需要填写信息验证
                if (model.isIn_whitelist()) {
                    //在白名单
                    titlebar.setHeaderTitle("邀请的成员");
                    ivTips.setImageResource(R.drawable.icon_blue_wait);
                    tvTips.setText("你已被此社团设置成特邀成员\n点击加入即可加入社团");
                    btClick.setText("立即加入");
                } else {
                    //不在白名单
                    titlebar.setHeaderTitle("信息验证");
                    ivTips.setImageResource(R.drawable.icon_blue_wait);
                    tvTips.setText("此社团需要进行审核\n才能加入社团");
                    btClick.setText("立即审核");
                }
            }
        } else {
            toast("页面信息错误");
            finish();
        }
    }

    @OnClick(R.id.btClick)
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btClick:
                if (btClick.getText().toString().contains("加入") || btClick.getText().toString().contains("审核")) {
                    Enroll();
                } else {
                    Intent intent = new Intent(this, EditInfo.class);
                    intent.putExtra("ORG_MODEL", model);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    /**
     * 申请接口
     */
    private void Enroll() {
        mLoadingDialog.setMessage("申请中...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.APPLY_JOIN_ORG);
        params.put("team", model.getId());
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                EventCenter.getInstance().post(new OrgHomeRefreshEvent());
                //更新首页社团
                EventCenter.getInstance().post(new HomeDataUpdateEvent(1));
                finish();
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
}
