package com.gather.android.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActPhotoListEntity;
import com.gather.android.entity.OrgPhotoEntity;
import com.gather.android.event.ActPhotoGalleryBackEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.ClickUtil;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.MultiTouchViewPager;

import java.io.File;
import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * 活动图片浏览大图
 * Created by Administrator on 2015/7/24.
 */
public class ActPhotoGallery extends BaseActivity {

    @InjectView(R.id.ivSave)
    ImageView ivSave;
    @InjectView(R.id.viewPager)
    MultiTouchViewPager viewPager;

    private int position;
    private ArrayList<OrgPhotoEntity> list;
    private int type;
    private int actId;
    private int page;
    private int maxPage;
    private MediaScannerConnection msc = null;

    private DraweePagerAdapter adapter;
    private boolean isRequest = false;
    private boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photo_gallery);
        Intent intent = getIntent();
        if (intent.hasExtra("LIST") && intent.hasExtra("POSITION") && intent.hasExtra("TYPE") && intent.hasExtra("ACT_ID") && intent.hasExtra("PAGE") && intent.hasExtra("MAX_PAGE")) {
            position = intent.getExtras().getInt("POSITION");
            list = (ArrayList<OrgPhotoEntity>) intent.getSerializableExtra("LIST");
            type = intent.getExtras().getInt("TYPE");
            actId = intent.getExtras().getInt("ACT_ID");
            page = intent.getExtras().getInt("PAGE");
            maxPage = intent.getExtras().getInt("MAX_PAGE");

            adapter = new DraweePagerAdapter();
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(position);
            viewPager.setOnPageChangeListener(new OnViewPageChangeListener());
        } else {
            toast("页面信息错误");
            finish();
        }
    }

    @OnClick(R.id.ivSave)
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.ivSave:
                if (!ClickUtil.isFastClick()) {
                    File file = getFile(Uri.parse(list.get(position).getImage_url()));
                    if (file != null) {
                        saveFileToPhonePhoto(file);
                    } else {
                        toast("图片保存失败");
                    }
                }

                break;
        }
    }

    private class OnViewPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            ActPhotoGallery.this.position = position;
            if (page < maxPage && !isRequest && (position == list.size() - 1)) {
                getMorePhoto();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 获取更多照片
     */
    private void getMorePhoto() {
        isRequest = true;
        BaseParams params;
        if (type == 10){//我的相册
            params = new BaseParams(API.MY_SHARE_PHOTO);
        }
        else {
            params = new BaseParams(API.ACT_ALBUM);
            params.put("creator_type", type);
        }
        params.put("activity", actId);
        params.put("page", page + 1);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                ActPhotoListEntity entity = JSON.parseObject(msg, ActPhotoListEntity.class);
                if (entity != null) {
                    page = page + 1;
                    list.addAll(list.size(), entity.getImages());
                    adapter.notifyDataSetChanged();
                    isChange = true;
                }
                isRequest = false;
            }

            @Override
            public void fail(int code, String error) {
                toast("获取照片失败");
                isRequest = false;
            }
        });
    }

    public class DraweePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(list.get(position).getImage_url()));
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());
            photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    onBackPressed();
                }
            });
            photoDraweeView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    onBackPressed();
                }
            });

            try {
                viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return photoDraweeView;
        }
    }

    private File getFile(Uri loadUri) {
        if (loadUri == null) {
            return null;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(loadUri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
        if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
            File file = ((FileBinaryResource) resource).getFile();
            File picFile = new File(PhoneManager.getSdCardRootPath() + "/Pictures/Gather_" + System.currentTimeMillis() + ".jpg");
            PhoneManager.copyFile(file, picFile);
            return picFile;
        } else {
            return null;
        }
    }

    private void saveFileToPhonePhoto(final File file) {
        try {
            msc = new MediaScannerConnection(ActPhotoGallery.this, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    msc.scanFile(file.getAbsolutePath(), "image/jpeg");
                    toast("下载成功");
                }

                @Override
                public void onScanCompleted(String s, Uri uri) {
                    msc.disconnect();
                }
            });
            msc.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isChange) {
            EventCenter.getInstance().post(new ActPhotoGalleryBackEvent(position, list, type, actId, page, maxPage));
        }
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}
