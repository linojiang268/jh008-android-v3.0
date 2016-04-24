package com.gather.android.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.R;
import com.gather.android.entity.BannerEntity;
import com.gather.android.event.ActDetailBannerClickEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.widget.banner.BannerAdapter;
import com.gather.android.widget.banner.DotView;
import com.gather.android.widget.banner.SliderBanner;

import java.util.ArrayList;

/**
 * Created by Christain on 2015/6/18.
 */
public class SliderBannerController {

    private SliderBanner mSliderBanner;
    private InnerAdapter mBannerAdapter = new InnerAdapter();
    private DotView mDotView;

    public SliderBannerController(SliderBanner sliderBanner) {
        mDotView = (DotView) sliderBanner.findViewById(R.id.dotView);
        mSliderBanner = sliderBanner;
        sliderBanner.setAdapter(mBannerAdapter);
    }

    public void play(ArrayList<BannerEntity> list) {
        mBannerAdapter.setData(list);
        mBannerAdapter.notifyDataSetChanged();
        mSliderBanner.setDotNum(list.size());
        mSliderBanner.beginPlay();
    }

    private class InnerAdapter extends BannerAdapter {
        private ArrayList<BannerEntity> mDataList;

        public void setData(ArrayList<BannerEntity> data) {
            mDataList = data;
        }

        public BannerEntity getItem(int position) {
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

            BannerEntity item = getItem(position);
            SimpleDraweeView imageView = (SimpleDraweeView) convertView.findViewById(R.id.ivBanner);
            if (TextUtils.isEmpty(item.getImgUrl())){
                imageView.setImageURI(null);
            }
            else {
                imageView.setImageURI(Uri.parse(item.getImgUrl()));
            }


            TextView titleTextView = (TextView) convertView.findViewById(R.id.tvBanner);
            titleTextView.setText(item.getTitle());

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

        private BannerEntity model;
        private int position;

        public OnClickItemListener(BannerEntity model, int position) {
            this.model = model;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            EventCenter.getInstance().post(new ActDetailBannerClickEvent(model, position));
        }
    }

}
