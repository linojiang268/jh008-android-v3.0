package com.gather.android.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {



    /**
     * 获取格式化的时间
     *
     * @param format    yyyy-MM-dd HH:mm; MM-dd HH:mm;HH:mm;yyyy-MM-dd;yyyy年M月d日
     *                  HH:mm;M月d日 HH:mm;H:mm;...
     * @param timeMills
     * @return
     */
    public static String getFormatedTime(String format, long timeMills) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(timeMills);
    }

    /**
     * 获取年月日
     *
     * @param oldStyleTime
     * @return
     */
    public static String getYMD(String oldStyleTime) {
        try {
            if (oldStyleTime.contains("-") && oldStyleTime.length() > 5) {
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date strtodate = oldFormat.parse(oldStyleTime, pos);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = format.format(strtodate);
                if (dateString != null) {
                    return dateString;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    /**
     * 获取时分
     */
    public static String getHM(String oldStyleTime) {
        try {
            if (oldStyleTime.contains("-") && oldStyleTime.length() > 5) {
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date strtodate = oldFormat.parse(oldStyleTime, pos);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String dateString = format.format(strtodate);
                if (dateString != null) {
                    return dateString;
                } else {
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断日期是否同一天
     */
    public static boolean isSameDay(String FirstTime, String SecondTime) {
        if (FirstTime.contains("-") && FirstTime.length() > 5) {
            try {
                SimpleDateFormat firstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date strtodate = firstFormat.parse(FirstTime, pos);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String firstString = format.format(strtodate);

                SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition secondpos = new ParsePosition(0);
                Date secondData = secondFormat.parse(SecondTime, secondpos);
                SimpleDateFormat secondformat = new SimpleDateFormat("yyyy-MM-dd");
                String secondString = secondformat.format(secondData);

                return firstString.equals(secondString);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 点分隔符的日期格式
     *
     * @return
     */
    public static String pointStyleDate(String oldStyleTime) {
        try {
            if (oldStyleTime.contains("-") && oldStyleTime.length() > 5) {
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date strtodate = oldFormat.parse(oldStyleTime, pos);
                SimpleDateFormat format = new SimpleDateFormat("M月d日 HH:mm");
                String dateString = format.format(strtodate);
                if (dateString != null) {
                    return dateString;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getStringtoLong(String time) {
        try {
            if (time.contains("-") && time.length() > 5) {
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date strtodate = oldFormat.parse(time, pos);
                return strtodate.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取用户年龄
     *
     * @param birthday
     * @return
     */
    public static int getUserAge(String birthday) {
        try {
            if (birthday.contains("-")) {
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
                ParsePosition pos = new ParsePosition(0);
                Date date = oldFormat.parse(birthday, pos);
                Date dateNow = new Date(System.currentTimeMillis());
                if (dateNow.getMonth() + 1 < date.getMonth() + 1) {
                    return dateNow.getYear() - date.getYear() - 1;
                } else if (dateNow.getMonth() == date.getMonth()) {
                    if (dateNow.getDate() < date.getDate()) {
                        return dateNow.getYear() - date.getYear() - 1;
                    } else {
                        return dateNow.getYear() - date.getYear();
                    }
                } else {
                    if (dateNow.getYear() == date.getYear()) {
                        return -1;
                    } else {
                        return dateNow.getYear() - date.getYear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取私信时间格式
     *
     * @param
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getMessageTime(long timeSeconds) {
        if (timeSeconds != 0) {
            long timeMills = timeSeconds;
            long millsNow = System.currentTimeMillis();
            long dMills = (millsNow - timeMills) / 1000;// 秒
            Date dateNow = new Date(millsNow);
            Date date = new Date(timeMills);
            if (dateNow.getYear() != date.getYear()) {
                return getFormatedTime("yyyy年M月d日  HH:mm", timeMills);
            }
            if (dMills <= 60) {
                return "刚刚";
            }
            if (dMills <= 3600) {
                return dMills / 60 + "分钟前";
            }
            if (dMills <= 7200) {
                return "1小时前";
            }
            if (dMills <= 172800) {
                if (dateNow.getDay() == date.getDay()) {
                    return "" + getFormatedTime("HH:mm", timeMills);
                } else {
                    return "昨天" + getFormatedTime("HH:mm", timeMills);
                }
            }
            if (dMills < 259200) {
                return "前天" + getFormatedTime("HH:mm", timeMills);
            } else {
                return getFormatedTime("M月d日 HH:mm", timeMills);
            }
        } else {
            return "未知";
        }
    }

}
