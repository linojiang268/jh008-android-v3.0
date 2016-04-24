package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.ActFileEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.utils.Checker;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.jihe.dialog.widget.NormalDialog;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.InjectView;

/**
 * Created by Levi on 2015/8/25.
 */
public class ActFileDownload extends BaseActivity implements Runnable{

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.ivFile)
    SimpleDraweeView ivFile;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.tvProgress)
    TextView tvProgress;
    @InjectView(R.id.btnDownload)
    ImageButton btnDownload;
    @InjectView(R.id.downloadArea)
    RelativeLayout downloadArea;

    private ActFileEntity entity;

    private boolean canceled = false;//取消下载
    private boolean destroy = false;//onDestroy时为true
    private boolean fileIsReady = false;
    private int status = -1;//下载状态0:正在下载；-1:未下载

    private File localFile;//本地文件

    private NormalDialog mStopDialog = null;
    private Dialog mCancelDialog = null;

    private Handler handler = new DownloadHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_file_download);
        entity = (ActFileEntity) getIntent().getSerializableExtra("ENTITY");
        initView();
    }

    @Override
    public void onBackPressed() {
        if (status == 0){
            if (mCancelDialog == null){
                mCancelDialog = DialogCreater.createNormalDialog(this, "温馨提示", "正在下载文件，是否确定要退出？", new OnBtnLeftClickL() {
                    @Override
                    public void onBtnLeftClick() {
                        mCancelDialog.dismiss();
                        finish();
                    }
                });
            }
            mCancelDialog.show();
        }
        else {
            finish();
        }
    }

    private void initView(){
        titlebar.setHeaderTitle(entity.getName());
        ivFile.setImageURI(Uri.parse("res://" + getPackageName() + "/" + entity.getFileIconId()));
        tvName.setText(entity.getName());
        tvProgress.setText(Checker.formatFileSize(this, entity.getSize()));
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 0) {//正在下载时取消下载
                    if (mStopDialog == null){
                        mStopDialog = DialogCreater.createNormalDialog(ActFileDownload.this, "温馨提示", "是否确定要停止下载？", new OnBtnLeftClickL() {
                            @Override
                            public void onBtnLeftClick() {
                                mStopDialog.dismiss();
                                btnDownload.setEnabled(false);
                                cancelDownload();
                            }
                        });
                        mStopDialog.btnText("停止下载", "继续下载");
                    }
                    mStopDialog.show();
                } else {//开始下载
                    btnDownload.setImageResource(R.drawable.btn_file_stop_download);
                    download();
                }
            }
        });

        ivFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileIsReady){
                    openFile();
                }
            }
        });

        new CheckFileProgress().execute();
    }

    private void download(){
        status = 0;
        new Thread(this).start();
    }

    private void cancelDownload(){
        canceled = true;
    }

    private void deleteFileSafly(File file){
        if (file != null && file.exists()){
            if (file.exists()){
                file.delete();
            }
        }
    }

    @Override
    protected void onDestroy() {
        cancelDownload();
        destroy = true;
        super.onDestroy();
    }


    //校验文件
    private class CheckFileProgress extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean exists = false;
            localFile = new File(Constant.DOWNLOAD_IMAGE_DIR_PATH, entity.getName());
            if (localFile.exists()){
                fileIsReady = true;
                exists = true;
            }
            return exists;
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            if (!exists){
                downloadArea.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void run() {
        //创建零时文件，零时文件只有文件名不一样，在成功下载后改为原来的文件名
        File tempFile = new File(Constant.DOWNLOAD_IMAGE_DIR_PATH, System.currentTimeMillis() + "." + entity.getExtension());
        deleteFileSafly(tempFile);
        File dir = tempFile.getParentFile();
        if (!dir.exists()){
            dir.mkdirs();
        }


        boolean success = false;
        long total = 0;
        InputStream is = null;
        OutputStream os = null;
        try {
            //请求文件
            Request request = new Request.Builder().url(entity.getUrl()).build();
            Response response = OkHttpUtil.getOkHttpClient().newCall(request).execute();
            if (!canceled){
                is = response.body().byteStream();
                total = response.body().contentLength();
            }
            if (!canceled){
                os = new FileOutputStream(localFile);
                if (!canceled){
                    // 1K的数据缓冲
                    byte[] bytes = new byte[1024];
                    // 读取到的数据长度
                    int len;
                    //已下载总长度
                    long dowloaded = 0;
                    //上次下载进度
                    int lastProgress = 0;
                    // 开始读取
                    while (!canceled && (len = is.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                        dowloaded += len;
                        //当前下载进度
                        int curProgress = (int) (dowloaded * 100 / total);
                        //为避免下载进度更新过于频繁，限定一定增长时才跟新UI
                        if (curProgress > lastProgress + 5 || curProgress == 100) {
                            lastProgress = curProgress;
                            String progressStr = Checker.formatDownloadProgress(ActFileDownload.this, total, dowloaded);
                            Message message = handler.obtainMessage(1, curProgress, curProgress, progressStr);
                            handler.sendMessage(message);
                        }

                    }
                    if (!canceled){
                        tempFile.renameTo(localFile);
                    }
                    success = true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (os != null){
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                os = null;
            }

            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        deleteFileSafly(tempFile);
        if (!canceled){
            if (!success){//下载失败
                handler.sendEmptyMessage(2);
            }
            else {//下载成功
                handler.sendEmptyMessage(3);
            }
        }
        else if(!destroy) {//取消下载
            handler.sendEmptyMessage(4);
        }

    }

    private class DownloadHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg != null){
                switch (msg.what){
                    case 1://progress
                        String str = (String) msg.obj;
                        tvProgress.setText(str);
                        progressBar.setProgress(msg.arg1);
                        break;
                    case 2://fail
                        progressBar.setProgress(0);
                        status = -1;
                        btnDownload.setImageResource(R.drawable.btn_file_download);
                        Toast.makeText(ActFileDownload.this, R.string.file_download_fail, Toast.LENGTH_LONG).show();
                        break;
                    case 3://success
                        fileIsReady = true;
                        status = -1;
                        downloadArea.setVisibility(View.INVISIBLE);
                        toast("下载成功");
                        break;
                    case 4://cancel
                        tvProgress.setText(Checker.formatFileSize(ActFileDownload.this, entity.getSize()));
                        progressBar.setProgress(0);
                        status = -1;
                        canceled = false;
                        btnDownload.setEnabled(true);
                        btnDownload.setImageResource(R.drawable.btn_file_download);
                        break;
                }
            }
        }
    }

    private void openFile(){
        if (!localFile.exists()){
            fileIsReady = false;
            Toast.makeText(this, "文件已修改或被删除", Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(4);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(localFile);
        switch (entity.getFileIconId()){
            case R.drawable.file_excel:
                intent.setDataAndType(uri, "application/vnd.ms-excel");
                break;
            case R.drawable.file_img:
                intent.setDataAndType(uri, "image/*");
                break;
            case R.drawable.file_pdf:
                intent.setDataAndType(uri, "application/pdf");
                break;
            case R.drawable.file_ppt:
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                break;
            case R.drawable.file_word:
                intent.setDataAndType(uri, "application/msword");
                break;
            default:
                String ex = entity.getExtension().toLowerCase();
                if (ex.equals("apk")){
                    intent.setDataAndType(uri,"application/vnd.android.package-archive");
                }
                else if (ex.equals("txt") || ex.equals("xml")){
                    intent.setDataAndType(uri, "text/plain");

                }
                else if (ex.equals("htm") || ex.equals("html")){
                    intent.setDataAndType(uri, "text/html");
                }
                else if (ex.equals("mp4") || ex.equals("rmvb") || ex.equals("avi") || ex.equals("mkv") || ex.equals("3gp")){
                    intent.setDataAndType(uri, "video/*");
                }
                else if (ex.equals("mp3") || ex.equals("ogg") || ex.equals("mov") || ex.equals("wav") || ex.equals("wma")){
                    intent.setDataAndType(uri, "audio/*");
                }
                else if (ex.equals("zip") || ex.equals("rar")){
                    intent.setDataAndType(uri, "application/x-zip-compressed");
                }
                else {
                    intent.setDataAndType(uri, "*/*");
                }
                break;
        }
        try{
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this, "无打开此类文件的应用", Toast.LENGTH_LONG).show();
        }
    }

}
