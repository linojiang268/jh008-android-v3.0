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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.R;
import com.gather.android.ui.activity.OrgHome;
import com.gather.android.entity.OrgDetailEntity;
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
public class OrgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<OrgDetailEntity>> {

    private List<OrgDetailEntity> list = new ArrayList<OrgDetailEntity>();
    private Context context;
    private int lastAnimatedPosition = -1; //上一个执行动画的位置

    public OrgListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_org_list, parent, false);
        return new OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position > 5) {
//            runEnterAnimation(holder.itemView, position);
//        }
        OrgViewHolder viewHolder = (OrgViewHolder) holder;

        OrgDetailEntity model = list.get(position);
        viewHolder.tvOrgName.setText(model.getName());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.getLogo_url()))
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(viewHolder.ivOrgIcon.getController())
                .setImageRequest(request)
                .build();
        viewHolder.ivOrgIcon.setController(controller);
//        viewHolder.ivOrgIcon.setImageURI(Uri.parse(model.getLogo_url()));
        viewHolder.tvOrgActNum.setText("新活动:" + model.getActivity_num());
        if (model.isJoined()) {
            viewHolder.tvOrgMark.setVisibility(View.VISIBLE);
            viewHolder.tvOrgMark.setText("已加入");
        } else {
            if (model.isRequested()) {
                viewHolder.tvOrgMark.setVisibility(View.VISIBLE);
                viewHolder.tvOrgMark.setText("已申请");
            } else {
                viewHolder.tvOrgMark.setVisibility(View.GONE);
            }
        }

        viewHolder.llItemAll.setOnClickListener(new OnItemClickListener(model));
    }

    private class OnItemClickListener implements View.OnClickListener {

        private OrgDetailEntity model;

        public OnItemClickListener(OrgDetailEntity model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, OrgHome.class);
            intent.putExtra("MODEL", model);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<OrgDetailEntity> data, boolean isRefresh) {
        if (isRefresh) {
            lastAnimatedPosition = -1;
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<OrgDetailEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    class OrgViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivOrgIcon)
        SimpleDraweeView ivOrgIcon;
        @InjectView(R.id.tvOrgName)
        TextView tvOrgName;
        @InjectView(R.id.tvOrgActNum)
        TextView tvOrgActNum;
        @InjectView(R.id.tvOrgMark)
        TextView tvOrgMark;
        @InjectView(R.id.llItemAll)
        LinearLayout llItemAll;

        public OrgViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
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
            enterAnim.setDuration(500);
            enterAnim.setInterpolator(new DecelerateInterpolator(3));
            enterAnim.start();
        }
    }
}
