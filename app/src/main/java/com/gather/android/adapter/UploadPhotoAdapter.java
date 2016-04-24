package com.gather.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.R;
import com.gather.android.event.UploadPhotoStatusChangeEvent;
import com.gather.android.manager.PhoneManager;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/7/14.
 */
public class UploadPhotoAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private DisplayMetrics metrics;
    private ArrayList<String> list;
    private int MAX_NUM;
    private boolean isUpload = false;
    private HashMap<String, Integer> uploadStatus = new HashMap<>();
    private WeakReference<ProgressBar> progressBarWeakReference;

    public UploadPhotoAdapter(Context context, int maxNum) {
        this.context = context;
        this.MAX_NUM = maxNum;
        this.metrics = context.getResources().getDisplayMetrics();
        this.list = new ArrayList<String>();
    }

    public boolean isUploading(){
        return  isUpload;
    }

    public void setUploadStatus(boolean isUpload){
        this.isUpload = isUpload;
        notifyDataSetChanged();
    }

    public void setItemUploadStatus(UploadPhotoStatusChangeEvent event){
        String path = event.getPath();
        int status = event.getCode();
        switch (status){
            case UploadPhotoStatusChangeEvent.CODE_SUCCESS:
                progressBarWeakReference = null;
                int position = -1;
                for (int i = 0; i< list.size(); i++){
                    if (path.equals(list.get(i))){
                        position = i;
                        break;
                    }
                }
                if (position != -1){
                    list.remove(position);
                    notifyDataSetChanged();
                }
                break;
            case UploadPhotoStatusChangeEvent.CODE_UPLOADING:
                if (progressBarWeakReference == null){
                    Logger.d(" progressBarWeakReference is null");
                }
                else if (progressBarWeakReference.get() == null){
                    Logger.d(" progressBarWeakReference.get() is null");
                }
                if (progressBarWeakReference != null && progressBarWeakReference.get() != null){
                    Logger.d(event.getProgress() + "");
                    progressBarWeakReference.get().setProgress(event.getProgress());
                }
                else {
                    uploadStatus.put(path, status);
                }
                break;
            default:
                uploadStatus.put(path, status);
                notifyDataSetChanged();
                break;
        }
    }

    public void resetItemUploadStatus(String path){
        uploadStatus.remove(path);
        notifyDataSetChanged();
    }

    public int getItemUploadStatus(String path){
        if (uploadStatus.containsKey(path)){
            return uploadStatus.get(path);//uploading or upload fail
        }
        else {
            return -1;//waiting
        }
    }

    public int getSelectedSize(){
        return list.size();
    }

    public boolean isFull() {
        return list.size() == MAX_NUM;
    }

    public void setPhotoList(ArrayList<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<String> getPhotoList() {
        return list;
    }

    public void add(String model) {
        this.list.add(model);
        notifyDataSetChanged();
    }

    public void add(List<String> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void del(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (isFull() || isUpload) {
            return list.size();
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_upload_photo, null);
            holder = new ViewHolder(convertView);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.itemFrame.getLayoutParams();
            params.width = (int) ((metrics.widthPixels - PhoneManager.dip2px(4 * 12)) / 3);
            params.height = params.width;
            holder.itemFrame.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == getCount() - 1 && !isUpload && !isFull()) {
            holder.rlAdd.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.tvUploadStatus.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            String path = getItem(position);

            holder.rlAdd.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            if (isUpload){
                holder.tvUploadStatus.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);
                int status = getItemUploadStatus(path);
                switch (status){
                    case UploadPhotoStatusChangeEvent.CODE_UPLOAD_START:
                        progressBarWeakReference = new WeakReference<ProgressBar>(holder.progressBar);
                        holder.tvUploadStatus.setText(R.string.uploading);
                        holder.progressBar.setProgress(0);
                        break;
                    case UploadPhotoStatusChangeEvent.CODE_UPLOADING:
                        holder.tvUploadStatus.setText(R.string.uploading);
                        if (progressBarWeakReference == null || progressBarWeakReference.get() == null){
                            progressBarWeakReference = new WeakReference<ProgressBar>(holder.progressBar);
                        }
                        break;
                    case UploadPhotoStatusChangeEvent.CODE_FAIL:
                        holder.tvUploadStatus.setText(R.string.upload_fail);
                        holder.progressBar.setProgress(0);
                        break;
                    default:
                        holder.tvUploadStatus.setText(R.string.wait_for_upload);
                        holder.progressBar.setProgress(0);
                        break;
                }
            }
            else {
                holder.progressBar.setVisibility(View.GONE);
                holder.tvUploadStatus.setVisibility(View.GONE);
            }
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + path))
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.imageView.getController())
                    .setImageRequest(request)
                    .build();
            holder.imageView.setController(controller);
        }
        return convertView;
    }

    private static class ViewHolder {
        public SimpleDraweeView imageView;
        public RelativeLayout rlAdd;
        public ProgressBar progressBar;
        public FrameLayout itemFrame;
        public TextView tvUploadStatus;

        public ViewHolder(View convertView) {
            super();
            tvUploadStatus = (TextView) convertView.findViewById(R.id.tvUploadStatus);
            imageView = (SimpleDraweeView) convertView.findViewById(R.id.ivPhoto);
            rlAdd = (RelativeLayout) convertView.findViewById(R.id.rlAdd);
            progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            itemFrame = (FrameLayout) convertView.findViewById(R.id.itemframe);
        }
    }
}
