package com.gather.android.event;

import com.gather.android.event.lifecycle.LifeCycleComponentManager;

import de.greenrobot.event.EventBus;

public class EventCenter {

    private static final EventBus instance = new EventBus();

    private EventCenter() {
    }

    public static SimpleEventHandler bindContainerAndHandler(Object container, SimpleEventHandler handler) {
        LifeCycleComponentManager.tryAddComponentToContainer(handler, container);
        return handler;
    }

    public static final EventBus getInstance() {
        return instance;
    }

}
