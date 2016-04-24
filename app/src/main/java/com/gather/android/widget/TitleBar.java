package com.gather.android.widget;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;

/**
 * Created by Christain on 2015/6/11.
 */
public class TitleBar extends Toolbar {

    private TextView tvTitle;
    private ImageButton ibtnBakcpress;
    private RelativeLayout barLeft;
    private RelativeLayout barRight;
    private RelativeLayout barCenter;

    private String mTitle;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.title_bar_layout, this);
        barLeft = (RelativeLayout) findViewById(R.id.bar_left);
        barCenter = (RelativeLayout) findViewById(R.id.bar_center);
        barRight = (RelativeLayout) findViewById(R.id.bar_right);
        ibtnBakcpress = (ImageButton) findViewById(R.id.ibtn_backpress);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    public ImageButton getBackImageButton() {
        return ibtnBakcpress;
    }

    public TextView getTitleTextView() {
        return tvTitle;
    }

    public void setHeaderTitle(String title) {
        mTitle = title;
        tvTitle.setText(title);
    }
    public void setHeaderTitle(int id) {
        mTitle = getResources().getString(id);
        tvTitle.setText(mTitle);
    }

    public String getHeaderTitle() {
        return mTitle;
    }

    private RelativeLayout.LayoutParams makeLayoutParams(View view) {
        ViewGroup.LayoutParams lpOld = view.getLayoutParams();
        RelativeLayout.LayoutParams lp = null;
        if (lpOld == null) {
            lp = new RelativeLayout.LayoutParams(-2, -1);
        } else {
            lp = new RelativeLayout.LayoutParams(lpOld.width, lpOld.height);
        }
        return lp;
    }

    /**
     * set customized view to left side
     *
     * @param view the view to be added to left side
     */
    public void setCustomizedLeftView(View view) {
        ibtnBakcpress.setVisibility(GONE);
//        RelativeLayout.LayoutParams lp = makeLayoutParams(view);
////        lp.addRule(CENTER_VERTICAL);
////        lp.addRule(ALIGN_PARENT_LEFT);
//        getLeftViewContainer().addView(view, lp);
        getLeftViewContainer().addView(view);
    }


    /**
     * set customized view to left side
     *
     * @param layoutId the xml layout file id
     */
    public void setCustomizedLeftView(int layoutId) {
        View view = inflate(getContext(), layoutId, null);
        setCustomizedLeftView(view);
    }

    /**
     * set customized view to center
     *
     * @param view the view to be added to center
     */
    public void setCustomizedCenterView(View view) {
        tvTitle.setVisibility(GONE);
        RelativeLayout.LayoutParams lp = makeLayoutParams(view);
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        getCenterViewContainer().addView(view, lp);
    }

    /**
     * set customized view to center
     *
     * @param layoutId the xml layout file id
     */
    public void setCustomizedCenterView(int layoutId) {
        View view = inflate(getContext(), layoutId, null);
        setCustomizedCenterView(view);
    }

    /**
     * set customized view to right side
     *
     * @param view the view to be added to right side
     */
    public void setCustomizedRightView(View view) {
//        RelativeLayout.LayoutParams lp = makeLayoutParams(view);
////        lp.addRule(CENTER_VERTICAL);
////        lp.addRule(ALIGN_PARENT_RIGHT);
//        getRightViewContainer().addView(view, lp);
        getRightViewContainer().addView(view);
    }

    public RelativeLayout getLeftViewContainer() {
        return barLeft;
    }

    public RelativeLayout getCenterViewContainer() {
        return barCenter;
    }

    public RelativeLayout getRightViewContainer() {
        return barRight;
    }

    public void setLeftOnClickListener(OnClickListener l) {
        barLeft.setOnClickListener(l);
    }

    public void setCenterOnClickListener(OnClickListener l) {
        barCenter.setOnClickListener(l);
    }

    public void setRightOnClickListener(OnClickListener l) {
        barRight.setOnClickListener(l);
    }
}
