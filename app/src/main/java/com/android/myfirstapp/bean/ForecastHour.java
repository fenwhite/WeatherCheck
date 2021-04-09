package com.android.myfirstapp.bean;

public class ForecastHour {
    private String time;
    private String temperature;
    private String text,icon;
    private String windScale,windDir;

    public String getTime() {
        return time;
    }

    public ForecastHour setTime(String time) {
        this.time = time;
        return this;
    }

    public String getTemperature() {
        return temperature;
    }

    public ForecastHour setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getText() {
        return text;
    }

    public ForecastHour setText(String text) {
        this.text = text;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public ForecastHour setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getWindScale() {
        return windScale;
    }

    public ForecastHour setWindScale(String windScale) {
        this.windScale = windScale;
        return this;
    }

    public String getWindDir() {
        return windDir;
    }

    public ForecastHour setWindDir(String windDir) {
        this.windDir = windDir;
        return this;
    }
}
