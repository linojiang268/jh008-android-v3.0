package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActScoreEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.RatingBarView;
import com.gather.android.widget.TitleBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 活动评分
 * Created by Administrator on 2015/7/20.
 */
public class ActScore extends BaseActivity implements RatingBarView.OnRatingListener {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.tvActTime)
    TextView tvActTime;
    @InjectView(R.id.tvActName)
    TextView tvActName;
    @InjectView(R.id.tvActDetail)
    TextView tvActDetail;
    @InjectView(R.id.tvActOrg)
    TextView tvActOrg;
    @InjectView(R.id.tvActPhoto)
    TextView tvActPhoto;
    @InjectView(R.id.llLowView)
    LinearLayout llLowView;
    @InjectView(R.id.gridView)
    NoScrollGridView gridView;
    @InjectView(R.id.tvEditText)
    EditText tvEditText;
    @InjectView(R.id.llHighView)
    LinearLayout llHighView;
    @InjectView(R.id.ratingBar)
    RatingBarView ratingBar;
    @InjectView(R.id.btnCommit)
    Button btnCommit;

    private ArrayList<MarkModel> markList = new ArrayList<MarkModel>();
    private MarkAdapter adapter;
    private ArrayList<ActScoreEntity> list;
    private int position;
    private int ratingStar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_score);

        Intent intent = getIntent();
        if (intent.hasExtra("LIST") && intent.hasExtra("POSITION")) {
            list = (ArrayList<ActScoreEntity>) intent.getSerializableExtra("LIST");
            position = intent.getExtras().getInt("POSITION");

            titlebar.setHeaderTitle("评价");
            titlebar.getBackImageButton().setVisibility(View.GONE);

            ratingBar.setOnRatingListener(this);

            adapter = new MarkAdapter(this);
            gridView.setAdapter(adapter);

            initView();
        } else {
            finish();
            toast("评分信息错误");
        }
    }

    private void initView() {
        llLowView.setVisibility(View.GONE);
        llHighView.setVisibility(View.VISIBLE);
        btnCommit.setVisibility(View.INVISIBLE);

        ActScoreEntity entity = list.get(position);
        tvActTime.setText(TimeUtil.getYMD(entity.getBegin_time()));
        tvActName.setText(entity.getTitle());

       List<String> mark = Arrays.asList(getResources().getStringArray(R.array.act_score));
        for (int i = 0; i < mark.size(); i++) {
            MarkModel model = new MarkModel();
            model.setMark(mark.get(i));
            markList.add(model);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return true;
        }
    }

    @OnClick({R.id.btnCommit})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btnCommit:
                Commit();
                break;
        }
    }

    private void Commit() {
        if (ratingStar == 0) {
            toast("请先评分");
            return;
        }
        BaseParams params = new BaseParams(API.ACT_SCORE);
        params.put("activity", list.get(position).getId());
        params.put("score", ratingStar);
        if (ratingStar < 3) {
            if (!TextUtils.isEmpty(tvEditText.getText().toString().trim())) {
                params.put("memo", tvEditText.getText().toString().trim());
            }
            int index = -1;
            for (int i = 0; i < markList.size(); i++) {
                if (markList.get(i).isHasSelected()) {
                    index = index + 1;
                    params.put("attributes[" + index + "]", markList.get(i).getMark());
                }
            }
        }
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                toast("评价成功");
                if (position == list.size() - 1) {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                } else {
                    Intent intent = new Intent(ActScore.this, ActScore.class);
                    intent.putExtra("LIST", list);
                    intent.putExtra("POSITION", position + 1);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }

            @Override
            public void fail(int code, String error) {
                toast("提交失败，请重试");
            }
        });
    }

    private class MarkAdapter extends BaseAdapter {

        private Context context;

        public MarkAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return markList.size();
        }

        @Override
        public MarkModel getItem(int position) {
            return markList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.item_score_mark, null);
                holder = new ViewHolder();

                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.textView.getLayoutParams();
                params.width = (metrics.widthPixels - PhoneManager.dip2px(16 * 2 + 10)) / 2;
                params.height = PhoneManager.dip2px(40);
                holder.textView.setLayoutParams(params);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MarkModel model = getItem(position);
            holder.textView.setText(model.getMark());
            holder.textView.setSelected(model.hasSelected);
            holder.textView.setOnClickListener(new OnMarkClickListener(model));
            return convertView;
        }

        private class ViewHolder {
            TextView textView;
        }

        private class OnMarkClickListener implements View.OnClickListener {

            private MarkModel model;

            public OnMarkClickListener(MarkModel model) {
                this.model = model;
            }

            @Override
            public void onClick(View v) {
                if (model.hasSelected) {
                    model.setHasSelected(false);
                } else {
                    model.setHasSelected(true);
                }
                notifyDataSetChanged();
            }
        }
    }

    private class MarkModel {

        private String mark;
        private boolean hasSelected;

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public boolean isHasSelected() {
            return hasSelected;
        }

        public void setHasSelected(boolean hasSelected) {
            this.hasSelected = hasSelected;
        }
    }

    @Override
    public void onRating(Object bindObject, int RatingScore) {
        ratingStar = RatingScore;
        if (!btnCommit.isShown()) {
            btnCommit.setVisibility(View.VISIBLE);
        }
        if (RatingScore <= 3) {
            llLowView.setVisibility(View.VISIBLE);
            llHighView.setVisibility(View.GONE);
        } else {
            llLowView.setVisibility(View.GONE);
            llHighView.setVisibility(View.VISIBLE);
        }
    }
}
