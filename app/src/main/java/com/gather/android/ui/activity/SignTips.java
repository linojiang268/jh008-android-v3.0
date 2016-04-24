package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.event.ActEnrollTipsEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.event.FinishScannerEvent;
import com.gather.android.event.OrgHomeRefreshEvent;
import com.gather.android.event.PayActResultEvent;
import com.gather.android.widget.TitleBar;

import butterknife.InjectView;

/**
 * 签到结果提示
 * Created by Administrator on 2015/8/27.
 */
public class SignTips extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.tvTipsOne)
    TextView tvTipsOne;
    @InjectView(R.id.tvTipsBig)
    TextView tvTipsBig;
    @InjectView(R.id.ivTips)
    ImageView ivTips;
    @InjectView(R.id.tvTipsTwo)
    TextView tvTipsTwo;

    private String type;
    private String actId;
    private int applicant_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_tips);

        Intent intent = getIntent();
        if (intent.hasExtra("TYPE") && intent.hasExtra("MSG") && intent.hasExtra("ICON") && intent.hasExtra("TWO") && intent.hasExtra("BIG")) {
            type = intent.getExtras().getString("TYPE", "");
            if (type.equals("ORG")) {
                titlebar.setHeaderTitle("申请结果");
            } else if (type.equals("SIGN")) {
                titlebar.setHeaderTitle("扫描结果");
            } else if (type.equals("ACT")) {
                actId = intent.getExtras().getString("ID", "");
                applicant_status = intent.getExtras().getInt("STATUS");
                titlebar.setHeaderTitle("报名结果");
            } else if (type.equals("PAY")) {
                titlebar.setHeaderTitle("支付结果");
            }
            tvTipsBig.setText(intent.getExtras().getString("BIG", ""));
            tvTipsOne.setText(intent.getExtras().getString("MSG", ""));
            ivTips.setImageResource(intent.getExtras().getInt("ICON"));
            tvTipsTwo.setText(intent.getExtras().getString("TWO", ""));

            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            toast("页面信息错误");
            finish();
        }
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
        if (type.equals("ORG")) {
            EventCenter.getInstance().post(new OrgHomeRefreshEvent());
        } else if (type.equals("SIGN")){
            EventCenter.getInstance().post(new FinishScannerEvent());
        } else if (type.equals("ACT")) {
            EventCenter.getInstance().post(new ActEnrollTipsEvent(actId, applicant_status));
        } else if (type.equals("PAY")) {
            if (!tvTipsBig.getText().toString().equals("对不起")) {
                EventCenter.getInstance().post(new PayActResultEvent());
            }
        }
        finish();
    }
}
