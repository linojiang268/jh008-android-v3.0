package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.entity.ActCheckInListEntity;
import com.gather.android.event.ActSignEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.TimeUtil;
import com.gather.android.widget.SlideView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 活动签到
 * Created by Administrator on 2015/8/25.
 */
public class ActSignDialog extends Dialog {

    @InjectView(R.id.tvSignTitle)
    TextView tvSignTitle;
    @InjectView(R.id.tvSignTime)
    TextView tvSignTime;
    @InjectView(R.id.tvSignNum)
    TextView tvSignNum;
    @InjectView(R.id.slider)
    SlideView slider;
    @InjectView(R.id.llSign)
    LinearLayout llSign;
    @InjectView(R.id.tvTips)
    TextView tvTips;
    @InjectView(R.id.llTips)
    LinearLayout llTips;

    private Context context;
    private ActCheckInListEntity entity;
//    private boolean isFromNoteBook;

    public ActSignDialog(Context context, ActCheckInListEntity entity) {
        super(context);
        this.context = context;
        this.entity = entity;
//        this.isFromNoteBook = isFromNoteBook;
    }

    public ActSignDialog(Context context, int theme, ActCheckInListEntity entity) {
        super(context, theme);
        this.context = context;
        this.entity = entity;
//        this.isFromNoteBook = isFromNoteBook;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_act_sign);
        ButterKnife.inject(this);
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        onWindowAttributesChanged(lp);
        setCanceledOnTouchOutside(false);

        llSign.setVisibility(View.VISIBLE);
        llTips.setVisibility(View.GONE);
        if (entity != null && entity.getCheck_list() != null && entity.getCheck_list().size() > 0) {
            initView();
        } else {
            dismiss();
        }

        slider.setSlideListener(new SlideView.SlideListener() {
            @Override
            public void onDone() {
                ActCheckIn(entity.getActivity_id(), entity.getStep(), entity.getVer());
            }
        });
    }

    private void initView() {
        tvSignTitle.setText(entity.getActivity_title());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
            tvSignTime.setText(getFirstDateKey(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvSignNum.setText(entity.getStep() + "");
    }

    private String getFirstDateKey(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String dateStr = sdf.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > 7) {
            return dateStr;
        } else {
            String[] WEEKS = context.getResources().getStringArray(R.array.weeks);
            return WEEKS[dayIndex - 1] + "，" + dateStr;
        }
    }

    /**
     * 活动签到
     */
    private void ActCheckIn(final String actId, final int step, String ver) {
//        Toast.makeText(context, "确认中...", Toast.LENGTH_SHORT).show();
        BaseParams params = new BaseParams(API.ACT_QRCODE_CHECKIN);
        params.put("activity_id", actId);
        params.put("step", step);
        params.put("ver", ver);
        OkHttpUtil.post(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                ActCheckInListEntity entity = JSON.parseObject(msg, ActCheckInListEntity.class);
                if (entity != null && entity.getCheck_list() != null && entity.getCheck_list().size() > 0) {
                    Toast.makeText(context, entity.getMessage(), Toast.LENGTH_LONG).show();
//                    if (!isFromNoteBook) {
//                        dismiss();
//                        EventCenter.getInstance().post(new FinishScannerEvent());
//                        Intent intent = new Intent(context, ActNoteBook.class);
//                        intent.putExtra("ID", actId);
//                        context.startActivity(intent);
//                    } else {
                        dismiss();
                        EventCenter.getInstance().post(new ActSignEvent(entity));
//                    }
                } else {
                    llSign.setVisibility(View.GONE);
                    tvTips.setText("签到信息异常\n请联系现场工作人员");
                    llTips.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void fail(int code, String error) {
                llSign.setVisibility(View.GONE);
                tvTips.setText(error);
                llTips.setVisibility(View.VISIBLE);
            }
        });
    }

}
