package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.R;
import com.gather.android.ui.activity.ActDetail;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.entity.ActEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.gather.android.utils.LocationUtils;
import com.gather.android.widget.CustomProgressBar;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/7/7.
 */
public class ActListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<ActEntity>> {

    private List<ActEntity> list = new ArrayList<ActEntity>();
    private Context context;
    private int lastAnimatedPosition = -1; //上一个执行动画的位置

    public ActListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_act_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position > 3) {
//            runEnterAnimation(holder.itemView, position);
//        }
        ViewHolder viewHolder = (ViewHolder) holder;

        ActEntity entity = list.get(position);
        viewHolder.tvTitle.setText(entity.getTitle());

        if (LocationUtils.getInstance().getMyLocation() != null && LocationUtils.getInstance().getMyLocation().getLatitude() != 0) {
            LatLng start = new LatLng(LocationUtils.getInstance().getMyLocation().getLatitude(), LocationUtils.getInstance().getMyLocation().getLongitude());
            LatLng end = entity.getLatLng();
            int distance = (int)DistanceUtil.getDistance(start, end)/1000;
            if (distance == 0) {
                viewHolder.tvAddress.setText(entity.getBrief_address() + "，   在现场");
            } else {
                viewHolder.tvAddress.setText(entity.getBrief_address() + "，  " + (int)DistanceUtil.getDistance(start, end)/1000 + "KM");
            }
        } else {
            viewHolder.tvAddress.setText(entity.getBrief_address());
        }
        if (!Checker.isEmpty(entity.getCover_url())){
            Uri uri = Uri.parse(entity.getCover_url());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolder.ivActimage.getController())
                    .build();
            viewHolder.ivActimage.setController(controller);
        }
        else {
            viewHolder.ivActimage.setImageURI(null);
        }
        //1筹备中，2报名中，3报名结束，4进行中，5已结束
        switch (entity.getSub_status()) {
            case 1:
                viewHolder.tvStatus.setText("即将开始");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_onregister);
                break;
            case 2:
                viewHolder.tvStatus.setText("报名中");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_onregister);
                break;
            case 3:
                viewHolder.tvStatus.setText("即将开始");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_onregister);
                break;
            case 4:
                viewHolder.tvStatus.setText("进行中");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_ongping);
                break;
            case 5:
                viewHolder.tvStatus.setText("已结束");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_end);
                break;
        }
        //活动资费状态：1 免费，2 AA制，3收费
        switch (entity.getEnroll_fee_type()) {
            case 1:
                viewHolder.tvStyle.setText("免费");
                break;
            case 2:
                viewHolder.tvStyle.setText("AA制");
                break;
            case 3:
                viewHolder.tvStyle.setText(entity.getEnroll_fee() + "元");
                break;
            default:
                viewHolder.tvStyle.setText("");
                break;
        }

        viewHolder.itemframe.setOnClickListener(new OnItemClick(position, entity.getId()));
    }


    private class OnItemClick extends OnItemClickListener<String>{
        public OnItemClick(int p, String m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, String id) {
            Intent intent = new Intent(context, ActDetail.class);
            intent.putExtra(ActDetail.EXTRA_ID, id);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<ActEntity> data, boolean isRefresh) {
        if (isRefresh) {
            lastAnimatedPosition = -1;
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<ActEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_frame)
        RelativeLayout itemframe;
        @InjectView(R.id.iv_actimage)
        SimpleDraweeView ivActimage;
        @InjectView(R.id.tv_status)
        TextView tvStatus;
        @InjectView(R.id.iv_rss)
        ImageView ivRss;
        @InjectView(R.id.iv_mygroup)
        ImageView ivMygroup;
        @InjectView(R.id.tv_style)
        TextView tvStyle;
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.tv_address)
        TextView tvAddress;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) ivActimage.getLayoutParams();
            layoutparams.height = context.getResources().getDisplayMetrics().widthPixels * 2 / 3;
            ivActimage.setLayoutParams(layoutparams);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder.setFadeDuration(200)
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setProgressBarImage(new CustomProgressBar())
                    .build();
            ivActimage.setHierarchy(hierarchy);
        }
    }

    /**
     * 每个Item进来的时候都执行动画
     *
     * @param itemView
     * @param position
     */
    public void runEnterAnimation(View itemView, int position) {
        if (position > lastAnimatedPosition) { // 只有在下滑的时候才执行动画
            lastAnimatedPosition = position;
            itemView.setTranslationY(PhoneManager.getScreenHeight());
            ObjectAnimator enterAnim = ObjectAnimator.ofFloat(itemView, "translationY", 0);
            enterAnim.setDuration(800);
            enterAnim.setInterpolator(new DecelerateInterpolator(3));
            enterAnim.start();
        }
    }
}
