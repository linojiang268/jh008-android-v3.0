package com.gather.android.event;

/**
 * Created by Levi on 2015/8/5.
 */
public class UploadPhotoStatusChangeEvent {
    public static final int CODE_UPLOAD_START = 50;
    public static final int CODE_UPLOADING = 10;
    public static final int CODE_SUCCESS = 20;
    public static final int CODE_FAIL = 30;
    public static final int CODE_OVER = 40;

    private int code;
    private String path;
    private int progress = 0;
    public UploadPhotoStatusChangeEvent(String path){
        this.path = path;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
