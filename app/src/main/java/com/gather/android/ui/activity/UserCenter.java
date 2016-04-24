package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.TimeUtil;
import com.shizhefei.HttpResponseStatus;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCNormalHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 个人中心（个人名片）
 * Created by Administrator on 2015/7/9.
 */
public class UserCenter extends BaseActivity {

    @InjectView(R.id.btn_backpress)
    ImageButton btnBackpress;
    @InjectView(R.id.ivUserIcon)
    SimpleDraweeView ivUserIcon;
    @InjectView(R.id.ivUserSex)
    ImageView ivUserSex;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvUserAge)
    TextView tvUserAge;
    @InjectView(R.id.gridView)
    GridView gridView;
    @InjectView(R.id.btChat)
    Button btChat;
    @InjectView(R.id.llChat)
    LinearLayout llChat;
    @InjectView(R.id.rlHeader)
    RelativeLayout rlHeader;

    private MVCHelper<List<Integer>> listViewHelper;
    private int userSex = 0;
    private String userId;
    private UserInfoEntity userInfoEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center);
        Intent intent = getIntent();
        if (intent.hasExtra("USER_ID") || intent.hasExtra("MODEL")) {
            if (intent.hasExtra("MODEL")) {
                userInfoEntity = (UserInfoEntity) intent.getSerializableExtra("MODEL");
                userId = userInfoEntity.getUid();
                userSex = userInfoEntity.getGender();
            } else {
                userId = intent.getExtras().getString("USER_ID");
            }
            llChat.setVisibility(View.GONE);
            btnBackpress.setImageResource(R.drawable.ic_backpress);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlHeader.getLayoutParams();
            params.width = PhoneManager.getDisplayMetrics(this).widthPixels;
            params.height = params.width * 12 / 16;
            rlHeader.setLayoutParams(params);

            setUserInfo();

            listViewHelper = new MVCNormalHelper<List<Integer>>(gridView);
            listViewHelper.setDataSource(new UserMarkSource());
            listViewHelper.setAdapter(new MarkAdapter(this));
            listViewHelper.refresh();
        } else {
            toast("页面信息错误");
            finish();
        }
    }

    /**
     * 显示个人信息
     */
    private void setUserInfo() {
        if (userSex == 2) {
            btnBackpress.setBackgroundResource(R.drawable.user_center_female_btn_click_style);
            rlHeader.setBackgroundColor(getResources().getColor(R.color.red));
            ivUserSex.setImageResource(R.drawable.icon_user_sex_female);
        } else {
            btnBackpress.setBackgroundResource(R.drawable.user_center_male_btn_click_style);
            rlHeader.setBackgroundColor(getResources().getColor(R.color.blue));
            ivUserSex.setImageResource(R.drawable.icon_user_sex_male);
        }
        if (userInfoEntity != null) {
            ivUserIcon.setImageURI(Uri.parse(userInfoEntity.getAvatarUrl()));
            tvUserName.setText(userInfoEntity.getNickName());
            if (!TextUtils.isEmpty(userInfoEntity.getBirthday())) {
                tvUserAge.setText(TimeUtil.getUserAge(userInfoEntity.getBirthday()) + "岁");
            }
        }
    }

    @OnClick({R.id.btn_backpress, R.id.ivUserIcon})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backpress:
                onBackPressed();
                break;
            case R.id.ivUserIcon:
                if (!TextUtils.isEmpty(userInfoEntity.getAvatarUrl())) {
                    Intent intent = new Intent(UserCenter.this, PhotoGallery.class);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(userInfoEntity.getAvatarUrl());
                    intent.putExtra("LIST", list);
                    intent.putExtra("POSITION", 0);
                    intent.putExtra("POSITION", 0);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
                break;
        }
    }

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    setUserInfo();
                    break;
            }
            return false;
        }
    };

    private final Handler handler = new Handler(callback);

    private class UserMarkSource extends BaseDataSource<List<Integer>> {

        @Override
        public List<Integer> refresh() throws Exception {
            BaseParams params = new BaseParams(API.MODIFY_PROFILE);
            params.put("user_id", userId);
            HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
            if (status.isSuccess()) {
                UserInfoEntity entity = JSON.parseObject(status.getResult(), UserInfoEntity.class);
                if (entity != null) {
                    userInfoEntity = entity;
                    userSex = userInfoEntity.getGender();
                    handler.sendEmptyMessage(0);
                    return userInfoEntity.getIntrestIds();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public List<Integer> loadMore() throws Exception {
            return null;
        }

        @Override
        public boolean hasMore() {
            return false;
        }
    }

    private class MarkAdapter extends BaseAdapter implements IDataAdapter<List<Integer>> {

        private List<Integer> list = new ArrayList<Integer>();
        private Context context;
        private List<String> intrestList;

        public MarkAdapter(Context context) {
            super();
            this.context = context;
            intrestList = Arrays.asList(getResources().getStringArray(R.array.intrest));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Integer getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.item_user_mark, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Integer id = getItem(position);
            holder.tvMark.setText(intrestList.get(id - 1));
            setMarkView(id - 1, holder.tvMark, holder.ivMark);

            return convertView;
        }


        @Override
        public void notifyDataChanged(List<Integer> strings, boolean isRefresh) {
            if (isRefresh) {
                list.clear();
            }
            list.addAll(strings);
            notifyDataSetChanged();
        }

        @Override
        public List<Integer> getData() {
            return list;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        class ViewHolder {
            ImageView ivMark;
            TextView tvMark;
            RelativeLayout llItemAll;

            public ViewHolder(View view) {
                ivMark = (ImageView) view.findViewById(R.id.ivMark);
                tvMark = (TextView) view.findViewById(R.id.tvMark);
                llItemAll = (RelativeLayout) view.findViewById(R.id.llItemAll);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llItemAll.getLayoutParams();
                params.width = (PhoneManager.getDisplayMetrics(UserCenter.this).widthPixels - 4 * PhoneManager.dip2px(8)) / 3;
                params.height = params.width;
                llItemAll.setLayoutParams(params);
            }
        }
    }

    private void setMarkView(int position, TextView view, ImageView imageView) {
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.icon_sport_user);
                view.setTextColor(getResources().getColor(R.color.red));
                break;
            case 1:
                imageView.setImageResource(R.drawable.icon_art_user);
                view.setTextColor(getResources().getColor(R.color.green));
                break;
            case 2:
                imageView.setImageResource(R.drawable.icon_travel_user);
                view.setTextColor(getResources().getColor(R.color.blue));
                break;
            case 3:
                imageView.setImageResource(R.drawable.icon_music_user);
                view.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 4:
                imageView.setImageResource(R.drawable.icon_join_user);
                view.setTextColor(getResources().getColor(R.color.style_color_primary));
                break;
            case 5:
                imageView.setImageResource(R.drawable.icon_read_user);
                view.setTextColor(getResources().getColor(R.color.red));
                break;
            case 6:
                imageView.setImageResource(R.drawable.icon_food_user);
                view.setTextColor(getResources().getColor(R.color.green));
                break;
            case 7:
                imageView.setImageResource(R.drawable.icon_health_user);
                view.setTextColor(getResources().getColor(R.color.blue));
                break;
            case 8:
                imageView.setImageResource(R.drawable.icon_business_user);
                view.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 9:
                imageView.setImageResource(R.drawable.icon_gongyi_user);
                view.setTextColor(getResources().getColor(R.color.style_color_primary));
                break;
            case 10:
                imageView.setImageResource(R.drawable.icon_family_user);
                view.setTextColor(getResources().getColor(R.color.red));
                break;
            case 11:
                imageView.setImageResource(R.drawable.icon_other_user);
                view.setTextColor(getResources().getColor(R.color.green));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
    }
}
