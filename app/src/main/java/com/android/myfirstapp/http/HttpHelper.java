package com.android.myfirstapp.http;

import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.ForecastHour;
import com.android.myfirstapp.bean.SunMoon;

import java.util.List;

public interface HttpHelper {
    public void getWeatherNow(City city);
    public void getWeather3D(String location);
    public void getWeather15D(String location);
    public void getWeather24Hourly(String location);
    public void getAirNow(String location);
    public void getSunMoon(String location);

}
