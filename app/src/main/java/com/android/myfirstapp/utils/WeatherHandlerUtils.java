package com.android.myfirstapp.utils;

import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.SunMoon;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.sunmoon.SunMoonBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;

import java.util.LinkedList;
import java.util.List;

public class WeatherHandlerUtils {
    public static BasicInfo getWeatherNow(WeatherNowBean weatherNowBean){
        if(weatherNowBean!=null && "200".equals(weatherNowBean.getCode().getCode())){
            BasicInfo basicInfo = new BasicInfo();
            WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
            basicInfo.setUpdateTime(now.getObsTime().substring(0,10))
                    .setText(now.getText())
                    .setIconCode(now.getIcon())
                    .setNowTemperature(now.getTemp())
                    .setWindDir(now.getWindDir())
                    .setWindScale(now.getWindScale());
            return basicInfo;
        }
        return null;
    }

    public static List<ForecastDay> getWeather3D(WeatherDailyBean weatherDailyBean){
        List<ForecastDay> list = new LinkedList<>();
        if(weatherDailyBean!=null && "200".equals(weatherDailyBean.getCode().getCode())){
            for (WeatherDailyBean.DailyBean day: weatherDailyBean.getDaily()) {
                ForecastDay forecastDay = new ForecastDay();
                forecastDay.setDate(day.getFxDate()).setText(day.getTextDay()).setIcon(day.getIconDay());
                forecastDay.setMinTemperature(day.getTempMin()).setMaxTemperature(day.getTempMax());
                list.add(forecastDay);
            }
        }
        return list;
    }

    public static Aqi getAirNow(AirNowBean airNowBean){
        if(airNowBean!=null && "200".equals(airNowBean.getCode().getCode())){
            Aqi aqi = new Aqi();
            AirNowBean.NowBean now = airNowBean.getNow();
            aqi.setApi(now.getAqi()).setCategory(now.getCategory()).setLevel(now.getLevel());
            if("NA".equals(now.getPrimary())){
                aqi.setPrimary("无");
            }else{
                aqi.setPrimary(now.getPrimary());
            }
            aqi.setPm25(now.getPm2p5()).setPm10(now.getPm10()).setSO2(now.getSo2());
            return aqi;
        }
        return null;
    }

    public static SunMoon getSunMoon(SunMoonBean sunMoonBean){
        if(sunMoonBean!=null && "200".equals(sunMoonBean.getCode().getCode())){
            SunMoon sunAndMoon = new SunMoon();
            SunMoonBean.SunMoonBeanBase obj1 = sunMoonBean.getSunMoonBean();
            List<SunMoonBean.MoonPhaseBean> obj2 = sunMoonBean.getMoonPhaseBeanList();
            sunAndMoon.setSunRaiseTime(obj1.getSunrise()).setSunSetTime(obj1.getSunset());
            sunAndMoon.setMoonRaiseTime(obj1.getMoonRise()).setMoonSetTime(obj1.getMoonSet());
            if(obj2.size()>0){
                sunAndMoon.setMoonState(obj2.get(0).getName());
            }
            return sunAndMoon;
        }
        return null;
    }
}
