package com.gather.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.ResetPwdTokenEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.utils.MobileTextWatcher;
import com.gather.android.widget.TitleBar;
import com.gather.android.widget.VerifyCodeButton;
import com.orhanobut.logger.Logger;

import butterknife.InjectView;

/**
 * 忘记密码
 * Created by Levi on 2015/7/6.
 */
public class ForgetPassword extends BaseActivity {

    @InjectView(R.id.btn_getverifycode)
    VerifyCodeButton btnGetVerifcode;

    @InjectView(R.id.btn_save)
    Button btnSave;

    @InjectView(R.id.et_phonenumber)
    EditText etPhoneNumber;

    @InjectView(R.id.et_verifycode)
    EditText etVerifycode;

    @InjectView(R.id.et_password)
    EditText etPassword;

    @InjectView(R.id.titlebar)
    TitleBar titlebar;

    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        titlebar.setHeaderTitle(R.string.reset_password);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        btnGetVerifcode.setTypeAndListener(VerifyCodeButton.TYPE_RESET_PWD, new VerifyCodeButton.OnGetVerifyCodeListener() {
            @Override
            public String bindPhonenumber() {
                return etPhoneNumber.getText().toString().replaceAll(" ", "");
            }

            @Override
            public void onCountdownStart(String msg) {
                toast(msg);
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        loadingDialog = LoadingDialog.createDialog(this, true);
        loadingDialog.setMessage(getString(R.string.isresetting_pwd));

        etPhoneNumber.addTextChangedListener(new MobileTextWatcher(etPhoneNumber));
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

    private void resetPassword(){
        do {
            String phone = etPhoneNumber.getText().toString().replaceAll(" ", "");
            if (!Checker.isMobilePhone(phone)){
                etPhoneNumber.requestFocus();
                toast(R.string.input_right_mobilephone);
                break;
            }
            String code = etVerifycode.getText().toString();
            if (Checker.isEmpty(code)){
                toast(R.string.input_verifycode);
                break;
            }
            String pwd = etPassword.getText().toString();
            if (!Checker.isEnoughLong(pwd, 6)){
                etPassword.requestFocus();
                toast(R.string.input_right_password);
                break;
            }
            loadingDialog.show();
            getToken();
        }
        while (false);
    }

    private void getToken(){
        BaseParams params = new BaseParams(API.GET_RESET_PWD_TOKEN);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                ResetPwdTokenEntity entity = JSON.parseObject(msg, ResetPwdTokenEntity.class);
                reset(entity.getToken());
                Logger.i(msg);
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
                loadingDialog.dismiss();
            }
        });
    }

    private void reset(String token){
        BaseParams params = new BaseParams(API.RESET_PWD);
        params.put("mobile", etPhoneNumber.getText().toString().replaceAll(" ", ""));
        params.put("code", etVerifycode.getText().toString());
        params.put("password", etPassword.getText().toString());
        params.put("_token", token);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                toast(R.string.reset_pwd_success);
                finish();
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
                loadingDialog.dismiss();
            }
        });
    }
}
