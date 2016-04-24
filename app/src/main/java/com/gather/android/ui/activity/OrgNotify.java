package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseDataSource;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.baseclass.SwipeBackActivity;
import com.gather.android.data.CityPref;
import com.gather.android.entity.OrgNotifyEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.TitleBar;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shizhefei.HttpResponseStatus;
import com.shizhefei.mvc.IDataAdapter;
import com.shizhefei.mvc.MVCHelper;
import com.shizhefei.mvc.MVCNormalHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * 社团通知
 * Created by Administrator on 2015/7/13.
 */
public class OrgNotify extends SwipeBackActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    private MVCHelper<List<OrgNotifyEntity>> listViewHelper;
    private List<OrgNotifyEntity> list = new ArrayList<>();
    private int orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_notify);
        Intent intent = getIntent();
        if (intent.hasExtra("ID")) {
            orgId = intent.getExtras().getInt("ID");
            titlebar.setHeaderTitle("通知");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            listViewHelper = new MVCNormalHelper<List<OrgNotifyEntity>>(recyclerView);
            listViewHelper.setDataSource(new OrgNotifySource());
            listViewHelper.setAdapter(new OrgNotifyAdapter(this));
            listViewHelper.refresh();
        } else {
            toast("社团通知信息错误");
            finish();
        }
    }

    private class OrgNotifySource extends BaseDataSource<List<OrgNotifyEntity>> {

        @Override
        public List<OrgNotifyEntity> refresh() throws Exception {
            BaseParams params = new BaseParams(API.GET_TAB_HOST_ORG_LIST);
            params.put("city", CityPref.getInstance().getCityId());
            params.put("name", "");
            params.put("page", 1);
            params.put("size", 20);
            HttpResponseStatus status = onResp(OkHttpUtil.syncGet(params));
            if (status.isSuccess()) {
                List<OrgNotifyEntity> list = null;
                try {
                    Thread.sleep(2000);
                    list = new ArrayList<OrgNotifyEntity>();
                    for (int i = 0; i < 10; i++) {
                        OrgNotifyEntity model = new OrgNotifyEntity();
                        model.setContent("通知" + i);
                        list.add(model);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    list = null;
                }
                return list;
            } else {
                return null;
            }
        }
    }

    private class OrgNotifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<OrgNotifyEntity>> {

        private Context context;
        private int lastAnimatedPosition;

        public OrgNotifyAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_org_notify, parent, false);
            return new NotifyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            runEnterAnimation(holder.itemView, position);
            NotifyViewHolder viewHolder = (NotifyViewHolder) holder;
            OrgNotifyEntity model = list.get(position);

//            viewHolder.tvContent.setText(model.getContent());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void notifyDataChanged(List<OrgNotifyEntity> orgNotifyEntifies, boolean isRefresh) {
            if (isRefresh) {
                lastAnimatedPosition = -1;
                list.clear();
            }
            list.addAll(orgNotifyEntifies);
            notifyDataSetChanged();
        }

        @Override
        public List<OrgNotifyEntity> getData() {
            return list;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        class NotifyViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llItemAll;
            TextView tvTime;
            TextView tvContent;

            public NotifyViewHolder(View itemView) {
                super(itemView);
                llItemAll = (LinearLayout) itemView.findViewById(R.id.llItemAll);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
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
                enterAnim.setDuration(700);
                enterAnim.setInterpolator(new DecelerateInterpolator(3));
                enterAnim.start();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listViewHelper.destory();
    }
}
