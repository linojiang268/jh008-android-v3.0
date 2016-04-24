package com.gather.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.R;
import com.gather.android.entity.OrgPhotoEntity;
import com.gather.android.manager.PhoneManager;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 社团活动相册详情
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoDetailAdapter extends BaseAdapter implements IDataAdapter<List<OrgPhotoEntity>> {

    private ArrayList<OrgPhotoEntity> list = new ArrayList<OrgPhotoEntity>();
    private Context context;
    private OnPhotoClickListener listener;
    private int imageSize = 300;
    private boolean isUserPhotoList = false;

    public OrgPhotoDetailAdapter(Context context, boolean isUserPhotoList) {
        super();
        this.context = context;
        this.isUserPhotoList = isUserPhotoList;
    }

    public ArrayList<OrgPhotoEntity> getList() {
        return  list;
    }

    public void setList(ArrayList<OrgPhotoEntity> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        int count = list.size();
        if (isUserPhotoList){
            return count == 0 ? 0 : count + 1;
        }
        else {
            return count;
        }
    }

    @Override
    public OrgPhotoEntity getItem(int position) {
        if (isUserPhotoList){
            if (position > 0) {
                return list.get(position - 1);
            }
            else {
                return null;
            }
        }
        else {
            return list.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_org_photo_detail, null);
            holder = new ViewHolder(convertView);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.itemFrame.getLayoutParams();
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            imageSize = (metrics.widthPixels - PhoneManager.dip2px(4 * 12)) / 3;
            params.width = imageSize;
            params.height = params.width;
            holder.itemFrame.setLayoutParams(params);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrgPhotoEntity model = getItem(position);

        if (isUserPhotoList && position == 0){
            holder.areaMyShare.setVisibility(View.VISIBLE);
            holder.ivPhoto.setVisibility(View.INVISIBLE);
            holder.areaMyShare.setOnClickListener(new OnImageClickListener(position));
        }
        else {
            holder.areaMyShare.setVisibility(View.GONE);
            holder.ivPhoto.setVisibility(View.VISIBLE);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.getImage_url()))
                    .setResizeOptions(new ResizeOptions(imageSize, imageSize))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.ivPhoto.getController())
                    .setImageRequest(request)
                    .build();
            holder.ivPhoto.setController(controller);
            holder.ivPhoto.setOnClickListener(new OnImageClickListener(position));
        }
        return convertView;
    }

    private class OnImageClickListener implements View.OnClickListener {

        private int position;

        public OnImageClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.OnPhotoClick(position);
            }
        }
    }

    @Override
    public void notifyDataChanged(List<OrgPhotoEntity> strings, boolean isRefresh) {
        if (isRefresh) {
            list.clear();
        }
        list.addAll(strings);
        notifyDataSetChanged();
    }

    @Override
    public List<OrgPhotoEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    static class ViewHolder {
        @InjectView(R.id.ivPhoto)
        SimpleDraweeView ivPhoto;
        @InjectView(R.id.itemFrame)
        FrameLayout itemFrame;
        @InjectView(R.id.areaMyShare)
        LinearLayout areaMyShare;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public interface OnPhotoClickListener {
        public void OnPhotoClick(int position);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.listener = listener;
    }
}
