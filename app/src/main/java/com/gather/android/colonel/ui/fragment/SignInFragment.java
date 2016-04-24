package com.gather.android.colonel.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.colonel.adpter.SignInListAdapter;
import com.gather.android.colonel.adpter.SuccessListAdapter;
import com.gather.android.colonel.data.SignInListData;
import com.gather.android.entity.SignInListEntity;
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
 * Created by Levi on 15/10/5.
 */
public class SignInFragment extends BaseFragment {
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<SignInListEntity> listViewHelper;
    private SuccessListAdapter adapter;
    private String actId;
    private int type;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.colonel_signup_success_fragment);
        ButterKnife.inject(this, getContentView());
        actId = getArguments().getString("ACTID", "");
        type = getArguments().getInt("TYPE", 0);
        initView();
    }

    private void initView(){

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
        listViewHelper = new MVCUltraHelper<SignInListEntity>(ptrLayout);
        listViewHelper.setDataSource(new SignInListData(actId, type));
        listViewHelper.setAdapter(new SignInListAdapter(getActivity()));

        listViewHelper.refresh();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
    }
}
