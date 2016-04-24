package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.TimePref;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.dialog.ToastDialog;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeDataUpdateEvent;
import com.gather.android.event.OrgHomeRefreshEvent;
import com.gather.android.event.OrgSecretSetEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.ElasticScrollView;
import com.jihe.dialog.listener.OnBtnLeftClickL;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.Subscribe;
import io.yunba.android.manager.YunBaManager;

/**
 * 社团主页
 * Created by Administrator on 2015/7/7.
 */
public class OrgHome extends BaseActivity {

    @InjectView(R.id.tvOrgWaitting)
    TextView tvOrgWaitting;
    @InjectView(R.id.tvOrgApply)
    TextView tvOrgApply;
    @InjectView(R.id.ivOrgExit)
    ImageView ivOrgExit;
    @InjectView(R.id.ivOrgIcon)
    SimpleDraweeView ivOrgIcon;
    @InjectView(R.id.ivOrgVertify)
    ImageView ivOrgVertify;
    @InjectView(R.id.tvOrgName)
    TextView tvOrgName;
    @InjectView(R.id.tvOrgMemberNum)
    TextView tvOrgMemberNum;
    @InjectView(R.id.tvOrgIntro)
    TextView tvOrgIntro;
    @InjectView(R.id.ivOrgMemberTip)
    ImageView ivOrgMemberTip;
    @InjectView(R.id.ivOrgActTip)
    ImageView ivOrgActTip;
    @InjectView(R.id.ivOrgNewTip)
    ImageView ivOrgNewsTip;
    @InjectView(R.id.ivOrgPhotoTip)
    ImageView ivOrgPhotoTip;
    @InjectView(R.id.llLineOne)
    LinearLayout llLineOne;
    @InjectView(R.id.llLineTwo)
    LinearLayout llLineTwo;
//    @InjectView(R.id.llLineThree)
//    LinearLayout llLineThree;
    @InjectView(R.id.line)
    View line;
    @InjectView(R.id.scrollView)
    ElasticScrollView scrollView;

    private OrgDetailEntity model = null;
    private String orgId;
    private boolean canClick;//当在获取社团资料时，是否允许点击

    private LoadingDialog mLoadingDialog;

    private Dialog mExitOrgDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_home);
        EventCenter.getInstance().register(this);
        Intent intent = getIntent();
        if (intent.hasExtra("MODEL") || intent.hasExtra("ID")) {
            if (intent.hasExtra("MODEL")) {
                model = (OrgDetailEntity) intent.getSerializableExtra("MODEL");
                orgId = model.getId();
            } else {
                orgId = intent.getExtras().getString("ID");
            }
            mLoadingDialog = LoadingDialog.createDialog(this, true);
            scrollView.setDamk(3f);
            scrollView.setElasticView(null);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLineOne.getLayoutParams();
            params.height = ((PhoneManager.getDisplayMetrics(this).widthPixels - 2 * getResources().getDimensionPixelOffset(R.dimen.line_size)) / 3);
            llLineOne.setLayoutParams(params);
            llLineTwo.setLayoutParams(params);
//            llLineThree.setLayoutParams(params);

            initView();
            getOrgDetail();
        } else {
            toast("加载社团主页错误");
            finish();
        }
    }

    private void initView() {
        initViewStatus();
        setOrgMessage(true);
    }

    private void setOrgMessage(boolean isInit) {
        if (model != null) {
            ivOrgIcon.setImageURI(Uri.parse(model.getLogo_url()));
            tvOrgName.setText(model.getName());
            tvOrgIntro.setText(model.getIntroduction());
            tvOrgMemberNum.setText(model.getMember_num() + "人");
            if (model.getCertification() == 2) {
                ivOrgVertify.setVisibility(View.VISIBLE);
            } else {
                ivOrgVertify.setVisibility(View.GONE);
            }
            if (model.isJoined()) {
                //已经加入社团
                if (!isInit) {
                    YunBaManager.subscribe(getApplicationContext(), "topic_team_" + orgId, GatherApplication.getInstance().pushListener);
                }
                MemberStatus();
            } else {
                //没有加入社团
                if (model.isRequested()) {
                    //已提交社团申请
                    WaittingStatus();
                } else {
                    //未提交社团申请
                    NotMemberStatus();
                }
            }

            if (!isInit) {
                //近期活动红点提示
                if (model.getActivities_updated_at() != 0) {
                    String key = "ACT_" + UserPref.getInstance().getUserInfo().getUid() + "_" + orgId;
                    long actTime =  TimePref.getInstance().getOrgLastTime(key);
                    if (actTime != 0) {
                        if (actTime >= model.getActivities_updated_at()) {
                            ivOrgActTip.setVisibility(View.GONE);
                        } else {
                            TimePref.getInstance().setOrgTipLastTime(key, model.getActivities_updated_at());
                            ivOrgActTip.setVisibility(View.VISIBLE);
                        }
                    } else {
                        TimePref.getInstance().setOrgTipLastTime(key, model.getActivities_updated_at());
                        ivOrgActTip.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivOrgActTip.setVisibility(View.GONE);
                }

                //往期回顾红点提示
                if (model.getNews_updated_at() != 0) {
                    String key = "NEWS_" + UserPref.getInstance().getUserInfo().getUid() + "_" + orgId;
                    long newsTime =  TimePref.getInstance().getOrgLastTime(key);
                    if (newsTime != 0) {
                        if (newsTime >= model.getNews_updated_at()) {
                            ivOrgNewsTip.setVisibility(View.GONE);
                        } else {
                            TimePref.getInstance().setOrgTipLastTime(key, model.getNews_updated_at());
                            ivOrgNewsTip.setVisibility(View.VISIBLE);
                        }
                    } else {
                        TimePref.getInstance().setOrgTipLastTime(key, model.getNews_updated_at());
                        ivOrgNewsTip.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivOrgNewsTip.setVisibility(View.GONE);
                }

                //社团成员红点提示
                if (model.getMembers_updated_at() != 0) {
                    String key = "MEMBER_" + UserPref.getInstance().getUserInfo().getUid() + "_" + orgId;
                    long memberTime =  TimePref.getInstance().getOrgLastTime(key);
                    if (memberTime != 0) {
                        if (memberTime >= model.getMembers_updated_at()) {
                            ivOrgMemberTip.setVisibility(View.GONE);
                        } else {
                            TimePref.getInstance().setOrgTipLastTime(key, model.getMembers_updated_at());
                            ivOrgMemberTip.setVisibility(View.VISIBLE);
                        }
                    } else {
                        TimePref.getInstance().setOrgTipLastTime(key, model.getMembers_updated_at());
                        ivOrgMemberTip.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivOrgMemberTip.setVisibility(View.GONE);
                }

                //相册红点提示
                if (model.getAlbums_updated_at() != 0) {
                    String key = "ALBUM_" + UserPref.getInstance().getUserInfo().getUid() + "_" + orgId;
                    long albumTime =  TimePref.getInstance().getOrgLastTime(key);
                    if (albumTime != 0) {
                        if (albumTime >= model.getAlbums_updated_at()) {
                            ivOrgPhotoTip.setVisibility(View.GONE);
                        } else {
                            TimePref.getInstance().setOrgTipLastTime(key, model.getAlbums_updated_at());
                            ivOrgPhotoTip.setVisibility(View.VISIBLE);
                        }
                    } else {
                        TimePref.getInstance().setOrgTipLastTime(key, model.getAlbums_updated_at());
                        ivOrgPhotoTip.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivOrgPhotoTip.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 获取社团详情
     */
    private void getOrgDetail() {
        canClick = false;
        BaseParams params = new BaseParams(API.GET_ORG_DETAIL);
        params.put("team", orgId);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                model = JSON.parseObject(msg, OrgDetailEntity.class);
                if (model != null) {
                    setOrgMessage(false);
                    canClick = true;
                } else {
                    toast("社团信息解析错误");
                    finish();
                }
            }

            @Override
            public void fail(int code, String error) {
                toast("获取社团信息失败");
                finish();
            }
        });
    }

    @Subscribe
    public void onEvent(OrgSecretSetEvent event) {
        if (Constant.SHOW_LOG) {
            toast("设置成功");
        }
        model.setVisibility(event.getVisible());
    }

    @Subscribe
    public void onEvent(OrgHomeRefreshEvent event) {
        getOrgDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }

    @OnClick({R.id.ibtn_backpress, R.id.ivOrgIcon, R.id.rlOrgAct, R.id.rlOrgHistory, R.id.rlOrgMember, R.id.rlOrgPhoto, R.id.rlOrgQRCode, R.id.rlOrgSetting, R.id.tvOrgApply, R.id.ivOrgExit})
    void OnClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ibtn_backpress:
                onBackPressed();
                break;
            case R.id.ivOrgIcon:
                if (!TextUtils.isEmpty(model.getLogo_url())) {
                    intent = new Intent(OrgHome.this, PhotoGallery.class);
                    ArrayList<String> list = new ArrayList<>();
                    list.add(model.getLogo_url());
                    intent.putExtra("LIST", list);
                    intent.putExtra("POSITION", 0);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
                break;
            case R.id.rlOrgAct:
                if (ivOrgActTip.isShown()) {
                    ivOrgActTip.setVisibility(View.GONE);
                }
                intent = new Intent(OrgHome.this, OrgActList.class);
                intent.putExtra("ID", orgId);
                startActivity(intent);
                break;
            case R.id.rlOrgHistory:
                if (ivOrgNewsTip.isShown()) {
                    ivOrgNewsTip.setVisibility(View.GONE);
                }
                intent = new Intent(this, OrgNews.class);
                intent.putExtra("ID", orgId);
                intent.putExtra("ORG_NAME", model.getName());
                intent.putExtra("ORG_LOGO", model.getLogo_url());
                startActivity(intent);
                break;
            case R.id.rlOrgMember:
                if (ivOrgMemberTip.isShown()) {
                    ivOrgMemberTip.setVisibility(View.GONE);
                }
                intent = new Intent(this, OrgMember.class);
                intent.putExtra("ID", orgId);
                intent.putExtra("TYPE", OrgMember.ORG);
                startActivity(intent);
                break;
            case R.id.rlOrgPhoto:
                if (ivOrgPhotoTip.isShown()) {
                    ivOrgPhotoTip.setVisibility(View.GONE);
                }
                intent = new Intent(this, OrgPhotoList.class);
                intent.putExtra("ID", orgId);
                startActivity(intent);
                break;
            case R.id.rlOrgQRCode:
                if (canClick && model != null) {
                    intent = new Intent(this, OrgQRCode.class);
                    intent.putExtra("MODEL", model);
                    startActivity(intent);
                }
                break;
            case R.id.rlOrgSetting:
                intent = new Intent(this, OrgSecretSet.class);
                intent.putExtra("ID", orgId);
                intent.putExtra("VISIBLE", model.getVisibility());
                startActivity(intent);
                break;
            case R.id.tvOrgApply:
                if (canClick) {
                    if (model.isIn_blacklist()) {
                        toast("该社团限制加入");
                    } else {
                        applyJoinOrg();
                    }
                }
                break;
            case R.id.ivOrgExit:
                if (mExitOrgDialog == null){
                    mExitOrgDialog = DialogCreater.createNormalDialog(this, "温馨提示", "确定要退出社团吗？", new OnBtnLeftClickL() {
                        @Override
                        public void onBtnLeftClick() {
                            if (canClick) {
                                exitOrg();
                            }
                            mExitOrgDialog.dismiss();
                        }
                    });
                }
                mExitOrgDialog.show();
                break;
        }
    }

    /**
     * 退出社团
     */
    private void exitOrg() {
        mLoadingDialog.setMessage("退出中...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.EXIT_ORG);
        params.put("team", orgId);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                //更新首页社团
                EventCenter.getInstance().post(new HomeDataUpdateEvent(1));

                YunBaManager.unsubscribe(getApplicationContext(), "topic_team_" + orgId, GatherApplication.getInstance().pushListener);
                getOrgDetail();
            }

            @Override
            public void fail(int code, String error) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast(error);
            }
        });
    }

    /**
     * 申请加入社团
     */
    private void applyJoinOrg() {
        if (model.getJoin_type() == 0) {
            //任何人都可以加入
            Enroll();
        } else {
            //需要审核才能加入
            if (model.getJoin_requirements() != null && model.getJoin_requirements().size() == 0) {
                Enroll();
            } else {
                Intent intent = new Intent(this, TipsActivity.class);
                intent.putExtra("MODEL", model);
                startActivity(intent);
            }
        }
    }

    /**
     * 申请接口
     */
    private void Enroll() {
        mLoadingDialog.setMessage("申请中...");
        mLoadingDialog.show();
        BaseParams params = new BaseParams(API.APPLY_JOIN_ORG);
        params.put("team", orgId);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
//                try {
//                    JSONObject object = new JSONObject(msg);
//                    toast(object.getString("message"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(OrgHome.this, SignTips.class);
//                intent.putExtra("TYPE", "ORG");
//                if (model.isIn_whitelist()) {
//                    intent.putExtra("BIG", "恭喜你");
//                    intent.putExtra("MSG", "加入" + model.getName() + "成功");
//                    intent.putExtra("TWO", "你可以在社团主页查看社团相关信息");
//                } else {
//                    intent.putExtra("BIG", "待审核");
//                    intent.putExtra("MSG", "团长审核通过后您方可加入");
//                    intent.putExtra("TWO", "请到“我的社团”中实时查看审核结果");
//                }
//                intent.putExtra("ICON", R.drawable.icon_green_success);
//                startActivity(intent);

                if (model.getJoin_type() == 0) {
                    ToastDialog.showToast(OrgHome.this,"成功", R.drawable.icon_green_success, 2, false);
                } else {
                    if (model.isIn_whitelist()) {
                        ToastDialog.showToast(OrgHome.this,"成功", R.drawable.icon_green_success, 2, false);
                    } else {
                        ToastDialog.showToast(OrgHome.this,"等待审核", R.drawable.icon_blue_wait, 2, false);
                    }
                }
                getOrgDetail();
                //更新首页社团
                EventCenter.getInstance().post(new HomeDataUpdateEvent(1));
            }

            @Override
            public void fail(int code, String error) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast(error);
            }
        });
    }

    /**
     * 不是社团成员的状态
     */
    private void NotMemberStatus() {
        tvOrgWaitting.setVisibility(View.GONE);
        tvOrgApply.setVisibility(View.VISIBLE);
        ivOrgExit.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        llLineTwo.setVisibility(View.GONE);
//        llLineThree.setVisibility(View.GONE);
    }

    /**
     * 进入主页初始化状态
     */
    private void initViewStatus() {
        tvOrgWaitting.setVisibility(View.GONE);
        tvOrgApply.setVisibility(View.GONE);
        ivOrgExit.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        llLineTwo.setVisibility(View.GONE);
//        llLineThree.setVisibility(View.GONE);
    }

    /**
     * 等待申请审核状态
     */
    private void WaittingStatus() {
        tvOrgWaitting.setVisibility(View.VISIBLE);
        tvOrgApply.setVisibility(View.GONE);
        ivOrgExit.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        llLineTwo.setVisibility(View.GONE);
//        llLineThree.setVisibility(View.GONE);
    }

    /**
     * 已是社团成员状态
     */
    private void MemberStatus() {
        tvOrgWaitting.setVisibility(View.GONE);
        tvOrgApply.setVisibility(View.GONE);
        ivOrgExit.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        llLineTwo.setVisibility(View.VISIBLE);
//        llLineThree.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
