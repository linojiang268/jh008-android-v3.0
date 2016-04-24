package com.gather.android.utils;


import android.content.Context;
import android.text.format.Formatter;

import com.gather.android.widget.NumberPicker;

/**
 * Created by Levi on 15/7/8.
 */
public class Checker {

    /**
     * 是否是手机号
     * @param str
     * @return
     */
    public static boolean isMobilePhone(String str){
        if (!isEmpty(str)){
//            return str.matches("^((13[0-9])|147|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            return str.matches("^([1-9])\\d{10}$");
        }
        return false;
    }

    /**
     * 是否为空或空字符串
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    /**
     * 是否是足够长度的字符串
     * @param str
     * @param minLong
     * @return
     */
    public static boolean isEnoughLong(String str, int minLong){
        return str != null && str.length() >= minLong;
    }

    /**
     * 152 2883 8270
     * @param phonenumber
     * @return
     */
    public static String formatPhoneNumber(String phonenumber){
        if (isEnoughLong(phonenumber, 11)){
            String phone = phonenumber.substring(0, 3) + " " + phonenumber.substring(3, 7) + " " + phonenumber.substring(7, 11);
            return phone;
        }
        else {
            return "";
        }
    }

    public static String formatFileSize(Context context,long sizeInByte){
        return Formatter.formatFileSize(context, sizeInByte);
    }

    public static String formatDownloadProgress(Context context, long total, long download){
        String t = formatFileSize(context, total);
        String d = formatFileSize(context, download);
        return String.format("%1$s / %2$s", d, t);
    }
}
