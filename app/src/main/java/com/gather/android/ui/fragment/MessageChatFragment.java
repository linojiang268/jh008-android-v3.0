package com.gather.android.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.R;
import com.gather.android.ui.activity.UserCenter;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.entity.MessageEntity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.TabHostLoadViewFactory;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 消息盒子----私信
 * Created by Administrator on 2015/7/28.
 */
public class MessageChatFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<MessageEntity>> listViewHelper;
    private ArrayList<MessageEntity> list = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_message_org);
        ButterKnife.inject(this, getContentView());

        listViewHelper.setLoadViewFractory(new TabHostLoadViewFactory());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);

        listViewHelper = new MVCUltraHelper<List<MessageEntity>>(ptrLayout);
        listViewHelper.setDataSource(new Data());
        listViewHelper.setAdapter(new MessageChatAdapter(getActivity()));
        listViewHelper.refresh();
    }

    /**
     * 获取消息数据
     */
    private class Data extends BaseDataSource<List<MessageEntity>> {

        private int page = 1;
        private int maxPage = 10;

        @Override
        public List<MessageEntity> refresh() throws Exception {
            return load(1);
        }

        @Override
        public List<MessageEntity> loadMore() throws Exception {
            return load(page + 1);
        }

        private List<MessageEntity> load(int page) {
            try {
                Thread.sleep(2000);
                List<MessageEntity> list = new ArrayList<MessageEntity>();
                for (int i = 0; i < 20; i++) {
                    MessageEntity model = new MessageEntity();
                    list.add(model);
                }
                this.page = page;
                return list;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public boolean hasMore() {
            return page < maxPage;
        }
    }

    /**
     * 消息列表Adapter
     */
    private class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<MessageEntity>> {

        private Context context;

        public MessageChatAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_chat, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            MessageEntity entity = list.get(position);

            if (position == 0) {
                viewHolder.viewFirstItem.setVisibility(View.VISIBLE);
            } else {
                viewHolder.viewFirstItem.setVisibility(View.GONE);
            }
            if (position == list.size() - 1) {
                viewHolder.bottomLine.setVisibility(View.VISIBLE);
            } else {
                viewHolder.bottomLine.setVisibility(View.GONE);
            }
            viewHolder.ivUserIcon.setOnClickListener(new OnUserIconClickListener());
        }

        private class OnUserIconClickListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, UserCenter.class);
                intent.putExtra("USER_ID", "2");
                startActivity(intent);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void notifyDataChanged(List<MessageEntity> orgMessageEntities, boolean isRefresh) {
            if (isRefresh) {
                list.clear();
            }
            list.addAll(orgMessageEntities);
            notifyDataSetChanged();
        }

        @Override
        public List<MessageEntity> getData() {
            return list;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View viewFirstItem;
            LinearLayout llItemAll;
            SimpleDraweeView ivUserIcon;
            View viewSex;
            ImageView ivShield;
            TextView tvOrgName;
            TextView tvContent;
            TextView tvTime;
            View bottomLine;

            public ViewHolder(View itemView) {
                super(itemView);

                viewFirstItem = (View) itemView.findViewById(R.id.ViewFistItem);
                llItemAll = (LinearLayout) itemView.findViewById(R.id.llItemAll);
                ivUserIcon = (SimpleDraweeView) itemView.findViewById(R.id.ivUserIcon);
                viewSex = (View) itemView.findViewById(R.id.viewSex);
                ivShield = (ImageView) itemView.findViewById(R.id.ivShield);
                tvOrgName = (TextView) itemView.findViewById(R.id.tvOrgName);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                bottomLine = (View) itemView.findViewById(R.id.bottomLine);
            }
        }
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
        listViewHelper.destory();
    }

}
