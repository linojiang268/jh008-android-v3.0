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
import com.gather.android.ui.activity.OrgSearch;
import com.gather.android.adapter.OrgListAdapter;
import com.gather.android.adapter.data.OrgListData;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.event.ChangeCityEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.TabHostLoadViewFactory;
import com.gather.android.widget.TitleBar;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.Subscribe;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by Administrator on 2015/7/7.
 */
public class OrgListFragment extends BaseFragment implements View.OnClickListener{

    @InjectView(R.id.toolbar)
    TitleBar toolbar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<OrgDetailEntity>> listViewHelper;
    private ImageButton ivSearch;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_org_list);
        ButterKnife.inject(this, getContentView());

        EventCenter.getInstance().register(this);

        toolbar.setHeaderTitle("社团");
        toolbar.getLeftViewContainer().setVisibility(View.GONE);

        ivSearch  = new ImageButton(getActivity());
        ivSearch.setImageResource(R.drawable.icon_titlebar_search);
        ivSearch.setBackgroundResource(R.drawable.titlebar_btn_click_style);
        ivSearch.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        toolbar.setCustomizedRightView(ivSearch);
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) ivSearch.getLayoutParams();
        params.width = getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size);
        params.height = params.width;
        ivSearch.setLayoutParams(params);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrgSearch.class);
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

        listViewHelper = new MVCUltraHelper<List<OrgDetailEntity>>(ptrLayout);
        listViewHelper.setDataSource(new OrgListData());
        listViewHelper.setAdapter(new OrgListAdapter(getActivity()));
        listViewHelper.refresh();
    }

    @Subscribe
    public void onEvent(ChangeCityEvent event) {
        if (listViewHelper != null) {
            listViewHelper.setDataSource(new OrgListData());
            listViewHelper.setAdapter(new OrgListAdapter(getActivity()));
            listViewHelper.refresh();
        }
    }

    @Override
    public void onClick(View v) {

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
