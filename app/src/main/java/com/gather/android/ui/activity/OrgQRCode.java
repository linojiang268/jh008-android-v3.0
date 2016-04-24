package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.SwipeBackActivity;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.OrgDetailEntity;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnOperItemClickL;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.model.ShareContentWebpage;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WechatShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import java.io.File;

import butterknife.InjectView;

/**
 * 社团二维码
 * Created by Administrator on 2015/7/8.
 */
public class OrgQRCode extends SwipeBackActivity {

    @InjectView(R.id.ivQRCode)
    SimpleDraweeView ivQRCode;

    private ImageButton ivShare;
    private SsoHandler mSsoHandler;
    private String mPathTemp = "";
    private OrgDetailEntity entity;
    private String shareTitle = "";
    private String shareContent = "";
    private String shareUrl = "";
    private String shareImageUrl = "";

    private boolean canShare = false;
    private Dialog mShareDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_qrcode);
        Intent intent = getIntent();
        if (intent.hasExtra("MODEL")) {
            entity = (OrgDetailEntity) intent.getSerializableExtra("MODEL");
            shareTitle = entity.getName();
            shareContent = entity.getIntroduction();
            if (Constant.SHOW_LOG) {
                shareUrl = Constant.DEFOULT_TEST_REQUEST_URL + "wap/team/detail?team_id=" + entity.getId();
            } else {
                shareUrl = Constant.DEFOULT_REQUEST_URL + "wap/team/detail?team_id=" + entity.getId();
            }
            ShareBlock.getInstance().initShare(Constant.WE_CHAT_APPID, Constant.SINA_APPID, Constant.TENCENT_APPID, Constant.WE_CHAT_APPID);
            ((TitleBar) toolbar).setHeaderTitle("社团分享");
            ivShare = new ImageButton(this);
            ivShare.setBackgroundResource(R.drawable.titlebar_btn_click_style);
            ivShare.setImageResource(R.drawable.icon_toolbar_share);
            ivShare.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ((TitleBar) toolbar).setCustomizedRightView(ivShare);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) ivShare.getLayoutParams();
            params.width = getResources().getDimensionPixelOffset(R.dimen.titlebar_button_size);
            params.height = params.width;
            ivShare.setLayoutParams(params);
            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Share();
                }
            });
            ((TitleBar) toolbar).getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            setQRCodeImage(Uri.parse(entity.getQr_code_url()));
        } else {
            toast("二维码信息错误");
            finish();
        }
    }

    private void setQRCodeImage(final Uri uri) {
        canShare = false;
        ControllerListener controllerListener = new BaseControllerListener() {

            @Override
            public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                canShare = true;
                shareImageUrl = getFilePath(Uri.parse(entity.getLogo_url()));
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                toast("图片加载失败");
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(uri)
                .build();
        ivQRCode.setController(controller);
    }

    private void Share() {
        if (canShare && !shareImageUrl.equals("")) {
            if (mShareDialog == null){
                mShareDialog = DialogCreater.createShareDialog(this, new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mShareDialog.dismiss();
                        IShareManager iShareManager = null;
                        switch (position) {
                            case 0:
                                iShareManager = new QQShareManager(OrgQRCode.this);
                                if (shareContent.length() > 70) {
                                    shareContent = shareContent.substring(0, 70);
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), QQShareManager.QZONE_SHARE_TYPE);
                                break;
                            case 1:
                                iShareManager = new WechatShareManager(OrgQRCode.this);
                                if (shareContent.length() > 70) {
                                    shareContent = shareContent.substring(0, 70);
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
                                break;
                            case 2:
                                iShareManager = new WechatShareManager(OrgQRCode.this);
                                if (shareContent.length() > 70) {
                                    shareContent = shareContent.substring(0, 70);
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);
                                break;
                            case 3:
                                iShareManager = new WeiboShareManager(OrgQRCode.this);
                                if ((shareContent + shareUrl).length() > 120) {
                                    shareContent = shareContent.substring(0, 120 - shareUrl.length());
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent + shareUrl, shareUrl, shareImageUrl), WeiboShareManager.WEIBO_SHARE_TYPE);
                                break;
                        }
                    }
                });
            }
            mShareDialog.show();
        } else {
            toast("二维码未显示");
        }
    }

    private String getFilePath(Uri loadUri) {
        if (loadUri == null) {
            return "";
        }
        ImageRequest imageRequest = ImageRequest.fromUri(loadUri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest);
        if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
            File file = ((FileBinaryResource) resource).getFile();
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSsoHandler = WeiboLoginManager.getSsoHandler();
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
