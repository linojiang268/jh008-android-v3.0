package com.gather.android.baseclass;

import com.gather.android.event.lifecycle.IComponentContainer;
import com.gather.android.event.lifecycle.LifeCycleComponent;
import com.gather.android.event.lifecycle.LifeCycleComponentManager;
import com.shizhefei.fragment.LazyFragment;

/**
 * Created by Christain on 2015/6/18.
 */
public class BaseFragment extends LazyFragment implements IFragment, IComponentContainer {

    private boolean mFirstResume = true;
    private LifeCycleComponentManager mComponentContainer = new LifeCycleComponentManager();

    @Override
    public void addComponent(LifeCycleComponent component) {
        this.mComponentContainer.addComponent(component);
    }

    @Override
    public void onEnter(Object data) {

    }

    @Override
    public void onLeave() {
        this.mComponentContainer.onBecomesTotallyInvisible();
    }

    @Override
    public void onBack() {
        this.mComponentContainer.onBecomesVisibleFromTotallyInvisible();
    }

    @Override
    public void onBackWithData(Object data) {
        this.mComponentContainer.onBecomesVisibleFromTotallyInvisible();
    }

    @Override
    public boolean processBackPressed() {
        return false;
    }

    @Override
    protected void onFragmentStopLazy() {
        super.onFragmentStopLazy();
        this.onLeave();
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        if (!this.mFirstResume) {
            this.onBack();
        }

        if (this.mFirstResume) {
            this.mFirstResume = false;
        }
    }

    public void onDestroy() {
        super.onDestroy();

        this.mComponentContainer.onDestroy();
    }

}
