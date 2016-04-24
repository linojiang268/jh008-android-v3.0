package com.gather.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.gather.android.R;
import com.gather.android.event.UpdatePhotoListEvent;
import com.gather.android.ui.activity.ActPhotoGallery;
import com.gather.android.ui.activity.MySharePhoto;
import com.gather.android.adapter.OrgPhotoDetailAdapter;
import com.gather.android.adapter.data.OrgPhotoDetailData;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.entity.OrgPhotoEntity;
import com.gather.android.event.ActPhotoGalleryBackEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.event.SimpleEventHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.shizhefei.gridview.GridViewWithHeaderAndFooter;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.Subscribe;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 社团相册详情
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoDetailFragment extends BaseFragment {

    @InjectView(R.id.gridView)
    GridViewWithHeaderAndFooter gridView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<OrgPhotoEntity>> listViewHelper;
    private OrgPhotoDetailAdapter adapter;
    private OrgPhotoDetailData data;
    private int type;
    private String actId;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_org_photo_detail);

        ButterKnife.inject(this, getContentView());
        Bundle bundle = getArguments();
        type = bundle.getInt("TYPE");
        actId = bundle.getString("ACT_ID");

        listViewHelper.setLoadViewFractory(new NormalLoadViewFactory());
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);

        listViewHelper = new MVCUltraHelper<List<OrgPhotoEntity>>(ptrLayout);
        data = new OrgPhotoDetailData(type, actId);
        listViewHelper.setDataSource(data);
        adapter = new OrgPhotoDetailAdapter(getActivity(), type == 1);
        adapter.setOnPhotoClickListener(new OrgPhotoDetailAdapter.OnPhotoClickListener() {
            @Override
            public void OnPhotoClick(int position) {
                Intent intent = null;
                if (type == 1 && position == 0) {
                    intent = new Intent(getActivity(), MySharePhoto.class);
                    intent.putExtra("ID", actId);
                } else {
                    intent = new Intent(getActivity(), ActPhotoGallery.class);
                    intent.putExtra("LIST", adapter.getList());
                    intent.putExtra("POSITION", type == 1 ? position - 1 : position);
                    intent.putExtra("TYPE", type);
                    intent.putExtra("ACT_ID", actId);
                    intent.putExtra("PAGE", data.getIndexPage());
                    intent.putExtra("MAX_PAGE", data.getMaxPage());
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        });
        listViewHelper.setAdapter(adapter);
        listViewHelper.refresh();

        EventCenter.bindContainerAndHandler(getActivity(), new SimpleEventHandler() {
            @Subscribe
            public void onEvent(ActPhotoGalleryBackEvent event) {
                if (event.getType() == type) {
                    data.setIndexPage(event.getPage());
                    gridView.setSelection(event.getPosition());
                    adapter.setList(event.getList());
                    adapter.notifyDataSetChanged();
                }
            }
        }).tryToRegisterIfNot();

        if (type == 1){
            EventCenter.getInstance().register(this);
        }
    }

    @Subscribe
    public void onEvent(UpdatePhotoListEvent event){
        listViewHelper.refresh();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
        listViewHelper.destory();
        if (type == 1){
            EventCenter.getInstance().unregister(this);
        }
    }


}
