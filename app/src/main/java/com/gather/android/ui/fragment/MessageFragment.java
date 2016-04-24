package com.gather.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.TitleBar;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 消息
 * Created by Christain on 2015/6/2.
 */
public class MessageFragment extends BaseFragment {

    @InjectView(R.id.indicator)
    FixedIndicatorView indicator;
    @InjectView(R.id.viewPager)
    SViewPager viewPager;
    @InjectView(R.id.titlebar)
    TitleBar titlebar;

    private ColorBar colorBar;
    private IndicatorViewPager indicatorViewPager;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_message);
        ButterKnife.inject(this, getContentView());

        titlebar.getBackImageButton().setVisibility(View.GONE);
        titlebar.setHeaderTitle("消息");
        colorBar = new ColorBar(getApplicationContext(), 0xFF00a9e8, PhoneManager.dip2px(2));
        indicator.setScrollBar(colorBar);
        int selectColor = getResources().getColor(R.color.black);
        indicator.setOnTransitionListener(new OnTransitionTextListener(15, 15, selectColor, selectColor));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCanScroll(true);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MessageAdapter(getChildFragmentManager()));
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
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
    }

}
