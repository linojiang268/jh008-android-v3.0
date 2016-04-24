package com.gather.android.event;


import com.gather.android.event.lifecycle.LifeCycleComponent;

public class SimpleEventHandler implements LifeCycleComponent {

    private boolean mRegistered = false;

    public SimpleEventHandler register() {
        if (!mRegistered) {
            mRegistered = true;
            EventCenter.getInstance().register(this);
        }
        return this;
    }

    public synchronized SimpleEventHandler tryToUnregister() {
        if (mRegistered) {
            mRegistered = false;
            EventCenter.getInstance().unregister(this);
        }
        return this;
    }

    public synchronized SimpleEventHandler tryToRegisterIfNot() {
        register();
        return this;
    }

    @Override
    public void onBecomesVisibleFromTotallyInvisible() {

    }

    @Override
    public void onBecomesPartiallyInvisible() {

    }

    @Override
    public void onBecomesVisible() {
        register();
    }

    @Override
    public void onBecomesTotallyInvisible() {

    }

    @Override
    public void onDestroy() {
        tryToUnregister();
    }

}