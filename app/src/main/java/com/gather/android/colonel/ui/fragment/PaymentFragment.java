package com.gather.android.colonel.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.colonel.adpter.PaymentListAdapter;
import com.gather.android.colonel.data.SignUpListData;
import com.gather.android.colonel.dialog.DetailDialog;
import com.gather.android.entity.SignUpEntity;
import com.gather.android.colonel.inter.OnShowDetailListener;
import com.gather.android.entity.SignUpListEntity;
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
 * 报名管理待缴费
 * Created by Levi on 2015/9/30.
 */
public class PaymentFragment extends BaseFragment implements OnShowDetailListener{

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<SignUpListEntity> listViewHelper;
    private PaymentListAdapter adapter;
    private String actId;

    private DetailDialog mDialog;
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.colonel_signup_payment_fragment);
        ButterKnife.inject(this, getContentView());
        actId = getArguments().getString("ACTID", "");
        initView();
    }

    private void initView(){
        mDialog = new DetailDialog(getActivity());

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
        listViewHelper.setDataSource(new SignUpListData(actId, 2));
        adapter = new PaymentListAdapter(getActivity());
        adapter.setOnShowDetailListener(this);
        listViewHelper.setAdapter(adapter);

        listViewHelper.refresh();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
    }

    @Override
    public void showDetail(SignUpEntity entity) {
        mDialog.setData(entity);
        mDialog.show();
    }
}
