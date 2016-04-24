package com.gather.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Christain on 2015/6/11.
 */
public class KeyBoardLinearLayout extends LinearLayout {
    private static final int SOFTKEYBOARD_MIN_HEIGHT = 20;
    private OnKeyBoardStateChangeListener mListener;

    public void setOnKeyBoardStateChangeListener(OnKeyBoardStateChangeListener listener){
        mListener = listener;
    }

    public KeyBoardLinearLayout(Context context) {
        super(context);
    }

    public KeyBoardLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoardLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null && oldh > 0){
            post(new Runnable() {
                @Override
                public void run() {
                    if (oldh - h > SOFTKEYBOARD_MIN_HEIGHT){
                        mListener.onStateChange(true);
                    }
                    else {
                        mListener.onStateChange(false);
                    }
                }
            });
        }
    }

    public interface OnKeyBoardStateChangeListener {
         public void onStateChange(boolean visible);
    }
}
