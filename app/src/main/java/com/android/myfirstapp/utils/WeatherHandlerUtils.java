package com.android.myfirstapp.utils;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.ForecastHour;
import com.android.myfirstapp.bean.SunMoon;
import com.android.myfirstapp.myApplication;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.sunmoon.SunMoonBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;

import java.util.LinkedList;
import java.util.List;

public class WeatherHandlerUtils {
    public static BasicInfo getWeatherNow(WeatherNowBean weatherNowBean){
        BasicInfo basicInfo = new BasicInfo();
        if(weatherNowBean!=null && "200".equals(weatherNowBean.getCode().getCode())){
            WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
            basicInfo.setCode(ContentUtils.HTTPOK);
            basicInfo.setUpdateTime(now.getObsTime().substring(0,10))
                    .setText(now.getText())
                    .setIconCode(now.getIcon())
                    .setNowTemperature(now.getTemp())
                    .setWindDir(now.getWindDir())
                    .setWindScale(now.getWindScale());
            return basicInfo;
        }
        basicInfo.setCode(ContentUtils.HTTPFAIL);
        basicInfo.setreText(myApplication.getContext().getResources().getString(R.string.bean_fail));
        return basicInfo;
    }

    public static List<ForecastDay> getWeatherDayList(WeatherDailyBean weatherDailyBean){
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

    public static List<ForecastHour> getWeatherHourly(WeatherHourlyBean weatherHourlyBean) {
        List<ForecastHour> list = new LinkedList<>();
        if (weatherHourlyBean!=null && weatherHourlyBean.getCode().getCode().equals("200")) {
            for (WeatherHourlyBean.HourlyBean hour : weatherHourlyBean.getHourly()) {
                ForecastHour forecastHour = new ForecastHour();
                forecastHour.setText(hour.getText()).setIcon(hour.getIcon());
                forecastHour.setWindDir(hour.getWindDir()).setWindScale(hour.getWindScale() + '级');
                forecastHour.setTemperature(hour.getTemp() + "°C");
                forecastHour.setTime(DateUtils.gethm(hour.getFxTime(), 'T'));
                list.add(forecastHour);
            }
        }
        return list;
    }

    public static Aqi getAirNow(AirNowBean airNowBean){
        Aqi aqi = new Aqi();
        if(airNowBean!=null && "200".equals(airNowBean.getCode().getCode())){
            aqi.setCode(ContentUtils.HTTPOK);

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
        aqi.setCode(ContentUtils.HTTPFAIL);
        aqi.setreText(myApplication.getContext().getResources().getString(R.string.bean_fail));
        return aqi;
    }

    public static SunMoon getSunMoon(SunMoonBean sunMoonBean){
        SunMoon sunAndMoon = new SunMoon();
        if(sunMoonBean!=null && "200".equals(sunMoonBean.getCode().getCode())){
            sunAndMoon.setCode(ContentUtils.HTTPOK);

            SunMoonBean.SunMoonBeanBase obj1 = sunMoonBean.getSunMoonBean();
            List<SunMoonBean.MoonPhaseBean> obj2 = sunMoonBean.getMoonPhaseBeanList();
            sunAndMoon.setSunRaiseTime(obj1.getSunrise()).setSunSetTime(obj1.getSunset());
            sunAndMoon.setMoonRaiseTime(obj1.getMoonRise()).setMoonSetTime(obj1.getMoonSet());
            if(obj2.size()>0){
                sunAndMoon.setMoonState(obj2.get(0).getName());
            }
        }else{
            sunAndMoon.setCode(ContentUtils.HTTPFAIL);
            sunAndMoon.setreText(myApplication.getContext().getResources().getString(R.string.bean_fail));
        }

        return sunAndMoon;
    }
}
