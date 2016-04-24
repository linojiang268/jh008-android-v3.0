package com.gather.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.event.EventCenter;
import com.gather.android.event.UploadPhotoStatusChangeEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.Pair;
import com.gather.android.http.ResponseHandler;
import com.gather.android.http.ResponseProfressHandler;
import com.gather.android.utils.BitmapUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件上传service,目前只做活动照片上传,虽然是异步上传，但在页面表现上还是做的同步
 * Created by Levi on 2015/8/5.
 */
public class UploadService extends Service {
    private static final String ACTION = "com.gather.android.service.UploadService";

    private static final String TAG = "UploadService";

    private static final String EXTRA_ACT_PHOTO_LIST = "extra_ap_list";
    private static final String EXTRA_ACT_ID = "extra_actid";
    private static final String EXTRA_TYPE = "extra_type";
    private static final int TYPE_UPLOAD_ACT_PHOTO = 111;

    public static void stopService(Context context){
        Intent intent = new Intent(context, UploadService.class);
        context.stopService(intent);
    }

    /**
     * 上传活动图片
     * @param uploadFilePathList
     */
    public static void uploadActPhotos(Context context, String actId, ArrayList<String> uploadFilePathList){
        if (context != null && uploadFilePathList != null && uploadFilePathList.size() > 0) {
            if (Constant.SHOW_LOG) {
                Logger.i("UploadService uploadActPhotos: " + uploadFilePathList.size());
            }
            Intent intent = new Intent(context, UploadService.class);
            intent.putExtra(EXTRA_TYPE, TYPE_UPLOAD_ACT_PHOTO);
            intent.putExtra(EXTRA_ACT_ID, actId);
            intent.putStringArrayListExtra(EXTRA_ACT_PHOTO_LIST, uploadFilePathList);
            context.startService(intent);
        }
    }

    /**
     * 活动图片伤处啊失败时，重新上传
     * @param context
     * @param uploadFilePath
     * @return
     */
    public static void reUploadActPhoto(Context context, String uploadFilePath){
        ArrayList<String> list = new ArrayList<>();
        list.add(uploadFilePath);
        uploadActPhotos(context,"0", list);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isUploading = false;
    private String actId = "-1";
    private List<String> uploadList = new ArrayList<>( );
    private Handler handler = new Handler();
    private String currentUploadOrgFilePath;//当前上传的原图地址
    private File currentUploadFile;//当前上传的文件
    private int maxImageSize = -1;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        synchronized (uploadList){
            uploadList.clear();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    public void onStart(Intent intent, int startId) {
//        doCommand(intent);
//        super.onStart(intent, startId);
//    }

    private void doCommand(Intent intent){
        if (intent != null && intent.hasExtra(EXTRA_TYPE)){
            int action = intent.getIntExtra(EXTRA_TYPE, 0);
            switch (action){
                case TYPE_UPLOAD_ACT_PHOTO:
                    String id = intent.getStringExtra(EXTRA_ACT_ID);
                    if (!"0".equals(id)){
                        actId = id;
                    }
                    ArrayList<String> filePathList = intent.getStringArrayListExtra(EXTRA_ACT_PHOTO_LIST);
                    if (filePathList != null && filePathList.size() > 0) {
                        synchronized (uploadList) {
                            uploadList.addAll(filePathList);
                        }
                        if (!isUploading){
                            checkAndUpload();
                        }
                    }
                    break;
            }
        }
    }

    private void checkAndUpload(){
        synchronized (uploadList){
            if (!uploadList.isEmpty()){
                isUploading = true;
                currentUploadOrgFilePath = uploadList.remove(0);

//                if (Constant.SHOW_LOG) {
//                    Logger.i("upload " + currentUploadOrgFilePath + "  \n " + uploadList.size());
//                }

                postStatusChange(UploadPhotoStatusChangeEvent.CODE_UPLOAD_START);
                currentUploadFile = handleOrgImageFile();
                if (currentUploadFile != null){
                    BaseParams params = new BaseParams(API.UPLOAD_ACT_PHOTO);
                    params.put("activity", actId);
                    params.put("image", new Pair<String, File>("image/jpg", currentUploadFile));
                    OkHttpUtil.post(params, new ResponseProfressHandler() {
                        @Override
                        public void success(String msg) {
                            try {
                                if (currentUploadFile != null && currentUploadFile.exists()){
                                    currentUploadFile.delete();
                                }
                            }
                            catch (Exception e){
                            }
                            postStatusChange(UploadPhotoStatusChangeEvent.CODE_SUCCESS);
                            deleayCheckAndUpload();
                        }

                        @Override
                        public void fail(int code, String error) {
                            postStatusChange(UploadPhotoStatusChangeEvent.CODE_FAIL);
                            deleayCheckAndUpload();
                        }

                        @Override
                        public void upload(int progress) {
                            postProgress(progress);
                        }
                    });
                }
                else {
                    postStatusChange(UploadPhotoStatusChangeEvent.CODE_FAIL);
                    deleayCheckAndUpload();
                }
            }
            else {
                isUploading = false;
                postStatusChange(UploadPhotoStatusChangeEvent.CODE_OVER);
            }
        }
    }

    /**
     * 处理原图
     */
    private File handleOrgImageFile(){
        if (maxImageSize == -1){
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            maxImageSize = metrics.heightPixels * metrics.heightPixels;
        }
        float scale = 1f;
        Bitmap bitmap = null;
        while (bitmap == null){
            try {
                bitmap = BitmapUtils.createImageThumbnail(currentUploadOrgFilePath, maxImageSize);
            }
            catch (Exception e){
                e.printStackTrace();
                scale -= 0.1f;
                maxImageSize = (int) (maxImageSize * scale);
                bitmap = null;
                if (scale <= 0f){
                    break;
                }
            }
        }
        if (bitmap != null) {
            long size = BitmapUtils.getBitmapsize(bitmap);
            //大于1M，再次缩放
            int quality = size > 1048576L ? 90 : 100;
            File file = new File(Constant.UPLOAD_FILES_DIR_PATH, System.currentTimeMillis() + ".jpg");
            boolean success = BitmapUtils.compressBitmap(bitmap, quality, file.getAbsolutePath());
            BitmapUtils.recycleBitmap(bitmap);
            if (success){
                return file;
            }
        }
        return null;
    }


    private void postStatusChange(int code){
        UploadPhotoStatusChangeEvent event = new UploadPhotoStatusChangeEvent(currentUploadOrgFilePath);
        event.setCode(code);
        EventCenter.getInstance().post(event);
    }

    private void postProgress(int progress){
        UploadPhotoStatusChangeEvent event = new UploadPhotoStatusChangeEvent(currentUploadOrgFilePath);
        event.setCode(UploadPhotoStatusChangeEvent.CODE_UPLOADING);
        event.setProgress(progress);
        EventCenter.getInstance().post(event);
    }

    private void deleayCheckAndUpload(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               checkAndUpload();
            }
        }, 500);
    }

}
