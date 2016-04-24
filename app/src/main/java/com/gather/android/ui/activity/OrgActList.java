package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.ui.fragment.ActBaseFragment;
import com.gather.android.widget.TitleBar;

import butterknife.InjectView;

/**
 * 社团发布的活动列表（近期活动）
 * Created by Administrator on 2015/8/4.
 */
public class OrgActList extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.flContent)
    FrameLayout flContent;

    private String orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_act_list);
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            orgId = intent.getExtras().getString("ID");
            titlebar.setHeaderTitle("近期活动");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            ActBaseFragment fragment = new ActBaseFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActBaseFragment.EXTRA_ORD_ID, orgId);
            bundle.putInt(ActBaseFragment.EXTRA_MODE, ActBaseFragment.MODE_ORG);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
        } else {
            toast("页面信息错误");
            finish();
        }

    }
}
