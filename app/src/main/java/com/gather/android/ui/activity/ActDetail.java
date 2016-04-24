package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.R;
import com.gather.android.adapter.data.ActDetailData;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.ActCheckInEntity;
import com.gather.android.entity.ActDetailEntityy;
import com.gather.android.entity.ActEnrollOrderEntity;
import com.gather.android.entity.BannerEntity;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.entity.VenueModel;
import com.gather.android.event.ActDetailBannerClickEvent;
import com.gather.android.event.ActEnrollTipsEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.event.PayActResultEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.utils.NormalLoadViewFactory;
import com.gather.android.utils.SliderBannerController;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.banner.DotView;
import com.gather.android.widget.banner.SliderBanner;
import com.gather.android.widget.scrolltitlebar.CustomTitleBar;
import com.jihe.dialog.listener.OnOperItemClickL;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.model.ShareContentWebpage;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WechatShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCNormalHelper;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.Subscribe;
import io.yunba.android.manager.YunBaManager;

/**
 * 活动详情
 * Created by Administrator on 2015/7/20.
 */
public class ActDetail extends BaseActivity {
    public static final String EXTRA_ID = "extra_id";
    @InjectView(R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.dotView)
    DotView dotView;
    @InjectView(R.id.sliderBanner)
    SliderBanner sliderBanner;
    @InjectView(R.id.tvIndexImageNum)
    TextView tvIndexImageNum;
    @InjectView(R.id.tvTotalImageNum)
    TextView tvTotalImageNum;
    @InjectView(R.id.tvEnrollNum)
    TextView tvEnrollNum;
    @InjectView(R.id.tvActName)
    TextView tvActName;
    @InjectView(R.id.tvActAddress)
    TextView tvActAddress;
    @InjectView(R.id.tvActTime)
    TextView tvActTime;
    @InjectView(R.id.ivActFlow)
    ImageView ivActFlow;
    @InjectView(R.id.tvActMember)
    TextView tvActMember;
    @InjectView(R.id.llActFlow)
    LinearLayout llActFlow;
    @InjectView(R.id.ivActAlbum)
    ImageView ivActAlbum;
    @InjectView(R.id.tvActAlbum)
    TextView tvActAlbum;
    @InjectView(R.id.llActAlbum)
    LinearLayout llActAlbum;
    @InjectView(R.id.ivActFile)
    ImageView ivActFile;
    @InjectView(R.id.tvActFile)
    TextView tvActFile;
    @InjectView(R.id.llActFile)
    LinearLayout llActFile;
    @InjectView(R.id.ivActRoute)
    ImageView ivActRoute;
    @InjectView(R.id.tvActRoute)
    TextView tvActRoute;
    @InjectView(R.id.llActRoute)
    LinearLayout llActRoute;
    @InjectView(R.id.tvActDetail)
    TextView tvActDetail;
    @InjectView(R.id.llActDetail)
    LinearLayout llActDetail;
    @InjectView(R.id.ivOrgIcon)
    SimpleDraweeView ivOrgIcon;
    @InjectView(R.id.tvOrgName)
    TextView tvOrgName;
    @InjectView(R.id.ivPhone)
    ImageView ivPhone;
    @InjectView(R.id.llActOrg)
    LinearLayout llActOrg;
    @InjectView(R.id.scrollView)
    ScrollView scrollView;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.ibtn_backpress)
    ImageButton ibtnBackpress;
    @InjectView(R.id.ivShare)
    ImageButton ivShare;
    @InjectView(R.id.titlebar)
    CustomTitleBar titlebar;
    @InjectView(R.id.tvActPrice)
    TextView tvActPrice;
    @InjectView(R.id.btnEnroll)
    Button btnEnroll;
    @InjectView(R.id.tvEnrollStatus)
    TextView tvEnrollStatus;
    @InjectView(R.id.llActEnroll)
    LinearLayout llActEnroll;
    @InjectView(R.id.flBottomItem)
    FrameLayout flBottomItem;
    @InjectView(R.id.btnActAddress)
    LinearLayout btnActAddress;

    /**
     * 分享
     */
    private String shareTitle = "";
    private String shareContent = "";
    private String shareUrl = "";
    private String shareImageUrl = "";
    private SsoHandler mSsoHandler;

    private MVCHelper<ActDetailEntityy> listViewHelper;

    private LoadingDialog mLoadingDialog;
    private boolean isRequest = false, canShare = false;
    private String actId;
    private ActDetailEntityy entity;
    private Dialog mTipsDialog = null;
    private Dialog mShareDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            actId = intent.getExtras().getString(EXTRA_ID);
            EventCenter.getInstance().register(ActDetail.this);
            mLoadingDialog = LoadingDialog.createDialog(ActDetail.this, true);
            listViewHelper.setLoadViewFractory(new NormalLoadViewFactory());
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) sliderBanner.getLayoutParams();
            params.width = PhoneManager.getScreenWidth();
            params.height = params.width * 3 / 4;
            sliderBanner.setLayoutParams(params);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                titlebar.setMinimumHeight(PhoneManager.dip2px(46));
            }
            titlebar.setTitleTextView((TextView) titlebar.findViewById(R.id.tv_title));
            titlebar.setBackgroundColor(0x00000000);
            titlebar.findViewById(R.id.tv_title).setVisibility(View.GONE);
            titlebar.setTransparentEnabled(true, 50, params.height - PhoneManager.dip2px(48));
            titlebar.addViewToFadeList(ibtnBackpress);
            titlebar.addViewToFadeList(findViewById(R.id.tv_title));
            titlebar.setVisibility(View.GONE);
            ibtnBackpress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            ShareBlock.getInstance().initShare(Constant.WE_CHAT_APPID, Constant.SINA_APPID, Constant.TENCENT_APPID, Constant.WE_CHAT_APPID);
            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (canShare && !shareImageUrl.equals("")) {
                        Share();
                    }
                }
            });
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    titlebar.setBackgroundColor(getResources().getColor(R.color.style_color_primary));
                    titlebar.onScroll(scrollView.getScrollY());
                }
            });

            listViewHelper = new MVCNormalHelper<ActDetailEntityy>(scrollView);
            listViewHelper.setDataSource(new ActDetailData(actId));
            listViewHelper.setAdapter(dataAdapter);
            listViewHelper.refresh();

            llActAlbum.setOnClickListener(onNoteBtnClickListener);
            llActFile.setOnClickListener(onNoteBtnClickListener);
            llActFlow.setOnClickListener(onNoteBtnClickListener);
            llActRoute.setOnClickListener(onNoteBtnClickListener);

        } else {
            toast("活动详情信息错误");
            finish();
        }
    }

    @OnClick({R.id.llActOrg, R.id.ivPhone, R.id.btnEnroll, R.id.btnActAddress, R.id.tvEnrollNum})
    void OnClick(View view) {
        switch (view.getId()) {
            //活动地点
            case R.id.btnActAddress:
                Intent venueIntent = new Intent(this, VenueMap.class);
                venueIntent.putExtra(VenueMap.EXTRA_MODEL, new VenueModel(entity));
                startActivity(venueIntent);
                break;
            //活动参与人列表
            case R.id.tvEnrollNum:
                Intent member = new Intent(this, OrgMember.class);
                member.putExtra("TYPE", OrgMember.ACT);
                member.putExtra("ID", entity.getId());
                startActivity(member);
                break;
            //主办方
            case R.id.llActOrg:
                Intent intent = new Intent(ActDetail.this, OrgHome.class);
                OrgDetailEntity model = new OrgDetailEntity();
                model.setName(entity.getTeam().getName());
                model.setId(entity.getTeam().getId() + "");
                model.setIntroduction(entity.getTeam().getIntroduction());
                model.setLogo_url(entity.getTeam().getLogo_url());
                intent.putExtra("MODEL", model);
                startActivity(intent);
                break;
            //主办方电话
            case R.id.ivPhone:
                Uri uri = Uri.parse("tel:" + entity.getTelephone());
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(it);
                break;
            case R.id.btnEnroll:
                if (entity.getEnroll_type() == 2 && !entity.isEnrolled_team()) {
                    toast("此活动只允许社团成员参加");
                } else {
                    if (btnEnroll.getText().toString().contains("报名")) {
                        Intent enroll = new Intent(ActDetail.this, ActEnrollInfo.class);
                        enroll.putExtra("MODEL", entity);
                        startActivity(enroll);
                    } else if (btnEnroll.getText().toString().contains("付款")) {
                        if (!isRequest) {
                            getActPayOrder();
                        }
                    }
                }
                break;
        }
    }

    //活动手册等相关按钮点击
    private View.OnClickListener onNoteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //需要报名成功
            if (entity.getApplicant_status() != 3) {
                if (mTipsDialog == null){
                    mTipsDialog = DialogCreater.createTipsDialog(ActDetail.this, "温馨提示", "该内容只能在报名成功后查看", "确定", true, null);
                }
                mTipsDialog.show();
                return;
            }
            Intent intent = null;
            switch (v.getId()) {
                case R.id.llActRoute:
                    if (entity.getRoadmap() != null && entity.getRoadmap().size() > 1) {
                        Intent intentRoute = new Intent(ActDetail.this, RouteMap.class);
                        intentRoute.putExtra(RouteMap.EXTRA_ROUTEMAP, (ArrayList<double[]>) entity.getRoadmap());
                        intentRoute.putExtra(RouteMap.EXTRA_ID, entity.getId());
                        if (entity.getApplicant_status() == 3) {
                            intentRoute.putExtra(RouteMap.EXTRA_MEMBER, "");
                        }
                        startActivity(intentRoute);
                    } else {
                        toast(R.string.no_route);
                    }
                    break;
                case R.id.llActAlbum:
                    intent = new Intent(ActDetail.this, OrgPhotoDetail.class);
                    intent.putExtra("ACT_ID", entity.getId());
                    if (entity.getApplicant_status() == 3) {
                        intent.putExtra("ADDED", true);
                    } else {
                        intent.putExtra("ADDED", false);
                    }
                    startActivity(intent);
                    break;
                case R.id.llActFile:
                    intent = new Intent(ActDetail.this, ActFileList.class);
                    intent.putExtra("ACT_ID", entity.getId());
                    startActivity(intent);
                    break;
                case R.id.llActFlow:
                    Intent note = new Intent(ActDetail.this, ActFlow.class);
                    note.putExtra("MODEL", entity.getFlowEntity());
                    startActivity(note);
                    break;
            }
        }
    };

    /**
     * 活动付款下订单
     */
    private void getActPayOrder() {
        isRequest = true;
        mLoadingDialog.setMessage("正在下单...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.GET_ACT_ENROLL_ORDER_INFO);
        params.put("activity_id", actId);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                try {
                    JSONObject object = new JSONObject(msg);
                    ActEnrollOrderEntity model = JSON.parseObject(object.getString("info"), ActEnrollOrderEntity.class);
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    isRequest = false;
                    Intent pay = new Intent(ActDetail.this, PayInfo.class);
                    pay.putExtra(PayInfo.FEE, model.getFee());
                    pay.putExtra(PayInfo.ORDER, model.getOrder_no());
                    pay.putExtra(PayInfo.ACTID, model.getActivity_id());
                    pay.putExtra(PayInfo.ACTENTITY, entity);
                    startActivity(pay);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                        mLoadingDialog.dismiss();
                    }
                    isRequest = false;
                    toast("数据解析失败");
                }
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                isRequest = false;
                finish();
            }
        });
    }

    @Subscribe
    public void onEvent(ActDetailBannerClickEvent event) {
        //轮播图点击
        if (entity.getImages_url().get(0) != null && !entity.getImages_url().get(0).equals("")) {
            int position = event.getPosition();
            Intent intent = new Intent(ActDetail.this, PhotoGallery.class);
            intent.putExtra("LIST", (ArrayList<String>) entity.getImages_url());
            intent.putExtra("POSITION", position);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    @Subscribe
    public void onEvent(ActEnrollTipsEvent event) {
        //活动报名提示页面点击
        if (event.getActId().equals(actId)) {
            entity.setApplicant_status(event.getApplicant_info());
            setEnrollStatus(entity.getApplicant_status());
        }
    }

    @Subscribe
    public void onEvent(PayActResultEvent event) {
        YunBaManager.subscribe(getApplicationContext(), "topic_activity_" + actId, GatherApplication.getInstance().pushListener);
        //支付成功刷新UI界面
        if (listViewHelper != null) {
            listViewHelper.refresh();
        }
    }

    private IDataAdapter<ActDetailEntityy> dataAdapter = new IDataAdapter<ActDetailEntityy>() {
        private ActDetailEntityy data;

        @Override
        public void notifyDataChanged(ActDetailEntityy data, boolean isRefresh) {
            this.data = data;
            entity = data;
            setActDetail(data);
        }

        @Override
        public boolean isEmpty() {
            return data == null;
        }

        @Override
        public ActDetailEntityy getData() {
            return data;
        }
    };

    /**
     * 设置活动详情信息
     */
    private void setActDetail(ActDetailEntityy data) {
        flBottomItem.setVisibility(View.VISIBLE);
        titlebar.setVisibility(View.VISIBLE);
        titlebar.setTitle(data.getTitle());
        tvActName.setText(data.getTitle());
        setOrgLogo(Uri.parse(entity.getTeam().getLogo_url()));
        tvOrgName.setText(entity.getTeam().getName());
        setHtmlDetail(data);
        tvActAddress.setText(data.getAddress());
        if (data.getImages_url().size() != 0) {
            BannerData(data.getImages_url());
        }
        tvTotalImageNum.setText("/" + data.getImages_url().size());
        if (entity.getEnrolled_num() > 0) {
            tvEnrollNum.setVisibility(View.VISIBLE);
            tvEnrollNum.setText("报名：" + entity.getEnrolled_num() + "人");
        } else {
            tvEnrollNum.setVisibility(View.GONE);
        }
        if (TimeUtil.isSameDay(data.getBegin_time(), data.getEnd_time())) {
            tvActTime.setText(TimeUtil.pointStyleDate(data.getBegin_time()) + "—" + TimeUtil.getHM(data.getEnd_time()));
        } else {
            tvActTime.setText(TimeUtil.pointStyleDate(data.getBegin_time()) + "—" + TimeUtil.pointStyleDate(data.getEnd_time()));
        }
        tvActAlbum.setText("照片墙(" + entity.getActivity_album_count() + ")");
        tvActFile.setText("文件(" + entity.getActivity_file_count() + ")");

        if (!Checker.isMobilePhone(entity.getTelephone())){
            ivPhone.setVisibility(View.GONE);
        }
        else {
            ivPhone.setVisibility(View.VISIBLE);
        }

        if (data.getEnroll_fee_type() == 1) {
            //免费
            tvActPrice.setText("免费");
        } else if (data.getEnroll_fee_type() == 2) {
            //AA制
            tvActPrice.setText("AA制");
        } else {
            //收费
            tvActPrice.setText(data.getEnroll_fee() + "元");
        }

        //签到
        boolean hasSignIn = false;
        List<ActCheckInEntity> list = entity.getActivity_check_in_list();
        if (list != null && list.size() > 0){
            for (int i = list.size() - 1; i >= 0; i--) {
                if (entity.getActivity_check_in_list().get(i).getStatus() == 1) {
                    hasSignIn = true;
                    break;
                }
            }
        }
        if (hasSignIn){
            btnEnroll.setVisibility(View.GONE);
            tvEnrollStatus.setVisibility(View.VISIBLE);
            tvEnrollStatus.setText("已签到");
            tvEnrollStatus.setTextColor(getResources().getColor(R.color.green));
        }
        else {
            switch (data.getSub_status()) {
                case 1://筹备中（发布活动到活动报名）
                    btnEnroll.setVisibility(View.GONE);
                    tvEnrollStatus.setVisibility(View.VISIBLE);
                    tvEnrollStatus.setText("即将报名");
                    tvEnrollStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 2://报名中
                    //报名状态
                    if (entity.getEnroll_limit() != 0 && entity.getAuditing() == 0 && entity.getEnrolled_num() >= entity.getEnroll_limit()) {
                        btnEnroll.setVisibility(View.GONE);
                        tvEnrollStatus.setVisibility(View.VISIBLE);
                        tvEnrollStatus.setText("已报满");
                        tvEnrollStatus.setTextColor(0xFF999999);
                    } else {
                        setEnrollStatus(data.getApplicant_status());
                    }
                    break;
                case 3://报名结束（报名结束到活动开始）
                    if (entity.getApplicant_status() == 2) {
                        btnEnroll.setVisibility(View.VISIBLE);
                        btnEnroll.setText("立即付款");
                        tvEnrollStatus.setVisibility(View.GONE);
                    } else {
                        btnEnroll.setVisibility(View.GONE);
                        tvEnrollStatus.setVisibility(View.VISIBLE);
                        tvEnrollStatus.setText("即将开始");
                        tvEnrollStatus.setTextColor(getResources().getColor(R.color.green));
                    }
                    break;
                case 4://进行中
                    btnEnroll.setVisibility(View.GONE);
                    tvEnrollStatus.setVisibility(View.VISIBLE);
                    tvEnrollStatus.setText("进行中");
                    tvEnrollStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case 5:
                    btnEnroll.setVisibility(View.GONE);
                    tvEnrollStatus.setVisibility(View.VISIBLE);
                    tvEnrollStatus.setText("已结束");
                    tvEnrollStatus.setTextColor(0xFF999999);
                    break;
            }
        }
    }

    /**
     * 加载html的活动详情
     */
    private void setHtmlDetail(ActDetailEntityy data) {
        tvActDetail.setMovementMethod(LinkMovementMethod.getInstance());
        new HtmlDetailProgress().execute(data.getDetail());
    }

    private class HtmlDetailProgress extends AsyncTask<String, Void, Spanned> {

        @Override
        protected Spanned doInBackground(String... params) {
            Html.ImageGetter imgGetter = new Html.ImageGetter() {
                public Drawable getDrawable(String source) {
                    Drawable drawable = null;
                    URL url;
                    try {
                        url = new URL(source);
                        drawable = Drawable.createFromStream(url.openStream(), null);  //获取网路图片
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    } catch (Exception e) {
                        drawable = null;
                    }
                    return drawable;
                }
            };
            return Html.fromHtml(params[0], imgGetter, null);
        }

        @Override
        protected void onPostExecute(Spanned spanned) {
            tvActDetail.setText(spanned);
        }
    }

    /**
     * 社团Logo加载
     */
    private void setOrgLogo(final Uri uri) {
        canShare = false;
        ControllerListener controllerListener = new BaseControllerListener() {

            @Override
            public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                canShare = true;
                shareImageUrl = getFilePath(Uri.parse(entity.getCover_url()));
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                if (Constant.SHOW_LOG) {
                    toast("图片加载失败");
                }
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(uri)
                .build();
        ivOrgIcon.setController(controller);
    }

    private String getFilePath(Uri loadUri) {
        if (loadUri == null) {
            return "";
        }
        ImageRequest imageRequest = ImageRequest.fromUri(loadUri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
        if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
            File file = ((FileBinaryResource) resource).getFile();
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }


    /**
     * 设置报名状态
     */
    private void setEnrollStatus(int applicant_info) {
        switch (applicant_info) {
            case -1://已失效
            case 0://初始状态
                btnEnroll.setVisibility(View.VISIBLE);
                btnEnroll.setText("立即报名");
                tvEnrollStatus.setVisibility(View.GONE);
                break;
            case 1://审核中
                btnEnroll.setVisibility(View.GONE);
                tvEnrollStatus.setVisibility(View.VISIBLE);
                tvEnrollStatus.setText("请等待审核");
                tvEnrollStatus.setTextColor(0xFF999999);
                break;
            case 2://付款
                btnEnroll.setVisibility(View.VISIBLE);
                btnEnroll.setText("立即付款");
                tvEnrollStatus.setVisibility(View.GONE);
                break;
            case 3://成功
                btnEnroll.setVisibility(View.GONE);
                tvEnrollStatus.setVisibility(View.VISIBLE);
                tvEnrollStatus.setText("报名成功");
                tvEnrollStatus.setTextColor(0xFF52be7f);
                break;
            case 4://付款超时
                btnEnroll.setVisibility(View.VISIBLE);
                btnEnroll.setText("重新报名");
                tvEnrollStatus.setVisibility(View.GONE);
                break;
            default:
                btnEnroll.setVisibility(View.VISIBLE);
                btnEnroll.setText("立即报名");
                tvEnrollStatus.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 轮播图
     */
    private void BannerData(List<String> imageList) {
        SliderBannerController sliderBannerController = new SliderBannerController(sliderBanner);

        final ArrayList<BannerEntity> list = new ArrayList<BannerEntity>();
        for (int i = 0; i < imageList.size(); i++) {
            BannerEntity model = new BannerEntity();
            model.setImgUrl(imageList.get(i));
            model.setTitle("");
            list.add(model);
        }
        sliderBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = (position + 1) % list.size();
                if (index == 0) {
                    tvIndexImageNum.setText(String.valueOf(list.size()));
                } else {
                    tvIndexImageNum.setText(String.valueOf((position + 1) % list.size()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sliderBannerController.play(list);
    }

    /**
     * 分享
     */
    private void Share() {
        shareTitle = entity.getTitle();
        String time = tvActTime.getText().toString();
        if (time.length() > 3) {
            shareContent = "活动时间:\n" + time;
        } else {
            shareContent = entity.getTeam().getName();
        }
        if (Constant.SHOW_LOG) {
            shareUrl = Constant.DEFOULT_TEST_REQUEST_URL + "wap/activity/detail?activity_id=" + entity.getId();
        } else {
            shareUrl = Constant.DEFOULT_REQUEST_URL + "wap/activity/detail?activity_id=" + entity.getId();
        }

        if (mShareDialog == null){
            mShareDialog = DialogCreater.createShareDialog(ActDetail.this, new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mShareDialog.dismiss();
                    IShareManager iShareManager = null;
                    switch (position) {
                        case 0:
                            iShareManager = new QQShareManager(ActDetail.this);
                            if (shareContent.length() > 70) {
                                shareContent = shareContent.substring(0, 70);
                            }
                            iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), QQShareManager.QZONE_SHARE_TYPE);
                            break;
                        case 1:
                            iShareManager = new WechatShareManager(ActDetail.this);
                            if (shareContent.length() > 70) {
                                shareContent = shareContent.substring(0, 70);
                            }
                            iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
                            break;
                        case 2:
                            iShareManager = new WechatShareManager(ActDetail.this);
                            if (shareContent.length() > 70) {
                                shareContent = shareContent.substring(0, 70);
                            }
                            iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);
                            break;
                        case 3:
                            iShareManager = new WeiboShareManager(ActDetail.this);
                            if ((shareTitle + shareContent + shareUrl).length() > 120) {
                                shareContent = shareContent.substring(0, 120 - shareUrl.length() - shareTitle.length());
                            }
                            iShareManager.share(new ShareContentWebpage(shareTitle, shareTitle + " " + shareContent + " " + shareUrl, shareUrl, shareImageUrl), WeiboShareManager.WEIBO_SHARE_TYPE);
                            break;
                    }
                }
            });
        }
        mShareDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
        EventCenter.getInstance().unregister(ActDetail.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSsoHandler = WeiboLoginManager.getSsoHandler();
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
