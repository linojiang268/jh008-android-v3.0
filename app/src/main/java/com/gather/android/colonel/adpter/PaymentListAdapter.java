package com.gather.android.colonel.adpter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.entity.SignUpEntity;
import com.gather.android.colonel.inter.OnShowDetailListener;
import com.gather.android.entity.SignUpListEntity;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 待审核列表adapter
 * Created by Levi on 2015/9/29.
 */
public class PaymentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<SignUpListEntity> {

    private List<SignUpEntity> list = new ArrayList<SignUpEntity>();
    private Context context;
    private String tips;

    private OnShowDetailListener listener;

    public PaymentListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setOnShowDetailListener(OnShowDetailListener l){
        listener = l;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType != 0){
            view = LayoutInflater.from(context).inflate(R.layout.colonel_signup_item_payment, parent, false);
            return new ViewHolder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.colonel_text_header, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        if (position == 0){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) h;
            headerViewHolder.tvHeaderTips.setText(tips);
        }
        else {
            ViewHolder holder = (ViewHolder) h;
            SignUpEntity entity = list.get(position - 1);

            holder.tvCallNumber.setText(entity.getMobile());
            holder.tvNickname.setText(entity.getName());
            holder.itemFrame.setOnClickListener(new OnItemClick(position, entity));
            holder.btnCall.setOnClickListener(new OnCallClick(position, entity.getMobile()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        else {
            return 1;
        }
    }



    //点击电话
    private class OnCallClick extends OnItemClickListener<String> {

        public OnCallClick(int p, String s) {
            super(p, s);
        }

        @Override
        public void onItemClick(int position, String s) {
            Uri uri = Uri.parse("tel:" + s);
            Intent it = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(it);
        }
    }

    //选择
    private class OnItemClick extends OnItemClickListener<SignUpEntity> {

        public OnItemClick(int p, SignUpEntity en) {
            super(p, en);
        }

        @Override
        public void onItemClick(int position, SignUpEntity en) {
            if (listener != null){
                listener.showDetail(en);
            }
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.btnCall)
        ImageView btnCall;
        @InjectView(R.id.tvNickname)
        TextView tvNickname;
        @InjectView(R.id.tvCallNumber)
        TextView tvCallNumber;
        @InjectView(R.id.itemFrame)
        LinearLayout itemFrame;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tvHeaderTips)
        TextView tvHeaderTips;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : list.size() + 1;
    }

    @Override
    public void notifyDataChanged(SignUpListEntity data, boolean isRefresh) {
        if (isRefresh) {
            list.clear();
        }
        list.addAll(data.getApplicants());
        tips = "共计 " + data.getTotal() + "人";
        notifyDataSetChanged();
    }

    @Override
    public SignUpListEntity getData() {
        return null;
    }

    public List<SignUpEntity> getDataList(){
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
