package com.gather.android.entity;

import java.util.List;

/**
 * Created by Levi on 2015/8/25.
 */
public class ActFileListEntity {
    private int code;
    private int pages;
    private List<ActFileEntity> files;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<ActFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<ActFileEntity> files) {
        this.files = files;
    }
}
