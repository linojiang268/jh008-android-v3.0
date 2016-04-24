package com.gather.android.widget.scrolltitlebar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Window;

import com.gather.android.R;
import com.gather.android.manager.PhoneManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ImmersiveTitleBar extends ExtendRelativeLayout {

    public static int SHADOW_MODE_PURE_COLOR = 1;
    public static int SHADOW_MODE_DRAWABLE = 2;

    private static final boolean TRANSLUCENT_STATUS_BAR = true;

    private boolean mFitSystemWindow = false;
    private boolean mImmersiveTitleBarEnabled;

    // ============================================================================
    // Draw shadow on status bar above API 19
    // ============================================================================
    private int mShadowMode = SHADOW_MODE_DRAWABLE;
    private Paint mPaint;
    private int mStatusBarHeight;
    private Drawable mShadowDrawable = null;

    public ImmersiveTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mImmersiveTitleBarEnabled = isImmersiveTitleBarEnabled();
        if (mImmersiveTitleBarEnabled) {
            mPaint = new Paint();
            mStatusBarHeight = PhoneManager.getStatusBarHigh();
            mPaint.setColor(getResources().getColor(R.color.transparent));
//            mPaint.setColor(0x26000000);
            setShadowDrawable(R.drawable.status_bar_shadow);
        }
    }

    /**
     * Set shadow drawable
     *
     * @param resourceId shadow drawable resourceId
     */
    public void setShadowDrawable(int resourceId) {
        mShadowDrawable = getContext().getResources().getDrawable(resourceId);
        if (mShadowDrawable != null) {
            mShadowDrawable.setBounds(0, 0, PhoneManager.getScreenWidth(), mShadowDrawable.getIntrinsicHeight());
        }
        // To make it nature, we don't set bounds to status bar height
        // mShadowDrawable.setBounds(0, 0, ViewUtils.getScreenWidth(), mStatusBarHeight);
    }

    /**
     * Set status bar shadow mode
     *
     * @param shadowMode shadow draw mode，One of {@link #SHADOW_MODE_DRAWABLE}, {@link #SHADOW_MODE_PURE_COLOR}.
     */
    public void setShadowMode(int shadowMode) {
        if (shadowMode == SHADOW_MODE_DRAWABLE) {
            mShadowMode = SHADOW_MODE_DRAWABLE;
        } else {
            mShadowMode = SHADOW_MODE_PURE_COLOR;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Immersive title bar is open, deal with shadow
        if (mImmersiveTitleBarEnabled && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            if (mShadowMode == SHADOW_MODE_DRAWABLE && mShadowDrawable != null) {
                mShadowDrawable.draw(canvas);
            } else {
                canvas.drawRect(0, 0, PhoneManager.getScreenWidth(), mStatusBarHeight, mPaint);
            }
        }
    }

    /**
     * open this if using fitsSystemWindows on root layout
     * @param fitsSystemWindows android:fitsSystemWindows
     */
    public void setTitleBarFitsSystemWindows(boolean fitsSystemWindows) {
        if (mFitSystemWindow != fitsSystemWindows) {
            mFitSystemWindow = fitsSystemWindows;
            if (mFitSystemWindow) {
                MarginLayoutParams marginLayoutParam = (MarginLayoutParams) getLayoutParams();
                marginLayoutParam.topMargin = -mStatusBarHeight;
                setLayoutParams(marginLayoutParam);
            }
        }
    }

    @Override
    public int getPaddingTop() {
        if (mImmersiveTitleBarEnabled) {
            return super.getPaddingTop() + mStatusBarHeight;
        }
        return super.getPaddingTop();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        if (!mImmersiveTitleBarEnabled) {
            return super.getSuggestedMinimumHeight();
        } else {
            int addition = 0;
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                addition = mStatusBarHeight;
            } else if (Build.VERSION.SDK_INT >= 20) {
                // Lollipop has a pure alpha black bar, so make title bar higher than kitkat
                addition = mStatusBarHeight + PhoneManager.dip2px(10);
            }
            return super.getSuggestedMinimumHeight() + addition;
        }
    }

    public static boolean isImmersiveTitleBarEnabled() {
        return TRANSLUCENT_STATUS_BAR && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Set theme mode on MIUI (dark)
     */
    public void setStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}