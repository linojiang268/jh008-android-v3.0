package com.gather.android.entity;

import com.gather.android.R;
import com.gather.android.utils.Checker;

import java.io.Serializable;

/**
 * Created by Levi on 2015/8/25.
 */
public class ActFileEntity implements Serializable{

    /**
     * extension : doc
     * size : 2048000
     * name : 活动手册.doc
     * id : 1
     * url : http://download.domain.com/activity_file.doc
     */
    private String extension;
    private long size;
    private String name;
    private int id;
    private String url;
    private String created_at;
    private boolean isFirst = false;
    private String dateKey;


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExtension() {
        return extension;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getFileIconId(){
        if (!Checker.isEmpty(extension)){

            String ex = extension.toLowerCase();
            if (ex.equals("jpg") || ex.equals("jpeg") || ex.equals("png") || ex.equals("gif") || ex.equals("bmp")){
                return R.drawable.file_img;
            }
            else if (ex.equals("doc") || ex.equals("docx")){
                return R.drawable.file_word;
            }
            else if (ex.equals("pdf")){
                return R.drawable.file_pdf;
            }
            else if (ex.equals("ppt") || ex.equals("pptx")){
                return R.drawable.file_ppt;
            }
            else if (ex.equals("xlsx") || ex.equals("xls")){
                return R.drawable.file_excel;
            }
        }
        return R.drawable.file_other;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public String getDateKey() {
        return (dateKey != null) ? dateKey : "";
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }
}
