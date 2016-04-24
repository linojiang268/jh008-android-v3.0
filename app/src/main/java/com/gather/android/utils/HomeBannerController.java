package com.gather.android.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.R;
import com.gather.android.entity.HomeBannerEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeBannerClickEvent;
import com.gather.android.widget.banner.BannerAdapter;
import com.gather.android.widget.banner.DotView;
import com.gather.android.widget.banner.SliderBanner;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeBannerController {


    private SliderBanner mSliderBanner;
    private InnerAdapter mBannerAdapter = new InnerAdapter();
    private DotView mDotView;

    public HomeBannerController(SliderBanner sliderBanner) {
        mDotView = (DotView) sliderBanner.findViewById(R.id.dotView);
        mSliderBanner = sliderBanner;
        sliderBanner.setAdapter(mBannerAdapter);
    }

    public void play(ArrayList<HomeBannerEntity> list) {
        mBannerAdapter.setData(list);
        mBannerAdapter.notifyDataSetChanged();
        mSliderBanner.setDotNum(list.size());
        mSliderBanner.beginPlay();
    }

    private class InnerAdapter extends BannerAdapter {
        private ArrayList<HomeBannerEntity> mDataList;

        public void setData(ArrayList<HomeBannerEntity> data) {
            mDataList = data;
        }

        public HomeBannerEntity getItem(int position) {
            if (mDataList == null)
                return null;
            return mDataList.get(getPositionForIndicator(position));
        }

        @Override
        public int getPositionForIndicator(int position) {
            if (null == mDataList || mDataList.size() == 0) {
                return 0;
            }
            return position % mDataList.size();
        }

        @Override
        public View getView(LayoutInflater layoutInflater, int position) {
            View convertView = layoutInflater.inflate(R.layout.banner_item, null);

            HomeBannerEntity item = getItem(position);
            SimpleDraweeView imageView = (SimpleDraweeView) convertView.findViewById(R.id.ivBanner);
            if (TextUtils.isEmpty(item.getImage_url())){
                imageView.setImageURI(null);
            }
            else {
                imageView.setImageURI(Uri.parse(item.getImage_url()));
            }


            TextView titleTextView = (TextView) convertView.findViewById(R.id.tvBanner);
            titleTextView.setText("");

            convertView.setTag(item);
            convertView.setOnClickListener(new OnClickItemListener(item, position % mDataList.size()));
            return convertView;
        }

        @Override
        public int getCount() {
            if (mDataList == null) {
                return 0;
            } else if (mDataList.size() == 1) {
                return 1;
            }
            return Integer.MAX_VALUE;
        }
    }

    class OnClickItemListener implements View.OnClickListener {

        private HomeBannerEntity model;
        private int position;

        public OnClickItemListener(HomeBannerEntity model, int position) {
            this.model = model;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            EventCenter.getInstance().post(new HomeBannerClickEvent(model, position));
        }
    }
}
