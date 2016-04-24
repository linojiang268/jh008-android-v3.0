package com.gather.android.event;

import java.io.File;

/**
 * 图片单选或者裁剪返回
 * Created by Levi on 2015/8/4.
 */
public class SingleImageSelectEvent {
    private String path;
    public SingleImageSelectEvent(String path){
        this.path = path;
    }

    public SingleImageSelectEvent(File file){
        this.path = file.getAbsolutePath();
    }

    public String getPath(){
        return path;
    }

    public File getFile(){
        return new File(path);
    }
}
