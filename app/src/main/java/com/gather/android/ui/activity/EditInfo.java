package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.HomeDataUpdateEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MobileTextWatcher;
import com.gather.android.widget.TitleBar;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCNormalHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 自定义信息填写后提交
 * Created by Administrator on 2015/7/22.
 */
public class EditInfo extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.scrollView)
    ScrollView scrollView;
    @InjectView(R.id.llContent)
    LinearLayout llContent;
    @InjectView(R.id.btnCommit)
    Button btnCommit;

    private MVCHelper<List<OrgDetailEntity.JoinRequirements>> listViewHelper;
    private ArrayList<OrgDetailEntity.JoinRequirements> list = new ArrayList<>();
    private List<EditText> editTexts = new ArrayList<EditText>();
    private OrgDetailEntity model;
    private HashMap<String, String> map = new HashMap<>();
    private LoadingDialog mLoadingDialog;
    private String beforeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);
        Intent intent = getIntent();
        if (intent.hasExtra("ORG_MODEL")) {
            model = (OrgDetailEntity) intent.getSerializableExtra("ORG_MODEL");
            list = model.getJoin_requirements();
            mLoadingDialog = LoadingDialog.createDialog(this, true);
            titlebar.setHeaderTitle("验证信息");
            btnCommit.setText("提交申请");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            listViewHelper = new MVCNormalHelper<List<OrgDetailEntity.JoinRequirements>>(scrollView);
            listViewHelper.setDataSource(new EditInfoSource());
            listViewHelper.setAdapter(dataAdapter);
            listViewHelper.refresh();
        } else {
            toast("页面信息错误");
            finish();
        }
    }

    @OnClick(R.id.btnCommit)
    void OnCommitClick() {
        mLoadingDialog.setMessage("提交中...");
        mLoadingDialog.show();
        for (int i = 0; i < list.size(); i++) {
            String content = editTexts.get(i).getText().toString().replaceAll(" ", "");
            if (TextUtils.isEmpty(content)) {
                map.clear();
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                toast("验证信息不能有空");
                return;
            }
            map.put(list.get(i).getId() + "", content);
        }
        Enroll();
    }

    /**
     * 提交申请
     */
    private void Enroll() {
        BaseParams params = new BaseParams(API.APPLY_JOIN_ORG);
        params.put("team", model.getId());
        params.put("requirements", JSON.toJSONString(map));
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                //更新首页社团
                EventCenter.getInstance().post(new HomeDataUpdateEvent(1));

                Intent intent = new Intent(EditInfo.this, SignTips.class);
                intent.putExtra("TYPE", "ORG");
                if (model.isIn_whitelist()) {
                    intent.putExtra("BIG", "恭喜你");
                    intent.putExtra("MSG", "加入" + model.getName());
                    intent.putExtra("TWO", "你可以在社团主页查看社团相关信息");
                    intent.putExtra("ICON", R.drawable.icon_green_success);
                } else {
                    intent.putExtra("BIG", "待审核");
                    intent.putExtra("MSG", "团长审核通过后您方可加入");
                    intent.putExtra("TWO", "请到“我的社团”中实时查看审核结果");
                    intent.putExtra("ICON", R.drawable.icon_blue_wait);
                }
                startActivity(intent);
                finish();
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

    private class EditInfoSource extends BaseDataSource<List<OrgDetailEntity.JoinRequirements>> {

        @Override
        public List<OrgDetailEntity.JoinRequirements> refresh() throws Exception {
            return list;
        }

        @Override
        public List<OrgDetailEntity.JoinRequirements> loadMore() throws Exception {
            return null;
        }
    }

    private IDataAdapter<List<OrgDetailEntity.JoinRequirements>> dataAdapter = new IDataAdapter<List<OrgDetailEntity.JoinRequirements>>() {

        private List<OrgDetailEntity.JoinRequirements> data;

        @Override
        public void notifyDataChanged(List<OrgDetailEntity.JoinRequirements> strings, boolean isRefresh) {
            data = strings;
            for (int i = 0; i < data.size(); i++) {
                createEditText(data.get(i).getRequirement());
            }
        }

        @Override
        public List<OrgDetailEntity.JoinRequirements> getData() {
            return data;
        }

        @Override
        public boolean isEmpty() {
            return data == null;
        }
    };

    private void createEditText(String string) {
        LinearLayout layout = new LinearLayout(EditInfo.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhoneManager.dip2px(48));
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(EditInfo.this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PhoneManager.dip2px(48));
        layout.setLayoutParams(textParams);
        textView.setGravity(Gravity.CENTER);
        textView.setText(string);
        textView.setTextSize(16);
        textView.setTextColor(0xFF999999);
        textView.setSingleLine(true);
        textView.setPadding(PhoneManager.dip2px(12), 0, PhoneManager.dip2px(12), 0);

        final EditText editText = new EditText(EditInfo.this);
        layout.addView(textView);
        layout.addView(editText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, PhoneManager.dip2px(48), 1.0f);
        editText.setLayoutParams(params);
        editText.setBackgroundColor(0x00000000);
        if (string.equals("手机号")) {
            editText.setText(UserPref.getInstance().getUserInfo().getMobile());
            editText.addTextChangedListener(new MobileTextWatcher(editText));
        }
        if (beforeString.equals("手机号")) {
            editText.requestFocus();
        }
        editText.setTextSize(16);
        editText.setSingleLine(true);
        editText.setTextColor(getResources().getColor(R.color.black));
        editText.setPadding(0, 0, PhoneManager.dip2px(12), 0);
        editText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        View line = new View(EditInfo.this);
        line.setBackgroundResource(R.color.line_color);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(param);

        editTexts.add(editText);
        llContent.addView(layout);
        llContent.addView(line);

        beforeString = string;
    }
}
