package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.event.EventCenter;
import com.gather.android.event.OrgSecretSetEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.widget.TitleBar;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 社团隐私设置
 * Created by Administrator on 2015/7/13.
 */
public class OrgSecretSet extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.ivMemberSee)
    ImageView ivMemberSee;
    @InjectView(R.id.ivAllSee)
    ImageView ivAllSee;

    private int oldVisible;
    private int visible;//隐私设置：0所有人可见，1仅社团成员可见
    private String orgId;

    private boolean isRequest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_secret_set);
        Intent intent = getIntent();
        if (intent.hasExtra("VISIBLE") && intent.hasExtra("ID")) {
            oldVisible = visible = intent.getExtras().getInt("VISIBLE");
            orgId = intent.getExtras().getString("ID");
            titlebar.setHeaderTitle("隐私设置");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            initView();
        } else {
            toast("社团隐私信息错误");
            finish();
        }
    }

    private void initView() {
        if (visible == 1) {
            ivMemberSee.setVisibility(View.VISIBLE);
            ivAllSee.setVisibility(View.GONE);
        } else {
            ivMemberSee.setVisibility(View.GONE);
            ivAllSee.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.llMemberSee, R.id.llAllSee})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.llMemberSee:
                if (visible == 0 && isRequest) {
                    updateSecretSet(1);
                }
                break;
            case R.id.llAllSee:
                if (visible == 1 && isRequest) {
                    updateSecretSet(0);
                }
                break;
        }
    }

    /**
     * 修改社团成员隐私设置
     */
    private void updateSecretSet(final int visible) {
        isRequest = false;
        BaseParams params = new BaseParams(API.CHANGE_ORG_SECRET_SET);
        params.put("team", orgId);
        params.put("visibility", visible);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                OrgSecretSet.this.visible = visible;
                if (visible == 1) {
                    ivMemberSee.setVisibility(View.VISIBLE);
                    ivAllSee.setVisibility(View.GONE);
                } else {
                    ivMemberSee.setVisibility(View.GONE);
                    ivAllSee.setVisibility(View.VISIBLE);
                }
                isRequest = true;
                toast("修改成功");
            }

            @Override
            public void fail(int code, String error) {
                isRequest = true;
                toast(error);
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
        if (oldVisible != visible) {
            EventCenter.getInstance().post(new OrgSecretSetEvent(visible));
        }
        super.onBackPressed();
    }
}
