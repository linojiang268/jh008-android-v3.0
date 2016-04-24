package com.gather.android.http;




/**
 * 带上传进度
 * Created by Christain on 2015/5/15.
 */
public abstract class ResponseProfressHandler extends ResponseHandler implements UploadProgressRequestBody.UploadProgressListener{
    @Override
    public void onUpload(int progress) {
        setProgress(progress);
        sendProgressMessage();
    }

}
