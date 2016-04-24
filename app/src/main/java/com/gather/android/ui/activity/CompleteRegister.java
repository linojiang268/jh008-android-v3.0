package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.RegisterInfoEntity;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.Pair;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.widget.TitleBar;
import com.gather.android.widget.VerifyCodeButton;

import java.io.File;

import butterknife.InjectView;

/**
 * 完成注册
 * Created by Levi on 2015/7/7.
 */
public class CompleteRegister extends BaseActivity{
    public static final String EXTRA_INFO = "extra_info";

    @InjectView(R.id.btn_getverifycode)
    VerifyCodeButton btnGetVerifcode;

    @InjectView(R.id.btn_complete)
    Button btnComplete;

    @InjectView(R.id.et_verifycode)
            
    EditText etVerifycode;

    @InjectView(R.id.tv_tips)
    TextView tvTips;

    @InjectView(R.id.titlebar)
    TitleBar titleBar;

    private LoadingDialog loadingDialog;

    private RegisterInfoEntity infoEntify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_register);

        Intent intent = getIntent();
        infoEntify = (RegisterInfoEntity) intent.getSerializableExtra(EXTRA_INFO);

        titleBar.setHeaderTitle(R.string.complete_register);
        titleBar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        loadingDialog = LoadingDialog.createDialog(this, true);
        loadingDialog.setMessage(getString(R.string.is_registing));

        btnGetVerifcode.setTypeAndListener(VerifyCodeButton.TYPE_REGISTER, new VerifyCodeButton.OnGetVerifyCodeListener() {
            @Override
            public String bindPhonenumber() {
                return infoEntify.getMobile();
            }

            @Override
            public void onCountdownStart(String msg) {
                tvTips.setText(msg);
            }

            @Override
            public void onCountdownEnd() {
            }

            @Override
            public void onError(String msg) {
                toast(msg);
            }

            @Override
            public void onGetCode(String code) {
                etVerifycode.setText(code);
            }
        });


        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeAction();
            }
        });
    }

    /**
     * 完成注册
     */
    private void completeAction(){
        String code = etVerifycode.getText().toString();
        if (Checker.isEmpty(code)){
            toast(R.string.input_verifycode);
        }
        else {
            registerAction(code);
        }
    }

    private void registerAction(String code){
        loadingDialog.show();
        BaseParams params = new BaseParams(API.REGISTER);
        params.put("mobile", infoEntify.getMobile());
        params.put("code", code);
        params.put("password", infoEntify.getPassword());
        params.put("nick_name", infoEntify.getNickName());
        params.put("gender", infoEntify.getGender());
        params.put("birthday", infoEntify.getBirthday());
        String[] tagIds = infoEntify.getTagIds();
        for (int i = 0; i < tagIds.length; i++){
            params.put("tagIds["+i+"]", tagIds[i]);
        }
        params.put("avatar", new Pair<String, File>("image/jpg", infoEntify.getAvatarFile()));
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                toast(R.string.register_success);
                loginAction();
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
                loadingDialog.dismiss();
            }
        });
    }


    private void loginAction(){
        loadingDialog.show();
        BaseParams params = new BaseParams(API.LOGIN);
        params.put("mobile", infoEntify.getMobile());
        params.put("password", infoEntify.getPassword());
        params.put("remember", true);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                UserInfoEntity entity = JSON.parseObject(msg, UserInfoEntity.class);
                UserPref.getInstance().setCurLoginUser(entity);
                loadingDialog.dismiss();
                IndexHome.checkAndStartIndexActivity(CompleteRegister.this);
                finish();
            }

            @Override
            public void fail(int code, String error) {
                loadingDialog.dismiss();
                toast(error);
                Intent intent = new Intent(CompleteRegister.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        PhoneManager.hideKeyboard(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        btnGetVerifcode.onDestroy();
        super.onDestroy();
    }
}
