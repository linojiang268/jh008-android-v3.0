package com.gather.android.colonel.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gather.android.R;
import com.gather.android.entity.AttrsEntity;
import com.gather.android.entity.SignUpEntity;
import com.jihe.dialog.widget.base.BaseDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Levi on 15/10/3.
 */
public class DetailDialog extends BaseDialog {
    private List<ItemEntity> list = new ArrayList<>();

    private DetailAdapter adapter;
    private ListView listview;
    private LinearLayout.LayoutParams layoutParams;

    /**
     * method execute order:
     * show:constrouctor---show---oncreate---onStart---onAttachToWindow
     * dismiss:dismiss---onDetachedFromWindow---onStop
     *
     * @param context
     */
    public DetailDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    @Override
    public View onCreateView() {
        int padding = dp2px(16);
        LinearLayout ll_container = new LinearLayout(context);
        ll_container.setOrientation(LinearLayout.VERTICAL);
        ll_container.setPadding(padding, padding, padding, padding);
        ll_container.setBackgroundColor(context.getResources().getColor(R.color.style_color_primary));

        listview = new ListView(context);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        listview.setLayoutParams(layoutParams);
        listview.setCacheColorHint(Color.TRANSPARENT);
        listview.setFadingEdgeLength(0);
        listview.setVerticalScrollBarEnabled(false);
        listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listview.setDividerHeight(0);

        adapter = new DetailAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
            }
        });

        ll_container.addView(listview);
//
        return ll_container;
    }

    @Override
    public boolean setUiBeforShow() {
        int count = adapter.getCount() > 10 ? 10 : adapter.getCount();
        layoutParams.height = dp2px(30) * count;
        listview.setLayoutParams(layoutParams);

        adapter.notifyDataSetChanged();
        return false;
    }

    public void setData(SignUpEntity entity){
        list.clear();
        ItemEntity entity1 = new ItemEntity("报名时间：" + entity.getApplicant_time(), false);
        list.add(entity1);
        if (entity.getEnroll_fee() > 0){
            ItemEntity entity2 = new ItemEntity("报名费用：" + entity.getEnrollFeeStr(), true);
            list.add(entity2);
        }
        List<AttrsEntity> attrs = entity.getAttrs();
        if (attrs != null && attrs.size() > 0){
            for (int i = 0; i < attrs.size(); i++){
                AttrsEntity attr = attrs.get(i);
                ItemEntity entity3 = new ItemEntity(attr.getKey() + "：" + attr.getValue(), false);
                list.add(entity3);
            }
        }
    }

    private class ItemEntity {
        private boolean isPay = false;
        private String content;
        public ItemEntity(String c, boolean b){
            content = c;
            isPay = b;
        }
    }

    private class DetailAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ItemEntity getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null){
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView tvContent = new TextView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvContent.setLayoutParams(layoutParams);
                tvContent.setMinHeight(dp2px(30));
                tvContent.setTextColor(Color.WHITE);
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tvContent.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                linearLayout.addView(tvContent);

                convertView = linearLayout;
                holder = new ViewHolder();
                holder.tvContent = tvContent;
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            ItemEntity entity = list.get(position);
            String content = entity.content;
            if (entity.isPay){
                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
                builder.setSpan(redSpan, 5, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvContent.setText(builder);
            }
            else {
                holder.tvContent.setText(entity.content);
            }
            return convertView;
        }
    }

    private class ViewHolder {
        private TextView tvContent;
    }

}
