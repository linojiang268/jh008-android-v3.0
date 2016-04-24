package com.gather.android.ui.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.entity.ActFlowEntity;
import com.gather.android.entity.ActPlansEntity;
import com.gather.android.widget.ElasticScrollView;
import com.gather.android.widget.NoScrollListView;
import com.gather.android.widget.TitleBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 活动手册
 * Created by Administrator on 2015/8/24.
 */
public class ActFlow extends BaseActivity {

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.tvActSign)
    TextView tvActSign;
    @InjectView(R.id.ivActSign)
    ImageView ivActSign;
    @InjectView(R.id.llNoProcess)
    LinearLayout llNoProcess;
    @InjectView(R.id.listview)
    NoScrollListView listview;
    @InjectView(R.id.tvActOrg)
    TextView tvActOrg;
    @InjectView(R.id.scrollView)
    ElasticScrollView scrollView;

    private ActFlowEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_flow);
        ButterKnife.inject(this);

        entity = (ActFlowEntity) getIntent().getSerializableExtra("MODEL");
        if (entity != null) {
            scrollView.setDamk(3f);
            scrollView.setElasticView(null);
            titlebar.setHeaderTitle("活动流程");
            titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            new InitFlowProgress().execute();
        } else {
            toast("活动手册信息错误");
            finish();
        }
    }

    private class InitFlowProgress extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            List<ActPlansEntity> list = entity.getActivity_plans();
            List<ActPlansEntity> result = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat keySdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            Stack<String> dateStack = new Stack<>();
            for (ActPlansEntity en: list) {
                try {
                    Date date = sdf.parse(en.getBegin_time());
                    String dateKey = keySdf.format(date);
                    String time = timeSdf.format(date);
                    en.setBegin_time(time);

                    Date endDate = sdf.parse(en.getEnd_time());
                    String endTime = timeSdf.format(endDate);
                    en.setEnd_time(endTime);

                    if (dateStack.contains(dateKey)) {
                        en.setIsFirst(false);
                        en.setDateKey(dateKey);
                    } else {
                        en.setIsFirst(true);
                        String key = getFirstDateKey(date);
                        en.setDateKey(key);
                        dateStack.add(dateKey);
                    }
                    result.add(en);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            setFlowInfo();
        }
    }

    private String getFirstDateKey(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd号");
        String dateStr = sdf.format(date);
        return dateStr;
    }

    private void setFlowInfo() {
        if (entity.getOrganizers() != null && entity.getOrganizers().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < entity.getOrganizers().size(); i++) {
                sb.append(entity.getOrganizers().get(i));
                if (i != entity.getOrganizers().size() - 1) {
                    sb.append("\n");
                }
            }
            tvActOrg.setText(sb.toString());
        } else {
            tvActOrg.setText(entity.getTeam().getName());
        }
        if (entity.getActivity_plans() != null && entity.getActivity_plans().size() > 0) {
            llNoProcess.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            ProcessAdapter adapter = new ProcessAdapter(ActFlow.this);
            listview.setAdapter(adapter);
        } else {
            llNoProcess.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }
        setSignStatus();
    }

    private void setSignStatus() {
        if (entity.getActivity_check_in_list() != null && entity.getActivity_check_in_list().size() > 0) {
            if (entity.getActivity_check_in_list().get(0).getStatus() == 0) {
                tvActSign.setVisibility(View.GONE);
//                tvActSign.setText(entity.getActivity_check_in_list().get(0).getStep() + "");
                ivActSign.setImageResource(R.drawable.icon_act_no_sign);
            } else {
                for (int i = entity.getActivity_check_in_list().size() - 1; i >= 0; i--) {
                    if (entity.getActivity_check_in_list().get(i).getStatus() == 1) {
                        tvActSign.setVisibility(View.VISIBLE);
                        tvActSign.setText(entity.getActivity_check_in_list().get(i).getStep() + "");
                        ivActSign.setImageResource(R.drawable.icon_act_has_sign);
                        return;
                    }
                }
            }
        } else {
            tvActSign.setVisibility(View.GONE);
            ivActSign.setVisibility(View.GONE);
        }
    }

    private class ProcessAdapter extends BaseAdapter {

        private Context context;

        public ProcessAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return entity.getActivity_plans().size();
        }

        @Override
        public ActPlansEntity getItem(int position) {
            return entity.getActivity_plans().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.item_act_process, parent, false);
                holder = new ViewHolder();
                holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                holder.tvDate.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                holder.tvDate.getPaint().setAntiAlias(true);//抗锯齿
                holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ActPlansEntity model = getItem(position);
            if (model.isFirst()) {
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.tvDate.setText(model.getDateKey());
            } else {
                holder.tvDate.setVisibility(View.GONE);
            }
            holder.tvTime.setText(model.getBegin_time() + "-" + model.getEnd_time());
            holder.tvContent.setText(model.getPlan_text());
            return convertView;
        }

        private class ViewHolder {
            TextView tvDate, tvTime, tvContent;
        }
    }

}
