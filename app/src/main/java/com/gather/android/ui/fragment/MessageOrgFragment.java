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
import gather.database.bean.OrgActMsg;
import gather.database.dao.OrgActMsgDao;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 消息盒子----社团
 * Created by Administrator on 2015/7/28.
 */
public class MessageOrgFragment extends BaseFragment {

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.ptrLayout)
    PtrClassicFrameLayout ptrLayout;

    private MVCHelper<List<OrgActMsg>> listViewHelper;
    private ArrayList<OrgActMsg> list = new ArrayList<>();

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

        listViewHelper = new MVCUltraHelper<List<OrgActMsg>>(ptrLayout);
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
    private class Data extends BaseDataSource<List<OrgActMsg>> {

        @Override
        public List<OrgActMsg> refresh() throws Exception {
            return load();
        }

        @Override
        public List<OrgActMsg> loadMore() throws Exception {
            return null;
        }

        private List<OrgActMsg> load() {
            BaseParams params = new BaseParams(API.GET_MESSAGE_LIST);
            params.put("type", 2);
            String lastTime = TimePref.getInstance().getOrgActMsgTime();
            if (TextUtils.isEmpty(lastTime)) {
                lastTime = TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
            }
            params.put("last_requested_time", lastTime);
            HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
            if (status.isSuccess()) {
                MessageListEntity entity = JSON.parseObject(status.getResult(), MessageListEntity.class);
                if (entity != null && !entity.getLast_requested_time().equals("") && entity.getMessages() != null) {
                    TimePref.getInstance().setOrgActMsgTime(entity.getLast_requested_time());
                    for (int i = 0; i < entity.getMessages().size(); i++) {
                        MessageEntity model = entity.getMessages().get(i);
                        OrgActMsg msg = new OrgActMsg();
                        msg.setId(model.getId());
                        msg.setUserId(UserPref.getInstance().getUserInfo().getUid());
                        msg.setTeamName(model.getTeam_name());
                        msg.setContent(model.getContent());
                        msg.setType(model.getType());
                        msg.setAttribute(model.getAttributes());
                        msg.setCreatedAt(model.getCreated_at());
                        msg.setReaded(false);
                        GatherApplication.getInstance().getDaoSession().getOrgActMsgDao().insertOrReplace(msg);
                    }
                    if (entity.getMessages().size() > 0) {
                        TimePref.getInstance().setRedTips(true);
                        EventCenter.getInstance().post(new CheckMsgReadedEvent(true));
                    }
                }
            }
//            DataBaseUtils.notifyTabReddot();
            QueryBuilder qb = GatherApplication.getInstance().getDaoSession().getOrgActMsgDao().queryBuilder();
            qb.where(OrgActMsgDao.Properties.UserId.eq(UserPref.getInstance().getUserInfo().getUid()));
            List<OrgActMsg> list = (List<OrgActMsg>) qb.list();
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
    private class MessageOrgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<OrgActMsg>> {

        private Context context;

        public MessageOrgAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_org, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            OrgActMsg entity = list.get(position);

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
            viewHolder.tvOrgName.setText(entity.getTeamName());
            viewHolder.tvContent.setText(entity.getContent());
            viewHolder.tvTime.setText(TimeUtil.getMessageTime(TimeUtil.getStringtoLong(entity.getCreatedAt())));
            if (entity.getReaded()) {
                viewHolder.tvContent.setTextColor(0xFF9D9D9D);
                viewHolder.tvOrgName.setTextColor(0xFF9D9D9D);
            } else {
                viewHolder.tvContent.setTextColor(0xFF545452);
                viewHolder.tvOrgName.setTextColor(0xFF1C1C1C);
            }
            if (entity.getType().equals(MessageEntity.TYPE_URL) || entity.getType().equals(MessageEntity.TYPE_ACTIVITY) || entity.getType().equals(MessageEntity.TYPE_TEAM)) {
                viewHolder.ivArrow.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivArrow.setVisibility(View.INVISIBLE);
            }
            viewHolder.llItemAll.setOnClickListener(new OnItemAllClickListener(entity));
            viewHolder.llItemAll.setOnLongClickListener(new OnItemLongClickListener(entity.getId(), position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void notifyDataChanged(List<OrgActMsg> orgMessageEntities, boolean isRefresh) {
            if (isRefresh) {
                list.clear();
            }
            list.addAll(orgMessageEntities);
            notifyDataSetChanged();
        }

        @Override
        public List<OrgActMsg> getData() {
            return list;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View viewFirstItem;
            LinearLayout llItemAll;
            TextView tvOrgName;
            TextView tvContent;
            TextView tvTime;
            ImageView ivArrow;
            View bottomLine;

            public ViewHolder(View itemView) {
                super(itemView);

                viewFirstItem = (View) itemView.findViewById(R.id.ViewFistItem);
                llItemAll = (LinearLayout) itemView.findViewById(R.id.llItemAll);
                tvOrgName = (TextView) itemView.findViewById(R.id.tvOrgName);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
                bottomLine = (View) itemView.findViewById(R.id.bottomLine);
            }
        }

        private class OnItemAllClickListener implements View.OnClickListener {

            private OrgActMsg entity;

            public OnItemAllClickListener(OrgActMsg entity) {
                this.entity = entity;
            }

            @Override
            public void onClick(View view) {
                TextView name = (TextView) view.findViewById(R.id.tvOrgName);
                TextView content = (TextView) view.findViewById(R.id.tvContent);
                if (!entity.getReaded()) {
                    entity.setReaded(true);
                    GatherApplication.getInstance().getDaoSession().getOrgActMsgDao().insertOrReplace(entity);
//                    DataBaseUtils.notifyTabReddot();
                    content.setTextColor(0xFF9D9D9D);
                    name.setTextColor(0xFF9D9D9D);
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
