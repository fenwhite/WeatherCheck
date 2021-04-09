package com.android.myfirstapp.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static SimpleDateFormat sdf = null;
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    public static String formatUTC(String s,String strPattern){
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(new Date(s));
    }
    public static String getYMD(String date){
        int pos = date.indexOf(' ');
        if(pos==-1){
            pos = date.indexOf('T');
            if(pos!=-1)
                return date.substring(0,pos);
            else {
                //not exist. format: YYYY-MM-DD
                return date;
            }
        }else{
            return date.substring(0,pos);
        }
    }

    public static boolean isHavehm(String date){
        return isHavehm(date,' ');
    }
    public static boolean isHavehm(String date,char sym){
        return date.indexOf(sym)!=-1;
    }

    public static String gethm(String date){
        return gethm(date,' ');
    }
    public static String gethm(String date,char sym){
        int pos = date.indexOf(sym);
        return date.substring(pos+1,pos+6);
    }

    public static int getHH(String date,char sym){
        if(isHavehm(date,sym)){
            StringBuffer sb = new StringBuffer(gethm(date, sym));
            return Integer.valueOf(gethm(date, sym).substring(0,2));
        }
        return -1;
    }
    public static int getmm(String date,char sym){
        if(isHavehm(date,sym)){
            StringBuffer sb = new StringBuffer(gethm(date, sym));
            return Integer.valueOf(gethm(date, sym).substring(3,5));
        }
        return -1;
    }

    public static String getTimePart(String date,char model){
        String YMD = getYMD(date);
        StringBuffer sb = new StringBuffer();
        switch (model){
            case 'Y':
                sb.append(YMD.substring(0,4));
                break;
            case 'H':
                if(YMD.charAt(5)=='0'){    // 03
                    sb.append(YMD.charAt(6));
                }else{                      //10
                    sb.append(YMD.substring(5,7));
                }
            case 'D':
                if(YMD.charAt(8)=='0'){
                    sb.append(YMD.charAt(9));
                }else{
                    sb.append(YMD.substring(8,10));
                }
        }
        return sb.toString();
    }

    public static String formatChinese(String date){
        StringBuffer tmp = new StringBuffer(getYMD(date));
        tmp.setCharAt(4,'年');
        tmp.setCharAt(7,'月');
        tmp.append('日');
        return tmp.toString();
    }

    public static boolean is00(String time){
        if(isHavehm(time)){
            return gethm(time).equals("00:00");
        }
        return false;
    }
}
