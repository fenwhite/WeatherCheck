package com.android.myfirstapp.bean;

public class BaseHttpBean {
    private int code;   //whether success?
    private String reText; //info about http

    public int getCode() {
        return code;
    }

    public BaseHttpBean setCode(int code) {
        this.code = code;
        return this;
    }

    public String getreText() {
        return reText;
    }

    public BaseHttpBean setreText(String errText) {
        this.reText = errText;
        return this;
    }
}
