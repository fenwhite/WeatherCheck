package com.android.myfirstapp.bean;

public class BasicInfo {
    private String updateTime;
    private String city,district;
    private String nowTemperature;
    private String iconCode;   //当前天气对应图标
    private String text;    //当前天气状况文字描述
    private String windDir;
    private String windScale;

    public BasicInfo() {
    }



    public BasicInfo(BasicInfo basicInfo) {
        this.updateTime = basicInfo.updateTime;
        this.nowTemperature = basicInfo.nowTemperature;
        this.iconCode = basicInfo.iconCode;
        this.text = basicInfo.text;
        this.windDir = basicInfo.windDir;
        this.windScale = basicInfo.windScale;
        this.city = basicInfo.city;
        this.district = basicInfo.district;
    }

    public String getDistrict() {
        return district;
    }

    public BasicInfo setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getCity() {
        return city;
    }

    public BasicInfo setCity(String city) {
        this.city = city;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public BasicInfo setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getNowTemperature() {
        return nowTemperature;
    }

    public BasicInfo setNowTemperature(String nowTemperature) {
        this.nowTemperature = nowTemperature;
        return this;
    }

    public String getIconCode() {
        return iconCode;
    }

    public BasicInfo setIconCode(String iconCode) {
        this.iconCode = iconCode;
        return this;
    }

    public String getText() {
        return text;
    }

    public BasicInfo setText(String text) {
        this.text = text;
        return this;
    }

    public String getWindDir() {
        return windDir;
    }

    public BasicInfo setWindDir(String windDir) {
        this.windDir = windDir;
        return this;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }
}
