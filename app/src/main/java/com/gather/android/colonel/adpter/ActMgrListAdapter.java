package com.gather.android.colonel.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.adapter.inter.OnItemClickListener;
import com.gather.android.colonel.ui.activity.AlbumMgr;
import com.gather.android.colonel.ui.activity.SendMessage;
import com.gather.android.colonel.ui.activity.SignInMgr;
import com.gather.android.colonel.ui.activity.SignUpMgr;
import com.gather.android.entity.ActMgrEntity;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Levi on 2015/9/29.
 */
public class ActMgrListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<ActMgrEntity>> {
    private List<ActMgrEntity> list = new ArrayList<ActMgrEntity>();
    private Context context;

    private int itemMenuVisible = -1;//-1是menu未展开，其他数字代表展开的item位置

    public ActMgrListAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.colonel_item_act_mgr, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        ViewHolder holder = (ViewHolder) h;
        ActMgrEntity entity = list.get(position);
        holder.tvActName.setText(entity.getTitle());
        holder.tvActDate.setText(entity.getBegin_time());
        switch (entity.getSub_status()){
            case 1:
            case 3:
                holder.tvActStatus.setText("即将开始");
                holder.tvActStatus.setBackgroundColor(context.getResources().getColor(R.color.green));
                break;
            case 2:
                holder.tvActStatus.setText("报名中");
                holder.tvActStatus.setBackgroundColor(context.getResources().getColor(R.color.blue));
                break;
            case 4:
                holder.tvActStatus.setText("进行中");
                holder.tvActStatus.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case 5:
                holder.tvActStatus.setText("已结束");
                holder.tvActStatus.setBackgroundColor(context.getResources().getColor(R.color.gray_dark));
                break;
        }

        holder.itemFrame.setOnClickListener(new OnItemClick(position, ""));
        holder.btnMessage.setOnClickListener(new OnMessageClick(position, entity.getId()));
        holder.btnSignUp.setOnClickListener(new OnSignUpClick(position, entity));
        holder.btnSignIn.setOnClickListener(new OnSignInClick(position, entity.getId()));

        if (itemMenuVisible == position){
            holder.menu.setVisibility(View.VISIBLE);
        }
        else {
            holder.menu.setVisibility(View.GONE);
        }
    }

    //点击报名
    private class OnSignUpClick extends OnItemClickListener<ActMgrEntity> {

        public OnSignUpClick(int p, ActMgrEntity e) {
            super(p, e);
        }

        @Override
        public void onItemClick(int position, ActMgrEntity en) {
            Intent intent = new Intent(context, SignUpMgr.class);
            //1审核要缴费；2审核不缴费；3不审核不缴费；4不审核要缴费
            int viewType;
            if (en.getEnroll_fee_type() == 3){
                //收费
                if (en.getAuditing() == 1){
                    viewType = 1;
                }
                else {
                    viewType = 4;
                }
            }
            else {
                //免费
                if (en.getAuditing() == 1){
                    viewType = 2;
                }
                else {
                    viewType = 3;
                }
            }
            intent.putExtra("TYPE", viewType);
            intent.putExtra("ACTID", en.getId());
            context.startActivity(intent);
        }
    }

    //点击签到
    private class OnSignInClick extends OnItemClickListener<String> {

        public OnSignInClick(int p, String m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, String s) {
            Intent intent = new Intent(context, SignInMgr.class);
            intent.putExtra("ACTID", s);
            context.startActivity(intent);
        }
    }

    //点击通知
    private class OnMessageClick extends OnItemClickListener<String> {

        public OnMessageClick(int p, String m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, String en) {

            Intent intent = new Intent(context, SendMessage.class);
            intent.putExtra("ACTID", en);
            context.startActivity(intent);
        }
    }

    //展开或关闭menu
    private class OnItemClick extends OnItemClickListener<String> {

        public OnItemClick(int p, String m) {
            super(p, m);
        }

        @Override
        public void onItemClick(int position, String s) {
            if (itemMenuVisible == position){
                itemMenuVisible = -1;
            }
            else {
                itemMenuVisible = position;
            }
            notifyDataSetChanged();
        }
    }

    public void closeMenu(){
        if (itemMenuVisible != -1){
            itemMenuVisible = -1;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.menu)
        LinearLayout menu;
        @InjectView(R.id.tvActStatus)
        TextView tvActStatus;
        @InjectView(R.id.tvActName)
        TextView tvActName;
        @InjectView(R.id.tvActDate)
        TextView tvActDate;
        @InjectView(R.id.btnSignUp)
        LinearLayout btnSignUp;
        @InjectView(R.id.btnSignIn)
        LinearLayout btnSignIn;
        @InjectView(R.id.btnMessage)
        LinearLayout btnMessage;
        @InjectView(R.id.itemFrame)
        LinearLayout itemFrame;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void notifyDataChanged(List<ActMgrEntity> data, boolean isRefresh) {
        if (isRefresh) {
            list.clear();
        }
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<ActMgrEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

}
