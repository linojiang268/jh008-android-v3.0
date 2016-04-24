package com.gather.android.http;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.orhanobut.logger.Logger;

/**
 * 请求回调基类
 * Created by Levi on 2015/9/23.
 */
public abstract class HandlerCallBack implements com.squareup.okhttp.Callback {
    private static class CallBackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null && msg.obj instanceof HandlerCallBack) {
                HandlerCallBack handlerCallBack = (HandlerCallBack) msg.obj;
                if (handlerCallBack != null) {
                    switch (msg.what) {
                        case 111:
                            handlerCallBack.handleResult();
                            break;
                        case 222:
                            handlerCallBack.handleProgress();
                            break;
                    }

                } else {
                    Logger.e("handler is null");
                }
            }
        }
    }
    private static Handler handler = new CallBackHandler();
    //----------------------------------------------------------------------------------------------


    protected String data;
    protected Intent broadcastIntent;
    protected int errorCode;
    protected boolean isSuccess = false;

    public void setData(String data){
        this.isSuccess = true;
        this.data = data;
    }

    public void setErrorMsg(int code, String msg){
        this.isSuccess = false;
        this.data = msg;
        this.errorCode = code;
    }

    public void setBroadcast(Intent intent, String msg){
        this.isSuccess = false;
        this.data = msg;
        this.broadcastIntent = intent;
    }

    public void setProgress(int progress){
        errorCode = progress;
    }

    private void handleResult() {
        if (isSuccess){
            if (Constant.SHOW_LOG){
                Logger.i("Request success: " + data);
            }
            success(data);
        }
        else {
            if (broadcastIntent != null){
                if (Constant.SHOW_LOG){
                    Logger.i("Request result to send broadcast: " + data);
                }
                GatherApplication.getInstance().sendBroadcast(broadcastIntent);
            }
            else {
                if (Constant.SHOW_LOG){
                    Logger.i("Request fail: " + errorCode + "\n" + data);
                }
                fail(errorCode, data);
            }
        }
    }

    private void handleProgress() {
        if (Constant.SHOW_LOG){
            Logger.i("upload progress: " + errorCode + "%");
        }
        upload(errorCode);
    }

    public void sendReulstMessage(){
        Message message = new Message();
        message.what = 111;
        message.obj = this;
        handler.sendMessage(message);
    }

    public void sendProgressMessage(){
        Message message = new Message();
        message.what = 222;
        message.obj = this;
        handler.sendMessage(message);
    }

    public abstract void success(String data);

    public abstract void fail(int errorCode, String msg);

    public abstract void upload(int progress);
}
