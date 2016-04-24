package com.gather.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gather.android.R;
import com.gather.android.ui.activity.NearbyActMap;
import com.gather.android.ui.activity.SearchActActivity;
import com.gather.android.adapter.ActListAdapter;
import com.gather.android.adapter.data.ActListData;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.entity.ActEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.TabHostLoadViewFactory;
import com.gather.android.widget.TitleBar;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 活动
 * Created by Christain on 2015/6/15.
 */
public class ActFragment extends BaseFragment {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    private ImageButton btnSearch;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;
    private MVCHelper<List<ActEntity>> listViewHelper;

    private ActListData listData;


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_act);
        ButterKnife.inject(this, getContentView());

        initView();
    }

    private void initView(){
        titlebar.getBackImageButton().setImageResource(R.drawable.ic_neayby_act);
        int padding = PhoneManager.dip2px(13);
        titlebar.getBackImageButton().setPadding(padding, padding, padding, padding);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NearbyActMap.class);
                startActivity(intent);
            }
        });
        titlebar.setHeaderTitle(R.string.activity_title);

        btnSearch  = new ImageButton(getActivity());
        btnSearch.setImageResource(R.drawable.icon_titlebar_search);
        btnSearch.setBackgroundResource(R.drawable.titlebar_btn_click_style);
        btnSearch.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        titlebar.setCustomizedRightView(btnSearch);
        ViewGroup.LayoutParams params =  btnSearch.getLayoutParams();
        params.width = getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size);
        params.height = params.width;
        btnSearch.setLayoutParams(params);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActActivity.class);
                startActivity(intent);
            }
        });

        listViewHelper.setLoadViewFractory(new TabHostLoadViewFactory());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);

        listViewHelper = new MVCUltraHelper<List<ActEntity>>(ptrLayout);
        listData = new ActListData(ActBaseFragment.MODE_NORMAL);
        listViewHelper.setDataSource(listData);
        listViewHelper.setAdapter(new ActListAdapter(getActivity()));

        listViewHelper.refresh();
    }


    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
    }
}
