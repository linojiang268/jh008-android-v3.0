package com.gather.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.widget.TitleBar;

import butterknife.InjectView;

/**
 * 关于我们
 * Created by Administrator on 2015/9/22.
 */
public class AboutUs extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.btnWebsite)
    TextView btnWebsite;
    @InjectView(R.id.btnPhoneCall)
    TextView btnPhoneCall;
    @InjectView(R.id.tvBeta)
    TextView tvBeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        titlebar.setHeaderTitle("关于我们");
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri uri = Uri.parse("http://www.jh008.com");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
            }
        });

        btnPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:4008761176");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        if (Constant.SHOW_LOG){
            tvBeta.setVisibility(View.VISIBLE);
        }
        else {
            tvBeta.setVisibility(View.GONE);
        }
    }
}
