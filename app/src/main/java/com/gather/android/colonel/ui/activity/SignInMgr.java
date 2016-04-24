package com.gather.android.colonel.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.colonel.ui.fragment.PaymentFragment;
import com.gather.android.colonel.ui.fragment.PendingFragment;
import com.gather.android.colonel.ui.fragment.SignInFragment;
import com.gather.android.colonel.ui.fragment.SuccessFragment;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.TitleBar;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import butterknife.InjectView;

/**
 * 签到管理
 * Created by Levi on 15/9/29.
 */
public class SignInMgr extends BaseActivity {
    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.indicator)
    FixedIndicatorView indicator;
    @InjectView(R.id.viewPager)
    SViewPager viewPager;
    @InjectView(R.id.frame)
    FrameLayout frame;

    private ColorBar colorBar;
    private IndicatorViewPager indicatorViewPager;

    private String actId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colonel_signin_mgr);

        Intent intent = getIntent();
        actId = intent.getStringExtra("ACTID");
        initView();
    }

    private void initView() {
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titlebar.setHeaderTitle("签到管理");

        frame.setVisibility(View.GONE);

        colorBar = new ColorBar(getApplicationContext(), 0xFF00a9e8, PhoneManager.dip2px(2));
        indicator.setScrollBar(colorBar);
        indicator.setOnTransitionListener(new OnTransitionTextListener(15, 15,
                Color.parseColor("#00a9e8"),
                Color.parseColor("#000000")));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCanScroll(true);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        indicatorViewPager.setCurrentItem(0, false);
    }

    private class PagerAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private final String[] TITLE = {"未签到", "已签到"};
        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.colonel_top_bar, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(TITLE[position]);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("ACTID", actId);
            bundle.putInt("TYPE", position);

            Fragment fragment = new SignInFragment();
            fragment.setArguments(bundle);

            return fragment;
        }
    }
}
