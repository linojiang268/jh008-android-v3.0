package com.gather.android.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gather.android.R;
import com.gather.android.adapter.ActFileListAdapter;
import com.gather.android.adapter.data.ActFileListData;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.entity.ActFileEntity;
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
 * Created by Levi on 2015/8/25.
 */
public class ActFileList extends BaseActivity {
    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<ActFileEntity>> listViewHelper;
    private String actId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_file_list);

        actId = getIntent().getStringExtra("ACT_ID");

        initView();
    }

    private void initView() {
        titlebar.setHeaderTitle(R.string.file);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        listViewHelper = new MVCUltraHelper<List<ActFileEntity>>(ptrLayout);
        listViewHelper.setDataSource(new ActFileListData(this, actId));
        listViewHelper.setAdapter(new ActFileListAdapter(this));

        listViewHelper.refresh();
    }
}
