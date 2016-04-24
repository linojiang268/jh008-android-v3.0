package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.ui.activity.ActDetail;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.entity.MyActEntity;
import com.gather.android.manager.PhoneManager;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/7/7.
 */
public class MyActListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<MyActEntity>> {
    private List<MyActEntity> list = new ArrayList<MyActEntity>();
    private Context context;
    private int lastAnimatedPosition = -1; //上一个执行动画的位置

    public MyActListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_act, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > 5) {
            runEnterAnimation(holder.itemView, position);
        }
        ViewHolder viewHolder = (ViewHolder) holder;
        MyActEntity entity = list.get(position);

        if (entity.isFirst()) {
            viewHolder.header.setVisibility(View.VISIBLE);
            viewHolder.tvDate.setText(entity.getDateKey());
        } else {
            viewHolder.header.setVisibility(View.GONE);
        }
        if (entity.isToday()){
            viewHolder.tvDate.setTextColor(Color.RED);
            viewHolder.tvDate.setText("今天");
        }
        else {
            viewHolder.tvDate.setTextColor(Color.parseColor("#999999"));
        }
        viewHolder.tvTime.setText(entity.getStartTime());
        viewHolder.tvAddr.setText(entity.getBrief_address());
        viewHolder.tvTitle.setText(entity.getTitle());
        viewHolder.tvJionerNum.setText(String.format(context.getString(R.string.jioner_count_format), entity.getEnrolled_num()));

//        if (entity.getSub_status() == 5) {
//            //已结束
//            viewHolder.tvActStatus.setText("已结束");
//            viewHolder.tvActStatus.setTextColor(0xFF666666);
//            viewHolder.tvActStatus.setBackgroundResource(R.drawable.gray_stroke_style);
//        } else {
//            if (entity.getSub_status() == 4 && entity.getApplicants_status() == 3) {
//                //进行中
//                viewHolder.tvActStatus.setText("进行中");
//                viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.blue));
//                viewHolder.tvActStatus.setBackgroundResource(R.drawable.blue_stroke_style);
//            } else {
//                if (entity.getApplicants_status() == 1 || entity.getApplicants_status() == 2) {
//                    //待确认
//                    viewHolder.tvActStatus.setText("待确认");
//                    viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.green));
//                    viewHolder.tvActStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                } else {
//                    //即将开始
//                    viewHolder.tvActStatus.setText("即将开始");
//                    viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.red));
//                    viewHolder.tvActStatus.setBackgroundResource(R.drawable.red_stroke_style);
//                }
//            }
//        }

//        if (entity.getApplicant_status() == 3) {
//            //报名成功
//            switch (entity.getSub_status()) {
//                case 1://筹备中/即将报名
//                    viewHolder.tvActStatus.setText("");
//                    viewHolder.tvActStatus.setBackgroundColor(0x00000000);
//                    break;
//                case 2://报名中
//                case 3://报名结束
//                    viewHolder.tvActStatus.setText("报名成功");
//                    viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.red));
//                    viewHolder.tvActStatus.setBackgroundResource(R.drawable.red_stroke_style);
//                    break;
//                case 4://进行中
//                    viewHolder.tvActStatus.setText("进行中");
//                    viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.blue));
//                    viewHolder.tvActStatus.setBackgroundResource(R.drawable.blue_stroke_style);
//                    break;
//                case 5://已结束
//                    viewHolder.tvActStatus.setText("已结束");
//                    viewHolder.tvActStatus.setTextColor(0xFF666666);
//                    viewHolder.tvActStatus.setBackgroundResource(R.drawable.gray_stroke_style);
//                    break;
//                default:
//                    viewHolder.tvActStatus.setText("");
//                    viewHolder.tvActStatus.setBackgroundColor(0x00000000);
//                    break;
//            }
//        } else {
//            //报名没有成功
//            switch (entity.getSub_status()) {
//                case 1://筹备中
//                    viewHolder.tvActStatus.setText("");
//                    viewHolder.tvActStatus.setBackgroundColor(0x00000000);
//                    break;
//                case 2://报名中
//                    if (entity.getApplicant_status() == 1) {
//                        //审核中
//                        viewHolder.tvActStatus.setText("等待审核");
//                        viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.green));
//                        viewHolder.tvActStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                    } else if (entity.getApplicant_status() == 2 || entity.getApplicant_status() == 4) {
//                        //立即付款
//                        viewHolder.tvActStatus.setText("未付款");
//                        viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.green));
//                        viewHolder.tvActStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                    } else {
//                        viewHolder.tvActStatus.setText("");
//                        viewHolder.tvActStatus.setBackgroundColor(0x00000000);
//                    }
//                    break;
//                case 3://报名结束
//                    if (entity.getApplicant_status() == 2) {
//                        //立即付款
//                        viewHolder.tvActStatus.setText("未付款");
//                        viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.green));
//                        viewHolder.tvActStatus.setBackgroundResource(R.drawable.green_stroke_style);
//                    } else {
//                        viewHolder.tvActStatus.setText("");
//                        viewHolder.tvActStatus.setBackgroundColor(0x00000000);
//                    }
//                    break;
//                case 4://进行中
//                case 5://已结束
//                default:
//                    viewHolder.tvActStatus.setText("");
//                    viewHolder.tvActStatus.setBackgroundColor(0x00000000);
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
                viewHolder.tvActStatus.setText("等待审核");
                viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.green));
                viewHolder.tvActStatus.setBackgroundResource(R.drawable.green_stroke_style);
                break;
            case 1:
                viewHolder.tvActStatus.setText("未付款");
                viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.green));
                viewHolder.tvActStatus.setBackgroundResource(R.drawable.green_stroke_style);
                break;
            case 2:
                viewHolder.tvActStatus.setText("报名成功");
                viewHolder.tvActStatus.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.tvActStatus.setBackgroundResource(R.drawable.red_stroke_style);
                break;
            case 3:
                viewHolder.tvActStatus.setText("已结束");
                viewHolder.tvActStatus.setTextColor(0xFF666666);
                viewHolder.tvActStatus.setBackgroundResource(R.drawable.gray_stroke_style);
                break;
        }

        viewHolder.itemFrame.setOnClickListener(new OnItemClick(position, entity));
    }

    private class OnItemClick extends OnItemClickListener<MyActEntity> {

        public OnItemClick(int p, MyActEntity m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, MyActEntity entity) {
            Intent intent = new Intent(context, ActDetail.class);
            intent.putExtra(ActDetail.EXTRA_ID, entity.getId());
            context.startActivity(intent);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<MyActEntity> data, boolean isRefresh) {
        if (isRefresh) {
            lastAnimatedPosition = -1;
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<MyActEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.header)
        LinearLayout header;
        @InjectView(R.id.tvTime)
        TextView tvTime;
        @InjectView(R.id.tvActStatus)
        TextView tvActStatus;
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.tvAddr)
        TextView tvAddr;
        @InjectView(R.id.tvJionerNum)
        TextView tvJionerNum;
        @InjectView(R.id.itemFrame)
        LinearLayout itemFrame;
        @InjectView(R.id.tvDate)
        TextView tvDate;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
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
            enterAnim.setDuration(500);
            enterAnim.setInterpolator(new DecelerateInterpolator(3));
            enterAnim.start();
        }
    }
}
