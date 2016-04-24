package com.gather.android.event;

/**
 * 社团隐私设置
 * Created by Christain on 2015/7/23.
 */
public class OrgSecretSetEvent {

    private int visible;

    public OrgSecretSetEvent(int visible) {
        this.visible = visible;
    }

    public int getVisible() {
        return visible;
    }

}
