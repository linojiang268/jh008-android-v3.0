package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.ui.activity.ActFileDownload;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.entity.ActFileEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.Checker;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Administrator on 2015/7/7.
 */
public class ActFileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<ActFileEntity>> {

    private List<ActFileEntity> list = new ArrayList<ActFileEntity>();
    private Context context;
    private int lastAnimatedPosition = -1; //上一个执行动画的位置

    public ActFileListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_act_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
//        if (position > 3) {
//            runEnterAnimation(viewholder.itemView, position);
//        }
        ViewHolder holder = (ViewHolder) viewholder;
        ActFileEntity entity = list.get(position);

        holder.ivFile.setImageResource(entity.getFileIconId());
        holder.tvFileName.setText(entity.getName());
        holder.tvFileSize.setText(Checker.formatFileSize(context, entity.getSize()));
        if (entity.isFirst()) {
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            holder.tvTime.setText(entity.getDateKey());
        } else {
            holder.line.setVisibility(View.GONE);
            holder.tvTime.setVisibility(View.GONE);
        }
//        holder.tvTime.setText(entity.getCreated_at());

        holder.itemFrame.setOnClickListener(new OnItemClick(position, entity));

    }


    private class OnItemClick extends OnItemClickListener<ActFileEntity> {
        public OnItemClick(int p, ActFileEntity m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, ActFileEntity entity) {
            Intent intent = new Intent(context, ActFileDownload.class);
            intent.putExtra("ENTITY", entity);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<ActFileEntity> data, boolean isRefresh) {
        if (isRefresh) {
            lastAnimatedPosition = -1;
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<ActFileEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_act_file.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tvTime)
        TextView tvTime;
        @InjectView(R.id.ivFile)
        ImageView ivFile;
        @InjectView(R.id.tvFileName)
        TextView tvFileName;
        @InjectView(R.id.tvFileSize)
        TextView tvFileSize;
        @InjectView(R.id.item_frame)
        RelativeLayout itemFrame;
        @InjectView(R.id.line)
        View line;

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
            enterAnim.setDuration(800);
            enterAnim.setInterpolator(new DecelerateInterpolator(3));
            enterAnim.start();
        }
    }

}
