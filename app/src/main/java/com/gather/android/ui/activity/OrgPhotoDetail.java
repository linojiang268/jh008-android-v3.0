package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.ui.fragment.OrgPhotoDetailFragment;
import com.shizhefei.view.viewpager.SViewPager;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 社团某一活动相册详情
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoDetail extends BaseActivity {

    @InjectView(R.id.tvSponsor)
    TextView tvSponsor;
    @InjectView(R.id.tvUser)
    TextView tvUser;
    @InjectView(R.id.viewPager)
    SViewPager viewPager;
    @InjectView(R.id.ibtn_backpress)
    ImageButton ibtBack;
    @InjectView(R.id.btnAdd)
    ImageButton btnAdd;

    private String actId;
    private boolean isAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_photo_detail);
        Intent intent = getIntent();
        if (intent.hasExtra("ACT_ID") && intent.hasExtra("ADDED")) {
            actId = intent.getExtras().getString("ACT_ID");
            isAdded = intent.getExtras().getBoolean("ADDED");

            btnAdd.setVisibility(View.GONE);

            ibtBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    onBackPressed();
                }
            });
            viewPager.setCanScroll(true);
            viewPager.setOffscreenPageLimit(2);
//            viewPager.setPrepareNumber(0);
            viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
            viewPager.setOnPageChangeListener(new ViewPagerChangeListener());

            initView();
        } else {
            toast("相册信息错误");
            finish();
        }
    }



    private void initView() {
        tvSponsor.setSelected(true);
        tvUser.setSelected(false);
        viewPager.setCurrentItem(0);
    }

    @OnClick({R.id.tvSponsor, R.id.tvUser, R.id.btnAdd})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tvSponsor:
                if (!tvSponsor.isSelected()) {
                    tvSponsor.setSelected(true);
                    tvUser.setSelected(false);
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.tvUser:
                if (!tvUser.isSelected()) {
                    tvUser.setSelected(true);
                    tvSponsor.setSelected(false);
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.btnAdd:
                Intent intent = new Intent(this, UploadPhoto.class);
                intent.putExtra("ID", actId);
                startActivity(intent);
                break;
        }
    }

    private class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                tvSponsor.setSelected(true);
                tvUser.setSelected(false);
                btnAdd.setVisibility(View.GONE);
            } else {
                tvUser.setSelected(true);
                tvSponsor.setSelected(false);
                if (isAdded) {
                    btnAdd.setVisibility(View.VISIBLE);
                } else {
                    btnAdd.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            if (arg0 == 0) {
                OrgPhotoDetailFragment fragment = new OrgPhotoDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("TYPE", 0);
                bundle.putString("ACT_ID", actId);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                OrgPhotoDetailFragment fragment = new OrgPhotoDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("TYPE", 1);
                bundle.putString("ACT_ID", actId);
                fragment.setArguments(bundle);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
