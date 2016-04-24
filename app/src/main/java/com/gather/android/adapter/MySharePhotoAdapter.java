package com.gather.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.R;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.entity.OrgPhotoEntity;
import com.gather.android.manager.PhoneManager;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/7/13.
 */
public class MySharePhotoAdapter extends BaseAdapter implements IDataAdapter<List<OrgPhotoEntity>> {

    private ArrayList<OrgPhotoEntity> list = new ArrayList<OrgPhotoEntity>();
    private Context context;
    private OnItemtClickListener listener;
    private int imageSize = 300;
    public MySharePhotoAdapter(Context context) {
        super();
        this.context = context;
    }

    public ArrayList<OrgPhotoEntity> getList() {
        return  list;
    }

    public void setList(ArrayList<OrgPhotoEntity> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public OrgPhotoEntity getItem(int position) {
        return list.get(position);
    }

    public void deletePhoto(int position){
        if (position >= 0 && position < list.size()){
            list.remove(position);
            notifyDataSetChanged();
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
            convertView = mInflater.inflate(R.layout.item_my_share_photo, null);
            holder = new ViewHolder(convertView);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.ivPhoto.getLayoutParams();
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            imageSize = (metrics.widthPixels - PhoneManager.dip2px(4 * 12)) / 3;
            params.width = imageSize;
            params.height = params.width;
            holder.ivPhoto.setLayoutParams(params);

            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) holder.btnDelete.getLayoutParams();
            params2.width = imageSize / 3;
            params2.height = params2.width;
            holder.btnDelete.setLayoutParams(params2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrgPhotoEntity model = getItem(position);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.getImage_url()))
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivPhoto.getController())
                .setImageRequest(request)
                .build();
        holder.ivPhoto.setController(controller);

        holder.btnDelete.setOnClickListener(new OnDeleteClickListener(position, model));
        holder.ivPhoto.setOnClickListener(new OnClickListener(position, model));
        return convertView;
    }

    private class OnClickListener extends OnItemClickListener<OrgPhotoEntity>{

        public OnClickListener(int p, OrgPhotoEntity m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, OrgPhotoEntity entity) {
            if (listener != null){
                listener.onItemClick(position);
            }
        }
    }

    private class OnDeleteClickListener extends OnItemClickListener<OrgPhotoEntity>{

        public OnDeleteClickListener(int p, OrgPhotoEntity m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, OrgPhotoEntity entity) {
            if (listener != null){
                listener.onDeleteClick(position, entity);
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
        @InjectView(R.id.btnDelete)
        ImageView btnDelete;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public interface OnItemtClickListener {
        public void onItemClick(int position);
        public void onDeleteClick(int position, OrgPhotoEntity entity);
    }

    public void setItemClickListener(OnItemtClickListener l) {
        listener = l;
    }
}
