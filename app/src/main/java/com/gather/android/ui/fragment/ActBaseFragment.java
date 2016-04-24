package com.gather.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.gather.android.R;
import com.gather.android.adapter.ActListAdapter;
import com.gather.android.adapter.data.ActListData;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.entity.ActEntity;
import com.gather.android.event.ChangeCityEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.gather.android.utils.TabHostLoadViewFactory;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.Subscribe;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 活动
 * Created by Christain on 2015/6/15.
 */
public class ActBaseFragment extends BaseFragment {

    public static String EXTRA_MODE = "extra_mode";
    public static String EXTRA_KEYWORD = "extra_keyword";
    public static String EXTRA_ORD_ID = "extra_org_id";
    public static int MODE_NORMAL = 1;
    public static int MODE_SEARCH = 2;
    public static int MODE_ORG = 3;//社团近期活动

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;
    @InjectView(R.id.maskFrame)
    ImageView maskFrame;

    private MVCHelper<List<ActEntity>> listViewHelper;

    private ActListData listData;

    private int mode;
    private String keyword;
    private String orgId;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_act_base);
        ButterKnife.inject(this, getContentView());

        mode = getArguments().getInt(EXTRA_MODE, MODE_NORMAL);
        keyword = getArguments().getString(EXTRA_KEYWORD, null);
        orgId = getArguments().getString(EXTRA_ORD_ID, "");
        EventCenter.getInstance().register(this);
        initView();
    }

    private void initView(){
        if (mode == MODE_NORMAL) {
            listViewHelper.setLoadViewFractory(new TabHostLoadViewFactory());
        } else {
            listViewHelper.setLoadViewFractory(new NormalLoadViewFactory());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);
//        ptrLayout.setPtrHandler(new PtrHandler() {
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                return mode == MODE_NORMAL || (mode == MODE_SEARCH && !TextUtils.isEmpty(keyword));
//            }
//
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//
//            }
//        });

        listViewHelper = new MVCUltraHelper<List<ActEntity>>(ptrLayout);
        listData = new ActListData(mode);
        listViewHelper.setDataSource(listData);
        listViewHelper.setAdapter(new ActListAdapter(getActivity()));

        if (mode == MODE_NORMAL){
            setListVisible(true);
            listViewHelper.refresh();
        }
        else if (mode == MODE_SEARCH){
            if (!Checker.isEmpty(keyword)){
                search(keyword);
                setListVisible(true);
            }
            else {
                setListVisible(false);
            }
        }
        else if (mode == MODE_ORG) {
            //社团近期活动
            setListVisible(true);
            listData.setOrgId(orgId);
            listViewHelper.refresh();
        }
    }

    public void search(String keyword){
        setListVisible(true);
        listData.setSearch(keyword);
        listViewHelper.refresh();
    }

    private void setListVisible(boolean visible){
        maskFrame.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Subscribe
    public void onEvent(ChangeCityEvent event) {
        if (listViewHelper != null && mode == MODE_NORMAL) {
            setListVisible(true);
            listData = new ActListData(mode);
            listViewHelper.setDataSource(listData);
            listViewHelper.refresh();
        }
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
        EventCenter.getInstance().unregister(this);
    }
}
