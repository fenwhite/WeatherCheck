package com.android.myfirstapp.bean;

import java.util.LinkedList;
import java.util.List;

public class Weather {
    private BasicInfo basicInfo;
    private List<ForecastDay> forecastDayList;
    private List<ForecastHour> forecastHourList;
    private Aqi aqi;
    private SunMoon sunAndMoon;

    public SunMoon getSunAndMoon() {
        return sunAndMoon;
    }

    public Weather setSunAndMoon(SunMoon sunAndMoon) {
        this.sunAndMoon = new SunMoon(sunAndMoon);
        return this;
    }

    public Aqi getAqi() {
        return aqi;
    }

    public Weather setAqi(Aqi aqi) {
        this.aqi = new Aqi(aqi);
        return this;
    }

    public List<ForecastHour> getForecastHourList() {
        return forecastHourList;
    }

    public void setForecastHourList(List<ForecastHour> forecastHourList) {
        if(this.forecastHourList!=null)
            forecastHourList.clear();
        //todo
        this.forecastHourList = forecastHourList;
    }

    public Weather setForecastDayList(List<ForecastDay> forecastDayList) {
        if(this.forecastDayList != null)
            forecastDayList.clear();
        this.forecastDayList = new LinkedList<>(forecastDayList);
        return this;
    }

    public List<ForecastDay> getforecastDayList(){
        return this.forecastDayList;
    }

    public BasicInfo getBasicInfo() {
        return basicInfo;
    }

    public Weather setBasicInfo(BasicInfo basicInfo) {
        this.basicInfo = new BasicInfo(basicInfo);
        return this;
    }
}
