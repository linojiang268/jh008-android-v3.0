package com.gather.android.event;

import java.util.ArrayList;

/**
 * 图片多选返回
 * Created by Levi on 2015/8/4.
 */
public class MultiImageSelectEvent {
    private ArrayList<String> pathList;
    public MultiImageSelectEvent(ArrayList<String> list){
        this.pathList = list;
    }

    public ArrayList<String> getPathList(){
        return pathList;
    }

}
