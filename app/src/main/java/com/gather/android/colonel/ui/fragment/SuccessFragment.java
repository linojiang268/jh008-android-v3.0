package com.gather.android.colonel.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.colonel.adpter.SuccessListAdapter;
import com.gather.android.colonel.data.SignUpListData;
import com.gather.android.colonel.dialog.RemarksDialog;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.SignUpEntity;
import com.gather.android.colonel.inter.OnAddRemarksListener;
import com.gather.android.entity.SignUpListEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.shizhefei.mvc.MVCHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 报名管理报名成功
 * Created by Levi on 2015/9/30.
 */
public class SuccessFragment extends BaseFragment implements OnAddRemarksListener {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<SignUpListEntity> listViewHelper;
    private SuccessListAdapter adapter;
    private String actId;
    private SignUpEntity entity;
    private int position;

    private RemarksDialog mRemarksDlg;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.colonel_signup_success_fragment);
        ButterKnife.inject(this, getContentView());
        actId = getArguments().getString("ACTID", "");
        initView();
    }

    private void initView(){
        mRemarksDlg = new RemarksDialog(getActivity());
        mRemarksDlg.setAddRemarksListener(new RemarksDialog.AddRemarksListener() {
            @Override
            public void addRemarks(String remarks) {
                addRemarksAction(remarks);
            }
        });
        loadingDialog = LoadingDialog.createDialog(getActivity(), false);
        loadingDialog.setMessage("正在添加备注...");


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
        listViewHelper = new MVCUltraHelper<SignUpListEntity>(ptrLayout);
        listViewHelper.setDataSource(new SignUpListData(actId, 3));
        adapter = new SuccessListAdapter(getActivity());
        adapter.setOnAddRemarksListener(this);
        listViewHelper.setAdapter(adapter);

        listViewHelper.refresh();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
    }

    @Override
    public void onAddRemarks(int position, SignUpEntity entity) {
        this.entity = entity;
        this.position = position;
        mRemarksDlg.setContentAndTitle(entity.getRemark(), "为 " + entity.getName() + " 添加备注");
        mRemarksDlg.show();
    }

    private void addRemarksAction(String remarks){
        loadingDialog.show();
        entity.setRemark(remarks);
        BaseParams params = new BaseParams(API.ADD_REMARKS);
        params.put("activity", actId);
        params.put("applicant", entity.getId());
        params.put("content", remarks);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String data) {
                loadingDialog.dismiss();
                adapter.updateRemarks(position, entity);
                Toast.makeText(getContext(), "备注添加成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void fail(int errorCode, String msg) {
                loadingDialog.dismiss();
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
