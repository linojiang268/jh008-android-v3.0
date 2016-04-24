package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.UploadPhotoAdapter;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.MultiImageSelectEvent;
import com.gather.android.event.SingleImageSelectEvent;
import com.gather.android.event.UpdatePhotoListEvent;
import com.gather.android.event.UploadPhotoStatusChangeEvent;
import com.gather.android.service.UploadService;
import com.gather.android.widget.MMAlert;
import com.gather.android.widget.TitleBar;
import com.shizhefei.gridview.GridViewWithHeaderAndFooter;

import java.util.ArrayList;

import butterknife.InjectView;
import de.greenrobot.event.Subscribe;

/**
 * 上传多张照片
 * Created by Administrator on 2015/7/14.
 */
public class UploadPhoto extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.gridView)
    GridViewWithHeaderAndFooter gridView;

    private int MAX_NUM = 9;
    private UploadPhotoAdapter adapter;
    private ArrayList<String> list = new ArrayList<>();
    private boolean isShowDialog;

    private boolean isUploadOver;
    private TextView btnUpload;
    private String actId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo);
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            actId = intent.getExtras().getString("ID");

            titlebar.setHeaderTitle("分享我的照片");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            btnUpload = new TextView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            btnUpload.setLayoutParams(layoutParams);
            btnUpload.setBackgroundResource(R.color.transparent);
            btnUpload.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.titlebar_height));
            btnUpload.setGravity(Gravity.CENTER);
            int padding = getResources().getDimensionPixelOffset(R.dimen.padding_5);
            btnUpload.setPadding(padding, 0, padding, 0);
            btnUpload.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size));
            btnUpload.setTextSize(16);
            btnUpload.setTextColor(getResources().getColor(R.color.white));
            btnUpload.setText(R.string.upload);
            btnUpload.setVisibility(View.INVISIBLE);
            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadClick();
                }
            });
            titlebar.setCustomizedRightView(btnUpload);

            adapter = new UploadPhotoAdapter(this, MAX_NUM);
            gridView.setAdapter(adapter);
            this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    if (adapter.isUploading()){
                        String path = adapter.getItem(position);
                        int status = adapter.getItemUploadStatus(path);
                        //如果上传失败，则重新上传
                        if (status == UploadPhotoStatusChangeEvent.CODE_FAIL){
                            isUploadOver = false;
                            UploadService.reUploadActPhoto(UploadPhoto.this, path);
                            adapter.resetItemUploadStatus(path);
                        }
                    }
                    else {
                        if (!adapter.isFull() && position == adapter.getCount() - 1) {
                            ImageSelectActivity.pickImages(UploadPhoto.this, MAX_NUM, true, list);
                        } else {
                            if (!isShowDialog) {
                                isShowDialog = true;
                                showMenuDialog(position);
                            }
                        }
                    }
                }
            });

            EventCenter.getInstance().register(this);

            ImageSelectActivity.pickImages(this, MAX_NUM, true, list);
        } else {
            toast("上传信息错误");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (adapter.isUploading() && !isUploadOver){
            toast(R.string.uploading_pelase_wait);
        }
        else {
            finish();
        }
    }

    private void updateUploadButtonStatus(){
        if (adapter.getSelectedSize() > 0){
            btnUpload.setVisibility(View.VISIBLE);
        }
        else {
            btnUpload.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadClick(){
        isUploadOver = false;
        btnUpload.setVisibility(View.INVISIBLE);
        adapter.setUploadStatus(true);
        UploadService.uploadActPhotos(this, actId, adapter.getPhotoList());
    }

    @Subscribe
    public void onEvent(MultiImageSelectEvent event){
        list = event.getPathList();
        adapter.setPhotoList(list);
        updateUploadButtonStatus();
    }

    @Subscribe
    public void onEvent(SingleImageSelectEvent event){
        list.add(event.getPath());
        adapter.setPhotoList(list);
        updateUploadButtonStatus();
    }

    @Subscribe
    public void onEvent(UploadPhotoStatusChangeEvent event){

        switch (event.getCode()){
            case UploadPhotoStatusChangeEvent.CODE_OVER:
                isUploadOver = true;
                if (adapter.getSelectedSize() > 0){
                    toast(R.string.uploading_over_with_fail);
                }
                else {
                    toast(R.string.act_photos_upload_over);
                    finish();
                }
                EventCenter.getInstance().post(new UpdatePhotoListEvent());
                break;
            default:
                adapter.setItemUploadStatus(event);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        UploadService.stopService(this);
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }

    private void showMenuDialog(final int position) {
        MMAlert.showAlert(UploadPhoto.this, "", new String[]{"查看照片", "删除照片"}, null, new MMAlert.OnAlertSelectId() {
            public void onDismissed() {
                isShowDialog = false;
            }

            public void onClick(int whichButton) {
                switch (whichButton) {
                    case 0:
                        isShowDialog = false;
                        Intent intent = new Intent(UploadPhoto.this, PhotoGallery.class);
                        intent.putExtra("LIST", list);
                        intent.putExtra("POSITION", position);
                        startActivity(intent);
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        break;
                    case 1:
                        isShowDialog = false;
                        adapter.del(position);
                        updateUploadButtonStatus();
                        break;
                }
            }
        });
    }
}
