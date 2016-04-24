package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.R;
import com.gather.android.ui.activity.OrgHome;
import com.gather.android.entity.OrgDetailEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HomeOrgListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<OrgDetailEntity> list = new ArrayList<OrgDetailEntity>();
    private int width, height;

    public HomeOrgListAdapter(Context context, int w, int h) {
        super();
        this.context = context;
        this.width = w;
        this.height = h;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_org_list, parent, false);



        FrameLayout itemFrame = (FrameLayout) view.findViewById(R.id.itemFrame);
        LinearLayout.LayoutParams frameParams = (LinearLayout.LayoutParams) itemFrame.getLayoutParams();
        frameParams.width = width;
        frameParams.height = height;
        itemFrame.setLayoutParams(frameParams);

        SimpleDraweeView icon = (SimpleDraweeView) view.findViewById(R.id.ivOrgIcon);
        FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) icon.getLayoutParams();
        iconParams.width = height;
        iconParams.height = height;
        icon.setLayoutParams(iconParams);
        view.findViewById(R.id.ivCircle).setLayoutParams(iconParams);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        OrgDetailEntity model = list.get(position);
        viewHolder.ivOrgIcon.setImageURI(Uri.parse(model.getLogo_url()));
        viewHolder.ivOrgIcon.setOnClickListener(new OnItemClickListener(model));
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivOrgIcon)
        SimpleDraweeView ivOrgIcon;
        @InjectView(R.id.itemFrame)
        FrameLayout itemFrame;
        @InjectView(R.id.ivCircle)
        ImageView ivCircle;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public void setMyOrgList(List<OrgDetailEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
