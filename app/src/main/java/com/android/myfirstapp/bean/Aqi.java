package com.android.myfirstapp.bean;

public class Aqi extends BaseHttpBean {
    private String api;
    //主要污染物 空气等级 等级文字描述
    private String primary,level,category;
    private String pm25,pm10,SO2,NO2,CO,O3;

    public Aqi() {
    }

    public Aqi(Aqi obj) {
        this.api = obj.api;
        this.primary = obj.primary;
        this.level = obj.level;
        this.category = obj.category;
        this.pm25 = obj.pm25;
        this.pm10 = obj.pm10;
        this.SO2 = obj.SO2;
    }

    public int getApi() {
        return Integer.valueOf(this.api);
    }

    public Aqi setApi(String api) {
        this.api = api;
        return this;
    }

    public String getPrimary() {
        return primary;
    }

    public Aqi setPrimary(String primary) {
        this.primary = primary;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public Aqi setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Aqi setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getPm25() {
        return pm25;
    }

    public Aqi setPm25(String pm25) {
        this.pm25 = pm25;
        return this;
    }

    public String getPm10() {
        return pm10;
    }

    public Aqi setPm10(String pm10) {
        this.pm10 = pm10;
        return this;
    }

    public String getSO2() {
        return SO2;
    }

    public Aqi setSO2(String SO2) {
        this.SO2 = SO2;
        return this;
    }

    public String getNO2() {
        return NO2;
    }

    public Aqi setNO2(String NO2) {
        this.NO2 = NO2;
        return this;
    }

    public String getCO() {
        return CO;
    }

    public void setCO(String CO) {
        this.CO = CO;
    }

    public String getO3() {
        return O3;
    }

    public void setO3(String o3) {
        O3 = o3;
    }
}
