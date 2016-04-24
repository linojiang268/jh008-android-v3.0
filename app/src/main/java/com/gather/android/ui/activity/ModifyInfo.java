package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.DatePickDialog;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.UpdateInfoEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.Checker;
import com.gather.android.widget.TitleBar;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * 修改资料
 * Created by Levi on 2015/8/3.
 */
public class ModifyInfo extends BaseActivity {
    public static final String EXTRA_MODIFY = "extra_modify";

    public static final int MODIFY_NICKNAME = 1;
    public static final int MODIFY_PHONENUMBER = 2;
    public static final int MODIFY_PASSWORD = 3;
    public static final int MODIFY_BIRTHDAY = 4;

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.editText)
    EditText editText;
    @InjectView(R.id.btnClear)
    ImageButton btnClear;
    @InjectView(R.id.editArea)
    LinearLayout editArea;
    @InjectView(R.id.tvAge)
    TextView tvAge;
    @InjectView(R.id.btnAge)
    LinearLayout btnAge;
    @InjectView(R.id.pwdArea)
    LinearLayout pwdArea;
    @InjectView(R.id.etPwd)
    EditText etPwd;
    @InjectView(R.id.btnClearPwd)
    ImageButton btnClearPwd;


    private int modifyType;
    private UserInfoEntity userInfo;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_info);

        Intent intent = getIntent();
        modifyType = intent.getIntExtra(EXTRA_MODIFY, 0);
        if (modifyType == 0) {
            finish();
        } else {
            userInfo = UserPref.getInstance().getUserInfo();
            initView();
        }

    }

    private void initView() {
        TextView btnSave = new TextView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnSave.setLayoutParams(layoutParams);
        btnSave.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size));
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding_5);
        btnSave.setPadding(padding, 0, padding, 0);
        btnSave.setGravity(Gravity.CENTER);
        btnSave.setBackgroundResource(R.drawable.titlebar_btn_click_style);
        btnSave.setTextColor(getResources().getColor(R.color.white));
        btnSave.setTextSize(16);
        btnSave.setText(R.string.save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveClick();
            }
        });
        titlebar.setCustomizedRightView(btnSave);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (modifyType == MODIFY_BIRTHDAY) {
            titlebar.setHeaderTitle(R.string.age);
            btnAge.setVisibility(View.VISIBLE);
            btnAge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBirthdayDialog();
                }
            });
            String ageStr = String.format(getString(R.string.age_format), userInfo.getAge());
            tvAge.setText(ageStr);
        } else {
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editText.setText("");
                }
            });
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    btnClear.setVisibility(TextUtils.isEmpty(editable.toString()) ? View.GONE : View.VISIBLE);
                }
            });

            switch (modifyType) {
                case MODIFY_NICKNAME:
                    titlebar.setHeaderTitle(R.string.nick_name);
                    editArea.setVisibility(View.VISIBLE);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                    editText.append(userInfo.getNickName());
                    editText.setHint(R.string.input_new_nickename);
                    break;
                case MODIFY_PASSWORD:
                    titlebar.setHeaderTitle(R.string.password);
                    editArea.setVisibility(View.VISIBLE);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setHint(R.string.input_old_pwd);

                    pwdArea.setVisibility(View.VISIBLE);
                    etPwd.setHint(R.string.input_new_pwd);
                    etPwd.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            btnClearPwd.setVisibility(TextUtils.isEmpty(editable.toString()) ? View.GONE : View.VISIBLE);
                        }
                    });
                    btnClearPwd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etPwd.setText("");
                        }
                    });
                    break;
                case MODIFY_PHONENUMBER:
                    titlebar.setHeaderTitle(R.string.phonenumber);
                    editArea.setVisibility(View.VISIBLE);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText.append(userInfo.getMobile());
                    editText.setHint(R.string.input_new_phone);
                    break;
            }
        }
    }

    private void showBirthdayDialog() {
        DatePickDialog mDateDialog = new DatePickDialog(this, R.style.dialog_untran);
        mDateDialog.setDateListener(new DatePickDialog.OnDateClickListener() {
            @Override
            public void onDateListener(int year, int month, int day, String date, int age) {
                userInfo.setBirthday(date);
                String text = String.format(getString(R.string.age_format), age);
                tvAge.setText(text);
            }
        });
        mDateDialog.show();
    }

    private boolean checkContent() {
        boolean isOk = false;
        switch (modifyType) {
            case MODIFY_BIRTHDAY:
                isOk = true;
                break;
            case MODIFY_NICKNAME:
                String nickName = editText.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    toast(R.string.input_nickname);
                } else {
                    userInfo.setNickName(nickName);
                    isOk = true;
                }
                break;
            case MODIFY_PASSWORD:
                String oldPwd = editText.getText().toString();
                String newPwd = etPwd.getText().toString();
                if (!Checker.isEnoughLong(oldPwd, 6)) {
                    toast(R.string.input_right_old_password);
                }
                else if (!Checker.isEnoughLong(newPwd, 6)) {
                    toast(R.string.input_right_new_password);
                }
                else {
                    isOk = true;
                }
                break;
            case MODIFY_PHONENUMBER:
                String phone = editText.getText().toString();
                if (!Checker.isMobilePhone(phone)) {
                    toast(R.string.input_right_mobilephone);
                } else {
                    userInfo.setMobile(phone);
                    isOk = true;
                }
                break;
        }
        return isOk;
    }

    private void saveClick() {
        if (checkContent()) {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog.createDialog(this, true);
                loadingDialog.setMessage(getString(R.string.saving));
            }
            loadingDialog.show();
            BaseParams params;
            if (modifyType == MODIFY_PASSWORD) {
                params = new BaseParams(API.MODIFY_PASSWORD);
                params.put("original_password", editText.getText().toString());
                params.put("new_password", etPwd.getText().toString());
            }
            else {
                params = new BaseParams(API.MODIFY_PROFILE);
                ArrayList<String> tagIds = userInfo.getTagIdsForArray();
                for (int i = 0; i < tagIds.size(); i++) {
                    params.put("tagIds[" + i + "]", tagIds.get(i));
                }
                params.put("nick_name", userInfo.getNickName());
                params.put("gender", userInfo.getGender());
                params.put("birthday", userInfo.getBirthday());
                params.put("mobile", userInfo.getMobile());
            }

            OkHttpUtil.post(params, new ResponseHandler() {
                @Override
                public void success(String msg) {
                    loadingDialog.dismiss();
                    toast(R.string.modify_info_success);
                    if (modifyType != MODIFY_PASSWORD){
                        EventCenter.getInstance().post(new UpdateInfoEvent());
                    }
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

}
