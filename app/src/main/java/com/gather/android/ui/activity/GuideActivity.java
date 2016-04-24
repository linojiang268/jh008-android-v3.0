package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorViewPagerAdapter;
import com.shizhefei.view.viewpager.SViewPager;

import butterknife.InjectView;

/**
 * 引导页
 * Created by Administrator on 2015/8/14.
 */
public class GuideActivity extends BaseActivity {

    @InjectView(R.id.viewPager)
    SViewPager viewPager;
    @InjectView(R.id.indicator)
    FixedIndicatorView indicator;

    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(adapter);
    }

    private IndicatorPagerAdapter adapter = new IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.guide_dot, container, false);
            }
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.guide_content_layout, container, false);
            }
            ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            ImageView ivImageTwo = (ImageView) convertView.findViewById(R.id.ivImageTwo);
            ImageView ivText = (ImageView) convertView.findViewById(R.id.ivText);
            TextView tvStart = (TextView) convertView.findViewById(R.id.tvStart);

            switch (position) {
                case 0:
                    ivImage.setVisibility(View.GONE);
                    ivImageTwo.setVisibility(View.VISIBLE);
                    ivImage.setImageResource(R.drawable.icon_guide_image_two);
                    ivText.setImageResource(R.drawable.icon_guide_text_two);
                    tvStart.setVisibility(View.GONE);
                    break;
                case 1:
                    ivImage.setVisibility(View.VISIBLE);
                    ivImageTwo.setVisibility(View.GONE);
                    ivImage.setImageResource(R.drawable.icon_guide_image_one);
                    ivText.setImageResource(R.drawable.icon_guide_text_one);
                    tvStart.setVisibility(View.GONE);
                    break;
                case 2:
                    ivImage.setVisibility(View.VISIBLE);
                    ivImageTwo.setVisibility(View.GONE);
                    ivImage.setImageResource(R.drawable.icon_guide_image_three);
                    ivText.setImageResource(R.drawable.icon_guide_text_three);
                    tvStart.setVisibility(View.VISIBLE);
                    tvStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GuideActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return 3;
        }
    };
}
