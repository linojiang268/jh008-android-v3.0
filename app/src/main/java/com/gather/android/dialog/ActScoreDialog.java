package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActScoreEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.manager.ClickUtil;
import com.gather.android.manager.PhoneManager;
import com.gather.android.widget.NoScrollGridView;
import com.gather.android.widget.RatingBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 活动评价对话框
 * Created by Administrator on 2015/8/24.
 */
public class ActScoreDialog extends Dialog {

    @InjectView(R.id.tvActTitle)
    TextView tvActTitle;
    @InjectView(R.id.tvActTime)
    TextView tvActTime;
    @InjectView(R.id.ivActMap)
    SimpleDraweeView ivActMap;
    @InjectView(R.id.tvOrgName)
    TextView tvOrgName;
    @InjectView(R.id.ivOrgIcon)
    SimpleDraweeView ivOrgIcon;
    @InjectView(R.id.llHasMap)
    LinearLayout llHasMap;
    @InjectView(R.id.ivNoMapOrgIcon)
    SimpleDraweeView ivNoMapOrgIcon;
    @InjectView(R.id.gridView)
    NoScrollGridView gridView;
    @InjectView(R.id.llNoMap)
    LinearLayout llNoMap;
    @InjectView(R.id.tvContent)
    TextView tvContent;
    @InjectView(R.id.ratingBar)
    RatingBarView ratingBar;
    @InjectView(R.id.tvCommit)
    TextView tvCommit;
    @InjectView(R.id.tvEnrollNum)
    TextView tvEnrollNum;
    @InjectView(R.id.llNormal)
    LinearLayout llNormal;
    @InjectView(R.id.etContent)
    EditText etContent;
    @InjectView(R.id.llEdit)
    LinearLayout llEdit;

    private Context context;
    private boolean isCanClick = true;
    private Animation alphaIn, otherAlphaIn;
    private ArrayList<MarkModel> markList = new ArrayList<MarkModel>();
    private MarkAdapter adapter;
    private ArrayList<ActScoreEntity> list;
    private int position;
    private int ratingStar = 0;

    public ActScoreDialog(Context context, ArrayList<ActScoreEntity> list, int position) {
        super(context);
        this.context = context;
        this.list = list;
        this.position = position;
    }

    public ActScoreDialog(Context context, int theme, ArrayList<ActScoreEntity> list, int position) {
        super(context, theme);
        this.context = context;
        this.list = list;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_act_score);
        ButterKnife.inject(this);
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        onWindowAttributesChanged(lp);
        setCanceledOnTouchOutside(false);
        alphaIn = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        otherAlphaIn =  AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        ratingBar.setOnRatingListener(new RatingBarView.OnRatingListener() {
            @Override
            public void onRating(Object bindObject, int RatingScore) {
                ratingStar = RatingScore;
                if (!tvCommit.isShown()) {
                    tvCommit.setVisibility(View.VISIBLE);
                }
                if (RatingScore <= 3) {
                    llHasMap.setVisibility(View.INVISIBLE);
                    if (!llNoMap.isShown()) {
                        llNoMap.setVisibility(View.VISIBLE);
                        llHasMap.clearAnimation();
                        llNoMap.startAnimation(otherAlphaIn);
                    }
                } else {
                    llNoMap.setVisibility(View.INVISIBLE);
                    if (!llHasMap.isShown()) {
                        llHasMap.setVisibility(View.VISIBLE);
                        llNoMap.clearAnimation();
                        llHasMap.startAnimation(otherAlphaIn);
                    }
                }
            }
        });

        if (list != null && list.size() > 0) {
            ActScoreEntity entity = list.get(position);
            initView(entity);
        } else {
            dismiss();
        }
    }

    private void initView(ActScoreEntity entity) {
        tvActTitle.setText(entity.getTitle());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(TextUtils.isEmpty(entity.getTeam().getLogo_url()) ? null : entity.getTeam().getLogo_url()))
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(ivOrgIcon.getController())
                .setImageRequest(request)
                .build();
        DraweeController controllers = Fresco.newDraweeControllerBuilder()
                .setOldController(ivNoMapOrgIcon.getController())
                .setImageRequest(request)
                .build();
        ivOrgIcon.setController(controller);
        ivNoMapOrgIcon.setController(controllers);
        tvOrgName.setText(entity.getTeam().getName());
        tvEnrollNum.setText("参与人数：" + entity.getEnrolled_num());
        ivActMap.setImageURI(Uri.parse(staticMapUrl(entity)));
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(entity.getBegin_time());
            tvActTime.setText(getFirstDateKey(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter = new MarkAdapter(context);
        gridView.setAdapter(adapter);
        List<String> mark = Arrays.asList(context.getResources().getStringArray(R.array.act_score));
        for (int i = 0; i < mark.size(); i++) {
            MarkModel model = new MarkModel();
            model.setMark(mark.get(i));
            markList.add(model);
        }
        adapter.notifyDataSetChanged();
    }

    private String getFirstDateKey(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dateStr = sdf.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > 7){
            return dateStr;
        }
        else {
            String[] WEEKS = context.getResources().getStringArray(R.array.weeks);
            return WEEKS[dayIndex - 1] + "，" + dateStr;
        }
    }

    private String staticMapUrl(ActScoreEntity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.map.baidu.com/staticimage?");
        sb.append("copyright=1");
        sb.append("&dpiType=ph");
        sb.append("&zoom=15");
        sb.append("&scale=1");
        sb.append("&width=");
        sb.append("800");
        sb.append("&height=");
        sb.append("400");
        sb.append("&center=");
        sb.append(entity.getLocation()[1] + "," + entity.getLocation()[0]);
        sb.append("&markers=");
        sb.append(entity.getLocation()[1] + "," + entity.getLocation()[0]);
//        for (int i = 0; i < model.getParking_spaces().size(); i++) {
//            sb.append("|");
//            sb.append(model.getParking_spaces().get(i).getLon() + "," + model.getParking_spaces().get(i).getLat());
//        }
        if (Constant.SHOW_LOG) {
            sb.append("&markerStyles=-1,http://dev.image.jhla.com.cn/app_icons/map/act_location.png");
        } else {
            sb.append("&markerStyles=-1,http://image.jh008.com/app_icons/map/act_location.png");
        }
//        for (int i = 0; i < model.getParking_spaces().size(); i++) {
//            sb.append("|");
//            sb.append("-1,http://jhla-app-icons.oss-cn-qingdao.aliyuncs.com/ic_location_parking.png");
//        }
        return sb.toString();
    }

    private class MarkAdapter extends BaseAdapter {

        private Context context;

        public MarkAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return markList.size();
        }

        @Override
        public MarkModel getItem(int position) {
            return markList.get(position);
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
                convertView = mInflater.inflate(R.layout.item_score_mark, null);
                holder = new ViewHolder();

                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.textView.getLayoutParams();
                params.width = (metrics.widthPixels - PhoneManager.dip2px(10 * 3 + 18 * 2)) / 2;
                params.height = PhoneManager.dip2px(35);
                holder.textView.setLayoutParams(params);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MarkModel model = getItem(position);
            holder.textView.setText(model.getMark());
            holder.textView.setSelected(model.hasSelected);
            holder.textView.setOnClickListener(new OnMarkClickListener(model));
            return convertView;
        }

        private class ViewHolder {
            TextView textView;
        }

        private class OnMarkClickListener implements View.OnClickListener {

            private MarkModel model;

            public OnMarkClickListener(MarkModel model) {
                this.model = model;
            }

            @Override
            public void onClick(View v) {
                if (isCanClick) {
                    isCanClick = false;
                    for (int i = 0; i < markList.size(); i++) {
                        markList.get(i).setHasSelected(false);
                    }
                    model.setHasSelected(true);
                    notifyDataSetChanged();
                    isCanClick = true;
                }
            }
        }
    }

    private class MarkModel {

        private String mark;
        private boolean hasSelected;

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public boolean isHasSelected() {
            return hasSelected;
        }

        public void setHasSelected(boolean hasSelected) {
            this.hasSelected = hasSelected;
        }
    }

    @OnClick({R.id.tvCommit, R.id.tvContent, R.id.tvEditOk})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tvContent:
                String content = tvContent.getText().toString().trim();
                if (content.equals("请留下宝贵意见")) {
                    etContent.setText("");
                } else {
                    etContent.setText(content);
                    etContent.setSelection(content.length());
                }
                llNormal.setVisibility(View.INVISIBLE);
                llEdit.setVisibility(View.VISIBLE);
                llNormal.clearAnimation();
                llEdit.startAnimation(alphaIn);
                etContent.requestFocus();
                break;
            case R.id.tvCommit:
                if (!ClickUtil.isFastClick()) {
                    Commit();
                }
                break;
            case R.id.tvEditOk:
                if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
                    tvContent.setTextColor(0xFF999999);
                    tvContent.setText("请留下宝贵意见");
                } else {
                    tvContent.setTextColor(0xFF333333);
                    tvContent.setText(etContent.getText().toString().trim());
                }
                etContent.clearFocus();
                if (llEdit.isShown()) {
                    llEdit.setVisibility(View.GONE);
                }
                llNormal.setVisibility(View.VISIBLE);
                llEdit.clearAnimation();
                llNormal.startAnimation(alphaIn);
                break;
        }
    }

    private void Commit() {
        if (ratingStar == 0) {
            Toast.makeText(context, "请先评分", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseParams params = new BaseParams(API.ACT_SCORE);
        params.put("activity", list.get(position).getId());
        params.put("score", ratingStar);
        if (!tvContent.getText().toString().trim().equals("请留下宝贵意见")) {
            params.put("memo", tvContent.getText().toString().trim());
        }
        if (ratingStar <= 3) {
            int index = -1;
            for (int i = 0; i < markList.size(); i++) {
                if (markList.get(i).isHasSelected()) {
                    index = index + 1;
                    params.put("attributes[" + index + "]", markList.get(i).getMark());
                }
            }
        }
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                Toast.makeText(context, "评价成功", Toast.LENGTH_SHORT).show();
                if (position == list.size() - 1) {
                    dismiss();
                } else {
                    dismiss();
                    ActScoreDialog scoreDialog = new ActScoreDialog(context, R.style.dialog_untran, list, position + 1);
                    scoreDialog.show();
                }
            }

            @Override
            public void fail(int code, String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
