package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.GatherApplication;
import com.gather.android.R;


/**
 * Created by Levi on 2015/8/27.
 */
public class ToastDialog extends Dialog {
    public static final int TOAST_LONG = 5;
    public static final int TOAST_SHORT = 3;

    private static ToastDialog instance = null;

    public static synchronized void showToast(Context context, String msg, int imgRes) {
        showToast(context, msg, imgRes, TOAST_SHORT, false);
    }

    public static synchronized void showToast(Context context, String msg, int imgRes, boolean cancelByUser) {
        showToast(context, msg, imgRes, TOAST_SHORT, cancelByUser);
    }

    public static synchronized void showToast(Context context, String msg, int imgRes, int duration) {
        showToast(context, msg, imgRes, duration, false);
    }

    public static synchronized void showToast(Context context,String msg){
        showToast(context, msg, -1, TOAST_SHORT, false);
    }

    public static synchronized void showToast(Context context, String msg, int imgRes, int duration, boolean cancelByUser) {
        if (instance == null) {
            instance = new ToastDialog(context);
        }
        instance.setDuration(duration);
        instance.setContent(msg, imgRes);
        instance.setCacnelByUser(cancelByUser);
        instance.show();
    }

    private TextView tvMessage;
    private ImageView ivImage;
    private LinearLayout dialogFrame;
    private boolean cancelByUser = false;
    private boolean isNormalToastStyle = false;
    private String message;
    private int imgRes;
    private int duration = TOAST_SHORT;
    private Handler handler = new ToastHandler();

    private ToastDialog(Context context) {
        super(context, R.style.LoadingDialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_toast);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(false);

        dialogFrame = (LinearLayout) findViewById(R.id.dialogFrame);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        ivImage = (ImageView) findViewById(R.id.ivImage);
    }

    @Override
    public void show() {
        super.show();
        updateDialogView();
        delayToDismiss();
    }

    @Override
    public void dismiss() {
        if (cancelByUser){
            dismissToast();
        }
    }

    public void dismissToast() {
        cancelDelayDismiss();
        super.dismiss();
        instance = null;
    }

    public void setDuration(int dur){
       if (dur > 0){
           duration = dur;
       }
       else {
           duration = TOAST_SHORT;
       }
    }

    public void setCacnelByUser(boolean enable){
        setCanceledOnTouchOutside(enable);
        cancelByUser = enable;
    }

    public void setContent(String msg, int imgRes){
        this.message = msg;
        this.imgRes = imgRes;
        this.isNormalToastStyle = imgRes == -1;
        if (isNormalToastStyle){
            this.imgRes = R.drawable.ic_toastdialog_normal;
        }
        if (isShowing()){
            updateDialogView();
            cancelDelayDismiss();
            delayToDismiss();
        }
    }

    private void updateDialogView(){
        tvMessage.setText(message);
        ivImage.setImageResource(imgRes);
        if (isNormalToastStyle){
            tvMessage.setTextColor(Color.WHITE);
            dialogFrame.setBackgroundResource(R.drawable.toast_dialog_black_bg);
        }
        else {
            tvMessage.setTextColor(Color.BLACK);
            dialogFrame.setBackgroundResource(R.drawable.toast_dialog_white_bg);
        }
    }

    private void delayToDismiss(){
        handler.sendEmptyMessageDelayed(0, duration * 1000L);
    }

    private void cancelDelayDismiss(){
        handler.removeMessages(0);
    }

    private class  ToastHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                dismissToast();
            }
        }
    }



}
