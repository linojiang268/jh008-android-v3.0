package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.OrgNewsEntity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.SingleImageSelectEvent;
import com.gather.android.http.PersistentCookieStore;
import com.gather.android.manager.ClickUtil;
import com.gather.android.utils.BitmapUtils;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnOperItemClickL;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.model.ShareContentWebpage;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WechatShareManager;
import com.liulishuo.share.weibo.WeiboShareManager;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.net.CookieStore;
import java.net.HttpCookie;

import butterknife.InjectView;
import de.greenrobot.event.Subscribe;

/**
 * 网页
 * Created by Administrator on 2015/8/3.
 */
public class WebActivity extends BaseActivity {
    private static final  int FILECHOOSER_RESULTCODE = 11;


    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.pbWebView)
    ProgressBar pbWebView;
    @InjectView(R.id.webView)
    WebView webView;

    private ImageButton ivShare;
    private String url;

    private String shareTitle = "";
    private String shareContent = "";
    private String shareUrl = "";
    private String shareImageUrl = "";

    private boolean isOrgNews = false;
    private OrgNewsEntity entity;
    private String orgName, orgLogo;
    private String mTitle;

    private ValueCallback mUploadMessage;
    private boolean isOpenFileChooser = false;
    private Dialog mShareDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        EventCenter.getInstance().register(this);

        Intent intent = getIntent();
        if (intent.hasExtra("URL")) {
            if (intent.hasExtra("TITLE")) {
                String title = intent.getExtras().getString("TITLE", "");
                setTitle(title);
            }
            url = intent.getExtras().getString("URL", "");
            if (intent.hasExtra("NEWS")) {
                isOrgNews = true;
                entity = (OrgNewsEntity) intent.getSerializableExtra("NEWS");
                orgName = intent.getExtras().getString("ORG_NAME", "");
                orgLogo = intent.getExtras().getString("ORG_LOGO", "");
                shareTitle = orgName;
                shareContent = entity.getTitle();
                if (Constant.SHOW_LOG) {
                    shareUrl = Constant.DEFOULT_TEST_REQUEST_URL + "wap/news/detail?news_id=" + entity.getId();
                } else {
                    shareUrl = Constant.DEFOULT_REQUEST_URL + "wap/news/detail?news_id=" + entity.getId();
                }
                shareImageUrl = getFilePath(Uri.parse(orgLogo));
                ShareBlock.getInstance().initShare(Constant.WE_CHAT_APPID, Constant.SINA_APPID, Constant.TENCENT_APPID, Constant.WE_CHAT_APPID);
                ivShare = new ImageButton(this);
                ivShare.setBackgroundResource(R.drawable.titlebar_btn_click_style);
                ivShare.setImageResource(R.drawable.icon_toolbar_share);
                ivShare.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                titlebar.setCustomizedRightView(ivShare);
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
            }

            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            pbWebView.setVisibility(View.VISIBLE);
            pbWebView.setMax(100);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        pbWebView.setProgress(100);
                        pbWebView.setVisibility(View.GONE);
                    } else {
                        pbWebView.setProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    setTitle(title);
                }

                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    if (mUploadMessage != null) return false;
                    mUploadMessage = filePathCallback;
                    selectImage(false);
                    return true;
                }

                // For Android 3.0+
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                    if (mUploadMessage != null) return;
                    mUploadMessage = uploadMsg;
                    selectImage(true);
                }

                // For Android < 3.0
                public void openFileChooser(ValueCallback<Uri> uploadMsg) {openFileChooser( uploadMsg, "" );}

                // For Android  > 4.1.1
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    openFileChooser(uploadMsg, acceptType);
                }


            });
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    pbWebView.setProgress(2);
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });

            synCookies(url);
            webView.loadUrl(url);
        } else {
            toast("网页信息错误");
            finish();
        }
    }

    private void selectImage(boolean isOpen){
        isOpenFileChooser = isOpen;
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
        ImageSelectActivity.pickImage(this, true);
    }

    private void setTitle(String title){
        if (TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(title)) {
            mTitle = title;
            titlebar.setHeaderTitle(mTitle);
        }
    }

    /**
     * 同步一下cookie
     */
    public void synCookies(String url) {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
        CookieStore store = new PersistentCookieStore(this);

        for (HttpCookie cookie : store.getCookies()){
            String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
            CookieManager.getInstance().setCookie(url, cookieString);
        }
        CookieSyncManager.getInstance().sync();
    }

    private void Share() {
        if (isOrgNews && !shareImageUrl.equals("")) {
            if (mShareDialog == null){
                mShareDialog = DialogCreater.createShareDialog(this, new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mShareDialog.dismiss();
                        IShareManager iShareManager = null;
                        switch (position) {
                            case 0:
                                iShareManager = new QQShareManager(WebActivity.this);
                                if (shareContent.length() > 70) {
                                    shareContent = shareContent.substring(0, 70);
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), QQShareManager.QZONE_SHARE_TYPE);
                                break;
                            case 1:
                                iShareManager = new WechatShareManager(WebActivity.this);
                                if (shareContent.length() > 70) {
                                    shareContent = shareContent.substring(0, 70);
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
                                break;
                            case 2:
                                iShareManager = new WechatShareManager(WebActivity.this);
                                if (shareContent.length() > 70) {
                                    shareContent = shareContent.substring(0, 70);
                                }
                                iShareManager.share(new ShareContentWebpage(shareTitle, shareContent, shareUrl, shareImageUrl), WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);
                                break;
                            case 3:
                                iShareManager = new WeiboShareManager(WebActivity.this);
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
            toast("信息缺失，分享失败");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ClickUtil.isFastClick()) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == FILECHOOSER_RESULTCODE){
//            if (mUploadMessage != null){
//                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
//                if (result != null){
//                    if (isOpenFileChooser){
//                        mUploadMessage.onReceiveValue(result);
//                    }
//                    else {
//                        Uri[] uris = new Uri[1];
//                        uris[0] = result;
//                        mUploadMessage.onReceiveValue(uris);
//                    }
//                }
//                else {
//                    mUploadMessage.onReceiveValue(null);
//                }
//                mUploadMessage = null;
//            }
//        }
        if (requestCode == ImageSelectActivity.REQUEST_CODE_SLELECT_IMAGE && resultCode != RESULT_OK){
            if (mUploadMessage != null){
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
        }
    }

    @Subscribe
    public void onEvent(SingleImageSelectEvent event){
        new ThumbnailProgress().execute(event.getPath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }

    private class ThumbnailProgress extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String orgFilePath = params[0];
            if (!TextUtils.isEmpty(orgFilePath)){
                File orgFile = new File(orgFilePath);
                if (orgFile.exists()) {
                    if (orgFile.length() > 1024100) {
                        Bitmap bitmap = BitmapUtils.createImageThumbnail(orgFile.getAbsolutePath(), 1280 * 1280);
                        if (bitmap != null){
                            File file = new File(Constant.TEMP_FILE_DIR_PATH, System.currentTimeMillis() + ".jpg");
                            File dir = file.getParentFile();
                            if (!dir.exists()){
                                dir.mkdirs();
                            }
                            if (BitmapUtils.compressBitmap(bitmap, 90, file.getAbsolutePath())){
                                return file.getAbsolutePath();
                            }
                        }
                    } else {
                        return orgFilePath;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (mUploadMessage == null){
                return;
            }
            if (!TextUtils.isEmpty(s)){
                Logger.d(new File(s).length() + "");
                try {
                    Uri result = Uri.fromFile(new File(s));
                    if (isOpenFileChooser){
                        mUploadMessage.onReceiveValue(result);
                    }
                    else {
                        Uri[] uris = new Uri[1];
                        uris[0] = result;
                        mUploadMessage.onReceiveValue(uris);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    if (mUploadMessage != null){
                        mUploadMessage.onReceiveValue(null);
                    }
                }
            }
            else {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = null;
        }
    }
}
