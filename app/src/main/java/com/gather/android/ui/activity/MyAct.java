package com.gather.android.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.MyActListAdapter;
import com.gather.android.adapter.data.MyActListData;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.entity.MyActEntity;
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
 * 我的活动
 * Created by Levi on 2015/7/29.
 */
public class MyAct extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<MyActEntity>> listViewHelper;
    private PopupWindow popWind;
    private TextView btnFilter;

    private MyActListData listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_act);

        initView();
    }

    private void initView() {
        titlebar.setHeaderTitle(R.string.my_act);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int titleBarBtnSize = getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size);
        btnFilter = new TextView(this);
        RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        btnFilter.setLayoutParams(params);
        btnFilter.setMinimumWidth(titleBarBtnSize);
        btnFilter.setPadding(5, 0, 5, 0);
        btnFilter.setGravity(Gravity.CENTER);
        btnFilter.setTextSize(16);
        btnFilter.setTextColor(getResources().getColor(R.color.white));
        btnFilter.setBackgroundResource(R.drawable.titlebar_btn_click_style);
        btnFilter.setText(R.string.filter);
        titlebar.setCustomizedRightView(btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterPop();
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

        listData = new MyActListData(this);
        listViewHelper = new MVCUltraHelper<List<MyActEntity>>(ptrLayout);
        listViewHelper.setDataSource(listData);
        listViewHelper.setAdapter(new MyActListAdapter(this));

        listViewHelper.refresh();
    }

    private void showFilterPop(){
        if (listViewHelper.isLoading())return;
        if (popWind == null){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.pop_my_act, null);
            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
            radioGroup.setOnCheckedChangeListener(new OnCheckedChange());

            popWind = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popWind.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            popWind.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });
            popWind.setOutsideTouchable(true);
            popWind.setAnimationStyle(R.style.DialogTopInAnim);
        }
        popWind.showAsDropDown(titlebar);
        backgroundAlpha(1f);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    private class OnCheckedChange implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            popWind.dismiss();
            // All, NotBeginning, WaitPay, Aoditing, Beginning, End;
            switch (id){
                case R.id.rbtn1://全部
                    listData.setType("All");
                    break;
                case R.id.rbtn2://待确认
                    listData.setType("Enrolling");
                    break;
                case R.id.rbtn3://即将开始
                    listData.setType("NotBeginning");
                    break;
                case R.id.rbtn4://进行中
                    listData.setType("Beginning");
                    break;
                case R.id.rbtn5://已结束
                    listData.setType("End");
                    break;
            }
            listViewHelper.refresh();
        }
    }
}
