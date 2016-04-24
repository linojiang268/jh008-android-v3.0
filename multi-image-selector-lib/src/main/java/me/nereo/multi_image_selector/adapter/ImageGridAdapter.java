package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;

    public ImageGridAdapter(Context context, int itemSize, boolean showCamera){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        this.mItemSize = itemSize;
    }
    /**
     * 显示选择指示器
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        if(showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
//        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size()>0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera){
            return position==0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size()+1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if(showCamera){
            if(i == 0){
                return null;
            }
            return mImages.get(i-1);
        }else{
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if(type == TYPE_CAMERA){
            view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
            FrameLayout flItem = (FrameLayout) view.findViewById(R.id.flItem);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flItem.getLayoutParams();
            params.width = mItemSize;
            params.height = mItemSize;
            flItem.setLayoutParams(params);
            view.setTag(null);
        }else if(type == TYPE_NORMAL){
            ViewHolde holde;
            if(view == null){
                view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                holde = new ViewHolde(view);
            }else{
                holde = (ViewHolde) view.getTag();
                if(holde == null){
                    view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                    holde = new ViewHolde(view);
                }
            }
            if(holde != null) {
                holde.bindData(getItem(i));
            }
        }

//        /** Fixed View Size */
//        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
//        if(lp.height != mItemSize){
//            view.setLayoutParams(mItemLayoutParams);
//        }

//        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
//        AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
//        params.width = (metrics.widthPixels - 2 * mContext.getResources().getDimensionPixelOffset(R.dimen.space_size)) / 3;
//        params.height = params.width;
//        view.setLayoutParams(params);

        return view;
    }

    class ViewHolde {
        SimpleDraweeView image;
        ImageView indicator;
        View mask;
        FrameLayout flItem;

        ViewHolde(View view){
            flItem = (FrameLayout) view.findViewById(R.id.flItem);
            image = (SimpleDraweeView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flItem.getLayoutParams();
            params.width = mItemSize;
            params.height = mItemSize;
            flItem.setLayoutParams(params);
            view.setTag(this);
        }

        void bindData(final Image data){
            if(data == null) return;
            // 处理单选和多选状态
            if(showSelectIndicator){
                indicator.setVisibility(View.VISIBLE);
                if(mSelectedImages.contains(data)){
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                }else{
                    // 未选择
                    indicator.setImageResource(R.drawable.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            }else{
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);

            if(mItemSize > 0) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + imageFile.getAbsolutePath()))
                        .setResizeOptions(new ResizeOptions(200, 200))
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(image.getController())
                        .setImageRequest(request)
                        .build();
                image.setController(controller);
//                image.setImageURI(Uri.parse("file://" + imageFile.getAbsolutePath()));
            }
        }
    }

}

