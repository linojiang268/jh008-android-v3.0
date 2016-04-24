package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.UpdateInfoEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.widget.TitleBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import butterknife.InjectView;

/**
 * 兴趣页，从ShowPage直接跳转此页
 * Created by Levi on 2015/7/7.
 */
public class IntrestPage extends BaseActivity {

    //启动模式
    public static final String EXTRA_MODE = "extra_mode";

    //如果是登录需要完善资料的用户，要把手机号传过来
    public static final String EXTRA_PHONE = "extra_phone";

    /**
     * 启动模式类型
     */
    //注册模式(默认)
    public static final int MODE_REGISTER = 0;
    //资料完善模式
    public static final int MODE_FIX_INFO = 1;
    //选择兴趣标签模式
    public static final int MODE_MODIFY = 2;



    private static  final int[] INTREST_BG = {R.color.red, R.color.yellow, R.color.green, R.color.blue, R.color.style_color_primary, R.color.red, R.color.yellow, R.color.green, R.color.blue, R.color.style_color_primary, R.color.red, R.color.yellow};


    @InjectView(R.id.gv_intrest)
    GridView gvIntrest;

    @InjectView(R.id.titlebar)
    TitleBar titleBar;

    private Stack<String> selectedMap = new Stack<>();

    private int mode;

    private List<String> INTREST_LIST;

    private LoadingDialog loadingDialog;
    private UserInfoEntity userInfo;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intrest_page);

        Intent intent = getIntent();
        mode = intent.getIntExtra(EXTRA_MODE, MODE_REGISTER);
        if (mode == MODE_MODIFY){
            userInfo = UserPref.getInstance().getUserInfo();
            List<String> list = userInfo.getTagIdsForArray();
            if (list!= null && list.size() > 0){
                for (int i = 0; i < list.size(); i++){
                    int pos = Integer.parseInt(list.get(i)) - 1;
                    selectedMap.add(String.valueOf(pos));
                }
            }
        }
        if (mode == MODE_FIX_INFO) {
            phone = intent.getExtras().getString(EXTRA_PHONE, "");
        }
        initView();
    }

    private void initView(){
        INTREST_LIST = Arrays.asList(getResources().getStringArray(R.array.intrest));


        DisplayMetrics metrics = getResources().getDisplayMetrics();

        titleBar.setHeaderTitle(R.string.intresting);
        titleBar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        TextView btnNext = new TextView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnNext.setLayoutParams(layoutParams);
        btnNext.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size));
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding_5);
        btnNext.setPadding(padding, 0, padding, 0);
        btnNext.setGravity(Gravity.CENTER);
        btnNext.setBackgroundResource(R.drawable.titlebar_btn_click_style);
        btnNext.setTextColor(getResources().getColor(R.color.white));
        btnNext.setTextSize(16);
        btnNext.setText(mode == MODE_MODIFY ? R.string.save : R.string.next_step);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == MODE_MODIFY){
                    completeAction();
                }
                else {
                    nextstepAction();
                }
            }
        });
        titleBar.setCustomizedRightView(btnNext);


//        int statusBarHeight = PhoneManager.getStatusBarHigh();
        int itemWidth = (int) ((metrics.widthPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8 * 3, metrics)) / 2);
//        int itemHeight = (int) ((metrics.heightPixels - statusBarHeight - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,48 * 2 + 5 * 5, metrics)) / 4);
        int itemHeight = itemWidth * 3 / 4;

        gvIntrest.setColumnWidth(itemWidth);
        gvIntrest.setAdapter(new IntrestsAdapter(itemWidth, itemHeight));
        gvIntrest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String key = String.valueOf(i);
                ImageView ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
                RelativeLayout itemFrame = (RelativeLayout) view.findViewById(R.id.item_frame);
                ImageView ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                if (selectedMap.contains(String.valueOf(i))){
                    selectedMap.remove(key);
                    ivSelected.setVisibility(View.GONE);
                    tvTitle.setTextColor(0xFF999999);
                    setUnSelectBackground(i, itemFrame, ivLogo);
                } else {
                    selectedMap.add(key);
                    ivSelected.setVisibility(View.VISIBLE);
                    tvTitle.setTextColor(0xFFFFFFFF);
                    setSelectBackground(i, itemFrame, ivLogo);
                }
            }
        });
    }

    private ArrayList<String> getSelectedIntrstList(){
        Iterator<String> iterator = selectedMap.iterator();
        ArrayList<String> list = new ArrayList<>();
        while (iterator.hasNext()){
            int realId = Integer.parseInt(iterator.next()) + 1;
            list.add(String.valueOf(realId));
        }
        return  list;
    }

    /**
     * 完成
     */
    private void completeAction(){
        if (selectedMap.size() == 0){
            toast(R.string.choose_intresting);
        }
        else {
            if (loadingDialog == null){
                loadingDialog = LoadingDialog.createDialog(this, true);
                loadingDialog.setMessage(getString(R.string.saving));
            }
            loadingDialog.show();
            BaseParams params = new BaseParams(API.MODIFY_PROFILE);
            ArrayList<String> tagIds = getSelectedIntrstList();
            for (int i = 0; i < tagIds.size(); i++){
                params.put("tagIds["+i+"]", tagIds.get(i));
            }
            params.put("nick_name", userInfo.getNickName());
            params.put("gender", userInfo.getGender());
            params.put("birthday", userInfo.getBirthday());
            params.put("mobile", userInfo.getMobile());
            OkHttpUtil.post(params, new ResponseHandler() {
                @Override
                public void success(String msg) {
                    loadingDialog.dismiss();
                    toast(R.string.modify_info_success);
                    EventCenter.getInstance().post(new UpdateInfoEvent());
                    finish();
                }

                @Override
                public void fail(int code, String error) {
                    loadingDialog.dismiss();
                    toast(error);
                }
            });
        }
    }

    /**
     * 下一步
     */
    private void nextstepAction(){
        if (selectedMap.size() == 0){
            toast(R.string.choose_intresting);
        }
        else {
            Intent intent = new Intent(IntrestPage.this, RegisterInfo.class);
            intent.putExtra(RegisterInfo.EXTRA_MODE, mode);
            if (mode == MODE_FIX_INFO) {
                intent.putExtra(RegisterInfo.EXTRA_PHONE, phone);
            }
            intent.putStringArrayListExtra(RegisterInfo.EXTRA_DATA, getSelectedIntrstList());
            startActivity(intent);
        }
    }

    private class IntrestsAdapter extends BaseAdapter {
        private int width, height;

        public IntrestsAdapter(int w, int h){
            width = w;
            height = h;
        }

        @Override
        public int getCount() {
            return INTREST_LIST.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null){
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_intrest,null);
                holder.itemFrame = (RelativeLayout) view.findViewById(R.id.item_frame);
                holder.ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
                holder.ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
                holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);

                ViewGroup.LayoutParams layoutParams = holder.itemFrame.getLayoutParams();
                layoutParams.height = height;
                layoutParams.width = width;
                holder.itemFrame.setLayoutParams(layoutParams);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }
            String title = INTREST_LIST.get(i);
            holder.tvTitle.setText(title);
            if (selectedMap.contains(String.valueOf(i))){
                holder.ivSelected.setVisibility(View.VISIBLE);
                holder.tvTitle.setTextColor(0xFFFFFFFF);
                setSelectBackground(i, holder.itemFrame, holder.ivLogo);
            }
            else {
                holder.ivSelected.setVisibility(View.GONE);
                holder.tvTitle.setTextColor(0xFF999999);
                setUnSelectBackground(i, holder.itemFrame, holder.ivLogo);
            }
            return view;
        }
    }

    private class  ViewHolder {
        ImageView ivLogo, ivSelected;
        RelativeLayout itemFrame;
        TextView tvTitle;
    }

    private void setSelectBackground(int position, View view, ImageView imageView) {
        view.setBackgroundColor(getResources().getColor(INTREST_BG[position]));
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.icon_sport_white);
                break;
            case 1:
                imageView.setImageResource(R.drawable.icon_art_white);
                break;
            case 2:
                imageView.setImageResource(R.drawable.icon_travel_white);
                break;
            case 3:
                imageView.setImageResource(R.drawable.icon_music_white);
                break;
            case 4:
                imageView.setImageResource(R.drawable.icon_join_white);
                break;
            case 5:
                imageView.setImageResource(R.drawable.icon_read_white);
                break;
            case 6:
                imageView.setImageResource(R.drawable.icon_food_white);
                break;
            case 7:
                imageView.setImageResource(R.drawable.icon_health_white);
                break;
            case 8:
                imageView.setImageResource(R.drawable.icon_business_white);
                break;
            case 9:
                imageView.setImageResource(R.drawable.icon_gongyi_white);
                break;
            case 10:
                imageView.setImageResource(R.drawable.icon_family_white);
                break;
            case 11:
                imageView.setImageResource(R.drawable.icon_other_white);
                break;
        }
    }

    private void setUnSelectBackground(int position, View view, ImageView imageView) {
        view.setBackgroundColor(0xFFCCCCCC);
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.icon_sport_gray);
                break;
            case 1:
                imageView.setImageResource(R.drawable.icon_art_gray);
                break;
            case 2:
                imageView.setImageResource(R.drawable.icon_travel_gray);
                break;
            case 3:
                imageView.setImageResource(R.drawable.icon_music_gray);
                break;
            case 4:
                imageView.setImageResource(R.drawable.icon_join_gray);
                break;
            case 5:
                imageView.setImageResource(R.drawable.icon_read_gray);
                break;
            case 6:
                imageView.setImageResource(R.drawable.icon_food_gray);
                break;
            case 7:
                imageView.setImageResource(R.drawable.icon_health_gray);
                break;
            case 8:
                imageView.setImageResource(R.drawable.icon_business_gray);
                break;
            case 9:
                imageView.setImageResource(R.drawable.icon_gongyi_gray);
                break;
            case 10:
                imageView.setImageResource(R.drawable.icon_family_gray);
                break;
            case 11:
                imageView.setImageResource(R.drawable.icon_other_gray);
                break;
        }
    }
}
