package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.ui.activity.WebActivity;
import com.gather.android.entity.OrgNewsEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.TimeUtil;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 社团往期回顾（资讯）Adapter
 * Created by Administrator on 2015/7/29.
 */
public class OrgNewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<OrgNewsEntity>> {

    private List<OrgNewsEntity> list = new ArrayList<OrgNewsEntity>();
    private Context context;
    private int lastAnimatedPosition = -1; //上一个执行动画的位置
    private String orgName, orgLogo;

    public OrgNewsListAdapter(Context context, String orgName, String orgLogo) {
        super();
        this.context = context;
        this.orgName = orgName;
        this.orgLogo = orgLogo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_org_news, parent, false);
        return new OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position > 5) {
//            runEnterAnimation(holder.itemView, position);
//        }
        OrgViewHolder viewHolder = (OrgViewHolder) holder;

        OrgNewsEntity model = list.get(position);
        viewHolder.tvTitle.setText(model.getTitle());
        viewHolder.ivPic.setImageURI(Uri.parse(model.getCover_url()));
        viewHolder.tvTime.setText(TimeUtil.getYMD(model.getPublish_time()));

        viewHolder.llItemAll.setOnClickListener(new OnItemClickListener(model));
    }

    private class OnItemClickListener implements View.OnClickListener {

        private OrgNewsEntity model;

        public OnItemClickListener(OrgNewsEntity model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("TITLE", "资讯详情");
            if (Constant.SHOW_LOG) {
                intent.putExtra("URL", Constant.DEFOULT_TEST_REQUEST_URL + "wap/news/detail?news_id=" + model.getId());
            } else {
                intent.putExtra("URL", Constant.DEFOULT_REQUEST_URL + "wap/news/detail?news_id=" + model.getId());
            }
            intent.putExtra("NEWS", model);
            intent.putExtra("ORG_NAME", orgName);
            intent.putExtra("ORG_LOGO", orgLogo);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<OrgNewsEntity> data, boolean isRefresh) {
        if (isRefresh) {
            lastAnimatedPosition = -1;
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<OrgNewsEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    class OrgViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivPic)
        SimpleDraweeView ivPic;
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.tvTime)
        TextView tvTime;
        @InjectView(R.id.llItemAll)
        LinearLayout llItemAll;

        public OrgViewHolder(View view) {
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
