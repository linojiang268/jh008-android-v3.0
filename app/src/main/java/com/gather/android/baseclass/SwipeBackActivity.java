
package com.gather.android.baseclass;

import android.os.Bundle;
import android.view.View;

import com.gather.android.widget.swipeback.SwipeBackActivityBase;
import com.gather.android.widget.swipeback.SwipeBackActivityHelper;
import com.gather.android.widget.swipeback.SwipeBackLayout;
import com.gather.android.widget.swipeback.Utils;

public abstract class SwipeBackActivity extends BaseActivity implements SwipeBackActivityBase {
    
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
