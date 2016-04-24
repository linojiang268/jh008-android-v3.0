package com.gather.android.colonel.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.colonel.adpter.PendingListAdapter;
import com.gather.android.colonel.data.SignUpListData;
import com.gather.android.colonel.dialog.DetailDialog;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.SignUpEntity;
import com.gather.android.colonel.inter.OnItemSelectStateChangedListener;
import com.gather.android.colonel.inter.OnShowDetailListener;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.SignUpListEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 报名管理待审核
 * Created by Levi on 2015/9/30.
 */
public class PendingFragment extends BaseFragment implements  OnItemSelectStateChangedListener, OnShowDetailListener{

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;
    @InjectView(R.id.btnPass)
    Button btnPass;
    @InjectView(R.id.btnRefuse)
    Button btnRefuse;
    @InjectView(R.id.llButtonBar)
    LinearLayout llButtonBar;

    private MVCHelper<SignUpListEntity> listViewHelper;
    private PendingListAdapter adapter;
    private String actId;

    private Dialog mDialog;
    private List<String> mSelectedList;

    private LoadingDialog mLoadingDilg;
    private DetailDialog mDeailDialog;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.colonel_signup_pending_fragment);
        ButterKnife.inject(this, getContentView());
        actId = getArguments().getString("ACTID", "");
        initView();
    }

    private void initView(){
        mDeailDialog = new DetailDialog(getActivity());
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass();
            }
        });

        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuse();
            }
        });

        mLoadingDilg = LoadingDialog.createDialog(getActivity(), true);
        mLoadingDilg.setMessage("正在操作...");


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
        listViewHelper.setDataSource(new SignUpListData(actId, 1));
        adapter = new PendingListAdapter(getActivity());
        adapter.setOnItemSelectStateChangedListener(this);
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
    public void onItemSelectStateChanged(boolean isEmpty) {
        llButtonBar.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showDetail(SignUpEntity entity) {
        mDeailDialog.setData(entity);
        mDeailDialog.show();
    }

    private void pass(){
        mSelectedList = adapter.getSelectedList();
        mDialog = DialogCreater.createNormalDialog(getActivity(),
                "", "是否通过选中的 " + mSelectedList.size() + " 人？", new OnBtnLeftClickL() {
                    @Override
                    public void onBtnLeftClick() {
                        mDialog.dismiss();
                        passAction();
                    }
                });
        mDialog.show();
    }

    private void refuse(){
        mSelectedList = adapter.getSelectedList();
        mDialog = DialogCreater.createNormalDialog(getActivity(),
                "", "是否拒绝选中的 " + mSelectedList.size() + " 人？", new OnBtnLeftClickL() {
                    @Override
                    public void onBtnLeftClick() {
                        mDialog.dismiss();
                        refuseAction();
                    }
                });
        mDialog.show();
    }

    private void passAction(){
        mLoadingDilg.show();
        BaseParams params = new BaseParams(API.ACT_PENDING_PASS);
        params.put("activity", actId);
        List<String> list = adapter.getSelectedList();
        for (int i = 0; i < list.size(); i++){
            params.put("applicants[" + i + "]", list.get(i));
        }
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String data) {
                mLoadingDilg.dismiss();
                adapter.removeSlectedFromListAndView();
                listViewHelper.refresh();
                Toast.makeText(getContext(), "操作成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void fail(int errorCode, String msg) {
                mLoadingDilg.dismiss();
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refuseAction(){
        mLoadingDilg.show();
        BaseParams params = new BaseParams(API.ACT_PENDING_REFUSE);
        params.put("activity", actId);
        List<String> list = adapter.getSelectedList();
        for (int i = 0; i < list.size(); i++){
            params.put("applicants[" + i + "]", list.get(i));
        }
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String data) {
                mLoadingDilg.dismiss();
                adapter.removeSlectedFromListAndView();
                listViewHelper.refresh();
                Toast.makeText(getContext(), "操作成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void fail(int errorCode, String msg) {
                mLoadingDilg.dismiss();
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


}
