package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.Checker;
import com.gather.android.utils.MobileTextWatcher;
import com.gather.android.widget.KeyBoardLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;

/**
 * 登录页面
 * Created by Levi on 2015/7/6.
 */
public class Login extends BaseActivity {
    @InjectView(R.id.btn_forget_pwd)
    Button btnForgetPwd;
    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.btn_register)
    Button btnRegister;
    @InjectView(R.id.et_phonenumber)
    EditText etPhoneNumber;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.keyboard)
    KeyBoardLinearLayout keyboard;
    @InjectView(R.id.logo)
    LinearLayout logo;

    private LoadingDialog loadingDialog;
    private Handler handler = new LoginHandler();
    private boolean isSoftInputOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        keyboard.setOnKeyBoardStateChangeListener(new KeyBoardLinearLayout.OnKeyBoardStateChangeListener() {
            @Override
            public void onStateChange(boolean visible) {
                isSoftInputOn = visible;
                if (visible){
                    logo.setVisibility(View.GONE);
                }
                else {
                    AlphaAnimation anim = new AlphaAnimation(0f, 1f);
                    logo.setVisibility(View.VISIBLE);
                    anim.setStartOffset(50);
                    anim.setDuration(300);
                    logo.startAnimation(anim);
                }

            }
        });


        btnForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAction(1);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAction(2);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAction(0);
            }
        });

        loadingDialog = LoadingDialog.createDialog(this, true);
        loadingDialog.setMessage(getString(R.string.is_logining));


        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    doAction(0);
                }
                return false;
            }
        });

        etPhoneNumber.addTextChangedListener(new MobileTextWatcher(etPhoneNumber));

        String phone = Checker.formatPhoneNumber(getSharedPreferences("mobile", Context.MODE_PRIVATE).getString("mobile", ""));
        etPhoneNumber.setText(phone);
        etPhoneNumber.setSelection(phone.length());
        if (phone.length() > 0) {
            etPassword.requestFocus();
        }
    }

    /**
     * 检查手机号和密码，无误后开始登陆
     */
    private void loginAction() {
        do {
            String phone = etPhoneNumber.getText().toString().replaceAll(" ", "");
            if (!Checker.isMobilePhone(phone)) {
                etPhoneNumber.requestFocus();
                toast(R.string.input_right_mobilephone);
                break;
            }
            String pwd = etPassword.getText().toString();
            if (!Checker.isEnoughLong(pwd, 6)) {
                etPassword.requestFocus();
                toast(R.string.input_right_password);
                break;
            }
            loginAction(phone, pwd);
        }
        while (false);
    }

    private void loginAction(final String phone, String pwd) {
        loadingDialog.show();
        BaseParams params = new BaseParams(API.LOGIN);
        params.put("mobile", phone);
        params.put("password", pwd);
        params.put("remember", true);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                UserInfoEntity entity = JSON.parseObject(msg, UserInfoEntity.class);
                UserPref.getInstance().setCurLoginUser(entity);
                SharedPreferences.Editor editor = getSharedPreferences("mobile", Context.MODE_PRIVATE).edit();
                editor.putString("mobile", entity.getMobile());
                editor.commit();
                loadingDialog.dismiss();
                IndexHome.checkAndStartIndexActivity(Login.this);
                finish();
            }

            @Override
            public void fail(int code, String error) {
                if (code != 10102) {
                    toast(error);
                } else {
                    try {
                        JSONObject object = new JSONObject(error);
                        if (object.has("push_alias")) {
                            UserPref.getInstance().setAlias(object.getString("push_alias"));
                        }
                        if (object.has("message")) {
                            toast(object.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //完善资料
                    Intent intent = new Intent(Login.this, IntrestPage.class);
                    intent.putExtra(IntrestPage.EXTRA_PHONE, phone);
                    intent.putExtra(IntrestPage.EXTRA_MODE, IntrestPage.MODE_FIX_INFO);
                    startActivity(intent);
                }
                loadingDialog.dismiss();
            }
        });
    }

    private void doAction(int act){
        if (isSoftInputOn){
            try {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            handler.sendEmptyMessageDelayed(act, 300);
        }
        else {
            handler.sendEmptyMessage(act);
        }
    }

    private class LoginHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    loginAction();
                    break;
                case 1:
                    Intent intent1 = new Intent(Login.this, ForgetPassword.class);
                    startActivity(intent1);
                    break;
                case 2:
                    Intent intent2 = new Intent(Login.this, ShowPage.class);
                    startActivity(intent2);
                    break;
            }
        }
    }
}
