package com.gather.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.DatePickDialog;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.RegisterInfoEntity;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.SingleImageSelectEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.Pair;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.Checker;
import com.gather.android.utils.MobileTextWatcher;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.TitleBar;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;

import butterknife.InjectView;
import de.greenrobot.event.Subscribe;

/**
 * 个人资料（注册）
 * Created by Levi on 2015/7/6.
 */
public class RegisterInfo extends BaseActivity {
    public static final String EXTRA_DATA = "extra_data";
    //启动模式
    public static final String EXTRA_MODE = "extra_mode";

    //如果是登录需要完善资料的用户，要把手机号传过来
    public static final String EXTRA_PHONE = "extra_phone";
    /**
     * 启动模式类型
     */
    //注册模式(默认)
    public static final int MODE_REGISTER = IntrestPage.MODE_REGISTER;
    //资料完善模式
    public static final int MODE_FIX_INFO = IntrestPage.MODE_FIX_INFO;

    @InjectView(R.id.btn_save)
    Button btnSave;

    @InjectView(R.id.iv_headportrait)
    SimpleDraweeView btnHeadportrait;

    @InjectView(R.id.et_phonenumber)
    EditText etPhonenumber;

    @InjectView(R.id.et_password)
    EditText etPassword;

    @InjectView(R.id.et_nickname)
    EditText etNickname;

    @InjectView(R.id.tv_sex)
    TextView tvSex;

    @InjectView(R.id.tv_age)
    TextView tvAge;

    @InjectView(R.id.btn_sex)
    LinearLayout btnSex;

    @InjectView(R.id.btn_age)
    LinearLayout btnAge;

    @InjectView(R.id.titlebar)
    TitleBar titleBar;

    private DatePickDialog mDateDialog;
    private LoadingDialog loadingDialog;


    private int mode;
    private String phone, password;
    private final RegisterInfoEntity infoEntity = new RegisterInfoEntity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_info);
        EventCenter.getInstance().register(this);

        Intent intent = getIntent();
        mode = intent.getIntExtra(EXTRA_MODE, MODE_REGISTER);
        ArrayList<String> intrestIds = intent.getStringArrayListExtra(EXTRA_DATA);
        if (intrestIds != null && intrestIds.size() > 0){
            String[] ids = new String[intrestIds.size()];
            for (int i = 0; i < intrestIds.size(); i++){
                ids[i] = intrestIds.get(i);
            }
            infoEntity.setTagIds(ids);
        }
        if (mode == MODE_FIX_INFO){
            btnSave.setText(R.string.save);
            phone = intent.getExtras().getString(EXTRA_PHONE, "");
            password = phone.substring(5, phone.length());
        }

        titleBar.setHeaderTitle(R.string.personal_info);
        titleBar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        btnHeadportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == MODE_FIX_INFO){
                    saveAction();
                }
                else {
                    nextAction();
                }
            }
        });
        btnAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btnSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSexDialog();
            }
        });


        //昵称再输入时靠左显示，输入完成后靠右显示
        etNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    etNickname.setGravity(Gravity.LEFT|Gravity.CENTER);
                }
                else {
                    etNickname.setGravity(Gravity.LEFT|Gravity.CENTER);
                }
            }
        });

        etPhonenumber.addTextChangedListener(new MobileTextWatcher(etPhonenumber));

        initView();
    }

    private void initView() {
        if (phone != null && mode == MODE_FIX_INFO) {
            etPhonenumber.setText(Checker.formatPhoneNumber(phone));
            etPhonenumber.setFocusable(false);
            etPassword.setText(password);
            etPassword.setFocusable(false);
            etNickname.requestFocus();
        }
    }


    @Subscribe
    public void onEvent(SingleImageSelectEvent event){
        //通过EventBus返回选择的头像路径
        infoEntity.setAvatarFile(event.getFile());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(event.getFile()))
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(btnHeadportrait.getController())
                .setImageRequest(request)
                .build();
        btnHeadportrait.setController(controller);
//        btnHeadportrait.setImageURI(Uri.fromFile(event.getFile()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }

    /**
     * 清楚页面输入框焦点，主要是针对昵称输入框
     */
    private void clearFocus(){
        View view = getWindow().getCurrentFocus();
        if (view!=null){
            view.clearFocus();
        }
    }

    /**
     * 选择生日选择框
     */
    private void showDateDialog(){
        clearFocus();
        if (mDateDialog == null){
            mDateDialog = new DatePickDialog(this,R.style.dialog_untran);
            mDateDialog.setDateListener(new DatePickDialog.OnDateClickListener() {
                @Override
                public void onDateListener(int year, int month, int day, String date, int age) {
                    Logger.i(date);
                    infoEntity.setBirthday(date);
                    String text = String.format(getString(R.string.age_format), age);
                    tvAge.setText(text);
                }
            });
        }
        mDateDialog.show();
    }

    /**
     * 显示性别选择框
     */
    private void showSexDialog() {
        clearFocus();
        MMAlert.showAlert(RegisterInfo.this, "", getResources().getStringArray(R.array.sexuality), null, new MMAlert.OnAlertSelectId() {
            public void onDismissed() {

            }

            public void onClick(int whichButton) {
                switch (whichButton) {
                    case 0:
                        infoEntity.setGender(1);//1 - male; 2 - famale
                        tvSex.setText(R.string.male);
                        break;
                    case 1:
                        infoEntity.setGender(2);//1 - male; 2 - famale
                        tvSex.setText(R.string.female);
                        break;
                }
            }
        });
    }

    /**
     * 选择头像
     */
    private void choosePhoto(){
        clearFocus();
        ImageSelectActivity.pickAndCropImage(this, true, Constant.HEADPORTRAIT_SIZE);
    }

    /**
     * 检查资料是否完整，完整返回true
     * @return
     */
    private boolean checkInfo(){
        do {
            //资料完整性检查
            String phone = etPhonenumber.getText().toString().replaceAll(" ", "");
            if (!Checker.isMobilePhone(phone)){
                etPhonenumber.requestFocus();
                toast(R.string.input_right_mobilephone);
                break;
            }
            String pwd = etPassword.getText().toString();
            if (!Checker.isEnoughLong(pwd, 6)){
                etPassword.requestFocus();
                toast(R.string.input_right_password);
                break;
            }
            String nickName = etNickname.getText().toString();
            if (Checker.isEmpty(nickName)){
                etNickname.requestFocus();
                toast(R.string.input_nickname);
                break;
            }
            if (infoEntity.getGender() == -1){
                toast(R.string.choose_sex);
                break;
            }
            if (Checker.isEmpty(infoEntity.getBirthday())){
                toast(R.string.choose_birthday);
                break;
            }
            if (infoEntity.getAvatarFile() == null){
                toast(R.string.choose_photo);
                break;
            }
            infoEntity.setNickName(nickName);
            infoEntity.setMobile(phone);
            infoEntity.setPassword(pwd);
            return  true;
        }
        while (false);
        return  false;
    }

    /**
     * 保存资料
     */
    private void saveAction(){
        if (checkInfo()){
            if (loadingDialog == null){
                loadingDialog = LoadingDialog.createDialog(this,true);
                loadingDialog.setMessage(getString(R.string.is_perfect_profile));
            }
            loadingDialog.show();
            BaseParams params = new BaseParams(API.PERFECT_PROFILE);
            params.put("mobile", infoEntity.getMobile());
            params.put("password", infoEntity.getPassword());
            params.put("nick_name", infoEntity.getNickName());
            params.put("gender", infoEntity.getGender());
            params.put("birthday", infoEntity.getBirthday());
            String[] tagIds = infoEntity.getTagIds();
            for (int i = 0; i < tagIds.length; i++){
                params.put("tagIds["+i+"]", tagIds[i]);
            }
            params.put("avatar", new Pair<String, File>("image/jpg", infoEntity.getAvatarFile()));
            OkHttpUtil.post(params, new ResponseHandler() {
                @Override
                public void success(String msg) {
                    loadUserInfo();
                }

                @Override
                public void fail(int code, String error) {
                    toast(error);
                    loadingDialog.dismiss();
                }
            });
        }
    }

    /**
     * 从服务器获取用户信息
     */
    private void loadUserInfo(){
        BaseParams params = new BaseParams(API.GET_PROFILE);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                try {
                    UserInfoEntity entity = JSON.parseObject(msg, UserInfoEntity.class);
                    UserPref.getInstance().updateInfo(entity);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    IndexHome.checkAndStartIndexActivity(RegisterInfo.this);
                    finish();
                }
            }

            @Override
            public void fail(int code, String error) {
                loadingDialog.dismiss();
                IndexHome.checkAndStartIndexActivity(RegisterInfo.this);
                finish();
            }
        });
    }

    /**
     * 下一步
     */
    private void nextAction(){
        if (checkInfo()){
            Intent intent = new Intent(this, CompleteRegister.class);
            intent.putExtra(CompleteRegister.EXTRA_INFO, infoEntity);
            startActivity(intent);
        }
    }
}
