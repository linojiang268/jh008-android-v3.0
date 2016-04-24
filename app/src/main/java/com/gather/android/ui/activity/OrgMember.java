package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.ui.fragment.OrgMemberFragment;
import com.gather.android.widget.TitleBar;

import butterknife.InjectView;

/**
 * 社团成员
 * Created by Administrator on 2015/7/9.
 */
public class OrgMember extends BaseActivity {

    public static final int ORG = 0x1001;
    public static final int ACT = 0x1002;

    @InjectView(R.id.flContent)
    FrameLayout flContent;

    private String orgId;
    private String actId;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_member);

        Intent intent = getIntent();
        if (intent.hasExtra("ID") && intent.hasExtra("TYPE")) {
            type = intent.getExtras().getInt("TYPE");
            if (type == ORG) {
                orgId = intent.getExtras().getString("ID");
                ((TitleBar) toolbar).setHeaderTitle("社团成员");
            } else {
                actId = intent.getExtras().getString("ID");
                ((TitleBar) toolbar).setHeaderTitle("活动成员");
            }
            ((TitleBar) toolbar).getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            OrgMemberFragment fragment = new OrgMemberFragment();
            Bundle bundle = new Bundle();
            if (type == ORG) {
                bundle.putString("ID", orgId);
            } else {
                bundle.putString("ID", actId);
            }
            bundle.putInt("TYPE", type);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
        } else {
            toast("页面信息错误");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
