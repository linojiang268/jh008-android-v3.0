package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.DatePickDialog;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.SingleImageSelectEvent;
import com.gather.android.event.UpdateInfoEvent;
import com.gather.android.event.UpdateUserIconEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.Pair;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.AppManage;
import com.gather.android.utils.Checker;
import com.gather.android.widget.ElasticScrollView;
import com.gather.android.widget.FlowLayout;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import de.greenrobot.event.Subscribe;
import io.yunba.android.manager.YunBaManager;

/**
 * Created by Levi on 2015/8/3.
 */
public class UserInfo extends BaseActivity implements View.OnClickListener{

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.ivHeadportrait)
    SimpleDraweeView btnHeadportrait;
    @InjectView(R.id.tvPhonenumber)
    TextView tvPhonenumber;
    @InjectView(R.id.btnPhonenumber)
    LinearLayout btnPhonenumber;
    @InjectView(R.id.btnPassword)
    LinearLayout btnPassword;
    @InjectView(R.id.tvNickname)
    TextView tvNickname;
    @InjectView(R.id.btnNickname)
    LinearLayout btnNickname;
    @InjectView(R.id.tvSex)
    TextView tvSex;
    @InjectView(R.id.tvAge)
    TextView tvAge;
    @InjectView(R.id.btnAge)
    LinearLayout btnAge;
    @InjectView(R.id.btnIntrest)
    LinearLayout btnIntrest;
    @InjectView(R.id.flIntrest)
    FlowLayout flIntrest;
    @InjectView(R.id.btnExit)
    Button btnExit;
    @InjectView(R.id.pbRefresh)
    ProgressBar pbRefresh;
    @InjectView(R.id.scrollView)
    ElasticScrollView scrollView;

    private DatePickDialog mDateDialog;

    private LoadingDialog loadingDialog;
    private List<String> INTREST_LABEL_LIST;
    private boolean isLoadingInfo =false;
    private UserInfoEntity entity;
    private boolean modifyUserIcon = false;

    private Dialog mExitDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        EventCenter.getInstance().register(this);

        INTREST_LABEL_LIST = Arrays.asList(getResources().getStringArray(R.array.intrest));

        initView();
        loadUserInfo();
    }

    private void initView(){
        loadingDialog = LoadingDialog.createDialog(this, true);
        titlebar.setHeaderTitle(R.string.personal_info);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        scrollView.setDamk(3f);
        scrollView.setElasticView(null);

        btnAge.setOnClickListener(this);
        btnIntrest.setOnClickListener(this);
        btnNickname.setOnClickListener(this);
        btnPassword.setOnClickListener(this);
//        btnPhonenumber.setOnClickListener(this);
        btnHeadportrait.setOnClickListener(this);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExitDialog == null) {
                    mExitDialog = DialogCreater.createNormalDialog(UserInfo.this, "提示", "真的要注销该帐号并退出吗？", new OnBtnLeftClickL() {
                        @Override
                        public void onBtnLeftClick() {
                            mExitDialog.dismiss();
                            exitClick();
                        }
                    });
                }
                mExitDialog.show();
            }
        });

        refreshInfoView();
    }

    private void exitClick(){
        UserPref.getInstance().clear();
        YunBaManager.stop(getApplicationContext());
        OkHttpUtil.get(new BaseParams(API.LOGIN_OUT), new ResponseHandler() {
            @Override
            public void success(String msg) {

            }

            @Override
            public void fail(int code, String error) {
            }
        });
        AppManage.getInstance().finishOther();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private void refreshInfoView(){
        entity = UserPref.getInstance().getUserInfo();

        btnHeadportrait.setImageURI(Checker.isEmpty(entity.getAvatarUrl()) ? null : Uri.parse(entity.getAvatarUrl()));
        tvPhonenumber.setText(Checker.formatPhoneNumber(entity.getMobile()));
        tvNickname.setText(entity.getNickName());
        String ageStr = String.format(getString(R.string.age_format), entity.getAge());
        tvAge.setText(ageStr);
        tvSex.setText(entity.getGender() == 1 ? R.string.male : R.string.female);

        List<String> myIntrestIdList = entity.getTagIdsForArray();
        flIntrest.removeAllViews();
        if (myIntrestIdList.size() > 0){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0;i < myIntrestIdList.size(); i++){
                int pos = Integer.parseInt(myIntrestIdList.get(i)) - 1;
                TextView textView = (TextView) inflater.inflate(R.layout.intrest_item, flIntrest, false);
                textView.setText(INTREST_LABEL_LIST.get(pos));
                flIntrest.addView(textView);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (isLoadingInfo){
            return;
        }
        switch (view.getId()){
            case R.id.btnAge:
//                modifyInfo(ModifyInfo.MODIFY_BIRTHDAY);
                showBirthdayDialog();
                break;
            case R.id.btnPhonenumber:
                modifyInfo(ModifyInfo.MODIFY_PHONENUMBER);
                break;
            case R.id.btnNickname:
                modifyInfo(ModifyInfo.MODIFY_NICKNAME);
                break;
            case R.id.btnPassword:
                modifyInfo(ModifyInfo.MODIFY_PASSWORD);
                break;
            case R.id.btnIntrest:
                Intent intent = new Intent(this, IntrestPage.class);
                intent.putExtra(IntrestPage.EXTRA_MODE, IntrestPage.MODE_MODIFY);
                startActivity(intent);
                break;
            case R.id.ivHeadportrait:
                ImageSelectActivity.pickAndCropImage(this, true, 400);
                break;
        }
    }

    private void showBirthdayDialog() {
        if (mDateDialog == null){
            mDateDialog = new DatePickDialog(this,R.style.dialog_untran);
            mDateDialog.setDateListener(new DatePickDialog.OnDateClickListener() {
                @Override
                public void onDateListener(int year, int month, int day, String date, int age) {
                    entity.setBirthday(date);
                    modifyBirthday();
                }
            });
        }
        int[] dateInt = DatePickDialog.getDate(entity.getBirthday());
        if (dateInt != null){
            mDateDialog.setDefDate(dateInt[0], dateInt[1], dateInt[2]);
        }
        Logger.i(entity.getBirthday());
        mDateDialog.show();
    }

    private void modifyBirthday(){
        loadingDialog.setMessage(getString(R.string.saving));
        loadingDialog.show();
        BaseParams params = getBaseParam();
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                toast(R.string.modify_info_success);
                String text = String.format(getString(R.string.age_format), entity.getAge());
                tvAge.setText(text);
                loadUserInfo();
            }

            @Override
            public void fail(int code, String error) {
                loadingDialog.dismiss();
                toast(error);
            }
        });
    }

    private BaseParams getBaseParam(){
        BaseParams params = new BaseParams(API.MODIFY_PROFILE);
        ArrayList<String> tagIds = entity.getTagIdsForArray();
        for (int i = 0; i < tagIds.size(); i++){
            params.put("tagIds["+i+"]", tagIds.get(i));
        }
        params.put("nick_name", entity.getNickName());
        params.put("gender", entity.getGender());
        params.put("birthday", entity.getBirthday());
        params.put("mobile", entity.getMobile());
        return params;
    }

    private void modifyInfo(int type){
        Intent intent = new Intent(this, ModifyInfo.class);
        intent.putExtra(ModifyInfo.EXTRA_MODIFY, type);
        startActivity(intent);
    }


    @Subscribe
    public void onEvent(UpdateInfoEvent event){
        loadUserInfo();
    }

    @Subscribe
    public void onEvent(SingleImageSelectEvent event){
        loadingDialog.setMessage(getString(R.string.saving));
        loadingDialog.show();
        BaseParams params = getBaseParam();
        params.put("avatar", new Pair<String, File>("image/jpg", event.getFile()));
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                toast(R.string.modify_info_success);
                modifyUserIcon = true;
                loadUserInfo();
            }

            @Override
            public void fail(int code, String error) {
                loadingDialog.dismiss();
                toast(error);
            }
        });
    }

    /**
     * 从服务器获取用户信息
     */
    private void loadUserInfo(){
        if (isLoadingInfo)return;
        pbRefresh.setVisibility(View.VISIBLE);
        isLoadingInfo = true;
        BaseParams params = new BaseParams(API.GET_PROFILE);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                isLoadingInfo = false;
                try {
                    UserInfoEntity entity = JSON.parseObject(msg, UserInfoEntity.class);
                    UserPref.getInstance().updateInfo(entity);
                    refreshInfoView();
                    EventCenter.getInstance().post(new UpdateUserIconEvent());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                pbRefresh.setVisibility(View.INVISIBLE);
            }

            @Override
            public void fail(int code, String error) {
                isLoadingInfo = false;
                pbRefresh.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }
}
