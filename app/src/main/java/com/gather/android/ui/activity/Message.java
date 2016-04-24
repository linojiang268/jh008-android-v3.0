package com.gather.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.ui.fragment.MessageOrgFragment;
import com.gather.android.ui.fragment.MessageSystemFragment;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.TitleBar;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/18.
 */
public class Message extends BaseActivity {

    @InjectView(R.id.indicator)
    FixedIndicatorView indicator;
    @InjectView(R.id.viewPager)
    SViewPager viewPager;
    @InjectView(R.id.titlebar)
    TitleBar titlebar;

    private ColorBar colorBar;
    private IndicatorViewPager indicatorViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        titlebar.setHeaderTitle("消息");
        colorBar = new ColorBar(getApplicationContext(), 0xFF00a9e8, PhoneManager.dip2px(2));
        indicator.setScrollBar(colorBar);
        int selectColor = getResources().getColor(R.color.black);
        indicator.setOnTransitionListener(new OnTransitionTextListener(15, 15, selectColor, selectColor));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCanScroll(true);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MessageAdapter(getSupportFragmentManager()));
    }

    private class MessageAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MessageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_top_bar, container, false);
            }
            TextView textView = (TextView) convertView;
            if (position == 0) {
                textView.setText("社团");
            }  else {
                textView.setText("系统");
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                MessageOrgFragment messageOrg = new MessageOrgFragment();
                return messageOrg;
            } else {
                MessageSystemFragment messageSystem = new MessageSystemFragment();
                return messageSystem;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
