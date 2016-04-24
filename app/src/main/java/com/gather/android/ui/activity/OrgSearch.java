package com.gather.android.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.OrgListAdapter;
import com.gather.android.adapter.data.OrgListData;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 社团搜索
 * Created by Administrator on 2015/7/7.
 */
public class OrgSearch extends BaseActivity {

    @InjectView(R.id.etContent)
    EditText etContent;
    @InjectView(R.id.ivClear)
    ImageView ivClear;
    @InjectView(R.id.tvSearch)
    TextView tvSearch;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;
    @InjectView(R.id.maskFrame)
    ImageView maskFrame;

    private MVCHelper<List<OrgDetailEntity>> listViewHelper;
    private OrgListData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_search);
        ButterKnife.inject(this);

        listViewHelper.setLoadViewFractory(new NormalLoadViewFactory());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);
        ptrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

            }
        });

        ivClear.setVisibility(View.GONE);
        etContent.addTextChangedListener(new textWatcher());
        listViewHelper = new MVCUltraHelper<List<OrgDetailEntity>>(ptrLayout);
        data = new OrgListData();
        listViewHelper.setDataSource(data);
        listViewHelper.setAdapter(new OrgListAdapter(this));
        setListVisible(false);
    }

    private class textWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            ivClear.setVisibility(Checker.isEmpty(editable.toString()) ? View.INVISIBLE : View.VISIBLE);
        }
    }


    @OnClick({R.id.ivClear, R.id.tvSearch, R.id.ibtn_backpress})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_backpress:
                onBackPressed();
                break;
            case R.id.ivClear:
                if (!TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    etContent.setText("");
                }
                break;
            case R.id.tvSearch:
                PhoneManager.hideKeyboard(OrgSearch.this);
                if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    toast("请先输入社团名字");
                    return;
                }
                data.setSearch(etContent.getText().toString().trim());
                listViewHelper.refresh();
                setListVisible(true);
                break;
        }
    }

    private void setListVisible(boolean visible) {
        maskFrame.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
    }
}
