package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.adapter.MySharePhotoAdapter;
import com.gather.android.adapter.data.MySharePhotoData;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.OrgPhotoEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.UpdatePhotoListEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.shizhefei.gridview.GridViewWithHeaderAndFooter;
import com.shizhefei.mvc.MVCHelper;

import java.util.List;

import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 我分享的活动图片
 * Created by Levi on 2015/9/10.
 */
public class MySharePhoto extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.gridView)
    GridViewWithHeaderAndFooter gridView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<OrgPhotoEntity>> listViewHelper;
    private MySharePhotoAdapter adapter;
    private MySharePhotoData data;
    private LoadingDialog loadingDialog;
    private Dialog mDelDialog;

    private String actId;
    private int deletedPhotoPosition = -1;
    private String deletedPhotoId;

    private boolean hasDeletePhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_share_photo);
        actId = getIntent().getStringExtra("ID");
        initView();
    }

    private void initView(){
        titlebar.setHeaderTitle(R.string.my_share);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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
        data = new MySharePhotoData(actId);
        listViewHelper.setDataSource(data);
        adapter = new MySharePhotoAdapter(this);
        adapter.setItemClickListener(new MySharePhotoAdapter.OnItemtClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MySharePhoto.this, ActPhotoGallery.class);
                intent.putExtra("LIST", adapter.getList());
                intent.putExtra("POSITION", position);
                intent.putExtra("TYPE", 10);
                intent.putExtra("ACT_ID", actId);
                intent.putExtra("PAGE", data.getIndexPage());
                intent.putExtra("MAX_PAGE", data.getMaxPage());
                startActivity(intent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }

            @Override
            public void onDeleteClick(int position, OrgPhotoEntity entity) {
                deletePhoto(position, entity.getId());
            }
        });
        listViewHelper.setAdapter(adapter);
        listViewHelper.refresh();
    }

    private void deletePhoto(int position, String id){
        deletedPhotoPosition = position;
        deletedPhotoId = id;
        if (mDelDialog == null){
            mDelDialog = DialogCreater.createNormalDialog(this, "温馨提示", "确定删除该照片？", new OnBtnLeftClickL() {
                @Override
                public void onBtnLeftClick() {
                    mDelDialog.dismiss();
                    deleteRequest();
                }
            });
        }
        mDelDialog.show();
    }

    private void deleteRequest(){
        if (loadingDialog == null){
            loadingDialog = LoadingDialog.createDialog(this, true);
            loadingDialog.setMessage("正在删除");
        }
        loadingDialog.show();
        BaseParams params = new BaseParams(API.DEL_MY_SHARE_PHOTO);
        params.put("activity", actId);
        params.put("images[0]", deletedPhotoId);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                toast("照片删除成功");
                adapter.deletePhoto(deletedPhotoPosition);
                hasDeletePhoto = true;
                if (adapter.getCount() == 0){
                    listViewHelper.refresh();
                }
            }

            @Override
            public void fail(int code, String error) {
                loadingDialog.dismiss();
                toast(error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hasDeletePhoto){
            EventCenter.getInstance().post(new UpdatePhotoListEvent());
        }
    }
}
