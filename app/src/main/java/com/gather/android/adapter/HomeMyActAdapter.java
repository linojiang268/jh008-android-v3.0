package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.gather.android.utils.Checker;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.CustomProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeMyActAdapter  extends BaseAdapter {

    private Context context;
    private List<ActEntity> list = new ArrayList<ActEntity>();

    public HomeMyActAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActEntity getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_home_my_act, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ActEntity entity = list.get(position);
        viewHolder.tvTitle.setText(entity.getTitle());


        viewHolder.tvAddrTime.setText(TimeUtil.pointStyleDate(entity.getBegin_time()) + " | " + entity.getAddress());
        if (!Checker.isEmpty(entity.getCover_url())){
            Uri uri = Uri.parse(entity.getCover_url());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolder.ivImage.getController())
                    .build();
            viewHolder.ivImage.setController(controller);
        }
        else {
            viewHolder.ivImage.setImageURI(null);
        }
//        if (entity.getApplicant_status() == 3) {
//            //报名成功
//            switch (entity.getSub_status()) {
//                case 1://筹备中
//                    viewHolder.tvStatus.setText("");
//                    viewHolder.tvStatus.setBackgroundColor(0x00000000);
//                    break;
//                case 2://报名中
//                case 3://报名结束
//                    viewHolder.tvStatus.setText("即将开始");
//                    viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_onregister);
//                    break;
//                case 4://进行中
//                    viewHolder.tvStatus.setText("进行中");
//                    viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_onregister);
//                    break;
//                case 5://已结束
//                    viewHolder.tvStatus.setText("已结束");
//                    viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_onregister);
//                    break;
//                default:
//                    viewHolder.tvStatus.setText("");
//                    viewHolder.tvStatus.setBackgroundColor(0x00000000);
//                    break;
//            }
//        } else {
//            //报名没有成功
//            switch (entity.getSub_status()) {
//                case 1://筹备中
//                    viewHolder.tvStatus.setText("");
//                    viewHolder.tvStatus.setBackgroundColor(0x00000000);
//                    break;
//                case 2://报名中
//                    if (entity.getApplicant_status() == 1) {
//                        //审核中
//                        viewHolder.tvStatus.setText("等待审核");
//                        viewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
//                        viewHolder.tvStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                    } else if (entity.getApplicant_status() == 2 || entity.getApplicant_status() == 4) {
//                        //立即付款
//                        viewHolder.tvStatus.setText("未付款");
//                        viewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
//                        viewHolder.tvStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                    } else {
//                        viewHolder.tvStatus.setText("");
//                        viewHolder.tvStatus.setBackgroundColor(0x00000000);
//                    }
//                    break;
//                case 3://报名结束
//                    if (entity.getApplicant_status() == 2) {
//                        //立即付款
//                        viewHolder.tvStatus.setText("未付款");
//                        viewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
//                        viewHolder.tvStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                    } else {
//                        viewHolder.tvStatus.setText("");
//                        viewHolder.tvStatus.setBackgroundColor(0x00000000);
//                    }
//                    break;
//                case 4://进行中
//                case 5://已结束
//                default:
//                    viewHolder.tvStatus.setText("");
//                    viewHolder.tvStatus.setBackgroundColor(0x00000000);
//                    break;
//            }
//        }

        int status = -1;//0等待审核；1未付款；2报名成功；3已结束
        if (entity.getApplicant_status() == 3) {//报名成功
            if (entity.getSub_status() == 5){//已结束
                status = 3;
            }
            else {
                status = 2;
            }
        } else {//报名还没有成功
            switch (entity.getSub_status()) {
                case 2://报名中
                    if (entity.getApplicant_status() == 1) {
                        status = 0;
                    } else if (entity.getApplicant_status() == 2 || entity.getApplicant_status() == 4) {
                        status = 1;
                    }
                    break;
                case 3://报名结束
                    if (entity.getApplicant_status() == 2) {
                        status = 1;
                    }
                    break;
            }
        }
        //0等待审核；1未付款；2报名成功；3已结束
        switch (status){
            case 0:
                viewHolder.tvStatus.setText("待审核");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_ongping);
                break;
            case 1:
                viewHolder.tvStatus.setText("未支付");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_ongping);
                break;
            case 2:
                viewHolder.tvStatus.setText("报名成功");//蓝色
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_enroll_success);
                break;
            case 3:
                viewHolder.tvStatus.setText("已结束");
                viewHolder.tvStatus.setBackgroundResource(R.drawable.act_status_end);
                break;
        }

        viewHolder.itemAll.setOnClickListener(new OnItemClick(position, entity.getId()));
        return view;
    }

    private class OnItemClick extends OnItemClickListener<String> {
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

    class ViewHolder {
        @InjectView(R.id.itemAll)
        RelativeLayout itemAll;
        @InjectView(R.id.ivImage)
        SimpleDraweeView ivImage;
        @InjectView(R.id.tvStatus)
        TextView tvStatus;
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.tvAddrTime)
        TextView tvAddrTime;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) ivImage.getLayoutParams();
            layoutparams.height = context.getResources().getDisplayMetrics().widthPixels * 2 / 3;
            ivImage.setLayoutParams(layoutparams);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder.setFadeDuration(200)
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setProgressBarImage(new CustomProgressBar())
                    .build();
            ivImage.setHierarchy(hierarchy);
        }
    }

    public void setMyActList(List<ActEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
