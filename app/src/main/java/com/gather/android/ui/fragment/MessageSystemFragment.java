package com.gather.android.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.R;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseFragment;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.TimePref;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.MessageEntity;
import com.gather.android.entity.MessageListEntity;
import com.gather.android.event.CheckMsgReadedEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.event.MessageNotifyEvent;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.manager.PhoneManager;
import com.gather.android.ui.activity.ActDetail;
import com.gather.android.ui.activity.OrgHome;
import com.gather.android.ui.activity.WebActivity;
import com.gather.android.utils.MVCUltraHelper;
import com.gather.android.utils.NormalLoadViewFactory;
import com.gather.android.utils.TimeUtil;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.shizhefei.HttpResponseStatus;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.Subscribe;
import gather.database.bean.SystemMsg;
import gather.database.dao.SystemMsgDao;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 消息盒子----系统
 * Created by Administrator on 2015/7/28.
 */
public class MessageSystemFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<SystemMsg>> listViewHelper;
    private ArrayList<SystemMsg> list = new ArrayList<>();
    private Dialog mDelDialog = null;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_message_org);
        ButterKnife.inject(this, getContentView());

        EventCenter.getInstance().register(this);
        listViewHelper.setLoadViewFractory(new NormalLoadViewFactory());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        final MaterialHeader header = new MaterialHeader(getApplicationContext());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PhoneManager.dip2px(15), 0, PhoneManager.dip2px(10));
        header.setPtrFrameLayout(ptrLayout);
        ptrLayout.setLoadingMinTime(800);
        ptrLayout.setDurationToCloseHeader(800);
        ptrLayout.setHeaderView(header);
        ptrLayout.addPtrUIHandler(header);

        listViewHelper = new MVCUltraHelper<List<SystemMsg>>(ptrLayout);
        listViewHelper.setDataSource(new Data());
        listViewHelper.setAdapter(new MessageOrgAdapter(getActivity()));
        listViewHelper.refresh();
    }

    @Subscribe
    public void onEvent(MessageNotifyEvent event) {
        if (listViewHelper != null) {
            listViewHelper.refresh();
        }
    }

    /**
     * 获取消息数据
     */
    private class Data extends BaseDataSource<List<SystemMsg>> {

        @Override
        public List<SystemMsg> refresh() throws Exception {
            return load();
        }

        @Override
        public List<SystemMsg> loadMore() throws Exception {
            return null;
        }

        private List<SystemMsg> load() {
            BaseParams params = new BaseParams(API.GET_MESSAGE_LIST);
            params.put("type", 1);
            String lastTime = TimePref.getInstance().getSystemMsgTime();
            if (TextUtils.isEmpty(lastTime)) {
                lastTime = TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
            }
            params.put("last_requested_time", lastTime);
            HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
            if (status.isSuccess()) {
                MessageListEntity entity = JSON.parseObject(status.getResult(), MessageListEntity.class);
                if (entity != null && !entity.getLast_requested_time().equals("") && entity.getMessages() != null) {
                    TimePref.getInstance().setSystemMsgTime(entity.getLast_requested_time());
                    for (int i = 0; i < entity.getMessages().size(); i++) {
                        MessageEntity model = entity.getMessages().get(i);
                        SystemMsg msg = new SystemMsg();
                        msg.setUserId(UserPref.getInstance().getUserInfo().getUid());
                        msg.setId(model.getId());
                        msg.setContent(model.getContent());
                        msg.setType(model.getType());
                        msg.setAttribute(model.getAttributes());
                        msg.setCreatedAt(model.getCreated_at());
                        msg.setReaded(false);
                        GatherApplication.getInstance().getDaoSession().getSystemMsgDao().insertOrReplace(msg);
                    }
                    if (entity.getMessages().size() > 0) {
                        TimePref.getInstance().setRedTips(true);
                        EventCenter.getInstance().post(new CheckMsgReadedEvent(true));
                    }
                }
            }
//            DataBaseUtils.notifyTabReddot();
            QueryBuilder qb = GatherApplication.getInstance().getDaoSession().getSystemMsgDao().queryBuilder();
            qb.where(SystemMsgDao.Properties.UserId.eq(UserPref.getInstance().getUserInfo().getUid()));
            List<SystemMsg> list = (List<SystemMsg>) qb.list();
            Collections.reverse(list);
            return list;
        }

        @Override
        public boolean hasMore() {
            return false;
        }
    }

    /**
     * 消息列表Adapter
     */
    private class MessageOrgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<SystemMsg>> {

        private Context context;

        public MessageOrgAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_system, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            SystemMsg entity = list.get(position);

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

            viewHolder.tvContent.setText(entity.getContent());
            if (entity.getReaded()) {
                viewHolder.tvContent.setTextColor(0xFF9D9D9D);
            } else {
                viewHolder.tvContent.setTextColor(0xFF545452);
            }
            if (entity.getType().equals(MessageEntity.TYPE_URL) || entity.getType().equals(MessageEntity.TYPE_ACTIVITY) || entity.getType().equals(MessageEntity.TYPE_TEAM)) {
                viewHolder.ivArrow.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivArrow.setVisibility(View.INVISIBLE);
            }
            viewHolder.tvTime.setText(TimeUtil.getMessageTime(TimeUtil.getStringtoLong(entity.getCreatedAt())));
            viewHolder.llItemAll.setOnClickListener(new OnItemAllClickListener(entity));
            viewHolder.llItemAll.setOnLongClickListener(new OnItemLongClickListener(entity.getId(), position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void notifyDataChanged(List<SystemMsg> systemmsgEntity, boolean isRefresh) {
            if (isRefresh) {
                list.clear();
            }
            list.addAll(systemmsgEntity);
            notifyDataSetChanged();
        }

        @Override
        public List<SystemMsg> getData() {
            return list;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View viewFirstItem;
            LinearLayout llItemAll;
            TextView tvContent;
            TextView tvTime;
            ImageView ivArrow;
            View bottomLine;

            public ViewHolder(View itemView) {
                super(itemView);

                viewFirstItem = (View) itemView.findViewById(R.id.ViewFistItem);
                llItemAll = (LinearLayout) itemView.findViewById(R.id.llItemAll);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
                bottomLine = (View) itemView.findViewById(R.id.bottomLine);
            }
        }

        private class OnItemAllClickListener implements View.OnClickListener {

            private SystemMsg entity;

            public OnItemAllClickListener(SystemMsg entity) {
                this.entity = entity;
            }

            @Override
            public void onClick(View view) {
                TextView content = (TextView) view.findViewById(R.id.tvContent);
                if (!entity.getReaded()) {
                    entity.setReaded(true);
                    GatherApplication.getInstance().getDaoSession().getSystemMsgDao().insertOrReplace(entity);
//                    DataBaseUtils.notifyTabReddot();
                    content.setTextColor(0xFF9D9D9D);
                }
                Intent intent = null;
                if (entity.getType().equals(MessageEntity.TYPE_TEXT)) {

                } else if (entity.getType().equals(MessageEntity.TYPE_URL)) {
                    try {
                        intent = new Intent(getActivity(), WebActivity.class);
                        JSONObject object = new JSONObject(entity.getAttribute());
                        intent.putExtra("URL", object.getString("url"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (entity.getType().equals(MessageEntity.TYPE_ACTIVITY)) {
                    try {
                        intent = new Intent(getActivity(), ActDetail.class);
                        JSONObject object = new JSONObject(entity.getAttribute());
                        intent.putExtra(ActDetail.EXTRA_ID, object.getString("activity_id"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (entity.getType().equals(MessageEntity.TYPE_TEAM)) {
                    try {
                        intent = new Intent(getActivity(), OrgHome.class);
                        JSONObject object = new JSONObject(entity.getAttribute());
                        intent.putExtra("ID", object.getString("team_id"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (Constant.SHOW_LOG) {
                        Toast.makeText(context, "未知类型", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        private class OnItemLongClickListener implements View.OnLongClickListener {

            private long id;
            private int position;

            public OnItemLongClickListener(long id, int position) {
                this.id = id;
                this.position = position;
            }

            @Override
            public boolean onLongClick(View v) {
                if (mDelDialog == null){
                    mDelDialog = DialogCreater.createNormalDialog(getActivity(), "温馨提示", "确定要删除该消息吗？", new OnBtnLeftClickL() {
                        @Override
                        public void onBtnLeftClick() {
                            mDelDialog.dismiss();
                            GatherApplication.getInstance().getDaoSession().getOrgActMsgDao().deleteByKey(id);
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                }
                mDelDialog.show();
                return true;
            }
        }
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.reset(this);
        if (listViewHelper != null) {
            listViewHelper.destory();
        }
        EventCenter.getInstance().unregister(this);
    }
}
