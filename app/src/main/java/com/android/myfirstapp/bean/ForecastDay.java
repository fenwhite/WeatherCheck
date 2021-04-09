package com.android.myfirstapp.bean;

public class ForecastDay {
    private String date;
    private String maxTemperature,minTemperature;
    private String text;    //天气文字描述
    private String icon;    //天气状况代码

    public String getDate() {
        return date;
    }

    public ForecastDay setDate(String date) {
        this.date = date;
        return this;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public ForecastDay setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
        return this;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public ForecastDay setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
        return this;
    }

    public String getText() {
        return text;
    }

    public ForecastDay setText(String text) {
        this.text = text;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public ForecastDay setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public String toString() {
        return "ForecastDay{" +
                "date='" + date + '\'' +
                ", maxTemperature='" + maxTemperature + '\'' +
                ", minTemperature='" + minTemperature + '\'' +
                ", text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
