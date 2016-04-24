package com.gather.android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.colonel.ui.activity.ActMgrList;
import com.gather.android.ui.activity.AboutUs;
import com.gather.android.ui.activity.ActDetail;
import com.gather.android.ui.activity.Message;
import com.gather.android.ui.activity.MyAct;
import com.gather.android.ui.activity.OrgHome;
import com.gather.android.ui.activity.SelectCity;
import com.gather.android.ui.activity.UserInfo;
import com.gather.android.ui.activity.WebActivity;
import com.gather.android.adapter.HomeMyActAdapter;
import com.gather.android.adapter.HomeOrgListAdapter;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.data.UserPref;
import com.gather.android.entity.ActListEntity;
import com.gather.android.entity.HomeBannerEntity;
import com.gather.android.entity.HomeBannerListEntity;
import com.gather.android.entity.HomeNoMyActEvent;
import com.gather.android.entity.MessageEntity;
import com.gather.android.entity.OrgListEntity;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeBannerClickEvent;
import com.gather.android.event.HomeDataUpdateEvent;
import com.gather.android.event.UpdateUserIconEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.qrcode.QRCodeScanner;
import com.gather.android.utils.Checker;
import com.gather.android.utils.HomeBannerController;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.banner.DotView;
import com.gather.android.widget.banner.SliderBanner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.Subscribe;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 首页
 * Created by Administrator on 2015/9/18.
 */
public class HomeFragment extends BaseFragment {

    @InjectView(R.id.titlebar)
    Toolbar titlebar;
    @InjectView(R.id.sliderBanner)
    SliderBanner sliderBanner;
    @InjectView(R.id.lineBanner)
    View lineBanner;
    @InjectView(R.id.OrgList)
    RecyclerView OrgList;
    @InjectView(R.id.llOrg)
    LinearLayout llOrg;
    @InjectView(R.id.tvNoMyAct)
    TextView tvNoMyAct;
    @InjectView(R.id.llMore)
    LinearLayout llMore;
    @InjectView(R.id.llNoAct)
    LinearLayout llNoAct;
    @InjectView(R.id.listview)
    NoScrollListView listview;
    @InjectView(R.id.llAct)
    LinearLayout llAct;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;
    @InjectView(R.id.ivUserIcon)
    SimpleDraweeView ivUserIcon;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.ivMore)
    ImageView ivMore;
    @InjectView(R.id.mask)
    LinearLayout mask;
    @InjectView(R.id.ivArrowLeft)
    ImageView ivArrowLeft;
    @InjectView(R.id.ivArrowRight)
    ImageView ivArrowRight;
    @InjectView(R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.dotView)
    DotView dotView;
    @InjectView(R.id.ivLoadIcon)
    ImageView ivLoadIcon;
    @InjectView(R.id.tvLoading)
    TextView tvLoading;
    @InjectView(R.id.ivColonel)
    ImageView ivColonel;

    private PopupWindow popWind;
    private HomeMyActAdapter myActAdapter;
    private HomeOrgListAdapter orgListAdapter;

    private boolean isOrgLoading = false;//正在加载社团
    private boolean isBannerLoading = false;//正在加载banner
    private boolean isActLoading = false;//正在加载活动

    private boolean isFirstLoadFail;
    private boolean loadOver = false;
    private boolean isFirstLoadData = true;
    private HomeHandler handler = new HomeHandler();
    private volatile  int ref = 0;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_home);
        ButterKnife.inject(this, getContentView());
        initView();
    }

    private void initView() {
        EventCenter.getInstance().register(this);
        resetInfo();
//        loadUserInfo();

        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);
        ptrLayout.disableWhenHorizontalMove(true);
        ptrLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshAllData();
            }
        });

        /**
         * 设置banner高度
         */
        LinearLayout.LayoutParams bannerParams = (LinearLayout.LayoutParams) sliderBanner.getLayoutParams();
        bannerParams.height = getResources().getDisplayMetrics().widthPixels / 2;
        sliderBanner.setLayoutParams(bannerParams);

        /**
         * 社团头像水平滑动列表
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        OrgList.setLayoutManager(linearLayoutManager);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, metrics);
        int itemWidth = (metrics.widthPixels - 2 * padding) / 4;
        int itemHeight = itemWidth * 4 / 5;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) OrgList.getLayoutParams();
        params.height = itemHeight;
        OrgList.setLayoutParams(params);

        orgListAdapter = new HomeOrgListAdapter(getActivity(), itemWidth, itemHeight);
        OrgList.setAdapter(orgListAdapter);


        /**
         * 我的活动列表
         */
        myActAdapter = new HomeMyActAdapter(getActivity());
        listview.setAdapter(myActAdapter);


        mask.setVisibility(View.VISIBLE);
        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadOver) {
                    ivLoadIcon.setImageResource(R.drawable.icon_loading_tips);
                    tvLoading.setText("玩命加载中......");
                    refreshAllData();
                }
            }
        });
        isFirstLoadData = true;
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        checkLocation();
        firstLoadData();
    }


    private void firstLoadData() {
        if (isFirstLoadData) {
            isFirstLoadData = false;
            isFirstLoadFail = false;
            refreshAllData();
        }
    }

    private synchronized void firstLoadFail() {
        isFirstLoadFail = true;
    }

    private void refreshAllData() {
        loadOver = false;
        ref = 0;
        getMyOrgList();
        getMyActData();
        getBannerData();
    }

    private class HomeHandler extends Handler{
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 1:
                    loadOver = true;
                    isFirstLoadFail = false;
                    ivLoadIcon.setImageResource(R.drawable.icon_error_tips);
                    tvLoading.setText("加载失败，请检查网络并重试");
                    break;
                case 2:
                    mask.setVisibility(View.GONE);
                    AlphaAnimation anim = new AlphaAnimation(1f, 0f);
                    anim.setDuration(500);
                    mask.startAnimation(anim);
                    break;
            }
        }
    }


    private void refreshOver() {
        ref ++;
        synchronized (this) {
            if (ref == 3) {
                if (mask.getVisibility() == View.VISIBLE) {
                    if (isFirstLoadFail) {
                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        handler.sendEmptyMessageDelayed(2, 500);
                    }
                } else {
                    ptrLayout.refreshComplete();
                }
            }
        }
    }


    @OnClick({R.id.ivUserIcon, R.id.ivMore, R.id.tvTitle, R.id.tvNoMyAct, R.id.llMore, R.id.ivColonel})
    void OnClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ivUserIcon:
                intent = new Intent(getActivity(), UserInfo.class);
                startActivity(intent);
                break;
            case R.id.tvTitle:
                intent = new Intent(getActivity(), SelectCity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_up_in, android.R.anim.fade_out);
                break;
            case R.id.ivMore:
                popMore();
                break;
            case R.id.tvNoMyAct:
                EventCenter.getInstance().post(new HomeNoMyActEvent());
                break;
            case R.id.llMore:
                intent = new Intent(getActivity(), MyAct.class);
                startActivity(intent);
                break;
            case R.id.ivColonel:
                intent = new Intent(getActivity(), ActMgrList.class);
                startActivity(intent);
                break;
        }
    }


    @Subscribe
    public void onEvent(UpdateUserIconEvent event) {
        resetInfo();
    }

    @Subscribe
    public void onEvent(HomeDataUpdateEvent event) {
        if (event != null) {
            switch (event.getType()) {
                case 0://刷新活动
                    getMyActData();
                    break;
                case 1://刷新社团
                    getMyOrgList();
                    break;
                case 2://刷新活动社团
                    getMyActData();
                    getMyOrgList();
                    break;
            }
        }
    }

    /**
     * Banner点击
     */
    @Subscribe
    public void onEvent(HomeBannerClickEvent event) {
        Intent intent = null;
        String type = event.getModel().getType();
        if (type.equals(MessageEntity.TYPE_TEXT)) {

        } else if (type.equals(MessageEntity.TYPE_URL)) {
            try {
                intent = new Intent(getActivity(), WebActivity.class);
                JSONObject object = new JSONObject(event.getModel().getAttributes());
                intent.putExtra("URL", object.getString("url"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equals(MessageEntity.TYPE_ACTIVITY)) {
            try {
                intent = new Intent(getActivity(), ActDetail.class);
                JSONObject object = new JSONObject(event.getModel().getAttributes());
                intent.putExtra(ActDetail.EXTRA_ID, object.getString("activity_id"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equals(MessageEntity.TYPE_TEAM)) {
            try {
                intent = new Intent(getActivity(), OrgHome.class);
                JSONObject object = new JSONObject(event.getModel().getAttributes());
                intent.putExtra("ID", object.getString("team_id"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (Constant.SHOW_LOG) {
            Toast.makeText(getActivity(), "未知类型", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查定位信息
     */
    private void checkLocation() {
        CityPref cityPref = CityPref.getInstance();
        if (cityPref.hasSelectedCity()) {
            tvTitle.setText(cityPref.getCityName());
        }
    }

    /**
     * 更多
     */
    private void popMore() {
        if (popWind == null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.pop_home_more, null);

            LinearLayout llQrCode = (LinearLayout) view.findViewById(R.id.llQrCode);
            llQrCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWind.dismiss();
                    Intent intent = new Intent(getActivity(), QRCodeScanner.class);
                    startActivity(intent);
                }
            });
            LinearLayout llMsg = (LinearLayout) view.findViewById(R.id.llMsg);
            llMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWind.dismiss();
                    Intent intent = new Intent(getActivity(), Message.class);
                    startActivity(intent);
                }
            });
            LinearLayout llAbout = (LinearLayout) view.findViewById(R.id.llAbout);
            llAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWind.dismiss();
                    Intent intent = new Intent(getActivity(), AboutUs.class);
                    startActivity(intent);
                }
            });

            popWind = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popWind.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            popWind.setOutsideTouchable(true);
            popWind.setAnimationStyle(R.style.DialogTopInAnim);

//            popWind.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    RotateAnimation animation = new RotateAnimation(45f, 0f, Animation.RELATIVE_TO_SELF,
//                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                    animation.setFillAfter(true);
//                    animation.setDuration(300);
//                    ivMore.startAnimation(animation);
//                }
//            });
        }
//        RotateAnimation animation = new RotateAnimation(0f,45f, Animation.RELATIVE_TO_SELF,
//                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setFillAfter(true);
//        animation.setDuration(300);
//        ivMore.startAnimation(animation);
        popWind.showAsDropDown(ivMore);

    }

    /**
     * 重新设置用户信息
     */
    private void resetInfo() {
        UserPref pref = UserPref.getInstance();
        UserInfoEntity entity = pref.getUserInfo();
        if (entity != null) {
            ivUserIcon.setImageURI(Checker.isEmpty(entity.getAvatarUrl()) ? null : Uri.parse(entity.getAvatarUrl()));
            if (pref.isColonel()){
                ivColonel.setVisibility(View.VISIBLE);
            }
            else {
                ivColonel.setVisibility(View.GONE);
            }
        }
    }

//    /**
//     * 从服务器获取用户信息
//     */
//    private void loadUserInfo(){
//        BaseParams params = new BaseParams(API.GET_PROFILE);
//        OkHttpUtil.get(params, new ResponseHandler() {
//            @Override
//            public void success(String msg) {
//                try {
//                    UserInfoEntity entity = JSON.parseObject(msg, UserInfoEntity.class);
//                    UserPref.getInstance().updateInfo(entity);
//                    resetInfo();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void fail(int code, String error) {
//                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    /**
     * 获取轮播图
     */
    private void getBannerData() {
        if (isBannerLoading) {
            return;
        }
        isBannerLoading = true;
        BaseParams params = new BaseParams(API.HOME_BANNER);
        params.put("city", CityPref.getInstance().getCityId());
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                HomeBannerListEntity entity = JSON.parseObject(msg, HomeBannerListEntity.class);
                if (entity != null && entity.getBanners().size() > 0) {
                    sliderBanner.setVisibility(View.VISIBLE);
                    lineBanner.setVisibility(View.VISIBLE);
                    setBannerData(entity.getBanners());
                } else {
                    sliderBanner.setVisibility(View.GONE);
                    lineBanner.setVisibility(View.GONE);
                }
                isBannerLoading = false;
                refreshOver();
            }

            @Override
            public void fail(int code, String error) {
                isBannerLoading = false;
                firstLoadFail();
                refreshOver();
            }
        });
    }

    /**
     * 轮播图
     */
    private void setBannerData(ArrayList<HomeBannerEntity> list) {
        HomeBannerController sliderBannerController = new HomeBannerController(sliderBanner);
        sliderBannerController.play(list);
    }

    /**
     * 获取我的活动
     */
    private void getMyActData() {
        if (isActLoading){
            return;
        }
        isActLoading =true;
        BaseParams params = new BaseParams(API.HOME_MY_ACT);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                ActListEntity actList = JSON.parseObject(msg, ActListEntity.class);
                if (actList != null && actList.getActivities() != null && actList.getActivities().size() > 0) {
                    listview.setVisibility(View.VISIBLE);
                    llNoAct.setVisibility(View.GONE);
                    llMore.setVisibility(View.VISIBLE);
                    myActAdapter.setMyActList(actList.getActivities());
                } else {
                    listview.setVisibility(View.GONE);
                    llNoAct.setVisibility(View.VISIBLE);
                    llMore.setVisibility(View.GONE);
                }
                isActLoading = false;
                refreshOver();
            }

            @Override
            public void fail(int code, String error) {
                isActLoading = false;
                firstLoadFail();
                refreshOver();
            }
        });
    }

    /**
     * 获取我的社团列表
     */
    private void getMyOrgList() {
        if (isOrgLoading){
            return;
        }
        isOrgLoading = true;
        BaseParams params = new BaseParams(API.HOME_MY_ORG_LIST);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                OrgListEntity entity = JSON.parseObject(msg, OrgListEntity.class);
                if (entity != null && entity.getTeams() != null && entity.getTeams().size() > 0) {
                    llOrg.setVisibility(View.VISIBLE);
                    orgListAdapter.setMyOrgList(entity.getTeams());
                    if (orgListAdapter.getItemCount() > 4) {
                        ivArrowRight.setVisibility(View.VISIBLE);
                        ivArrowLeft.setVisibility(View.VISIBLE);
                    } else {
                        ivArrowLeft.setVisibility(View.GONE);
                        ivArrowRight.setVisibility(View.GONE);
                    }
                } else {
                    llOrg.setVisibility(View.GONE);
                }
                isOrgLoading = false;
                refreshOver();

            }

            @Override
            public void fail(int code, String error) {
                isOrgLoading = false;
                firstLoadFail();
                refreshOver();
            }
        });
    }


    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        OkHttpUtil.getOkHttpClient().cancel(this);
        ButterKnife.reset(this);
        EventCenter.getInstance().unregister(this);
    }




}
