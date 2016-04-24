package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.ui.activity.OrgPhotoDetail;
import com.gather.android.entity.OrgPhotoListEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.TimeUtil;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 社团相册列表
 * Created by Administrator on 2015/7/13.
 */
public class OrgPhotoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<OrgPhotoListEntity>> {

    private Context context;
    private List<OrgPhotoListEntity> list = new ArrayList<>();
    private int lastAnimatedPosition;

    public OrgPhotoListAdapter (Context context) {
        super();
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_org_photo_list, parent, false);
        return new PhotoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        runEnterAnimation(holder.itemView, position);
        PhotoListViewHolder viewHolder = (PhotoListViewHolder) holder;

        OrgPhotoListEntity entity = list.get(position);
        viewHolder.tvName.setText(entity.getTitle());
        viewHolder.tvTime.setText(TimeUtil.getYMD(entity.getPublish_time()));

        viewHolder.llItemAll.setOnClickListener(new OnItemClickListenr(entity.getId(), entity.isAdded_activity()));
    }

    private class OnItemClickListenr implements View.OnClickListener {

        private String actId;
        private boolean isadded;

        public OnItemClickListenr(String actId, boolean isadded) {
            this.actId = actId;
            this.isadded = isadded;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, OrgPhotoDetail.class);
            intent.putExtra("ACT_ID", actId);
            intent.putExtra("ADDED", isadded);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<OrgPhotoListEntity> orgPhotoListEntifies, boolean isRefresh) {
        if (isRefresh) {
            lastAnimatedPosition = -1;
            list.clear();
        }
        list.addAll(orgPhotoListEntifies);
        notifyDataSetChanged();
    }

    @Override
    public List<OrgPhotoListEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    static class PhotoListViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llItemAll;
        TextView tvName;
        TextView tvTime;

        public PhotoListViewHolder(View itemView) {
            super(itemView);
            llItemAll = (LinearLayout) itemView.findViewById(R.id.llItemAll);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        }
    }

    /**
     * 每个Item进来的时候都执行动画
     * @param itemView
     * @param position
     */
    public void runEnterAnimation(View itemView, int position){
        if (position > lastAnimatedPosition) { // 只有在下滑的时候才执行动画
            lastAnimatedPosition = position;
            itemView.setTranslationY(PhoneManager.getScreenHeight());
            ObjectAnimator enterAnim = ObjectAnimator.ofFloat(itemView, "translationY", 0);
            enterAnim.setDuration(700);
            enterAnim.setInterpolator(new DecelerateInterpolator(3));
            enterAnim.start();
        }
    }
}
