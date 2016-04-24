package com.gather.android.colonel.adpter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.colonel.inter.OnItemSelectStateChangedListener;
import com.gather.android.colonel.inter.OnShowDetailListener;
import com.gather.android.entity.SignUpEntity;
import com.gather.android.entity.SignUpListEntity;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 待审核列表adapter
 * Created by Levi on 2015/9/29.
 */
public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<SignUpListEntity> {

    private List<SignUpEntity> list = new ArrayList<SignUpEntity>();
    private Context context;

    private Stack<String> mSelectedStack = new Stack<>();
    private OnItemSelectStateChangedListener listener1;
    private OnShowDetailListener listener2;

    private String tips = "已选择 0 人";

    public PendingListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setOnItemSelectStateChangedListener(OnItemSelectStateChangedListener l){
        listener1 = l;
    }

    public void setOnShowDetailListener(OnShowDetailListener l){
        listener2 = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType != 0){
            view = LayoutInflater.from(context).inflate(R.layout.colonel_signup_item_pending, parent, false);
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
            holder.ivSelect.setOnClickListener(new OnItemClick(position, entity.getId()));
            holder.btnDetail.setOnClickListener(new OnDetailClick(position, entity));
            holder.tvCallNumber.setOnClickListener(new OnCallClick(position, entity.getMobile()));

            if (mSelectedStack.contains(entity.getId())){
                holder.ivSelect.setImageResource(R.drawable.colonel_icon_selected);
            }
            else {
                holder.ivSelect.setImageResource(R.drawable.colonel_icon_unselected);
            }
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

    //点击详情
    private class OnDetailClick extends OnItemClickListener<SignUpEntity> {

        public OnDetailClick(int p, SignUpEntity e) {
            super(p, e);
        }

        @Override
        public void onItemClick(int position, SignUpEntity entity) {
            if (listener2 != null){
                listener2.showDetail(entity);
            }
        }
    }

    //选择
    private class OnItemClick extends OnItemClickListener<String> {

        public OnItemClick(int p, String m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, String id) {
            boolean isEmpty = mSelectedStack.isEmpty();
            if (mSelectedStack.contains(id)){
                mSelectedStack.remove(id);
            }
            else {
                mSelectedStack.add(id);
            }
            tips = "已选择 " + mSelectedStack.size() + "人";
            notifyDataSetChanged();
            if (listener1 != null){
                if (isEmpty){
                    if (!mSelectedStack.isEmpty()){
                        listener1.onItemSelectStateChanged(false);
                    }
                }
                else {
                    if (mSelectedStack.isEmpty()){
                        listener1.onItemSelectStateChanged(true);
                    }
                }
            }
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


    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivSelect)
        ImageView ivSelect;
        @InjectView(R.id.tvNickname)
        TextView tvNickname;
        @InjectView(R.id.tvCallNumber)
        TextView tvCallNumber;
        @InjectView(R.id.btnDetail)
        ImageView btnDetail;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
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

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : list.size() + 1;
    }

    public int getListSize(){
        return list.size();
    }

    @Override
    public void notifyDataChanged(SignUpListEntity data, boolean isRefresh) {
        if (isRefresh) {
            list.clear();
        }
        list.addAll(data.getApplicants());
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

    public List<String> getSelectedList(){
        Iterator<String> iterator = mSelectedStack.iterator();
        List<String> list = new ArrayList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    public void removeSlectedFromListAndView(){
        tips = "已选择 0 人";
        mSelectedStack.clear();
        notifyDataSetChanged();
    }
}
