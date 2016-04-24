package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gather.android.R;
import com.gather.android.adapter.OrgPhotoListAdapter;
import com.gather.android.adapter.data.OrgPhotoListData;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.entity.OrgPhotoListEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.gather.android.widget.TitleBar;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 社团相册列表
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoList extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<OrgPhotoListEntity>> listViewHelper;
    private String orgId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_photo_list);
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            orgId = intent.getExtras().getString("ID");

            titlebar.setHeaderTitle("相册");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

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

            listViewHelper = new MVCUltraHelper<List<OrgPhotoListEntity>>(ptrLayout);
            listViewHelper.setDataSource(new OrgPhotoListData(orgId));
            listViewHelper.setAdapter(new OrgPhotoListAdapter(this));
            listViewHelper.refresh();
        } else {
            toast("社团相册信息错误");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listViewHelper.destory();
    }
}
