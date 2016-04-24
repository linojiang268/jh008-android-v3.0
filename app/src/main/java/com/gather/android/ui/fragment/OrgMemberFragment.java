package com.gather.android.ui.fragment;

import android.os.Bundle;

import com.gather.android.R;
import com.gather.android.ui.activity.OrgMember;
import com.gather.android.adapter.OrgMemberAdapter;
import com.gather.android.adapter.data.OrgMemberData;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.entity.OrgMemberEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.shizhefei.gridview.GridViewWithHeaderAndFooter;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 社团成员或者活动成员列表
 * Created by Administrator on 2015/7/10.
 */
public class OrgMemberFragment extends BaseFragment {

    @InjectView(R.id.gridView)
    GridViewWithHeaderAndFooter gridView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<OrgMemberEntity>> listViewHelper;
    private int type;
    private String actId, orgId;
    private OrgMemberData data;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_org_member);
        ButterKnife.inject(this, getContentView());
        type = getArguments().getInt("TYPE");
        if (type == OrgMember.ORG) {
            orgId = getArguments().getString("ID");
        } else {
            actId = getArguments().getString("ID");
        }

        listViewHelper.setLoadViewFractory(new NormalLoadViewFactory());
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);

        listViewHelper = new MVCUltraHelper<List<OrgMemberEntity>>(ptrLayout);
        data = new OrgMemberData(type);
        if (type == OrgMember.ORG) {
            data.setOrgId(orgId);
        } else {
            data.setActId(actId);
        }
        listViewHelper.setDataSource(data);
        listViewHelper.setAdapter(new OrgMemberAdapter(getActivity()));
        listViewHelper.refresh();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
        listViewHelper.destory();
    }

}
